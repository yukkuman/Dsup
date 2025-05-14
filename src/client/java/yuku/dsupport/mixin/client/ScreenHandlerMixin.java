package yuku.dsupport.mixin.client;

import yuku.dsupport.itemLock.ClientLockRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void cancelDropLockedItem(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler handler = (ScreenHandler)(Object)this;

        // クリック対象がスロット内（slotIndex >= 0）の場合
        if (slotIndex >= 0 && slotIndex < handler.slots.size()) {
            Slot slot = handler.slots.get(slotIndex);
            if (slot.hasStack()) {
                ItemStack stack = slot.getStack();
                if (ClientLockRegistry.getLockState(stack) == 2) {
                    ci.cancel();
                    handler.setStackInSlot(slotIndex, 0, stack.copy());
                }
            }
        }

        // プレイヤーのカーソルアイテムもチェック（外枠クリックやドラッグドロップ）
        ItemStack carried = player.currentScreenHandler.getCursorStack();
        if (!carried.isEmpty() && ClientLockRegistry.getLockState(carried) == 1) {
            ci.cancel();
            handler.setCursorStack(carried.copy());
        }

        ItemStack cursor = player.currentScreenHandler.getCursorStack();
        if (!cursor.isEmpty()) {
            int lockState = ClientLockRegistry.getLockState(cursor);
            if (lockState == 2 || (lockState == 1 && actionType == SlotActionType.THROW)) {
                // カーソルのアイテム復元
                player.currentScreenHandler.setCursorStack(cursor.copy());

            }
        }


    }

}
