package yuku.dsupport.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuku.dsupport.itemLock.ClientLockRegistry;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;dropSelectedItem(Z)Z"
            ),
            cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // GUI外 & プレイヤーが存在 & optionsがnullでないことを確認
        if (client.currentScreen == null && client.player != null && client.options != null) {
            if (client.options.dropKey.isPressed()) {
                ItemStack stack = client.player.getMainHandStack();
                int lockState = ClientLockRegistry.getLockState(stack);

                if (lockState > 0) {
                    // ドロップをキャンセル

                    ci.cancel();

                    // 任意：プレイヤーに通知
                    client.player.sendMessage(Text.literal("このアイテムはロック中のためドロップできません").formatted(Formatting.RED), true);
                }
            }
        }
    }
}
