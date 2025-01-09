package hollowed.pulchra.networking;

import hollowed.pulchra.Pulchra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PedestalPacketPayload(BlockPos blockPos, ItemStack stack) implements CustomPayload {
    public static final Id<PedestalPacketPayload> ID = new Id<>(Identifier.of(Pulchra.MOD_ID, "pedestal_packet"));

    public static final PacketCodec<RegistryByteBuf, PedestalPacketPayload> CODEC = PacketCodec.of(PedestalPacketPayload::write, PedestalPacketPayload::new);

    public PedestalPacketPayload(RegistryByteBuf buf) {
        this(buf.readBlockPos(), decodeStack(buf));
    }

    private static ItemStack decodeStack(RegistryByteBuf buf) {
        return buf.readBoolean() ? ItemStack.PACKET_CODEC.decode(buf) : ItemStack.EMPTY;
    }

    public void write(RegistryByteBuf buf) {
        buf.writeBlockPos(blockPos);
        boolean hasItem = !stack.isEmpty();
        buf.writeBoolean(hasItem);
        if (hasItem) {
            ItemStack.PACKET_CODEC.encode(buf, stack);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
