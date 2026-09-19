package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.client.render.BarrelBlockEntityRenderer;
import dev.gotitim.climatic_plants.client.render.QuernBlockEntityRenderer;
import dev.gotitim.climatic_plants.content.ClimaticFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class ClimaticPlantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ClimaticBlockEntities.QUERN, QuernBlockEntityRenderer::new);
        BlockEntityRenderers.register(ClimaticBlockEntities.FLUID_BARREL, BarrelBlockEntityRenderer::new);

        FluidRenderingRegistry.register(
                ClimaticFluids.YEAST,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(ARGB.opaque(0xE6C99C))
                )
        );
    }
}
