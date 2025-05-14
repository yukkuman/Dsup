package yuku.dsupport.itemLock;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


public class LockKeyHandler {

    public static final KeyBinding LOCK_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.clientitemlock.lock",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_L,
                    "category.clientitemlock"
            )
    );

    public static void register() {

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ClientLockRegistry.save();
        });


    }

    public static void cancelItemDrop(ClickSlotC2SPacket clickPacket, CallbackInfo ci) {

        ItemStack stack = clickPacket.getStack();
        if (stack.isEmpty()) return;

        int lockState = ClientLockRegistry.getLockState(stack);
        SlotActionType actionType = clickPacket.getActionType();

        // 完全ロック → すべてキャンセル
        if (lockState == 2) {
            ci.cancel();
        }

        // セミロック → ドロップ関連・数字キー移動をキャンセル
        if (lockState == 1) {
            if (actionType == SlotActionType.THROW || actionType == SlotActionType.QUICK_CRAFT || actionType == SlotActionType.SWAP) {
                ci.cancel();
            }
        }
    }
}

