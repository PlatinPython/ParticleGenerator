package platinpython.vfxgenerator.client.gui.screen;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.client.gui.widget.ToggleButton;
import platinpython.vfxgenerator.client.gui.widget.VFXGeneratorOptionsList;
import platinpython.vfxgenerator.tileentity.VFXGeneratorTileEntity;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Color;
import platinpython.vfxgenerator.util.Constants.ParticleConstants;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.network.NetworkHandler;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDataSyncPKT;

public class VFXGeneratorScreen extends Screen {
	public final VFXGeneratorTileEntity tileEntity;

	private VFXGeneratorOptionsList particleOptionsList;

	public VFXGeneratorScreen(VFXGeneratorTileEntity tileEntity) {
		super(new TranslationTextComponent("block.vfxgenerator.vfx_generator"));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void init() {
		ToggleButton button = new ToggleButton(this.width / 2 - 30, 20, 60, 10, this.tileEntity::setParticleEnabled, this.tileEntity::isParticleEnabled, this::sendToServer);
		addButton(button);

		this.particleOptionsList = new VFXGeneratorOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

		this.particleOptionsList.addMultipleChoiceButton(ParticleConstants.PARTICLE_OPTIONS, (s) -> {
			this.tileEntity.setParticleSelected(Arrays.asList(s));
		}, () -> this.tileEntity.getParticleSelected().get(0), this::sendToServer);

		this.particleOptionsList.addToggleButton(ClientUtils.getGuiTranslationTextComponent("rgb"), ClientUtils.getGuiTranslationTextComponent("hsb"), this.tileEntity::setParticleUseHSB, this.tileEntity::isParticleUseHSB, this::sendToServer);

		this.particleOptionsList.getToggleableRangeSliderBuilder().applyValueFunction(this::sendToServer).toggleValueSupplier(this.tileEntity::isParticleUseHSB).prefixFirst(ClientUtils.getGuiTranslationTextComponent("red")).minValueFirst(0F).maxValueFirst(255F).setLeftValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorBot());
			Color newColor = new Color(value.intValue(), oldColor.getGreen(), oldColor.getBlue());
			this.tileEntity.setParticleRGBColorBot(newColor.getRGB());
		}).setRightValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorTop());
			Color newColor = new Color(value.intValue(), oldColor.getGreen(), oldColor.getBlue());
			this.tileEntity.setParticleRGBColorTop(newColor.getRGB());
		}).leftValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorBot()).getRed();
		}).rightValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorTop()).getRed();
		}).prefixSecond(ClientUtils.getGuiTranslationTextComponent("hue")).suffixSecond(new StringTextComponent("°")).minValueSecond(0F).maxValueSecond(360F).setLeftValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorBot();
			this.tileEntity.setParticleHSBColorBot(new float[] { value / 360F, oldHsb[1], oldHsb[2] });
		}).setRightValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorTop();
			this.tileEntity.setParticleHSBColorTop(new float[] { value / 360F, oldHsb[1], oldHsb[2] });
		}).leftValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorBot()[0] * 360F;
		}).rightValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorTop()[0] * 360F;
		}).build();

		this.particleOptionsList.getToggleableRangeSliderBuilder().applyValueFunction(this::sendToServer).toggleValueSupplier(this.tileEntity::isParticleUseHSB).prefixFirst(ClientUtils.getGuiTranslationTextComponent("green")).minValueFirst(0F).maxValueFirst(255F).setLeftValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorBot());
			Color newColor = new Color(oldColor.getRed(), value.intValue(), oldColor.getBlue());
			this.tileEntity.setParticleRGBColorBot(newColor.getRGB());
		}).setRightValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorTop());
			Color newColor = new Color(oldColor.getRed(), value.intValue(), oldColor.getBlue());
			this.tileEntity.setParticleRGBColorTop(newColor.getRGB());
		}).leftValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorBot()).getGreen();
		}).rightValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorTop()).getGreen();
		}).prefixSecond(ClientUtils.getGuiTranslationTextComponent("saturation")).suffixSecond(new StringTextComponent("%")).minValueSecond(0F).maxValueSecond(100F).setLeftValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorBot();
			this.tileEntity.setParticleHSBColorBot(new float[] { oldHsb[0], value / 100F, oldHsb[2] });
		}).setRightValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorTop();
			this.tileEntity.setParticleHSBColorTop(new float[] { oldHsb[0], value / 100F, oldHsb[2] });
		}).leftValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorBot()[1] * 100F;
		}).rightValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorTop()[1] * 100F;
		}).build();

		this.particleOptionsList.getToggleableRangeSliderBuilder().applyValueFunction(this::sendToServer).toggleValueSupplier(this.tileEntity::isParticleUseHSB).prefixFirst(ClientUtils.getGuiTranslationTextComponent("blue")).minValueFirst(0F).maxValueFirst(255F).setLeftValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorBot());
			Color newColor = new Color(oldColor.getRed(), oldColor.getGreen(), value.intValue());
			this.tileEntity.setParticleRGBColorBot(newColor.getRGB());
		}).setRightValueFunctionFirst((value) -> {
			Color oldColor = new Color(this.tileEntity.getParticleRGBColorTop());
			Color newColor = new Color(oldColor.getRed(), oldColor.getGreen(), value.intValue());
			this.tileEntity.setParticleRGBColorTop(newColor.getRGB());
		}).leftValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorBot()).getBlue();
		}).rightValueSupplierFirst(() -> {
			return (float) new Color(this.tileEntity.getParticleRGBColorTop()).getBlue();
		}).prefixSecond(ClientUtils.getGuiTranslationTextComponent("brightness")).suffixSecond(new StringTextComponent("%")).minValueSecond(0F).maxValueSecond(100F).setLeftValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorBot();
			this.tileEntity.setParticleHSBColorBot(new float[] { oldHsb[0], oldHsb[1], value / 100F });
		}).setRightValueFunctionSecond((value) -> {
			float[] oldHsb = this.tileEntity.getParticleHSBColorTop();
			this.tileEntity.setParticleHSBColorTop(new float[] { oldHsb[0], oldHsb[1], value / 100F });
		}).leftValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorBot()[2] * 100F;
		}).rightValueSupplierSecond(() -> {
			return this.tileEntity.getParticleHSBColorTop()[2] * 100F;
		}).build();

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("lifetime"), ClientUtils.getGuiTranslationTextComponent("ticks"), ParticleConstants.MIN_LIFETIME, ParticleConstants.MAX_LIFETIME, 1F, (value) -> this.tileEntity.setParticleLifetimeBot(value.intValue()), (value) -> this.tileEntity.setParticleLifetimeTop(value.intValue()), () -> (float) this.tileEntity.getParticleLifetimeBot(), () -> (float) this.tileEntity.getParticleLifetimeTop(), this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("size"), StringTextComponent.EMPTY, ParticleConstants.MIN_SIZE, ParticleConstants.MAX_SIZE, .1F, this.tileEntity::setParticleSizeBot, this.tileEntity::setParticleSizeTop, this.tileEntity::getParticleSizeBot, this.tileEntity::getParticleSizeTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("spawnX"), StringTextComponent.EMPTY, ParticleConstants.MIN_SPAWN, ParticleConstants.MAX_SPAWN, .1F, this.tileEntity::setParticleSpawnXBot, this.tileEntity::setParticleSpawnXTop, this.tileEntity::getParticleSpawnXBot, this.tileEntity::getParticleSpawnXTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("spawnY"), StringTextComponent.EMPTY, ParticleConstants.MIN_SPAWN, ParticleConstants.MAX_SPAWN, .1F, this.tileEntity::setParticleSpawnYBot, this.tileEntity::setParticleSpawnYTop, this.tileEntity::getParticleSpawnYBot, this.tileEntity::getParticleSpawnYTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("spawnZ"), StringTextComponent.EMPTY, ParticleConstants.MIN_SPAWN, ParticleConstants.MAX_SPAWN, .1F, this.tileEntity::setParticleSpawnZBot, this.tileEntity::setParticleSpawnZTop, this.tileEntity::getParticleSpawnZBot, this.tileEntity::getParticleSpawnZTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("motionX"), StringTextComponent.EMPTY, ParticleConstants.MIN_MOTION, ParticleConstants.MAX_MOTION, .01F, this.tileEntity::setParticleMotionXBot, this.tileEntity::setParticleMotionXTop, this.tileEntity::getParticleMotionXBot, this.tileEntity::getParticleMotionXTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("motionY"), StringTextComponent.EMPTY, ParticleConstants.MIN_MOTION, ParticleConstants.MAX_MOTION, .01F, this.tileEntity::setParticleMotionYBot, this.tileEntity::setParticleMotionYTop, this.tileEntity::getParticleMotionYBot, this.tileEntity::getParticleMotionYTop, this::sendToServer);

		this.particleOptionsList.addRangeSlider(ClientUtils.getGuiTranslationTextComponent("motionZ"), StringTextComponent.EMPTY, ParticleConstants.MIN_MOTION, ParticleConstants.MAX_MOTION, .01F, this.tileEntity::setParticleMotionZBot, this.tileEntity::setParticleMotionZTop, this.tileEntity::getParticleMotionZBot, this.tileEntity::getParticleMotionZTop, this::sendToServer);

		this.particleOptionsList.addSlider(ClientUtils.getGuiTranslationTextComponent("delay"), ClientUtils.getGuiTranslationTextComponent("ticks"), ParticleConstants.MIN_DELAY, ParticleConstants.MAX_DELAY, 1F, (value) -> this.tileEntity.setParticleDelay(value.intValue()), () -> (float) this.tileEntity.getParticleDelay(), this::sendToServer);

		this.particleOptionsList.addSlider(ClientUtils.getGuiTranslationTextComponent("gravity"), StringTextComponent.EMPTY, ParticleConstants.MIN_GRAVITY, ParticleConstants.MAX_GRAVITY, .01F, this.tileEntity::setParticleGravity, this.tileEntity::getParticleGravity, this::sendToServer);

		this.particleOptionsList.addToggleButton(ClientUtils.getGuiTranslationTextComponent("collision").append(": ").append(ClientUtils.getGuiTranslationTextComponent("disabled")), ClientUtils.getGuiTranslationTextComponent("collision").append(": ").append(ClientUtils.getGuiTranslationTextComponent("enabled")), this.tileEntity::setParticleCollision, this.tileEntity::isParticleCollision, this::sendToServer);

		this.particleOptionsList.addToggleButton(ClientUtils.getGuiTranslationTextComponent("fullbright").append(": ").append(ClientUtils.getGuiTranslationTextComponent("disabled")), ClientUtils.getGuiTranslationTextComponent("fullbright").append(": ").append(ClientUtils.getGuiTranslationTextComponent("enabled")), this.tileEntity::setParticleFullbright, this.tileEntity::isParticleFullbright, this::sendToServer);

		this.particleOptionsList.children().parallelStream().forEach((entry) -> entry.setActive(this.tileEntity.isParticleEnabled()));

		this.addWidget(this.particleOptionsList);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.particleOptionsList.render(matrixStack, mouseX, mouseY, partialTicks);
		if (!this.tileEntity.isParticleEnabled()) {
			this.fillGradient(matrixStack, 0, 32, this.width, this.height - 32, 0xC0101010, 0xD0101010);
		}
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		AbstractGui.drawCenteredString(matrixStack, this.font, ClientUtils.getGuiTranslationTextComponent("particle"), this.width / 2, 10, 0xFFFFFFFF);

		// START TESTING
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SourceFactor.SRC_COLOR, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ZERO);
		this.minecraft.getTextureManager().bind(AtlasTexture.LOCATION_PARTICLES);
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		TextureAtlasSprite sprite = minecraft.particleEngine.textureAtlas.getSprite(new ResourceLocation(VFXGenerator.MOD_ID, "particle/" + this.tileEntity.getParticleSelected().get(this.minecraft.level.random.nextInt(this.tileEntity.getParticleSelected().size()))));
		Vector3f[] cornerCoordinates = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };

		for (int i = 0; i < 4; ++i) {
			Vector3f cornerCoordinate = cornerCoordinates[i];
//			cornerCoordinate.mul(((this.minecraft.level.getGameTime() % 100) - 50) * 2);
			cornerCoordinate.mul((float) Util.map(Math.sin(this.minecraft.level.getGameTime() / 5F), -1D, 1D, 10D, 20D));
			cornerCoordinate.add(this.width / 2F, this.height / 2F, 0F);
		}

		float u0 = sprite.getU0();
		float u1 = sprite.getU1();
		float v0 = sprite.getV0();
		float v1 = sprite.getV1();
		bufferBuilder.vertex((double) cornerCoordinates[0].x(), (double) cornerCoordinates[0].y(), (double) cornerCoordinates[0].z()).uv(u1, v1).endVertex();
		bufferBuilder.vertex((double) cornerCoordinates[1].x(), (double) cornerCoordinates[1].y(), (double) cornerCoordinates[1].z()).uv(u1, v0).endVertex();
		bufferBuilder.vertex((double) cornerCoordinates[2].x(), (double) cornerCoordinates[2].y(), (double) cornerCoordinates[2].z()).uv(u0, v0).endVertex();
		bufferBuilder.vertex((double) cornerCoordinates[3].x(), (double) cornerCoordinates[3].y(), (double) cornerCoordinates[3].z()).uv(u0, v1).endVertex();
		tessellator.end();
		// END TESTING
	}

	@Override
	public void tick() {
		if (minecraft.player.distanceToSqr(Vector3d.atCenterOf(tileEntity.getBlockPos())) > 64.0D) {
			onClose();
		}
		this.particleOptionsList.children().parallelStream().forEach((entry) -> {
			entry.updateValue();
			entry.setActive(this.tileEntity.isParticleEnabled());
		});
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private final void sendToServer() {
		NetworkHandler.INSTANCE.sendToServer(new VFXGeneratorDataSyncPKT(this.tileEntity.saveToTag(new CompoundNBT()), this.tileEntity.getBlockPos()));
	}
}
