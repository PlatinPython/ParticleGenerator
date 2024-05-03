package platinpython.vfxgenerator.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import platinpython.vfxgenerator.util.Util;

import java.util.List;

public class VFXGeneratorOptionsList
    extends ContainerObjectSelectionList<VFXGeneratorOptionsList.VFXGeneratorOptionsListEntry> {
    public VFXGeneratorOptionsList(Minecraft minecraft, int width, int height, int top, int itemHeight) {
        super(minecraft, width, height, top, itemHeight);
    }

    public void addButton(Component displayText, Runnable onPress) {
        this.addEntry(VFXGeneratorOptionsListEntry.addButton(this.width, displayText, onPress));
    }

    public void addToggleButton(
        Component displayTextFalse,
        Component displayTextTrue,
        Util.BooleanConsumer setValueFunction,
        Util.BooleanSupplier valueSupplier,
        Runnable applyValueFunction
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry.addToggleButton(
                this.width, displayTextFalse, displayTextTrue, setValueFunction, valueSupplier, applyValueFunction
            )
        );
    }

    public void addSlider(
        Component prefix,
        Component suffix,
        double minValue,
        double maxValue,
        float stepSize,
        Util.FloatConsumer setValueFunction,
        Util.FloatSupplier valueSupplier,
        Runnable applyValueFunction
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry.addSlider(
                this.width, prefix, suffix, minValue, maxValue, stepSize, setValueFunction, valueSupplier,
                applyValueFunction
            )
        );
    }

    public void addRangeSlider(
        Component prefix,
        Component suffix,
        double minValue,
        double maxValue,
        float stepSize,
        Util.FloatConsumer setLeftValueFunction,
        Util.FloatConsumer setRightValueFunction,
        Util.FloatSupplier leftValueSupplier,
        Util.FloatSupplier rightValueSupplier,
        Runnable applyValueFunction
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry.addRangeSlider(
                this.width, prefix, suffix, minValue, maxValue, stepSize, setLeftValueFunction, setRightValueFunction,
                leftValueSupplier, rightValueSupplier, applyValueFunction
            )
        );
    }

    public ToggleableRangeSliderBuilder getToggleableRangeSliderBuilder() {
        return new ToggleableRangeSliderBuilder(this, this.width);
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 156;
    }

    public static class VFXGeneratorOptionsListEntry
        extends ContainerObjectSelectionList.Entry<VFXGeneratorOptionsListEntry> {
        private final UpdateableWidget child;

        private VFXGeneratorOptionsListEntry(UpdateableWidget child) {
            this.child = child;
        }

        public static VFXGeneratorOptionsListEntry addButton(int guiWidth, Component displayText, Runnable onPress) {
            return new VFXGeneratorOptionsListEntry(new UpdateableWidget(guiWidth / 2 - 155, 0, 310, 20, () -> {}) {

                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    this.setMessage(displayText);
                    Minecraft minecraft = Minecraft.getInstance();
                    Font font = minecraft.font;
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableDepthTest();
                    guiGraphics.blitSprite(
                        this.active && this.isHoveredOrFocused()
                            ? new ResourceLocation("widget/button_highlighted")
                            : new ResourceLocation("widget/button"),
                        this.getX(), this.getY(), this.width, this.height
                    );
                    guiGraphics.drawCenteredString(
                        font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2,
                        getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24
                    );
                }

                @SuppressWarnings("deprecation")
                @Override
                public void onClick(double mouseX, double mouseY) {
                    onPress.run();
                }

                @Override
                public void updateValue() {}

                @Override
                protected void updateMessage() {}

                @Override
                public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
            });
        }

        public static VFXGeneratorOptionsListEntry addToggleButton(
            int guiWidth,
            Component displayTextFalse,
            Component displayTextTrue,
            Util.BooleanConsumer setValueFunction,
            Util.BooleanSupplier valueSupplier,
            Runnable applyValueFunction
        ) {
            return new VFXGeneratorOptionsListEntry(
                new ToggleTextButton(
                    guiWidth / 2 - 155, 0, 310, 20, displayTextFalse, displayTextTrue, setValueFunction, valueSupplier,
                    applyValueFunction
                )
            );
        }

        public static VFXGeneratorOptionsListEntry addSlider(
            int guiWidth,
            Component prefix,
            Component suffix,
            double minValue,
            double maxValue,
            float stepSize,
            Util.FloatConsumer setValueFunction,
            Util.FloatSupplier valueSupplier,
            Runnable applyValueFunction
        ) {
            return new VFXGeneratorOptionsListEntry(
                new FloatSlider(
                    guiWidth / 2 - 155, 0, 310, 20, prefix, suffix, minValue, maxValue, stepSize, setValueFunction,
                    valueSupplier, applyValueFunction
                )
            );
        }

        public static VFXGeneratorOptionsListEntry addRangeSlider(
            int guiWidth,
            Component prefix,
            Component suffix,
            double minValue,
            double maxValue,
            float stepSize,
            Util.FloatConsumer setLeftValueFunction,
            Util.FloatConsumer setRightValueFunction,
            Util.FloatSupplier leftValueSupplier,
            Util.FloatSupplier rightValueSupplier,
            Runnable applyValueFunction
        ) {
            return new VFXGeneratorOptionsListEntry(
                new FloatRangeSlider(
                    guiWidth / 2 - 155, 0, 310, 20, prefix, suffix, minValue, maxValue, stepSize, setLeftValueFunction,
                    setRightValueFunction, leftValueSupplier, rightValueSupplier, applyValueFunction
                )
            );
        }

        @Override
        public void render(
            GuiGraphics guiGraphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean isMouseOver,
            float partialTicks
        ) {
            this.child.setY(top);
            this.child.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        public void setActive(boolean active) {
            this.child.active = active;
            if (!active) {
                this.child.setFocused(false);
            }
        }

        public void updateValue() {
            this.child.updateValue();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.child);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.child);
        }
    }

    public class ToggleableVFXGeneratorOptionsListEntry extends VFXGeneratorOptionsListEntry {
        private final UpdateableWidget firstChild;
        private final UpdateableWidget secondChild;

        private final Util.BooleanSupplier toggleValueSupplier;

        public ToggleableVFXGeneratorOptionsListEntry(
            UpdateableWidget firstChild,
            UpdateableWidget secondChild,
            Util.BooleanSupplier toggleValueSupplier
        ) {
            super(firstChild);
            this.firstChild = firstChild;
            this.secondChild = secondChild;
            this.toggleValueSupplier = toggleValueSupplier;
            this.updateValue();
        }

        @Override
        public void render(
            GuiGraphics guiGraphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean isMouseOver,
            float partialTicks
        ) {
            this.firstChild.setY(top);
            this.secondChild.setY(top);
            this.firstChild.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.secondChild.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public void setActive(boolean active) {
            this.firstChild.active = active;
            this.secondChild.active = active;
            if (!active) {
                this.firstChild.setFocused(false);
                this.secondChild.setFocused(false);
            }
        }

        @Override
        public void updateValue() {
            this.firstChild.updateValue();
            this.secondChild.updateValue();
            if (toggleValueSupplier.get()) {
                this.firstChild.visible = false;
                this.secondChild.visible = true;
            } else {
                this.firstChild.visible = true;
                this.secondChild.visible = false;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.firstChild, this.secondChild);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.firstChild, this.secondChild);
        }
    }

    public final class ToggleableRangeSliderBuilder {
        private final VFXGeneratorOptionsList list;
        private final int guiWidth;

        private float stepSize = 1F;

        private Component prefixFirst = Component.empty();
        private Component suffixFirst = Component.empty();
        private double minValueFirst = 0F;
        private double maxValueFirst = 0F;
        private Util.FloatConsumer setLeftValueFunctionFirst = (value) -> {};
        private Util.FloatConsumer setRightValueFunctionFirst = (value) -> {};
        private Util.FloatSupplier leftValueSupplierFirst = () -> 0F;
        private Util.FloatSupplier rightValueSupplierFirst = () -> 0F;

        private Component prefixSecond = Component.empty();
        private Component suffixSecond = Component.empty();
        private double minValueSecond = 0F;
        private double maxValueSecond = 0F;
        private Util.FloatConsumer setLeftValueFunctionSecond = (value) -> {};
        private Util.FloatConsumer setRightValueFunctionSecond = (value) -> {};
        private Util.FloatSupplier leftValueSupplierSecond = () -> 0F;
        private Util.FloatSupplier rightValueSupplierSecond = () -> 0F;

        private Runnable applyValueFunction = () -> {};
        private Util.BooleanSupplier toggleValueSupplier = () -> false;

        private ToggleableRangeSliderBuilder(VFXGeneratorOptionsList list, int guiWidth) {
            this.list = list;
            this.guiWidth = guiWidth;
        }

        public ToggleableRangeSliderBuilder stepSize(float stepSize) {
            this.stepSize = stepSize;
            return this;
        }

        public ToggleableRangeSliderBuilder prefixFirst(Component prefixFirst) {
            this.prefixFirst = prefixFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder suffixFirst(Component suffixFirst) {
            this.suffixFirst = suffixFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder minValueFirst(double minValueFirst) {
            this.minValueFirst = minValueFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder maxValueFirst(double maxValueFirst) {
            this.maxValueFirst = maxValueFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder setLeftValueFunctionFirst(Util.FloatConsumer setLeftValueFunctionFirst) {
            this.setLeftValueFunctionFirst = setLeftValueFunctionFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder setRightValueFunctionFirst(Util.FloatConsumer setRightValueFunctionFirst) {
            this.setRightValueFunctionFirst = setRightValueFunctionFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder leftValueSupplierFirst(Util.FloatSupplier leftValueSupplierFirst) {
            this.leftValueSupplierFirst = leftValueSupplierFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder rightValueSupplierFirst(Util.FloatSupplier rightValueSupplierFirst) {
            this.rightValueSupplierFirst = rightValueSupplierFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder prefixSecond(Component prefixSecond) {
            this.prefixSecond = prefixSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder suffixSecond(Component suffixSecond) {
            this.suffixSecond = suffixSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder minValueSecond(double minValueSecond) {
            this.minValueSecond = minValueSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder maxValueSecond(double maxValueSecond) {
            this.maxValueSecond = maxValueSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder setLeftValueFunctionSecond(Util.FloatConsumer setLeftValueFunctionSecond) {
            this.setLeftValueFunctionSecond = setLeftValueFunctionSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder setRightValueFunctionSecond(
            Util.FloatConsumer setRightValueFunctionSecond
        ) {
            this.setRightValueFunctionSecond = setRightValueFunctionSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder leftValueSupplierSecond(Util.FloatSupplier leftValueSupplierSecond) {
            this.leftValueSupplierSecond = leftValueSupplierSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder rightValueSupplierSecond(Util.FloatSupplier rightValueSupplierSecond) {
            this.rightValueSupplierSecond = rightValueSupplierSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder applyValueFunction(Runnable applyValueFunction) {
            this.applyValueFunction = applyValueFunction;
            return this;
        }

        public ToggleableRangeSliderBuilder toggleValueSupplier(Util.BooleanSupplier toggleValueSupplier) {
            this.toggleValueSupplier = toggleValueSupplier;
            return this;
        }

        public void build() {
            FloatRangeSlider firstSlider = new FloatRangeSlider(
                guiWidth / 2 - 155, 0, 310, 20, prefixFirst, suffixFirst, minValueFirst, maxValueFirst, stepSize,
                setLeftValueFunctionFirst, setRightValueFunctionFirst, leftValueSupplierFirst, rightValueSupplierFirst,
                applyValueFunction
            );
            FloatRangeSlider secondSlider = new FloatRangeSlider(
                guiWidth / 2 - 155, 0, 310, 20, prefixSecond, suffixSecond, minValueSecond, maxValueSecond, stepSize,
                setLeftValueFunctionSecond, setRightValueFunctionSecond, leftValueSupplierSecond,
                rightValueSupplierSecond, applyValueFunction
            );
            this.list
                .addEntry(new ToggleableVFXGeneratorOptionsListEntry(firstSlider, secondSlider, toggleValueSupplier));
        }
    }
}
