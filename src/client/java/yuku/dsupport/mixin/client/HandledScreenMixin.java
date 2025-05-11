package yuku.dsupport.mixin.client;

import yuku.dsupport.itemLock.ClientLockRegistry;
import yuku.dsupport.itemLock.LockKeyHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (LockKeyHandler.LOCK_KEY.matchesKey(keyCode, scanCode)) {
            HandledScreen<?> screen = (HandledScreen<?>) (Object)this;

            MinecraftClient client = MinecraftClient.getInstance();
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / client.getWindow().getHeight();

            Slot slot = ((HandledScreenAccessor) screen).callGetSlotAt(mouseX, mouseY);
            if (slot != null && slot.hasStack()) {
                ItemStack stack = slot.getStack();
                ClientLockRegistry.cycleLockState(stack);
                cir.setReturnValue(true); // 入力を消費
            }
        }

        if (keyCode == MinecraftClient.getInstance().options.dropKey.getDefaultKey().getCode()) {
            HandledScreen<?> screen = (HandledScreen<?>) (Object)this;

            MinecraftClient client = MinecraftClient.getInstance();
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / client.getWindow().getHeight();

            Slot slot = ((HandledScreenAccessor) screen).callGetSlotAt(mouseX, mouseY);
            if (slot != null && slot.hasStack()) {
                ItemStack stack = slot.getStack();
                int state = ClientLockRegistry.getLockState(stack);
                if (state > 0) {
                    cir.setReturnValue(true); // ドロップキーによるドロップをキャンセル
                }
            }
        }
    }


    @Inject(
            method = "drawSlot",
            at = @At("TAIL")
    )
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {

        HandledScreen<?> screen = (HandledScreen<?>) (Object)this;

        int slotId = slot.id;
        int state = ClientLockRegistry.getLockState(screen.getScreenHandler().getSlot(slotId).getStack());
        if (state == 0) return;

        // スロットの座標
        int left = slot.x;
        int top = slot.y;

        // 枠線の色（ARGB）
        int color = switch (state) {
            case 1 -> 0x80FFA500; // 薄いオレンジ（半透明）
            case 2 -> 0x80FF0000;      // 薄い赤（半透明）
            default -> 0;
        };

        // 1px の四角形を描く
        context.fill(left, top, left + 16, top + 16, color);
    }


}
