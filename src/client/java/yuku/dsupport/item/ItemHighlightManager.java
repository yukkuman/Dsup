package yuku.dsupport.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import yuku.dsupport.mixin.client.EntityAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static yuku.dsupport.item.ItemRarityChecker.shouldHighlight;

public class ItemHighlightManager {

    public static boolean enableHighlight = true;
    private static final Map<Integer, Integer> highlightTicks = new HashMap<>();
    public static int growSec = 10;

    private static final File HighLight_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "itemhighlighter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!HighLight_FILE.exists()) return;
        try (Reader reader = new FileReader(HighLight_FILE)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                switch (key) {
                    case "GrowSec" -> growSec = Integer.parseInt(String.valueOf(entry.getValue()));
                    case "enableHighLight" -> enableHighlight = Boolean.parseBoolean(String.valueOf(entry.getValue()));
                    case "minimumRarity" -> ItemRarityChecker.MINIMUM_HIGHLIGHT_RARITY = ItemRarity.fromString(String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
            System.err.println("[ItemHighLighter] 状態の読み込みに失敗: " + e.getMessage());
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(HighLight_FILE)) {
            JsonObject json = new JsonObject();
            json.addProperty("GrowSec", growSec);
            json.addProperty("enableHighLight", enableHighlight);
            json.addProperty("minimumRarity", ItemRarityChecker.MINIMUM_HIGHLIGHT_RARITY.keyword);
            GSON.toJson(json, writer);
        } catch (Exception e) {
            System.err.println("[ItemHighLighter] 状態の保存に失敗: " + e.getMessage());
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ItemHighlightManager::onTick);
    }

    private static void onTick(MinecraftClient client) {
        if (!enableHighlight) return;
        if (client.world == null) return;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getStack();
                if (!highlightTicks.containsKey(itemEntity.getId()) && shouldHighlight(stack)) {
                    highlightTicks.put(itemEntity.getId(), growSec*20);
                    ItemRarity rarity = ItemRarity.fromLore(stack.getTooltip(client.player, TooltipContext.ADVANCED));
                    makeEntityGlowWithColor(client, entity, rarity.color);
                }
            }
        }

        highlightTicks.replaceAll((id, ticks) -> ticks - 1);
        Iterator<Map.Entry<Integer, Integer>> iterator = highlightTicks.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getValue()>0) continue;
            int id = entry.getKey();
            if (entry.getValue()==-1) {
                highlightTicks.replace(id, 0);
                continue;
            }
            stopItemGlow((ItemEntity) client.world.getEntityById(id));
            if (client.world.getEntityById(id)!=null) continue;
            iterator.remove();
        }
    }

    private static void makeEntityGlowWithColor(MinecraftClient client, Entity entity, Formatting color) {
        if (client.world == null || client.player == null) return;

        Scoreboard scoreboard = client.world.getScoreboard();
        String teamName = "glow_" + color.getName();

        // 既存のチームを取得、または作成
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setColor(color);
            team.setShowFriendlyInvisibles(false);
            team.setFriendlyFireAllowed(true);
        }

        // エンティティをチームに追加
        scoreboard.addScoreHolderToTeam(entity.getNameForScoreboard(), team);
        // 発光を有効にする
        try {

            TrackedData<Byte> entityFlags = EntityAccessor.getFLAGS();
            byte currentFlags = entity.getDataTracker().get(entityFlags);
            entity.getDataTracker().set(entityFlags, (byte) (currentFlags | 0x40)); // 0x40 は 1 << 6

            // デバッグ用: 発光処理を試みたことをログに出力
            // System.out.println("[ItemGlowMod] Made ItemEntity " + itemEntity.getId() + " glow.");

        } catch (Exception e) {
            // エラー処理: FLAGSフィールドが見つからない、アクセスできない等の問題発生時
            System.err.println("[ItemGlowMod] Failed to set glowing state for ItemEntity " + entity.getId() + ": " + e.getMessage());
            // e.printStackTrace(); // 詳細なスタックトレース
        }
    }

    public static void stopItemGlow(ItemEntity itemEntity) {
        if (itemEntity == null || !itemEntity.isAlive()) {
            // エンティティが無効な場合は何もしない
            return;
        }

        // クライアント側でのみ実行することを保証（呼び出し元で制御するか、ここでチェック）
        if (!itemEntity.getWorld().isClient) {
            // System.err.println("[ItemGlowMod] stopItemGlow should only be called on the client side.");
            return; // サーバーサイドで呼ばれた場合は何もしないか、エラーを出す
        }

        try {
            // Accessor経由でEntity.FLAGS (TrackedData<Byte>オブジェクト) を取得
            TrackedData<Byte> entityFlags = EntityAccessor.getFLAGS();

            byte currentFlags = itemEntity.getDataTracker().get(entityFlags);

            // 発光ビット(0x40)が立っているか確認
            if ((currentFlags & 0x40) != 0) { // すでに発光している場合のみ更新
                // 発光ビット(0x40)をオフにする
                // ~0x40 は、0x40のビット以外が全て1のマスク (例: ...10111111)
                // これを現在のフラグとAND演算することで、該当ビットのみを0にし、他は維持する
                byte newFlags = (byte) (currentFlags & ~0x40);
                itemEntity.getDataTracker().set(entityFlags, newFlags);

                // デバッグ用ログ（任意）
                // System.out.println("[ItemGlowMod] Stopped glow for ItemEntity " + itemEntity.getId());
            }

        } catch (Exception e) {
            System.err.println("[ItemGlowMod] Failed to stop glowing state for ItemEntity " + itemEntity.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


}
