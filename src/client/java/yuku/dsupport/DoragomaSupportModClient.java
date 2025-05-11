package yuku.dsupport;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import yuku.dsupport.item.ItemHighlightManager;
import yuku.dsupport.itemLock.ClientLockRegistry;
import yuku.dsupport.itemLock.LockKeyHandler;
import yuku.dsupport.partyUI.PartyHudRenderer;

public class DoragomaSupportModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LockKeyHandler.register();
		ClientLockRegistry.load();
		ItemHighlightManager.register();


		HudRenderCallback.EVENT.register(PartyHudRenderer::render);

	}
}