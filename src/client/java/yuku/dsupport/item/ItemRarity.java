package yuku.dsupport.item;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public enum ItemRarity {

    COMMON("ノーマル", 0, Formatting.GRAY),
    RARE("レア", 1, Formatting.GREEN),
    SUPERRARE("スーパーレア", 2, Formatting.AQUA),
    ULTRARARE("ウルトラレア", 3, Formatting.RED),
    LEGENDARY("レジェンド", 4, Formatting.YELLOW),
    MIRACLE("ミラクル", 5, Formatting.GOLD),
    SPECIAL("スペシャル", 6, Formatting.DARK_PURPLE);

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
