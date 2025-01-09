package hollowed.pulchra.blocks;

import hollowed.pulchra.Pulchra;
import hollowed.pulchra.blocks.custom.BigCandleBlock;
import hollowed.pulchra.blocks.custom.PedestalBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block BIG_CANDLE = registerBlock("big_candle",
            new BigCandleBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE).strength(0.2F, 0F).nonOpaque().luminance(BigCandleBlock.STATE_TO_LUMINANCE)));

    public static final Block PEDESTAL = registerBlock("pedestal",
            new PedestalBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).strength(1F, 6F).nonOpaque().requiresTool()));

    public static final Block SLATE = registerBlock("slate",
            new PillarBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.DEEPSLATE).strength(1F, 6F)));

    public static final Block SLATE_BRICKS = registerBlock("slate_bricks",
            new PillarBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.DEEPSLATE).strength(1F, 6F)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Pulchra.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Pulchra.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Pulchra.LOGGER.info("Registering ModBlocks for " + Pulchra.MOD_ID);
    }
}
