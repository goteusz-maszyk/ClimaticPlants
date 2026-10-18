package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.client.render.BarrelBlockEntityRenderer;
import dev.gotitim.climatic_plants.client.render.QuernBlockEntityRenderer;
import dev.gotitim.climatic_plants.client.screen.BarrelScreen;
import dev.gotitim.climatic_plants.content.ClimaticFluids;
import dev.gotitim.climatic_plants.content.quern.QuernBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClimaticPlantsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ClimaticBlockEntities.QUERN, QuernBlockEntityRenderer::new);
        BlockEntityRenderers.register(ClimaticBlockEntities.FLUID_BARREL, BarrelBlockEntityRenderer::new);

        MenuScreens.register(ClimaticBlockEntities.BARREL_CONTAINER, BarrelScreen::new);

        FluidRenderingRegistry.register(
                ClimaticFluids.YEAST,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(ARGB.opaque(0xE6C99C))));

        registerQuernOutline();
    }

    private static void registerQuernOutline() {
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null || minecraft.player == null) {
                return true;
            }
            if (!(minecraft.hitResult instanceof BlockHitResult hit)
                    || !hit.getBlockPos().equals(outline.pos())) {
                return true;
            }
            VoxelShape shape =
                    QuernBlock.getPlayerSelectionShape(minecraft.level, outline.pos(), minecraft.player, hit);
            if (shape == null) return true;

            var pose = context.poseStack();
            var cameraPos = context.levelState().cameraRenderState.pos;
            pose.pushPose();
            pose.translate(
                    outline.pos().getX() - cameraPos.x,
                    outline.pos().getY() - cameraPos.y,
                    outline.pos().getZ() - cameraPos.z);
            context.submitNodeCollector().submitShapeOutline(pose, shape, RenderTypes.lines(), 0xFF000000, 2.0F, false);
            pose.popPose();
            return false;
        });
    }
}
