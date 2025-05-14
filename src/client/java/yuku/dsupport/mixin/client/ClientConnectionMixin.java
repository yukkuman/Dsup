package yuku.dsupport.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuku.dsupport.itemLock.ClientLockRegistry;
import yuku.dsupport.itemLock.LockKeyHandler;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof PlayerActionC2SPacket actionPacket) {
            PlayerActionC2SPacket.Action action = actionPacket.getAction();

            if (action == PlayerActionC2SPacket.Action.DROP_ITEM || action == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    ItemStack stack = client.player.getMainHandStack();
                    int lockState = ClientLockRegistry.getLockState(stack);

                    if (lockState > 0) {
                        client.player.sendMessage(
                                Text.literal("このアイテムはロックされているためドロップできません").formatted(Formatting.RED),
                                true
                        );
                        ci.cancel(); // パケット送信キャンセル
                    }
                }
            }
        }
        if (packet instanceof ClickSlotC2SPacket clickPacket) {

            MinecraftClient client = MinecraftClient.getInstance();

            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / client.getWindow().getHeight();

            if (client.currentScreen==null) return;



            Slot slot = ((HandledScreenAccessor) client.currentScreen).callGetSlotAt(mouseX, mouseY);

            ItemStack stack = slot==null ? null : slot.getStack();

            int lockState = ClientLockRegistry.getLockState(stack);


            // 完全ロック → すべてキャンセル
            if (lockState == 2) {
                ci.cancel();
                return;
            }

            int lockState_curried = ClientLockRegistry.getLockState(client.player.currentScreenHandler.getCursorStack());

            // セミロック → ドロップ関連・数字キー移動をキャンセル
            if (lockState_curried == 1) {
                if (clickPacket.getSlot() == -999) {
                    ci.cancel();
                }
            }


        }

    }
}
