package yuku.dsupport.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ItemRarityChecker {

    public static ItemRarity MINIMUM_HIGHLIGHT_RARITY = ItemRarity.SUPERRARE;


    public static boolean shouldHighlight(ItemStack stack) {
        // if (!stack.) return false;

        MinecraftClient client = MinecraftClient.getInstance();

        List<Text> lore = stack.getTooltip(client.player, TooltipContext.ADVANCED);
        ItemRarity rarity = ItemRarity.fromLore(lore);
        return rarity.level >= MINIMUM_HIGHLIGHT_RARITY.level;
    }

}
