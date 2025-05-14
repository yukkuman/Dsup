package yuku.dsupport.item;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public enum ItemRarity {

    SPECIAL("スペシャル", 6, Formatting.DARK_PURPLE),
    MIRACLE("ミラクル", 5, Formatting.GOLD),
    LEGENDARY("レジェンド", 4, Formatting.YELLOW),
    ULTRARARE("ウルトラレア", 3, Formatting.RED),
    SUPERRARE("スーパーレア", 2, Formatting.AQUA),
    RARE("レア", 1, Formatting.GREEN),
    COMMON("ノーマル", 0, Formatting.GRAY);

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
