package platinpython.vfxgenerator.util.data;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.Asymmetry;
import dev.lukebemish.codecextras.mutable.DataElementType;
import dev.lukebemish.codecextras.stream.mutable.StreamDataElementType;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.Constants;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ParticleData {

    private static final StreamDataElementType<FriendlyByteBuf, ParticleData, Boolean> ENABLED = StreamDataElementType
        .create("enabled", Codec.BOOL, ByteBufCodecs.BOOL.mapStream(FriendlyByteBuf::asByteBuf), data -> data.enabled);
    private static final StreamDataElementType<FriendlyByteBuf, ParticleData, TreeSet<ResourceLocation>> ALL_SELECTED =
        StreamDataElementType.create(
            "all_selected", ResourceLocation.CODEC.listOf().xmap(TreeSet::new, List::copyOf),
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list())
                .map(TreeSet::new, List::copyOf)
                .mapStream(FriendlyByteBuf::asByteBuf),
            data -> data.allSelected
        );
    private static final StreamDataElementType<FriendlyByteBuf, ParticleData, Boolean> USE_HSB = StreamDataElementType
        .create("use_hsb", Codec.BOOL, ByteBufCodecs.BOOL.mapStream(FriendlyByteBuf::asByteBuf), data -> data.useHSB);
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Integer> RGB_COLOR =
        new BoundedRangeStreamDataElementType<>(
            "rgb_color", Range.getCodec(Codec.INT), Range.getStreamCodec(ByteBufCodecs.INT), data -> data.rgbColor,
            0xFF000000, 0xFFFFFFFF
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> HUE =
        new BoundedRangeStreamDataElementType<>(
            "hue", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.hue, 0F, 1F
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> SATURATION =
        new BoundedRangeStreamDataElementType<>(
            "saturation", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT),
            data -> data.saturation, 0F, 1F
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> BRIGHTNESS =
        new BoundedRangeStreamDataElementType<>(
            "brightness", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT),
            data -> data.brightness, 0F, 1F
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Integer> LIFETIME =
        new BoundedRangeStreamDataElementType<>(
            "lifetime", Range.getCodec(Codec.INT), Range.getStreamCodec(ByteBufCodecs.INT), data -> data.lifetime,
            Constants.ParticleConstants.Values.MIN_LIFETIME, Constants.ParticleConstants.Values.MAX_LIFETIME
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> SIZE =
        new BoundedRangeStreamDataElementType<>(
            "size", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.size,
            Constants.ParticleConstants.Values.MIN_SIZE, Constants.ParticleConstants.Values.MAX_SIZE
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> SPAWN_X =
        new BoundedRangeStreamDataElementType<>(
            "spawn_x", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.spawnX,
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> SPAWN_Y =
        new BoundedRangeStreamDataElementType<>(
            "spawn_y", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.spawnY,
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> SPAWN_Z =
        new BoundedRangeStreamDataElementType<>(
            "spawn_z", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.spawnZ,
            Constants.ParticleConstants.Values.MIN_SPAWN, Constants.ParticleConstants.Values.MAX_SPAWN
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> MOTION_X =
        new BoundedRangeStreamDataElementType<>(
            "motion_x", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.motionX,
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> MOTION_Y =
        new BoundedRangeStreamDataElementType<>(
            "motion_y", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.motionY,
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION
        );
    private static final BoundedRangeStreamDataElementType<FriendlyByteBuf, ParticleData, Float> MOTION_Z =
        new BoundedRangeStreamDataElementType<>(
            "motion_z", Range.getCodec(Codec.FLOAT), Range.getStreamCodec(ByteBufCodecs.FLOAT), data -> data.motionZ,
            Constants.ParticleConstants.Values.MIN_MOTION, Constants.ParticleConstants.Values.MAX_MOTION
        );
    private static final BoundedStreamDataElementType<FriendlyByteBuf, ParticleData, Integer> DELAY =
        new BoundedStreamDataElementType<>(
            "delay", Codec.INT, ByteBufCodecs.INT.mapStream(FriendlyByteBuf::asByteBuf), data -> data.delay,
            Constants.ParticleConstants.Values.MIN_DELAY, Constants.ParticleConstants.Values.MAX_DELAY
        );
    private static final BoundedStreamDataElementType<FriendlyByteBuf, ParticleData, Float> GRAVITY =
        new BoundedStreamDataElementType<>(
            "gravity", Codec.FLOAT, ByteBufCodecs.FLOAT.mapStream(FriendlyByteBuf::asByteBuf), data -> data.gravity,
            Constants.ParticleConstants.Values.MIN_GRAVITY, Constants.ParticleConstants.Values.MAX_GRAVITY
        );
    private static final StreamDataElementType<FriendlyByteBuf, ParticleData, Boolean> COLLISION =
        StreamDataElementType.create(
            "collision", Codec.BOOL, ByteBufCodecs.BOOL.mapStream(FriendlyByteBuf::asByteBuf), data -> data.collision
        );
    private static final StreamDataElementType<FriendlyByteBuf, ParticleData, Boolean> FULL_BRIGHT =
        StreamDataElementType.create(
            "full_bright", Codec.BOOL, ByteBufCodecs.BOOL.mapStream(FriendlyByteBuf::asByteBuf), data -> data.fullBright
        );

    public static final Codec<Asymmetry<Consumer<ParticleData>, ParticleData>> FULL_CODEC = DataElementType.codec(
        true, ENABLED, ALL_SELECTED, USE_HSB, RGB_COLOR, HUE, SATURATION, BRIGHTNESS, LIFETIME, SIZE, SPAWN_X, SPAWN_Y,
        SPAWN_Z, MOTION_X, MOTION_Y, MOTION_Z, DELAY, GRAVITY, COLLISION, FULL_BRIGHT
    );
    public static final Codec<Asymmetry<Consumer<ParticleData>, ParticleData>> DIFF_CODEC = DataElementType.codec(
        false, ENABLED, ALL_SELECTED, USE_HSB, RGB_COLOR, HUE, SATURATION, BRIGHTNESS, LIFETIME, SIZE, SPAWN_X, SPAWN_Y,
        SPAWN_Z, MOTION_X, MOTION_Y, MOTION_Z, DELAY, GRAVITY, COLLISION, FULL_BRIGHT
    );
    public static final StreamCodec<FriendlyByteBuf, Asymmetry<Consumer<ParticleData>, ParticleData>> DIFF_STREAM_CODEC =
        StreamDataElementType.streamCodec(
            false, ENABLED, ALL_SELECTED, USE_HSB, RGB_COLOR, HUE, SATURATION, BRIGHTNESS, LIFETIME, SIZE, SPAWN_X,
            SPAWN_Y, SPAWN_Z, MOTION_X, MOTION_Y, MOTION_Z, DELAY, GRAVITY, COLLISION, FULL_BRIGHT
        );

    public final OwnedDataElement<Boolean> enabled;
    public final OwnedDataElement<TreeSet<ResourceLocation>> allSelected;
    public final OwnedDataElement<Boolean> useHSB;
    public final OwnedDataElement.BoundedRange<Integer> rgbColor;
    public final OwnedDataElement.BoundedRange<Float> hue;
    public final OwnedDataElement.BoundedRange<Float> saturation;
    public final OwnedDataElement.BoundedRange<Float> brightness;
    public final OwnedDataElement.BoundedRange<Integer> lifetime;
    public final OwnedDataElement.BoundedRange<Float> size;
    public final OwnedDataElement.BoundedRange<Float> spawnX;
    public final OwnedDataElement.BoundedRange<Float> spawnY;
    public final OwnedDataElement.BoundedRange<Float> spawnZ;
    public final OwnedDataElement.BoundedRange<Float> motionX;
    public final OwnedDataElement.BoundedRange<Float> motionY;
    public final OwnedDataElement.BoundedRange<Float> motionZ;
    public final OwnedDataElement.Bounded<Integer> delay;
    public final OwnedDataElement.Bounded<Float> gravity;
    public final OwnedDataElement<Boolean> collision;
    public final OwnedDataElement<Boolean> fullBright;

    public ParticleData(BlockEntity owner) {
        this.enabled = new OwnedDataElement<>(true, owner);
        this.allSelected = new OwnedDataElement<>(
            Util.getThreeRandomElements(
                DataManager.selectableParticles().keySet(), ResourceLocation::compareNamespaced
            ), owner
        );
        this.useHSB = new OwnedDataElement<>(false, owner);
        this.rgbColor = new OwnedDataElement.BoundedRange<>(new Range<>(0xFF000000, 0xFFFFFFFF), owner, RGB_COLOR);
        this.hue = new OwnedDataElement.BoundedRange<>(new Range<>(0F, 1F), owner, HUE);
        this.saturation = new OwnedDataElement.BoundedRange<>(new Range<>(0F, 1F), owner, SATURATION);
        this.brightness = new OwnedDataElement.BoundedRange<>(new Range<>(0F, 1F), owner, BRIGHTNESS);
        this.lifetime = new OwnedDataElement.BoundedRange<>(new Range<>(20, 80), owner, LIFETIME);
        this.size = new OwnedDataElement.BoundedRange<>(new Range<>(1F, 3F), owner, SIZE);
        this.spawnX = new OwnedDataElement.BoundedRange<>(new Range<>(-1F, 1F), owner, SPAWN_X);
        this.spawnY = new OwnedDataElement.BoundedRange<>(new Range<>(0F, 0F), owner, SPAWN_Y);
        this.spawnZ = new OwnedDataElement.BoundedRange<>(new Range<>(-1F, 1F), owner, SPAWN_Z);
        this.motionX = new OwnedDataElement.BoundedRange<>(new Range<>(-0.1F, 0.1F), owner, MOTION_X);
        this.motionY = new OwnedDataElement.BoundedRange<>(new Range<>(0.1F, 0.1F), owner, MOTION_Y);
        this.motionZ = new OwnedDataElement.BoundedRange<>(new Range<>(-0.1F, 0.1F), owner, MOTION_Z);
        this.delay = new OwnedDataElement.Bounded<>(5, owner, DELAY);
        this.gravity = new OwnedDataElement.Bounded<>(0F, owner, GRAVITY);
        this.collision = new OwnedDataElement<>(false, owner);
        this.fullBright = new OwnedDataElement<>(false, owner);
    }

    public Tag saveToTag() {
        return FULL_CODEC.encodeStart(NbtOps.INSTANCE, Asymmetry.ofEncoding(this))
            .resultOrPartial(VFXGenerator.LOGGER::error)
            .orElse(EndTag.INSTANCE);
    }

    public void loadFromTag(Tag tag) {
        FULL_CODEC.parse(NbtOps.INSTANCE, tag)
            .flatMap(Asymmetry::decoding)
            .resultOrPartial(VFXGenerator.LOGGER::error)
            .ifPresent(consumer -> consumer.accept(this));
    }
}
