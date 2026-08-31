package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.client.render.BarrelBlockEntityRenderer;
import dev.gotitim.climatic_plants.client.render.QuernBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ClimaticPlantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ClimaticBlockEntities.QUERN, QuernBlockEntityRenderer::new);
        BlockEntityRenderers.register(ClimaticBlockEntities.FLUID_BARREL, BarrelBlockEntityRenderer::new);
    }
}
