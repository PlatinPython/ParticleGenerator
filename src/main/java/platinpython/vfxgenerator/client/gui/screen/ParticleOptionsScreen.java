package platinpython.vfxgenerator.client.gui.screen;

import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.Container;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.client.gui.widget.ToggleButton;
import platinpython.vfxgenerator.client.gui.widget.VFXGeneratorOptionsList;
import platinpython.vfxgenerator.util.BoxRendering;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Constants;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.data.Range;
import platinpython.vfxgenerator.util.network.packets.ParticleDataSyncPayload;

@SuppressWarnings("UnstableApiUsage")
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

        addRenderableWidget(new ToggleButton(this.width / 2 - 30, 20, 60, 10, this.particleData.enabled));

        this.optionsList = new VFXGeneratorOptionsList(this.minecraft, this.width, this.height - 64, 32, 25);

        this.optionsList.addButton(
            ClientUtils.getGuiTranslationTextComponent("selectTypes"),
            () -> this.minecraft.setScreen(new ParticleTextureSelectionScreen(this))
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("rgb"), ClientUtils.getGuiTranslationTextComponent("hsb"),
            this.particleData.useHSB
        );

        this.optionsList.getToggleableRangeSliderBuilder()
            .toggleSupplier(this.particleData.useHSB::get)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("red"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setRangeFirst(range -> {
                Range<Integer> oldRange = this.particleData.rgbColor.get();
                this.particleData.rgbColor.set(
                    oldRange.with(
                        FastColor.ARGB32.color(
                            range.start().intValue(), FastColor.ARGB32.green(oldRange.start()),
                            FastColor.ARGB32.blue(oldRange.start())
                        ),
                        FastColor.ARGB32.color(
                            range.end().intValue(), FastColor.ARGB32.green(oldRange.end()),
                            FastColor.ARGB32.blue(oldRange.end())
                        )
                    )
                );
            })
            .getRangeFirst(
                () -> new Range<>(
                    (float) FastColor.ARGB32.red(this.particleData.rgbColor.get().start()),
                    (float) FastColor.ARGB32.red(this.particleData.rgbColor.get().end())
                )
            )
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("hue"))
            .suffixSecond(Component.literal("°"))
            .minValueSecond(0F)
            .maxValueSecond(360F)
            .setRangeSecond(
                range -> this.particleData.hue
                    .set(this.particleData.hue.get().with(range.start() / 360, range.end() / 360))
            )
            .getRangeSecond(
                () -> new Range<>(this.particleData.hue.get().start() * 360, this.particleData.hue.get().end() * 360)
            )
            .build();

        this.optionsList.getToggleableRangeSliderBuilder()
            .toggleSupplier(this.particleData.useHSB::get)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("green"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setRangeFirst(range -> {
                Range<Integer> oldRange = this.particleData.rgbColor.get();
                this.particleData.rgbColor.set(
                    oldRange.with(
                        FastColor.ARGB32.color(
                            FastColor.ARGB32.red(oldRange.start()), range.start().intValue(),
                            FastColor.ARGB32.blue(oldRange.start())
                        ),
                        FastColor.ARGB32.color(
                            FastColor.ARGB32.red(oldRange.end()), range.end().intValue(),
                            FastColor.ARGB32.blue(oldRange.end())
                        )
                    )
                );
            })
            .getRangeFirst(
                () -> new Range<>(
                    (float) FastColor.ARGB32.green(this.particleData.rgbColor.get().start()),
                    (float) FastColor.ARGB32.green(this.particleData.rgbColor.get().end())
                )
            )
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("saturation"))
            .suffixSecond(Component.literal("%"))
            .minValueSecond(0F)
            .maxValueSecond(100F)
            .setRangeSecond(
                range -> this.particleData.saturation
                    .set(this.particleData.saturation.get().with(range.start() / 100, range.end() / 100))
            )
            .getRangeSecond(
                () -> new Range<>(
                    this.particleData.saturation.get().start() * 100, this.particleData.saturation.get().end() * 100
                )
            )
            .build();

        this.optionsList.getToggleableRangeSliderBuilder()
            .toggleSupplier(this.particleData.useHSB::get)
            .prefixFirst(ClientUtils.getGuiTranslationTextComponent("blue"))
            .minValueFirst(0F)
            .maxValueFirst(255F)
            .setRangeFirst(range -> {
                Range<Integer> oldRange = this.particleData.rgbColor.get();
                this.particleData.rgbColor.set(
                    oldRange.with(
                        FastColor.ARGB32.color(
                            FastColor.ARGB32.red(oldRange.start()), FastColor.ARGB32.green(oldRange.start()),
                            range.start().intValue()
                        ),
                        FastColor.ARGB32.color(
                            FastColor.ARGB32.red(oldRange.end()), FastColor.ARGB32.green(oldRange.end()),
                            range.end().intValue()
                        )
                    )
                );
            })
            .getRangeFirst(
                () -> new Range<>(
                    (float) FastColor.ARGB32.blue(this.particleData.rgbColor.get().start()),
                    (float) FastColor.ARGB32.blue(this.particleData.rgbColor.get().end())
                )
            )
            .prefixSecond(ClientUtils.getGuiTranslationTextComponent("brightness"))
            .suffixSecond(Component.literal("%"))
            .minValueSecond(0F)
            .maxValueSecond(100F)
            .setRangeSecond(
                range -> this.particleData.brightness
                    .set(this.particleData.brightness.get().with(range.start() / 100, range.end() / 100))
            )
            .getRangeSecond(
                () -> new Range<>(
                    this.particleData.brightness.get().start() * 100, this.particleData.brightness.get().end() * 100
                )
            )
            .build();

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("lifetime"), ClientUtils.getGuiTranslationTextComponent("ticks"),
            Constants.ParticleConstants.Values.MIN_LIFETIME, Constants.ParticleConstants.Values.MAX_LIFETIME, 1F,
            range -> this.particleData.lifetime
                .set(this.particleData.lifetime.get().with(range.start().intValue(), range.end().intValue())),
            () -> new Range<>(
                this.particleData.lifetime.get().start().floatValue(),
                this.particleData.lifetime.get().end().floatValue()
            )
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("size"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SIZE, Constants.ParticleConstants.Values.MAX_SIZE, .1F,
            this.particleData.size
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnX"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData.spawnX
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnY"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData.spawnY
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("spawnZ"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN, .1F,
            this.particleData.spawnZ
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionX"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData.motionX
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionY"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData.motionY
        );

        this.optionsList.addRangeSlider(
            ClientUtils.getGuiTranslationTextComponent("motionZ"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION, .01F,
            this.particleData.motionZ
        );

        this.optionsList.addSlider(
            ClientUtils.getGuiTranslationTextComponent("delay"), ClientUtils.getGuiTranslationTextComponent("ticks"),
            Constants.ParticleConstants.Values.MIN_DELAY, Constants.ParticleConstants.Values.MAX_DELAY, 1F,
            value -> this.particleData.delay.set(value.intValue()), () -> this.particleData.delay.get().floatValue()
        );

        this.optionsList.addSlider(
            ClientUtils.getGuiTranslationTextComponent("gravity"), Component.empty(),
            Constants.ParticleConstants.Values.MIN_GRAVITY, Constants.ParticleConstants.Values.MAX_GRAVITY, .01F,
            this.particleData.gravity
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("collision")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("disabled")),
            ClientUtils.getGuiTranslationTextComponent("collision")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("enabled")),
            this.particleData.collision
        );

        this.optionsList.addToggleButton(
            ClientUtils.getGuiTranslationTextComponent("fullbright")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("disabled")),
            ClientUtils.getGuiTranslationTextComponent("fullbright")
                .append(": ")
                .append(ClientUtils.getGuiTranslationTextComponent("enabled")),
            this.particleData.fullBright
        );

        this.optionsList.children().forEach((entry) -> entry.setActive(this.particleData.enabled.get()));

        this.addRenderableWidget(this.optionsList);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (!this.particleData.enabled.get()) {
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
            entry.setActive(this.particleData.enabled.get());
        });
        this.sendToServer();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.sendToServer();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected final void sendToServer() {
        if (!ParticleData.ANY_DIRTY.test(this.particleData)) {
            return;
        }
        PacketDistributor.sendToServer(
            new ParticleDataSyncPayload(Asymmetry.ofEncoding(this.particleData), this.blockEntity.getBlockPos())
        );
        VFXGenerator.LOGGER.info("Screen: {}", this.particleData.saveToTag());
        ParticleData.CLEANER.accept(this.particleData);
    }
}
