package yuku.dsupport.itemLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientLockRegistry {
    private static final Map<String, Integer> lockMap = new HashMap<>();

    private static final File LOCK_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "itemlocker.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!LOCK_FILE.exists()) return;
        try (Reader reader = new FileReader(LOCK_FILE)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            lockMap.clear();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                int state = Integer.parseInt(String.valueOf(entry.getValue()));
                lockMap.put(key, state);
            }
        } catch (Exception e) {
            System.err.println("[ItemLocker] ロック状態の読み込みに失敗: " + e.getMessage());
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(LOCK_FILE)) {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Integer> entry : lockMap.entrySet()) {
                json.addProperty(String.valueOf(entry.getKey()), entry.getValue());
            }
            GSON.toJson(json, writer);
        } catch (Exception e) {
            System.err.println("[ItemLocker] ロック状態の保存に失敗: " + e.getMessage());
        }
    }

    public static String getItemKey(ItemStack stack) {
        return stack.getItem().toString() + "::" + stack.getName().getString();
    }

    public static int getLockState(ItemStack stack) {
        return stack != null ? lockMap.getOrDefault(getItemKey(stack), 0) : 0;
    }

    public static void cycleLockState(ItemStack stack) {
        String key = getItemKey(stack);
        int next = (lockMap.getOrDefault(key, 0) + 1) % 3;
        lockMap.put(key, next);
    }
}
