package platinpython.vfxgenerator.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import platinpython.vfxgenerator.util.Util;

public class ToggleTextButton extends UpdateableWidget {
    private final Component displayTextFalse;
    private final Component displayTextTrue;

    private final Util.BooleanConsumer setValueFunction;
    private final Util.BooleanSupplier valueSupplier;

    public ToggleTextButton(
        int x,
        int y,
        int width,
        int height,
        Component displayTextFalse,
        Component displayTextTrue,
        Util.BooleanConsumer setValueFunction,
        Util.BooleanSupplier valueSupplier,
        Runnable applyValueFunction
    ) {
        super(x, y, width, height, applyValueFunction);
        this.displayTextFalse = displayTextFalse;
        this.displayTextTrue = displayTextTrue;
        this.setValueFunction = setValueFunction;
        this.valueSupplier = valueSupplier;
        this.updateMessage();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(
            this.active && this.isHoveredOrFocused()
                ? new ResourceLocation("widget/button_highlighted")
                : new ResourceLocation("widget/button"),
            this.getX(), this.getY(), this.width, this.height
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderScrollingString(guiGraphics, minecraft.font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFunction.accept(!this.valueSupplier.get());
        this.updateMessage();
        this.applyValue();
    }

    @Override
    public void updateValue() {
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(valueSupplier.get() ? displayTextTrue : displayTextFalse);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
