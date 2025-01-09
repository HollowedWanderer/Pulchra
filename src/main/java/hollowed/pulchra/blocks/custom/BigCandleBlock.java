package hollowed.pulchra.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;
import java.util.function.ToIntFunction;

public class BigCandleBlock extends AbstractBigCandleBlock {

    public static final MapCodec<BigCandleBlock> CODEC = createCodec(BigCandleBlock::new);
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE;
    public static final EnumProperty<PillarPart> PART = EnumProperty.of("part", PillarPart.class);

    @Override
    protected MapCodec<? extends AbstractBigCandleBlock> getCodec() {
        return CODEC;
    }

    public BigCandleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(PART, PillarPart.DEFAULT));
    }

    @Override
    protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
        // Calculate the offset to position the particle slightly above the block
        double xOffset = 0.5; // Center horizontally
        double yOffset = 1.4; // Slightly above the block
        double zOffset = 0.5; // Center horizontally

        return List.of(new Vec3d(xOffset, yOffset, zOffset));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, PART);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState().with(PART, this.getPillarPart(world, pos));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.setBlockState(pos, state.with(PART, this.getPillarPart(world, pos)), 3);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Check if a block is placed above and extinguish the candle
        if (direction == Direction.UP && state.get(LIT)) {
            if (!neighborState.isAir() && (state.get(PART) == PillarPart.TOP || state.get(PART) == PillarPart.DEFAULT)) {
                return state.with(LIT, false);
            }
        }

        // Update the pillar part property
        if (direction.getAxis() == Direction.Axis.Y) {
            return state.with(PART, this.getPillarPart((World) world, pos));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);

        // Light the candle
        if (heldItem.isOf(Items.FLINT_AND_STEEL) && (state.get(PART) == PillarPart.DEFAULT || state.get(PART) == PillarPart.TOP) && !state.get(LIT)) {
            world.setBlockState(pos, state.with(LIT, true), 3);
            world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            // Update luminance dynamically
            this.updateLuminance(world, pos, 8);

            if (!player.isCreative()) {
                heldItem.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ItemActionResult.SUCCESS;
        }

        // Extinguish the candle
        if (heldItem.isEmpty() && state.get(LIT)) {
            world.setBlockState(pos, state.with(LIT, false), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);

            // Reset luminance
            this.updateLuminance(world, pos, 0);
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.FAIL;
    }

    private void updateLuminance(World world, BlockPos pos, int luminance) {
        BlockPos currentPos = pos.down();
        int currentLuminance = luminance - 1;

        while (currentLuminance > 0) {
            BlockState currentState = world.getBlockState(currentPos);
            if (currentState.getBlock() instanceof BigCandleBlock) {
                world.setBlockState(currentPos, currentState.with(LIT, true), 3);
                currentLuminance--;
                currentPos = currentPos.down();
            } else {
                break;
            }
        }

        // Extinguish any remaining candles below the lit chain
        while (world.getBlockState(currentPos).getBlock() instanceof BigCandleBlock) {
            BlockState currentState = world.getBlockState(currentPos);
            if (currentState.get(LIT)) {
                world.setBlockState(currentPos, currentState.with(LIT, false), 3);
            }
            currentPos = currentPos.down();
        }
    }

    static {
        STATE_TO_LUMINANCE = (state) -> (Boolean) state.get(LIT) ? 8 : 0;
    }


    private PillarPart getPillarPart(World world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        BlockState stateAbove = world.getBlockState(pos.up());

        boolean isSameBelow = stateBelow.getBlock() instanceof BigCandleBlock;
        boolean isSameAbove = stateAbove.getBlock() instanceof BigCandleBlock;

        if (isSameBelow && isSameAbove) {
            return PillarPart.MIDDLE;
        } else if (isSameBelow) {
            return PillarPart.TOP;
        } else if (isSameAbove) {
            return PillarPart.BOTTOM;
        } else {
            return PillarPart.DEFAULT;
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum PillarPart implements StringIdentifiable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top"),
        DEFAULT("default");

        private final String name;

        PillarPart(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
