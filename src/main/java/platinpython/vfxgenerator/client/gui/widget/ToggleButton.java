package platinpython.vfxgenerator.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import platinpython.vfxgenerator.util.data.OwnedDataElement;

@SuppressWarnings("UnstableApiUsage")
public class ToggleButton extends UpdateableWidget {
    private final OwnedDataElement<Boolean> dataElement;

    public ToggleButton(int x, int y, int width, int height, OwnedDataElement<Boolean> dataElement) {
        super(x, y, width, height);
        this.dataElement = dataElement;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);
        if (this.dataElement.get()) {
            guiGraphics.fill(
                this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
                0xFF00FF00
            );
            guiGraphics.blitSprite(
                this.isHoveredOrFocused()
                    ? new ResourceLocation("widget/button_highlighted")
                    : new ResourceLocation("widget/button"),
                this.getX() + this.width / 2, this.getY(), this.width / 2, this.height
            );
        } else {
            guiGraphics.fill(
                this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
                0xFFFF0000
            );
            guiGraphics.blitSprite(
                this.isHoveredOrFocused()
                    ? new ResourceLocation("widget/button_highlighted")
                    : new ResourceLocation("widget/button"),
                this.getX(), this.getY(), this.width / 2, this.height
            );
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.dataElement.set(!this.dataElement.get());
    }

    @Override
    public void updateValue() {}

    @Override
    protected void updateMessage() {}

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
