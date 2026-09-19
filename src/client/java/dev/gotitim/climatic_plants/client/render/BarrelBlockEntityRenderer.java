package dev.gotitim.climatic_plants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarrelBlockEntityRenderer implements BlockEntityRenderer<FluidBarrelBlockEntity, BarrelBlockEntityRenderer.BarrelRenderState> {
    private static final float MIN_XZ = 0.1875F;
    private static final float MAX_XZ = 0.8125F;
    private static final float BOTTOM_Y = 0.140625F;
    private static final float TOP_Y = 0.890625F;
    private static final float FLUID_RANGE = TOP_Y - BOTTOM_Y;

    private final ItemModelResolver itemModelResolver;

    public BarrelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public BarrelRenderState createRenderState() {
        return new BarrelRenderState();
    }

    @Override
    public void extractRenderState(FluidBarrelBlockEntity blockEntity, BarrelRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.shouldRender = !blockEntity.getBlockState().getValue(FluidBarrelBlock.SEALED) && blockEntity.getBlockState().getValue(FluidBarrelBlock.FACING) == Direction.UP;
        state.fluidFill = blockEntity.getFluidFillPercent();
        state.fluidVariant = blockEntity.getFluidStorage().getResource();
        state.items = resolveItem(blockEntity);
        state.level = (ClientLevel) blockEntity.getLevel();
    }

    private List<ItemStackRenderState> resolveItem(FluidBarrelBlockEntity barrel) {
        ArrayList<ItemStackRenderState> items = new ArrayList<>();
        for (ItemStack item : barrel.getItems()) {
            if (item.isEmpty()) continue;
            ItemStackRenderState renderState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(renderState, item, ItemDisplayContext.FIXED, barrel.getLevel(), null, 0);
            items.add(renderState);
        }
        return items;
    }

    @Override
    public void submit(BarrelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.shouldRender) return;

        assert state.fluidVariant != null;
        if (!state.fluidVariant.isBlank() && state.fluidFill > 0) {
            float shrink = state.fluidFill > 0.03f ? 0f : (0.03f - state.fluidFill) * 7f;
            final float minX = MIN_XZ + shrink;
            final float minZ = MIN_XZ + shrink;
            final float maxX = MAX_XZ - shrink;
            final float maxZ = MAX_XZ - shrink;
            final float y = BOTTOM_Y + FLUID_RANGE * state.fluidFill;

            final int lightCoords = state.lightCoords;
            final int color = FluidVariantRendering.getColor(state.fluidVariant, state.level, state.blockPos);

            FluidStateModelSet modelSet = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
            FluidModel model = modelSet.get(state.fluidVariant.getFluid().defaultFluidState());
            final TextureAtlasSprite sprite = model.stillMaterial().sprite();

            RenderSetup setup = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                                           .withTexture("Sampler0", sprite.atlasLocation())
                                           .useLightmap()
                                           .useOverlay()
                                           .createRenderSetup();
            RenderType renderType = RenderType.create("barrel_fluid", setup);

            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                buffer.addVertex(pose, minX, y, minZ).setColor(color).setUv(sprite.getU(minX), sprite.getV(
                        minZ)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0, 1, 0);
                buffer.addVertex(pose, minX, y, maxZ).setColor(color).setUv(sprite.getU(minX), sprite.getV(
                        maxZ)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0, 1, 0);
                buffer.addVertex(pose, maxX, y, maxZ).setColor(color).setUv(sprite.getU(maxX), sprite.getV(
                        maxZ)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0, 1, 0);
                buffer.addVertex(pose, maxX, y, minZ).setColor(color).setUv(sprite.getU(maxX), sprite.getV(
                        minZ)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0, 1, 0);
            });
        }

        for (ItemStackRenderState item : state.items) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.15625F, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class BarrelRenderState extends BlockEntityRenderState {
        public boolean shouldRender;
        public float fluidFill;
        public List<ItemStackRenderState> items = new ArrayList<>();
        public @Nullable FluidVariant fluidVariant;
        public @Nullable BlockAndTintGetter level;
    }
}
