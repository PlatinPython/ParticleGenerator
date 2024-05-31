package platinpython.vfxgenerator.client.gui.widget;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.data.OwnedDataElement;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.particle.ParticleTypes;
import platinpython.vfxgenerator.util.particle.types.SingleParticle;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.TreeSet;

@SuppressWarnings("UnstableApiUsage")
public class ImageSelectionWidget extends UpdateableWidget {
    private final ResourceLocation particleId;
    private final OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement;

    private boolean selected;

    public ImageSelectionWidget(
        int x,
        int y,
        int width,
        int height,
        ResourceLocation particleId,
        OwnedDataElement<ImmutableSortedSet<ResourceLocation>> dataElement
    ) {
        super(x, y, width, height);
        this.particleId = particleId;
        this.dataElement = dataElement;
        this.updateValue();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fill(
            this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height,
            this.selected ? 0xFFFFFFFF : 0xFF000000
        );
        guiGraphics.fill(
            this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 0xFF000000
        );
        guiGraphics.blitSprite(
            this.selected ? new ResourceLocation("widget/button_highlighted") : new ResourceLocation("widget/button"),
            this.getX(), this.getY(), this.width, this.height
        );
        this.renderImage(
            guiGraphics.pose().last().pose(), this.getX() + 5, this.getY() + 5, this.getX() + this.width - 5,
            this.getY() + this.height - 5
        );
    }

    @SuppressWarnings("deprecation")
    private void renderImage(Matrix4f matrix, int minX, int minY, int maxX, int maxY) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        TextureAtlasSprite sprite = selectTextureAtlasSprite();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        bufferBuilder.vertex(matrix, minX, maxY, 0F).uv(u0, v1).endVertex();
        bufferBuilder.vertex(matrix, maxX, maxY, 0F).uv(u1, v1).endVertex();
        bufferBuilder.vertex(matrix, maxX, minY, 0F).uv(u1, v0).endVertex();
        bufferBuilder.vertex(matrix, minX, minY, 0F).uv(u0, v0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    private TextureAtlasSprite selectTextureAtlasSprite() {
        ParticleType particleType = DataManager.selectableParticles().get(this.particleId);
        if (particleType != null) {
            if (particleType.type() == ParticleTypes.SINGLE) {
                return ClientUtils.getTextureAtlasSprite(((SingleParticle) particleType).value());
            }
        }
        return ClientUtils.getTextureAtlasSprite(MissingTextureAtlasSprite.getLocation());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (Screen.hasControlDown()) {
            this.selected = !this.selected;
            if (!this.selected) {
                this.selected = this.dataElement.get().size() <= 1;
            }
            TreeSet<ResourceLocation> set = new TreeSet<>(this.dataElement.get());
            if (this.selected) {
                set.add(this.particleId);
            } else {
                set.remove(this.particleId);
            }
            this.dataElement.set(ImmutableSortedSet.copyOfSorted(set));
        } else {
            this.dataElement
                .set(ImmutableSortedSet.orderedBy(ResourceLocation::compareNamespaced).add(this.particleId).build());
            this.selected = true;
        }
    }

    @Override
    public void updateValue() {
        this.selected = this.dataElement.get().contains(particleId);
    }

    @Override
    protected void updateMessage() {}

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
