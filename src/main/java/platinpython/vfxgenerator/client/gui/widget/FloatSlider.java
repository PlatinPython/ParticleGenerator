package platinpython.vfxgenerator.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import platinpython.vfxgenerator.util.Util;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatSlider extends UpdateableWidget {
    private static final ResourceLocation SLIDER_SPRITE = new ResourceLocation("widget/slider");
    private static final ResourceLocation HIGHLIGHTED_SPRITE = new ResourceLocation("widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE_SPRITE = new ResourceLocation("widget/slider_handle");
    private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE =
        new ResourceLocation("widget/slider_handle_highlighted");

    private final float minValue;
    private final float maxValue;
    private final float stepSize;
    private final DecimalFormat format;
    private final Component prefix;
    private final Component suffix;
    private final Consumer<Float> setValue;
    private final Supplier<Float> getValue;
    private double sliderValue;

    public FloatSlider(
        int x,
        int y,
        int width,
        int height,
        Component prefix,
        Component suffix,
        float minValue,
        float maxValue,
        float stepSize,
        Consumer<Float> setValue,
        Supplier<Float> getValue
    ) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.format = Float.toString(this.stepSize).split("\\.")[1].length() == 1
            && Float.toString(this.stepSize).split("\\.")[1].equals("0")
                ? new DecimalFormat("0")
                : new DecimalFormat(Float.toString(this.stepSize).replaceAll("\\d", "0"));
        this.prefix = prefix;
        this.suffix = suffix;
        this.setValue = setValue;
        this.getValue = getValue;
        this.setupSliderValues(this.getValue.get());
    }

    private void setupSliderValues(double value) {
        this.sliderValue = Util.clamp(value, this.minValue, this.maxValue, this.stepSize);
        this.setValue.accept(this.getSliderValue());
        this.updateMessage();
    }

    protected ResourceLocation getSprite() {
        return this.isFocused() && this.active ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    protected ResourceLocation getHandleSprite() {
        return this.isHovered && this.active ? SLIDER_HANDLE_HIGHLIGHTED_SPRITE : SLIDER_HANDLE_SPRITE;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(getSprite(), this.getX(), this.getY(), this.width, this.height);
        guiGraphics.blitSprite(
            getHandleSprite(), this.getX() + (int) (this.sliderValue * (double) (this.width - 8)), this.getY(), 8,
            this.getHeight()
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void updateValue() {
        if (this.getValue.get() != this.getSliderValue()) {
            this.sliderValue = Util.clamp(this.getValue.get(), this.minValue, this.maxValue, this.stepSize);
        }
        this.updateMessage();
    }

    private void setValueFromMouse(double mouseX) {
        this.setSliderValue((mouseX - (double) (this.getX() + 4)) / (double) (this.width - 8));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == GLFW.GLFW_KEY_LEFT;
        if (flag || keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (flag) {
                if (this.sliderValue != 0D) {
                    this.setSliderValue(
                        Util.clamp((this.getSliderValue() - this.stepSize), this.minValue, this.maxValue, this.stepSize)
                    );
                }
            } else {
                if (this.sliderValue != 1D) {
                    this.setSliderValue(
                        Util.clamp((this.getSliderValue() + this.stepSize), this.minValue, this.maxValue, this.stepSize)
                    );
                }
            }
        }
        return false;
    }

    private float getSliderValue() {
        return (float) (this.sliderValue * (this.maxValue - this.minValue) + this.minValue);
    }

    private void setSliderValue(double value) {
        double d0 = this.sliderValue;
        this.sliderValue = Util.toValue(value, this.minValue, this.maxValue, this.stepSize);
        if (d0 != this.sliderValue) {
            this.setValue.accept(this.getSliderValue());
        }

        this.updateMessage();
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    protected void updateMessage() {
        setMessage(
            Component.empty()
                .append(prefix)
                .append(": ")
                .append(format.format(getSliderValue()))
                .append(suffix.getString().isEmpty() ? "" : " ")
                .append(suffix)
        );
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput
                    .add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                narrationElementOutput
                    .add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }
    }
}
