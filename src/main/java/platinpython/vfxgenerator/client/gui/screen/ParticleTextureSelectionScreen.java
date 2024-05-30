package platinpython.vfxgenerator.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import platinpython.vfxgenerator.client.gui.widget.TextureOptionsList;

public class ParticleTextureSelectionScreen extends Screen {
    private final ParticleOptionsScreen parent;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private TextureOptionsList textureOptionsList;

    protected ParticleTextureSelectionScreen(ParticleOptionsScreen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) {
            return;
        }

        this.textureOptionsList = new TextureOptionsList(
            this.minecraft, this.width, this.height - 64, 32, 50, this.parent.particleData.activeSelected
        );

        this.addRenderableWidget(this.textureOptionsList);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }
        if (!Container.stillValidBlockEntity(this.parent.blockEntity, this.minecraft.player)) {
            this.parent.onClose();
        }
        this.textureOptionsList.children().forEach(TextureOptionsList.TextureOptionsListEntry::updateValue);
        this.parent.sendToServer();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) {
            return;
        }

        this.minecraft.setScreen(this.parent);
    }
}
