package platinpython.vfxgenerator.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Color;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.registries.BlockEntityRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class VFXGeneratorBlockEntity extends BlockEntity {
    public static final String PARTICLE_DATA_KEY = "particleData";

    private final ParticleData particleData = new ParticleData(this);

    public VFXGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.VFX_GENERATOR.get(), pos, state);
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(blockEntity instanceof VFXGeneratorBlockEntity generatorBlockEntity)) {
            return;
        }
        if ((state.getValue(VFXGeneratorBlock.INVERTED) && !state.getValue(VFXGeneratorBlock.POWERED))
            || (!state.getValue(VFXGeneratorBlock.INVERTED) && state.getValue(VFXGeneratorBlock.POWERED))) {
            if (generatorBlockEntity.particleData.enabled.get()) {
                if (level.getGameTime() % generatorBlockEntity.particleData.delay.get() == 0) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    if (generatorBlockEntity.particleData.getSelected().isEmpty()) {
                        return;
                    }
                    ParticleType particleType =
                        DataManager.selectableParticles().get(generatorBlockEntity.particleData.getRandomSelected());
                    if (particleType == null) {
                        return;
                    }

                    int color;
                    if (generatorBlockEntity.particleData.useHSB.get()) {
                        color = Color.getRandomHSBColor(random, new float[]{
                            generatorBlockEntity.particleData.hue.get().start(),
                            generatorBlockEntity.particleData.saturation.get().start(),
                            generatorBlockEntity.particleData.brightness.get().start()
                        }, new float[]{
                            generatorBlockEntity.particleData.hue.get().end(),
                            generatorBlockEntity.particleData.saturation.get().end(),
                            generatorBlockEntity.particleData.brightness.get().end()
                        });
                    } else {
                        color = Color.getRandomRGBColor(
                            random, generatorBlockEntity.particleData.rgbColor.get().start(),
                            generatorBlockEntity.particleData.rgbColor.get().end()
                        );
                    }

                    int lifetime = Math.round(
                        (generatorBlockEntity.particleData.lifetime.get().start()
                            + (random.nextFloat() * (generatorBlockEntity.particleData.lifetime.get().end()
                                - generatorBlockEntity.particleData.lifetime.get().start())))
                    );

                    float size = generatorBlockEntity.particleData.size.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.size.get().end()
                            - generatorBlockEntity.particleData.size.get().start()));

                    Vec3 center = Vec3.atCenterOf(pos);
                    double spawnX = center.x + generatorBlockEntity.particleData.spawnX.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.spawnX.get().end()
                            - generatorBlockEntity.particleData.spawnX.get().start()));
                    double spawnY = center.y + generatorBlockEntity.particleData.spawnY.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.spawnY.get().end()
                            - generatorBlockEntity.particleData.spawnY.get().start()));
                    double spawnZ = center.z + generatorBlockEntity.particleData.spawnZ.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.spawnZ.get().end()
                            - generatorBlockEntity.particleData.spawnZ.get().start()));
                    center = new Vec3(spawnX, spawnY, spawnZ);

                    double motionX = generatorBlockEntity.particleData.motionX.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.motionX.get().end()
                            - generatorBlockEntity.particleData.motionX.get().start()));
                    double motionY = generatorBlockEntity.particleData.motionY.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.motionY.get().end()
                            - generatorBlockEntity.particleData.motionY.get().start()));
                    double motionZ = generatorBlockEntity.particleData.motionZ.get().start()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.motionZ.get().end()
                            - generatorBlockEntity.particleData.motionZ.get().start()));
                    Vec3 motion = new Vec3(motionX, motionY, motionZ);

                    ClientUtils.addParticle(
                        level, particleType, color, lifetime, size, center, motion,
                        generatorBlockEntity.particleData.gravity.get(),
                        generatorBlockEntity.particleData.collision.get(),
                        generatorBlockEntity.particleData.fullBright.get()
                    );
                }
            }
        }
    }

    public CompoundTag saveToTag(CompoundTag tag) {
        tag.put(PARTICLE_DATA_KEY, this.particleData.saveToTag());
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        this.particleData.loadFromTag(Objects.requireNonNullElse(tag.get(PARTICLE_DATA_KEY), EndTag.INSTANCE));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        CustomData particleData = componentInput.get(DataComponentRegistry.PARTICLE_DATA);
        if (particleData != null) {
            this.loadFromTag(particleData.copyTag());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.PARTICLE_DATA, CustomData.of(this.saveToTag(new CompoundTag())));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        saveToTag(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        loadFromTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveToTag(super.getUpdateTag(provider));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        loadFromTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        loadFromTag(pkt.getTag());
    }
}
