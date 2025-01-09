package hollowed.pulchra.blocks.entities.renderer;

import hollowed.pulchra.blocks.entities.custom.PedestalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    private static final Vec3d ITEM_POS = new Vec3d(0.5, 1.5, 0.5);

    public PedestalRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(PedestalBlockEntity pedestalBlockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack heldItem = pedestalBlockEntity.getStack(0);

        // Proceed only if the item stack is not empty
        if (!heldItem.isEmpty()) {
            // Get the world time for animation
            long worldTime = Objects.requireNonNull(pedestalBlockEntity.getWorld()).getTime();
            float rotation = (worldTime + tickDelta) * 2; // Smooth rotation
            float bob = (float) Math.sin((worldTime + tickDelta) * 0.1f) * 0.0875f; // Smooth bobbing

            renderItem(heldItem, ITEM_POS.add(0, bob, 0), rotation, matrices, vertexConsumers, light, overlay, pedestalBlockEntity.getWorld());
        }
    }

    private void renderItem(ItemStack itemStack, Vec3d offset, float yRot, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, World world) {
        matrices.push();

        // Get the item renderer instance
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        // Apply transformations
        matrices.translate(offset.x, offset.y, offset.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
        matrices.scale(0.65f, 0.65f, 0.65f);

        if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof MiningToolItem || itemStack.getItem() instanceof MaceItem) {
            matrices.translate(0, 0.1f, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
        }

        // Render the item
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, world, (int) world.getTime());

        matrices.pop();
    }
}
