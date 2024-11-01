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
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.registries.BlockEntityRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class VFXGeneratorBlockEntity extends BlockEntity {
    public static final String PARTICLE_DATA_KEY = "particle_data";

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
            ParticleData particleData = generatorBlockEntity.particleData;
            if (particleData.enabled.get()) {
                if (level.getGameTime() % particleData.delay.get() == 0) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    if (particleData.activeSelected.getView().isEmpty()) {
                        return;
                    }
                    ParticleType particleType = DataManager.selectableParticles()
                        .get(Util.randomElement(particleData.activeSelected.getView()));
                    if (particleType == null) {
                        return;
                    }

                    int color;
                    if (particleData.useHSB.get()) {
                        color = Color.getRandomHSBColor(
                            random, particleData.hue.get().start(), particleData.saturation.get().start(),
                            particleData.brightness.get().start(), particleData.hue.get().end(),
                            particleData.saturation.get().end(), particleData.brightness.get().end()
                        );
                    } else {
                        color = Color.getRandomRGBColor(
                            random, particleData.rgbColor.get().start(), particleData.rgbColor.get().end()
                        );
                    }

                    int lifetime =
                        random.nextInt(particleData.lifetime.get().start(), particleData.lifetime.get().end() + 1);

                    float size =
                        random.nextFloat(particleData.size.get().start(), Math.nextUp(particleData.size.get().end()));

                    Vec3 center = Vec3.atCenterOf(pos);
                    double spawnX = random
                        .nextFloat(particleData.spawnX.get().start(), Math.nextUp(particleData.spawnX.get().end()));
                    double spawnY = random
                        .nextFloat(particleData.spawnY.get().start(), Math.nextUp(particleData.spawnY.get().end()));
                    double spawnZ = random
                        .nextFloat(particleData.spawnZ.get().start(), Math.nextUp(particleData.spawnZ.get().end()));
                    center = center.add(spawnX, spawnY, spawnZ);

                    double motionX = random
                        .nextFloat(particleData.motionX.get().start(), Math.nextUp(particleData.motionX.get().end()));
                    double motionY = random
                        .nextFloat(particleData.motionY.get().start(), Math.nextUp(particleData.motionY.get().end()));
                    double motionZ = random
                        .nextFloat(particleData.motionZ.get().start(), Math.nextUp(particleData.motionZ.get().end()));
                    Vec3 motion = new Vec3(motionX, motionY, motionZ);

                    ClientUtils.addParticle(
                        level, particleType, color, lifetime, size, center, motion, particleData.gravity.get(),
                        particleData.collision.get(), particleData.fullBright.get()
                    );
                }
            }
        }
    }

    public CompoundTag saveToTag(CompoundTag tag) {
        tag.put(PARTICLE_DATA_KEY, this.particleData.saveToTag());
        return tag;
    }

    public CompoundTag saveDiffToTag(CompoundTag tag) {
        tag.put(PARTICLE_DATA_KEY, this.particleData.saveDiffToTag());
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        String particleDataKey = tag.contains(PARTICLE_DATA_KEY) ? PARTICLE_DATA_KEY : "particleData";
        this.particleData.loadFromTag(Objects.requireNonNullElse(tag.get(particleDataKey), EndTag.INSTANCE));
    }

    public void loadDiffFromTag(CompoundTag tag) {
        this.particleData.loadDiffFromTag(Objects.requireNonNullElse(tag.get(PARTICLE_DATA_KEY), EndTag.INSTANCE));
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

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(PARTICLE_DATA_KEY);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.saveToTag(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.loadFromTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveToTag(super.getUpdateTag(provider));
    }

    public CompoundTag getDiffUpdateTag(HolderLookup.Provider provider) {
        return this.saveDiffToTag(super.getUpdateTag(provider));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet =
            ClientboundBlockEntityDataPacket.create(this, (blockEntity, registryAccess) -> {
                if (blockEntity instanceof VFXGeneratorBlockEntity vfxGeneratorBlockEntity) {
                    return vfxGeneratorBlockEntity.getDiffUpdateTag(registryAccess);
                } else {
                    return blockEntity.getUpdateTag(registryAccess);
                }
            });
        ParticleData.CLEANER.accept(this.particleData);
        return packet;
    }

    @Override
    public void onDataPacket(
        Connection connection,
        ClientboundBlockEntityDataPacket packet,
        HolderLookup.Provider lookupProvider
    ) {
        this.loadDiffFromTag(packet.getTag());
    }
}
