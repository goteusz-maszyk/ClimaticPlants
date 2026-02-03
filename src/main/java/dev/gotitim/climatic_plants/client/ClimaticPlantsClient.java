package dev.gotitim.climatic_plants.client;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.ModBlocks;
import dev.gotitim.climatic_plants.network.PlantClimatePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.client.renderer.RenderType;

public class ClimaticPlantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FROZEN_BUSH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_FROZEN_BUSH, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEAD_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_DEAD_SAPLING, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEAD_FUNGUS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_DEAD_FUNGUS, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BURNT_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FROZEN_CROP, RenderType.cutout());

        ClientConfigurationNetworking.registerGlobalReceiver(PlantClimatePayload.ID, (payload, context) -> {
            ClimaticPlants.climates.clear();
            ClimaticPlants.climates.putAll(payload.data());
        });

    }
}
