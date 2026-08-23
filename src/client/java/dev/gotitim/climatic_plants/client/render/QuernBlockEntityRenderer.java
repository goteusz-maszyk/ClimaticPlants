package dev.gotitim.climatic_plants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.gotitim.climatic_plants.content.quern.QuernBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class QuernBlockEntityRenderer implements BlockEntityRenderer<QuernBlockEntity, QuernBlockEntityRenderer.QuernRenderState> {
    private final ItemModelResolver itemModelResolver;

    public QuernBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public QuernRenderState createRenderState() {
        return new QuernRenderState();
    }

    @Override
    public void extractRenderState(QuernBlockEntity blockEntity, QuernRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.handstone = resolveItem(blockEntity, QuernBlockEntity.SLOT_HANDSTONE, 0);
        state.inputItem = resolveItem(blockEntity, QuernBlockEntity.SLOT_INPUT, 1);
        state.outputItem = resolveItem(blockEntity, QuernBlockEntity.SLOT_OUTPUT, 2);
        state.outputItemCount = blockEntity.getItem(QuernBlockEntity.SLOT_OUTPUT).getCount();
        state.recipeTimer = blockEntity.recipeTimer / QuernBlockEntity.MANUAL_TICKS;
    }

    private ItemStackRenderState resolveItem(QuernBlockEntity quern, int slot, int seed) {
        ItemStackRenderState renderState = new ItemStackRenderState();
        if (!quern.getItem(slot).isEmpty()) {
            itemModelResolver.updateForTopItem(renderState, quern.getItem(slot), ItemDisplayContext.FIXED, quern.getLevel(), null, seed);
        }
        return renderState;
    }

    @Override
    public void submit(QuernRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.handstone.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.705, 0.5);
            float rotation = state.recipeTimer * 360;
            poseStack.mulPose(Axis.YN.rotationDegrees(rotation));
            poseStack.translate(-0.5, -0.705, -0.5);
            poseStack.translate(0.5, 0.6875, 0.5);
            poseStack.scale(1.25f, 1.25f, 1.25f);
            state.handstone.submit(poseStack, submitNodeCollector, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (!state.inputItem.isEmpty()) {
            float height = state.handstone.isEmpty() ? 0.75f : 0.875f;
            poseStack.pushPose();
            poseStack.translate(0.5, height, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(45f));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            state.inputItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (!state.outputItem.isEmpty()) {
            for (int i = 0; i < state.outputItemCount; i++) {
                double yPos = 0.625D;
                poseStack.pushPose();
                switch (i / 16) {
                    case 0 -> poseStack.translate(0.125D, yPos, 0.125D + (0.046875D * i));
                    case 1 -> poseStack.translate(0.125D + (0.046875D * (i - 16)), yPos, 0.875D);
                    case 2 -> poseStack.translate(0.875D, yPos, 0.875D - (0.046875D * (i - 32)));
                    case 3 -> poseStack.translate(0.875D - (0.046875D * (i - 48)), yPos, 0.125D);
                    default -> {}
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(90F * (float) (i/16)));
                poseStack.mulPose(Axis.XP.rotationDegrees(75F));
                poseStack.scale(0.125F, 0.125F, 0.125F);
                state.outputItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }

    public static class QuernRenderState extends BlockEntityRenderState {
        public ItemStackRenderState handstone = new ItemStackRenderState();
        public ItemStackRenderState inputItem = new ItemStackRenderState();
        public ItemStackRenderState outputItem = new ItemStackRenderState();
        public float recipeTimer;
        public int outputItemCount;
    }
}
