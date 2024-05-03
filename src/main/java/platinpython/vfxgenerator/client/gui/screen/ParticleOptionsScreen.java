package platinpython.vfxgenerator.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.client.gui.widget.ToggleButton;
import platinpython.vfxgenerator.client.gui.widget.VFXGeneratorOptionsList;
import platinpython.vfxgenerator.util.BoxRendering;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Color;
import platinpython.vfxgenerator.util.Constants;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDataSyncPayload;

public class ParticleOptionsScreen extends Screen {
    protected final VFXGeneratorBlockEntity blockEntity;
    protected final ParticleData particleData;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private VFXGeneratorOptionsList optionsList;

    public ParticleOptionsScreen(VFXGeneratorBlockEntity blockEntity) {
        super(Component.empty());
        this.blockEntity = blockEntity;
        this.particleData = blockEntity.getParticleData();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void init() {
        if (this.minecraft == null) {
            return;
        }

        addRenderableWidget(Button.builder(ClientUtils.getGuiTranslationTextComponent("areaBox"), button -> {
            if (blockEntity.getBlockPos().equals(BoxRendering.currentRenderPos)) {
                BoxRendering.currentRenderPos = null;
            } else {
                BoxRendering.currentRenderPos = blockEntity.getBlockPos();
            }
        }).bounds(6, this.height - 26, 120, 20).build());

        addRenderableWidget(
            new ToggleButton(
                this.width / 2 - 30, 20, 60, 10, this.particleData::setEnabled, this.particleData::isEnabled,
                this::sendToServer
            )
        );

        this.optionsList = new VFXGeneratorOptionsList(this.minecraft, this.width, this.height - 64, 32, 25);

        this.optionsList.addButton(
            ClientUtils.getGuiTranslationTextComponent("selectTypes"),
            () -> this.minecraft.setScreen(new ParticleTextureSelectionScreen(this))
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("rgb"), ClientUtils.getGuiTranslationTextComponent("hsb"),
            this.particleData::setUseHSB, this.particleData::useHSB, this::sendToServer
        );

        this.optionsList.getToggleableRangeSliderBuilder()
            .applyValueFunction(this::sendToServer)
            .toggleValueSupplier(this.particleData::useHSB)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("red"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setLeftValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorBot());
                Color newColor = new Color((int) value, oldColor.getGreen(), oldColor.getBlue());
                this.particleData.setRGBColorBot(newColor.getRGB());
            })
            .setRightValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorTop());
                Color newColor = new Color((int) value, oldColor.getGreen(), oldColor.getBlue());
                this.particleData.setRGBColorTop(newColor.getRGB());
            })
            .leftValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorBot()).getRed())
            .rightValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorTop()).getRed())
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("hue"))
            .suffixSecond(Component.literal("°"))
            .minValueSecond(0F)
            .maxValueSecond(360F)
            .setLeftValueFunctionSecond((value) -> this.particleData.setHueBot(value / 360F))
            .setRightValueFunctionSecond((value) -> this.particleData.setHueTop(value / 360F))
            .leftValueSupplierSecond(() -> this.particleData.getHueBot() * 360F)
            .rightValueSupplierSecond(() -> this.particleData.getHueTop() * 360F)
            .build();

        this.optionsList.getToggleableRangeSliderBuilder()
            .applyValueFunction(this::sendToServer)
            .toggleValueSupplier(this.particleData::useHSB)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("green"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setLeftValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorBot());
                Color newColor = new Color(oldColor.getRed(), (int) value, oldColor.getBlue());
                this.particleData.setRGBColorBot(newColor.getRGB());
            })
            .setRightValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorTop());
                Color newColor = new Color(oldColor.getRed(), (int) value, oldColor.getBlue());
                this.particleData.setRGBColorTop(newColor.getRGB());
            })
            .leftValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorBot()).getGreen())
            .rightValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorTop()).getGreen())
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("saturation"))
            .suffixSecond(Component.literal("%"))
            .minValueSecond(0F)
            .maxValueSecond(100F)
            .setLeftValueFunctionSecond((value) -> this.particleData.setSaturationBot(value / 100F))
            .setRightValueFunctionSecond((value) -> this.particleData.setSaturationTop(value / 100F))
            .leftValueSupplierSecond(() -> this.particleData.getSaturationBot() * 100F)
            .rightValueSupplierSecond(() -> this.particleData.getSaturationTop() * 100F)
            .build();

        this.optionsList.getToggleableRangeSliderBuilder()
            .applyValueFunction(this::sendToServer)
            .toggleValueSupplier(this.particleData::useHSB)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("blue"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setLeftValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorBot());
                Color newColor = new Color(oldColor.getRed(), oldColor.getGreen(), (int) value);
                this.particleData.setRGBColorBot(newColor.getRGB());
            })
            .setRightValueFunctionFirst((value) -> {
                Color oldColor = new Color(this.particleData.getRGBColorTop());
                Color newColor = new Color(oldColor.getRed(), oldColor.getGreen(), (int) value);
                this.particleData.setRGBColorTop(newColor.getRGB());
            })
            .leftValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorBot()).getBlue())
            .rightValueSupplierFirst(() -> (float) new Color(this.particleData.getRGBColorTop()).getBlue())
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("brightness"))
            .suffixSecond(Component.literal("%"))
            .minValueSecond(0F)
            .maxValueSecond(100F)
            .setLeftValueFunctionSecond((value) -> this.particleData.setBrightnessBot(value / 100F))
            .setRightValueFunctionSecond((value) -> this.particleData.setBrightnessTop(value / 100F))
            .leftValueSupplierSecond(() -> this.particleData.getBrightnessBot() * 100F)
            .rightValueSupplierSecond(() -> this.particleData.getBrightnessTop() * 100F)
            .build();

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("lifetime"), ClientUtils.getGuiTranslationTextComponent("ticks"),
            Constants.ParticleConstants.Values.MIN_LIFETIME, Constants.ParticleConstants.Values.MAX_LIFETIME, 1F,
            (value) -> this.particleData.setLifetimeBot((int) value),
            (value) -> this.particleData.setLifetimeTop((int) value), () -> (float) this.particleData.getLifetimeBot(),
            () -> (float) this.particleData.getLifetimeTop(), this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("size"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SIZE, Constants.ParticleConstants.Values.MAX_SIZE, .1F,
            this.particleData::setSizeBot, this.particleData::setSizeTop, this.particleData::getSizeBot,
            this.particleData::getSizeTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnX"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData::setSpawnXBot, this.particleData::setSpawnXTop, this.particleData::getSpawnXBot,
            this.particleData::getSpawnXTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnY"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData::setSpawnYBot, this.particleData::setSpawnYTop, this.particleData::getSpawnYBot,
            this.particleData::getSpawnYTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnZ"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData::setSpawnZBot, this.particleData::setSpawnZTop, this.particleData::getSpawnZBot,
            this.particleData::getSpawnZTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionX"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData::setMotionXBot, this.particleData::setMotionXTop, this.particleData::getMotionXBot,
            this.particleData::getMotionXTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionY"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData::setMotionYBot, this.particleData::setMotionYTop, this.particleData::getMotionYBot,
            this.particleData::getMotionYTop, this::sendToServer
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionZ"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData::setMotionZBot, this.particleData::setMotionZTop, this.particleData::getMotionZBot,
            this.particleData::getMotionZTop, this::sendToServer
        );

        this.optionsList.addSlider(
            ClientUtils.getGuiTranslationTextComponent("delay"), ClientUtils.getGuiTranslationTextComponent("ticks"),
            Constants.ParticleConstants.Values.MIN_DELAY, Constants.ParticleConstants.Values.MAX_DELAY, 1F,
            (value) -> this.particleData.setDelay((int) value), () -> (float) this.particleData.getDelay(),
            this::sendToServer
        );

        this.optionsList.addSlider(
            ClientUtils.getGuiTranslationTextComponent("gravity"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_GRAVITY, Constants.ParticleConstants.Values.MAX_GRAVITY, .01F,
            this.particleData::setGravity, this.particleData::getGravity, this::sendToServer
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("collision")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("disabled")),
            ClientUtils.getGuiTranslationTextComponent("collision")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("enabled")),
            this.particleData::setCollision, this.particleData::hasCollision, this::sendToServer
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("fullbright")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("disabled")),
            ClientUtils.getGuiTranslationTextComponent("fullbright")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("enabled")),
            this.particleData::setFullBright, this.particleData::isFullBright, this::sendToServer
        );

        this.optionsList.children().forEach((entry) -> entry.setActive(this.particleData.isEnabled()));

        this.addRenderableWidget(this.optionsList);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (!this.particleData.isEnabled()) {
            guiGraphics.fillGradient(0, 32, this.width, this.height - 32, 0xC0101010, 0xD0101010);
        }
        guiGraphics.drawCenteredString(
            this.font, ClientUtils.getGuiTranslationTextComponent("particle"), this.width / 2, 10, 0xFFFFFFFF
        );
    }

    @Override
    public void tick() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }
        if (!Container.stillValidBlockEntity(this.blockEntity, this.minecraft.player)) {
            this.onClose();
        }
        this.optionsList.children().forEach((entry) -> {
            entry.updateValue();
            entry.setActive(this.particleData.isEnabled());
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected final void sendToServer() {
        PacketDistributor.sendToServer(
            new VFXGeneratorDataSyncPayload(
                this.blockEntity.saveToTag(new CompoundTag()), this.blockEntity.getBlockPos()
            )
        );
    }
}
