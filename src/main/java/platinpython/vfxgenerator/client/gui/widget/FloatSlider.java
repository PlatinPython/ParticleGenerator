package platinpython.vfxgenerator.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;
import platinpython.vfxgenerator.util.Util;

import java.text.DecimalFormat;

public class FloatSlider extends UpdateableWidget {
    private final double minValue, maxValue;
    private final float stepSize;
    private final DecimalFormat format;
    private final Component prefix;
    private final Component suffix;
    private final Util.FloatConsumer setValueFunction;
    private final Util.FloatSupplier valueSupplier;
    private double sliderValue;

    public FloatSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue,
                       double maxValue, float stepSize, Util.FloatConsumer setValueFunction,
                       Util.FloatSupplier valueSupplier, Runnable applyValueFunction) {
        super(x, y, width, height, applyValueFunction);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.format = Float.toString(this.stepSize).split("\\.")[1].length() == 1 && Float.toString(this.stepSize)
                                                                                          .split("\\.")[1].equals("0") ?
                      new DecimalFormat("0") :
                      new DecimalFormat(Float.toString(this.stepSize).replaceAll("\\d", "0"));
        this.prefix = prefix;
        this.suffix = suffix;
        this.setValueFunction = setValueFunction;
        this.valueSupplier = valueSupplier;
        this.setupSliderValues(this.valueSupplier.get());
    }

    private void setupSliderValues(double value) {
        this.sliderValue = Util.clamp(value, this.minValue, this.maxValue, this.stepSize);
        this.setValueFunction.accept((float) this.getSliderValue());
        this.applyValue();
        this.updateMessage();
    }

    @Override
    protected int getYImage(boolean isHovered) {
        return 0;
    }

    private int getYImageNoDisabled(boolean isHovered) {
        if (!this.active) {
            return 1;
        }
        return isHovered ? 2 : 1;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(matrixStack, this.getX() + (int) (this.sliderValue * (double) (this.width - 8)), this.getY(), 0,
                  46 + this.getYImageNoDisabled(isHovered) * 20, 4, this.height
        );
        this.blit(matrixStack, this.getX() + (int) (this.sliderValue * (double) (this.width - 8)) + 4, this.getY(), 196,
                  46 + this.getYImageNoDisabled(isHovered) * 20, 4, this.height
        );
    }

    @Override
    public void updateValue() {
        if (this.valueSupplier.get() != this.getSliderValue()) {
            this.sliderValue = Util.clamp(this.valueSupplier.get(), this.minValue, this.maxValue, this.stepSize);
        }
        this.updateMessage();
    }

    private void setValueFromMouse(double mouseX) {
        this.setSliderValue((mouseX - (double) (this.getX() + 4)) / (double) (this.width - 8));
    }

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
                            Util.clamp((this.getSliderValue() - this.stepSize), this.minValue, this.maxValue,
                                       this.stepSize
                            ));
                }

            } else {
                if (this.sliderValue != 1D) {
                    this.setSliderValue(
                            Util.clamp((this.getSliderValue() + this.stepSize), this.minValue, this.maxValue,
                                       this.stepSize
                            ));
                }

            }
        }
        return false;
    }

    private double getSliderValue() {
        return this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
    }

    private void setSliderValue(double value) {
        double d0 = this.sliderValue;
        this.sliderValue = Util.toValue(value, this.minValue, this.maxValue, this.stepSize);
        if (d0 != this.sliderValue) {
            this.setValueFunction.accept((float) this.getSliderValue());
            this.applyValue();
        }

        this.updateMessage();
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.empty()
                            .append(prefix)
                            .append(": ")
                            .append(format.format(getSliderValue()))
                            .append(suffix.getString().isEmpty() ? "" : " ")
                            .append(suffix));
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
                narrationElementOutput.add(NarratedElementType.USAGE,
                                           Component.translatable("narration.slider.usage.focused")
                );
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE,
                                           Component.translatable("narration.slider.usage.hovered")
                );
            }
        }
    }
}
