package dev.gotitim.climatic_plants.client.screen;

import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class BarrelSealButton extends Button {
    private static final Component SEAL = Component.translatable("tooltip.climatic_plants.seal_barrel");
    private static final Component UNSEAL = Component.translatable("tooltip.climatic_plants.unseal_barrel");
    private final BooleanSupplier sealed;

    public BarrelSealButton(int x, int y, BooleanSupplier sealed, Button.OnPress onPress) {
        super(x, y, 20, 20, sealed.getAsBoolean() ? UNSEAL : SEAL, onPress, DEFAULT_NARRATION);
        this.sealed = sealed;
    }

    @Override
    protected void extractTooltipForNextRenderPass(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        setTooltip(Tooltip.create(sealed.getAsBoolean() ? UNSEAL : SEAL));
        super.extractTooltipForNextRenderPass(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BarrelScreen.TEXTURE,
                getX(),
                getY(),
                236.0F,
                sealed.getAsBoolean() ? 0.0F : 20.0F,
                20,
                20,
                256,
                256);
    }
}
