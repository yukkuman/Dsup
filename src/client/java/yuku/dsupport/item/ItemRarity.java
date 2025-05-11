package yuku.dsupport.item;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public enum ItemRarity {

    COMMON("コモン", 0, Formatting.GRAY),
    UNCOMMON("至高", 1, Formatting.GREEN),
    RARE("レア", 2, Formatting.AQUA),
    LEGENDARY("伝説", 3, Formatting.YELLOW),
    MYTHIC("神話", 4, Formatting.DARK_PURPLE);

    public final String keyword;
    public final int level;
    public final Formatting color;

    ItemRarity(String keyword, int level, Formatting color) {
        this.keyword = keyword;
        this.level = level;
        this.color = color;
    }

    public static ItemRarity fromLore(List<Text> lore) {
        for (Text line : lore) {
            String s = line.getString();
            for (ItemRarity r : values()) {
                if (s.contains(r.keyword)) {
                    return r;
                }
            }
        }
        return COMMON;
    }

}
