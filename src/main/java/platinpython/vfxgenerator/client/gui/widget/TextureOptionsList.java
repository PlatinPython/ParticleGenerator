package platinpython.vfxgenerator.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;
import platinpython.vfxgenerator.util.data.OwnedDataElement;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.List;

public class TextureOptionsList extends ContainerObjectSelectionList<TextureOptionsList.TextureOptionsListEntry> {
    public TextureOptionsList(
        Minecraft minecraft,
        int width,
        int height,
        int top,
        int itemHeight,
        OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement
    ) {
        super(minecraft, width, height, top, itemHeight);
        this.init(dataElement);
    }

    private void init(OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement) {
        List<ResourceLocation> list =
            DataManager.selectableParticles().keySet().stream().sorted(ResourceLocation::compareNamespaced).toList();
        for (int i = 0; i < list.size() - list.size() % 3; i += 3) {
            addEntry(
                TextureOptionsListEntry
                    .addThreeTextures(this.width, list.get(i), list.get(i + 1), list.get(i + 2), dataElement)
            );
        }
        switch (list.size() % 3) {
            case 1 -> addEntry(TextureOptionsListEntry.addOneTexture(this.width, list.getLast(), dataElement));
            case 2 -> addEntry(
                TextureOptionsListEntry
                    .addTwoTextures(this.width, list.get(list.size() - 2), list.getLast(), dataElement)
            );
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 76;
    }

    public static class TextureOptionsListEntry extends ContainerObjectSelectionList.Entry<TextureOptionsListEntry> {
        private final ImageSelectionWidget child1;
        @Nullable
        private final ImageSelectionWidget child2;
        @Nullable
        private final ImageSelectionWidget child3;

        private TextureOptionsListEntry(
            ImageSelectionWidget child1,
            @Nullable ImageSelectionWidget child2,
            @Nullable ImageSelectionWidget child3
        ) {
            this.child1 = child1;
            this.child2 = child2;
            this.child3 = child3;
        }

        public static TextureOptionsListEntry addOneTexture(
            int guiWidth,
            ResourceLocation particleId1,
            OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement
        ) {
            ImageSelectionWidget child1 =
                new ImageSelectionWidget(guiWidth / 2 - 25, 0, 50, 50, particleId1, dataElement);
            return new TextureOptionsListEntry(child1, null, null);
        }

        public static TextureOptionsListEntry addTwoTextures(
            int guiWidth,
            ResourceLocation particleId1,
            ResourceLocation particleId2,
            OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement
        ) {
            ImageSelectionWidget child1 =
                new ImageSelectionWidget(guiWidth / 2 - 50, 0, 50, 50, particleId1, dataElement);
            ImageSelectionWidget child2 = new ImageSelectionWidget(guiWidth / 2, 0, 50, 50, particleId2, dataElement);
            return new TextureOptionsListEntry(child1, child2, null);
        }

        public static TextureOptionsListEntry addThreeTextures(
            int guiWidth,
            ResourceLocation particleId1,
            ResourceLocation particleId2,
            ResourceLocation particleId3,
            OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement
        ) {
            ImageSelectionWidget child1 =
                new ImageSelectionWidget(guiWidth / 2 - 75, 0, 50, 50, particleId1, dataElement);
            ImageSelectionWidget child2 =
                new ImageSelectionWidget(guiWidth / 2 - 25, 0, 50, 50, particleId2, dataElement);
            ImageSelectionWidget child3 =
                new ImageSelectionWidget(guiWidth / 2 + 25, 0, 50, 50, particleId3, dataElement);
            return new TextureOptionsListEntry(child1, child2, child3);
        }

        public void updateValue() {
            this.children().forEach(ImageSelectionWidget::updateValue);
        }

        @Override
        public List<ImageSelectionWidget> children() {
            if (child2 == null) {
                return ImmutableList.of(child1);
            } else if (child3 == null) {
                return ImmutableList.of(child1, child2);
            } else {
                return ImmutableList.of(child1, child2, child3);
            }
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
            this.child1.setY(top);
            this.child1.render(guiGraphics, mouseX, mouseY, partialTicks);
            if (child2 != null) {
                this.child2.setY(top);
                this.child2.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
            if (child3 != null) {
                this.child3.setY(top);
                this.child3.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return children();
        }
    }
}
