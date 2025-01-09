package hollowed.pulchra.blocks.entities.custom;

import hollowed.pulchra.blocks.entities.ModBlockEntities;
import hollowed.pulchra.networking.PedestalPacketPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PedestalBlockEntity extends BlockEntity implements ComponentHolder, SidedInventory, ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    // Handles dropping the contents when the block is broken
    public void drops() {
        if (world != null) {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    world.spawnEntity(itemEntity);
                }
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.writeNbt(nbt, items, registries);
        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, items, registries);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        Inventories.writeNbt(nbt, items, registries); // Write the current inventory data to NBT
        return nbt; // Return the NBT data for synchronization
    }

    @Override
    public void markDirty() {
        super.markDirty();
        assert world != null;
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    // Static tick method for block entity ticking
    public static void tick(World world, PedestalBlockEntity blockEntity) {
        if (world.isClient()) {
            return; // Don't run logic on the client side
        }

        blockEntity.markDirty(); // Mark the entity as dirty to save state
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        markDirty();

        // Sync the item with all players on the server
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getPlayers().forEach(serverPlayer -> {
                ServerPlayNetworking.send(serverPlayer, new PedestalPacketPayload(this.pos, stack));
            });

            // Force a chunk update for rendering
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack stack = Inventories.splitStack(getItems(), slot, count);
        if (!stack.isEmpty()) {
            markDirty();
        }

        setStack(slot, Items.AIR.getDefaultStack());

        return stack;
    }


    // SidedInventory methods to control hopper interaction
    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0}; // Only slot 0 is available
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        // Allow insertion only if the pedestal is empty
        return items.get(slot).isEmpty() && stack.getCount() > 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // Allow extraction only if there's an item in the pedestal
        return !items.get(slot).isEmpty();
    }
}
