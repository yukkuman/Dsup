package yuku.dsupport.partyUI;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public abstract class PartyHudRenderer {

    private static final int ICON_SIZE = 16;

    public static void render(DrawContext context, float v) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) return;

        int x = 10;
        int y = 10;

        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;

            String name = player.getName().toString();

            if (!PartyTracker.currentMembers.contains(name)) continue;

            // 顔アイコン

            Identifier skin = ((AbstractClientPlayerEntity) player).getSkinTextures().texture();
            context.drawTexture(skin, x, y, 8, 8, ICON_SIZE, ICON_SIZE, 64, 64);


            // 名前表示
            context.drawText(client.textRenderer, name, x + 20, y + 2, 0xFFFFFF, true);

            // 距離表示
            double dist = client.player.squaredDistanceTo(player);
            int meters = (int) Math.sqrt(dist);
            context.drawText(client.textRenderer, meters + "m", x + 20, y + 12, 0xAAAAAA, false);

            // HPバー（可能なら）
            float hp = player.getHealth();
            float maxHp = player.getMaxHealth();
            int barWidth = 40;
            int hpBar = (int) (barWidth * (hp / maxHp));

            context.fill(x + 20, y + 22, x + 20 + barWidth, y + 26, 0xFF000000); // 背景
            context.fill(x + 20, y + 22, x + 20 + hpBar, y + 26, 0xFFFF5555);   // 赤いHPバー

            y += 32;
        }
    }
}
