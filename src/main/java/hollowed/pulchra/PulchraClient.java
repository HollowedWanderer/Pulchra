package hollowed.pulchra;

import hollowed.pulchra.blocks.ModBlocks;
import hollowed.pulchra.blocks.entities.ModBlockEntities;
import hollowed.pulchra.blocks.entities.renderer.PedestalRenderer;
import hollowed.pulchra.client.pedestal.PedestalTooltipRenderer;
import hollowed.pulchra.networking.PedestalPacketReceiver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PulchraClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        /*
            Block Renderers
         */
        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.PEDESTAL, ModBlocks.BIG_CANDLE);

        /*
            Packets
         */
        PedestalPacketReceiver.registerClientPacket();

        /*
            In Game Tooltips
         */
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.world != null && client.player != null && client.player.isSneaking()) {
                int screenWidth = client.getWindow().getScaledWidth();
                int screenHeight = client.getWindow().getScaledHeight();
                PedestalTooltipRenderer.renderTooltip(context, screenWidth, screenHeight);
            }
        });
    }
}
