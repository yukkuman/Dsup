package yuku.dsupport.ModMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import yuku.dsupport.item.ItemHighlightManager;
import yuku.dsupport.item.ItemRarity;
import yuku.dsupport.item.ItemRarityChecker;


@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("ハイライト設定"));

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("基本設定"));

            general.addEntry(
                    ConfigEntryBuilder.create()
                            .startBooleanToggle(Text.literal("アイテムを光らせる"), ItemHighlightManager.enableHighlight)
                            .setSaveConsumer(newValue -> ItemHighlightManager.enableHighlight = newValue)
                            .build()
            );

            general.addEntry(
                    ConfigEntryBuilder.create()
                            .startEnumSelector(Text.literal("最低レアリティ"), ItemRarity.class, ItemRarityChecker.MINIMUM_HIGHLIGHT_RARITY)
                            .setSaveConsumer(newValue -> ItemRarityChecker.MINIMUM_HIGHLIGHT_RARITY = newValue)
                            .build()
            );

            general.addEntry(
                    ConfigEntryBuilder.create()
                            .startIntField(Text.literal("光らせる時間"), ItemHighlightManager.growSec)
                            .setSaveConsumer(newValue -> ItemHighlightManager.growSec = newValue)
                            .build()
            );

            // 任意で保存処理
            builder.setSavingRunnable(ItemHighlightManager::save);

            return builder.build();
        };
    }
}