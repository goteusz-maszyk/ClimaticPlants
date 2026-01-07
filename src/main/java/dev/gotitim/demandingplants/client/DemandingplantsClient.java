package dev.gotitim.demandingplants.client;

import dev.gotitim.demandingplants.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class DemandingplantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FROZEN_BUSH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_FROZEN_BUSH, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEAD_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_DEAD_SAPLING, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEAD_FUNGUS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_DEAD_FUNGUS, RenderType.cutout());
    }
}
