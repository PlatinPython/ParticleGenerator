package platinpython.vfxgenerator.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import platinpython.vfxgenerator.util.data.OwnedDataElement;

@SuppressWarnings("UnstableApiUsage")
public class ToggleTextButton extends UpdateableWidget {
    private final Component displayTextFalse;
    private final Component displayTextTrue;

    private final OwnedDataElement<Boolean> dataElement;

    public ToggleTextButton(
        int x,
        int y,
        int width,
        int height,
        Component displayTextFalse,
        Component displayTextTrue,
        OwnedDataElement<Boolean> dataElement
    ) {
        super(x, y, width, height);
        this.displayTextFalse = displayTextFalse;
        this.displayTextTrue = displayTextTrue;
        this.dataElement = dataElement;
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
                ? ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                : ResourceLocation.withDefaultNamespace("widget/button"),
            this.getX(), this.getY(), this.width, this.height
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderScrollingString(guiGraphics, minecraft.font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.dataElement.set(!this.dataElement.get());
        this.updateMessage();
    }

    @Override
    public void updateValue() {
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.dataElement.get() ? displayTextTrue : displayTextFalse);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
