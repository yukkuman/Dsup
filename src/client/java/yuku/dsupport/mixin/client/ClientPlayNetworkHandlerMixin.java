package com.dogoma.mixin.client;

import com.dogoma.partyUI.PartyTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onChat(GameMessageS2CPacket packet, CallbackInfo ci) {
        String raw = packet.content().getString();

        System.out.println("Log : "+ raw);

        // パーティー参加
        if (raw.matches("^(.*?)がパーティーに参加しました！$")) {
            String name = raw.replace("がパーティーに参加しました！", "");
            PartyTracker.join(name);
        }

        // パーティーからキックされた（他人）
        else if (raw.matches("^(.*?)がパーティーからキックされました。$")) {
            String name = raw.replace("がパーティーからキックされました。", "");
            PartyTracker.leave(name);
        }

        // 自分がキックされた
        else if (raw.contains("あなたは") && raw.contains("によってパーティーからキックされました。")) {
            PartyTracker.resetParty();
        }

        // リーダー交代
        else if (raw.matches("^(.*?)が新しいパーティーリーダーになりました！$")) {
            String name = raw.replace("が新しいパーティーリーダーになりました！", "");
            PartyTracker.setLeader(name);
        }

        // パーティー解散
        else if (raw.equals("パーティーは解散されました。")) {
            PartyTracker.resetParty();
        }

        // 自分が退出
        else if (raw.contains("あなたは") && raw.contains("のパーティーから退出しました。")) {
            PartyTracker.resetParty();
        }

        else if (raw.startsWith("パーティーの所有者")) {
            String owner = raw.replace("パーティーの所有者:", "").trim();
            PartyTracker.setLeader(owner);

        }
    }

}
