package platinpython.vfxgenerator.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lukebemish.codecextras.mutable.DataElement;
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
import platinpython.vfxgenerator.util.data.OwnedDataElement;
import platinpython.vfxgenerator.util.data.Range;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
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
        OwnedDataElement<Boolean> dataElement
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry.addToggleButton(this.width, displayTextFalse, displayTextTrue, dataElement)
        );
    }

    public void addSlider(
        Component prefix,
        Component suffix,
        float minValue,
        float maxValue,
        float stepSize,
        DataElement<Float> dataElement
    ) {
        this.addSlider(prefix, suffix, minValue, maxValue, stepSize, dataElement::set, dataElement::get);
    }

    public void addSlider(
        Component prefix,
        Component suffix,
        float minValue,
        float maxValue,
        float stepSize,
        Consumer<Float> setValue,
        Supplier<Float> getValue
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry
                .addSlider(this.width, prefix, suffix, minValue, maxValue, stepSize, setValue, getValue)
        );
    }

    public void addRangeSlider(
        Component prefix,
        Component suffix,
        float minValue,
        float maxValue,
        float stepSize,
        DataElement<Range<Float>> dataElement
    ) {
        this.addRangeSlider(prefix, suffix, minValue, maxValue, stepSize, dataElement::set, dataElement::get);
    }

    public void addRangeSlider(
        Component prefix,
        Component suffix,
        float minValue,
        float maxValue,
        float stepSize,
        Consumer<Range<Float>> setRange,
        Supplier<Range<Float>> getRange
    ) {
        this.addEntry(
            VFXGeneratorOptionsListEntry
                .addRangeSlider(this.width, prefix, suffix, minValue, maxValue, stepSize, setRange, getRange)
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

    public static class VFXGeneratorOptionsListEntry extends Entry<VFXGeneratorOptionsListEntry> {
        private final UpdateableWidget child;

        private VFXGeneratorOptionsListEntry(UpdateableWidget child) {
            this.child = child;
        }

        public static VFXGeneratorOptionsListEntry addButton(int guiWidth, Component displayText, Runnable onPress) {
            return new VFXGeneratorOptionsListEntry(new UpdateableWidget(guiWidth / 2 - 155, 0, 310, 20) {
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
                            ? ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                            : ResourceLocation.withDefaultNamespace("widget/button"),
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
            OwnedDataElement<Boolean> dataElement
        ) {
            return new VFXGeneratorOptionsListEntry(
                new ToggleTextButton(guiWidth / 2 - 155, 0, 310, 20, displayTextFalse, displayTextTrue, dataElement)
            );
        }

        public static VFXGeneratorOptionsListEntry addSlider(
            int guiWidth,
            Component prefix,
            Component suffix,
            float minValue,
            float maxValue,
            float stepSize,
            Consumer<Float> setValue,
            Supplier<Float> getValue
        ) {
            return new VFXGeneratorOptionsListEntry(
                new FloatSlider(
                    guiWidth / 2 - 155, 0, 310, 20, prefix, suffix, minValue, maxValue, stepSize, setValue, getValue
                )
            );
        }

        public static VFXGeneratorOptionsListEntry addRangeSlider(
            int guiWidth,
            Component prefix,
            Component suffix,
            float minValue,
            float maxValue,
            float stepSize,
            Consumer<Range<Float>> setRange,
            Supplier<Range<Float>> getRange
        ) {
            return new VFXGeneratorOptionsListEntry(
                new FloatRangeSlider(
                    guiWidth / 2 - 155, 0, 310, 20, prefix, suffix, minValue, maxValue, stepSize, setRange, getRange
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

        private final BooleanSupplier toggleSupplier;

        public ToggleableVFXGeneratorOptionsListEntry(
            UpdateableWidget firstChild,
            UpdateableWidget secondChild,
            BooleanSupplier toggleSupplier
        ) {
            super(firstChild);
            this.firstChild = firstChild;
            this.secondChild = secondChild;
            this.toggleSupplier = toggleSupplier;
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
            if (this.toggleSupplier.getAsBoolean()) {
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
        private float minValueFirst = 0F;
        private float maxValueFirst = 0F;
        private Consumer<Range<Float>> setRangeFirst = range -> {};
        private Supplier<Range<Float>> getRangeFirst = () -> new Range<>(0F, 0F);

        private Component prefixSecond = Component.empty();
        private Component suffixSecond = Component.empty();
        private float minValueSecond = 0F;
        private float maxValueSecond = 0F;
        private Consumer<Range<Float>> setRangeSecond = range -> {};
        private Supplier<Range<Float>> getRangeSecond = () -> new Range<>(0F, 0F);

        private BooleanSupplier toggleSupplier = () -> false;

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

        public ToggleableRangeSliderBuilder minValueFirst(float minValueFirst) {
            this.minValueFirst = minValueFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder maxValueFirst(float maxValueFirst) {
            this.maxValueFirst = maxValueFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder setRangeFirst(Consumer<Range<Float>> setRangeFirst) {
            this.setRangeFirst = setRangeFirst;
            return this;
        }

        public ToggleableRangeSliderBuilder getRangeFirst(Supplier<Range<Float>> getRangeFirst) {
            this.getRangeFirst = getRangeFirst;
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

        public ToggleableRangeSliderBuilder minValueSecond(float minValueSecond) {
            this.minValueSecond = minValueSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder maxValueSecond(float maxValueSecond) {
            this.maxValueSecond = maxValueSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder setRangeSecond(Consumer<Range<Float>> setRangeSecond) {
            this.setRangeSecond = setRangeSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder getRangeSecond(Supplier<Range<Float>> getRangeSecond) {
            this.getRangeSecond = getRangeSecond;
            return this;
        }

        public ToggleableRangeSliderBuilder toggleSupplier(BooleanSupplier toggleSupplier) {
            this.toggleSupplier = toggleSupplier;
            return this;
        }

        public void build() {
            FloatRangeSlider firstSlider = new FloatRangeSlider(
                this.guiWidth / 2 - 155, 0, 310, 20, this.prefixFirst, this.suffixFirst, this.minValueFirst,
                this.maxValueFirst, this.stepSize, this.setRangeFirst, this.getRangeFirst
            );
            FloatRangeSlider secondSlider = new FloatRangeSlider(
                this.guiWidth / 2 - 155, 0, 310, 20, this.prefixSecond, this.suffixSecond, this.minValueSecond,
                this.maxValueSecond, this.stepSize, this.setRangeSecond, this.getRangeSecond
            );
            this.list
                .addEntry(new ToggleableVFXGeneratorOptionsListEntry(firstSlider, secondSlider, this.toggleSupplier));
        }
    }
}
