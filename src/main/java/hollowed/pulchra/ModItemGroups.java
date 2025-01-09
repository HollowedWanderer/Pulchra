package hollowed.pulchra;

import hollowed.pulchra.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.SLATE))
            .displayName(Text.translatable("itemGroup.pulchra.pulchra").withColor(0x006c3e))
            .entries((context, entries) -> {
                entries.add(ModBlocks.SLATE);
                entries.add(ModBlocks.SLATE_BRICKS);
                entries.add(ModBlocks.PEDESTAL);
            })
            .build();


    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of("pulchra", "pulchra"), ITEM_GROUP);
        Pulchra.LOGGER.info("Registering Item Group for " + Pulchra.MOD_ID);
    }
}
