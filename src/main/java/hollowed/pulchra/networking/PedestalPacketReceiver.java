package hollowed.pulchra.networking;

import hollowed.pulchra.blocks.entities.custom.PedestalBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PedestalPacketReceiver {
    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(PedestalPacketPayload.ID, (payload, context) -> context.client().execute(() -> {
                World world = context.player().getWorld();
                BlockPos pos = payload.blockPos();
                ItemStack stack = payload.stack();

                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof PedestalBlockEntity) {
                    ((PedestalBlockEntity) entity).setStack(0, stack);
                    world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
                }
        }));
    }
}
