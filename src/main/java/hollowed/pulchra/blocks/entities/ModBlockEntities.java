package hollowed.pulchra.blocks.entities;

import hollowed.pulchra.Pulchra;
import hollowed.pulchra.blocks.ModBlocks;
import hollowed.pulchra.blocks.entities.custom.PedestalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Pulchra.MOD_ID, "pedestal"),
                    FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new,
                            ModBlocks.PEDESTAL).build());

    public static void initialize() {
        Pulchra.LOGGER.info("Antiquities Block Entities Initialized");
    }
}
