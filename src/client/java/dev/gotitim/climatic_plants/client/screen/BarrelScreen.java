package dev.gotitim.climatic_plants.client.screen;

import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlockEntity;
import dev.gotitim.climatic_plants.content.container.BarrelContainerMenu;
import dev.gotitim.climatic_plants.util.CalendarUtils;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BarrelScreen extends AbstractContainerScreen<BarrelContainerMenu> {
    static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("climatic_plants", "textures/gui/barrel.png");

    private static final int TANK_X = 7;
    private static final int TANK_Y = 19;
    private static final int TANK_WIDTH = 18;
    private static final int TANK_HEIGHT = 52;

    private static final int TANK_INNER_X = 8;
    private static final int TANK_INNER_BOTTOM = 70;
    private static final int TANK_INNER_WIDTH = 16;
    private static final int TANK_FILL_MAX = 50;

    private static final int SEAL_BUTTON_X = 123;
    private static final int SEAL_BUTTON_Y = 35;

    private static final int SEAL_DATE_Y = 75;

    public BarrelScreen(BarrelContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 178);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new BarrelSealButton(leftPos + SEAL_BUTTON_X, topPos + SEAL_BUTTON_Y, menu::isSealed, _ -> {
            assert minecraft.gameMode != null;
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

        FluidBarrelBlockEntity barrel = menu.getBarrel();
        if (!barrel.getFluidStorage().isResourceBlank()) {
            fillTank(graphics, barrel);
        }

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos + TANK_X,
                topPos + TANK_Y,
                176.0F,
                0.0F,
                TANK_WIDTH,
                TANK_HEIGHT,
                256,
                256);
    }

    private void fillTank(GuiGraphicsExtractor graphics, FluidBarrelBlockEntity barrel) {
        FluidVariant variant = barrel.getFluidStorage().getResource();
        FluidStateModelSet modelSet = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
        TextureAtlasSprite sprite = modelSet.get(variant.getFluid().defaultFluidState())
                .stillMaterial()
                .sprite();
        int color = FluidVariantRendering.getColor(
                variant, barrel.getLevel() instanceof ClientLevel level ? level : null, barrel.getBlockPos());

        int fillHeight = (int) Math.ceil(TANK_FILL_MAX * barrel.getFluidFillPercent());
        int x = leftPos + TANK_INNER_X;
        int top = topPos + TANK_INNER_BOTTOM - fillHeight;
        int bottom = topPos + TANK_INNER_BOTTOM;

        graphics.enableScissor(x, top, x + TANK_INNER_WIDTH, bottom);
        for (int y = top; y < bottom; y += 16) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16, color);
        }
        graphics.disableScissor();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (!menu.isSealed()) return;

        for (int i = FluidBarrelBlockEntity.SLOT_FLUID_CONTAINER_IN; i <= FluidBarrelBlockEntity.SLOT_MAIN; i++) {
            Slot slot = menu.slots.get(i);
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, -2130706433);
        }

        FluidBarrelBlockEntity barrel = menu.getBarrel();

        assert barrel.getLevel() != null;
        graphics.centeredText(
                font,
                CalendarUtils.sealDate(barrel.getRecipeTick(), barrel.getLevel()),
                imageWidth / 2,
                SEAL_DATE_Y,
                0xFF777777);
        Component recipeOutput = barrel.getRecipeOutput();
        if (recipeOutput != null) {
            graphics.centeredText(
                    font,
                    Component.translatable("tooltip.climatic_plants.making_recipe", recipeOutput),
                    imageWidth / 2,
                    SEAL_DATE_Y - font.lineHeight - 1,
                    0xFF777777);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int relX = mouseX - leftPos;
        int relY = mouseY - topPos;
        if (relX < TANK_X || relY < TANK_Y || relX >= TANK_X + TANK_WIDTH || relY >= TANK_Y + TANK_HEIGHT) return;

        FluidBarrelBlockEntity barrel = menu.getBarrel();
        if (barrel.getFluidStorage().isResourceBlank()) return;

        FluidVariant variant = barrel.getFluidStorage().getResource();
        Component fluidName = FluidVariantRendering.getTooltip(variant).getFirst();
        graphics.setTooltipForNextFrame(
                font,
                Component.translatable(
                        "tooltip.climatic_plants.fluid_units_of",
                        barrel.getFluidStorage().getAmount(),
                        1000 * barrel.getFluidStorage().getAmount() / FluidConstants.BUCKET,
                        fluidName),
                mouseX,
                mouseY);
    }
}
