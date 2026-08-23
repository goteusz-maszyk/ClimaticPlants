package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.client.render.QuernBlockEntityRenderer;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ClimaticPlantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ClimaticBlocks.QUERN_BLOCK_ENTITY, QuernBlockEntityRenderer::new);
    }
}
