package platinpython.vfxgenerator.util.data;

import com.google.common.collect.ImmutableList;
import dev.lukebemish.codecextras.mutable.DataElement;
import net.minecraft.world.level.block.entity.BlockEntity;
import platinpython.vfxgenerator.util.Util;

import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class OwnedDataElement<T> extends DataElement.Simple<T> {
    private final BlockEntity owner;

    public OwnedDataElement(T value, BlockEntity owner) {
        super(value);
        this.owner = owner;
    }

    @Override
    public synchronized void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (dirty) {
            this.owner.setChanged();
        }
    }

    public static class Viewable<E, T extends Collection<E>> extends OwnedDataElement<T> {
        private ImmutableList<E> view;

        public Viewable(T value, BlockEntity owner) {
            super(value, owner);
            this.view = ImmutableList.copyOf(value);
        }

        @Override
        public synchronized void set(T value) {
            super.set(value);
            this.view = ImmutableList.copyOf(value);
        }

        public ImmutableList<E> getView() {
            return this.view;
        }
    }

    public static class Bounded<T extends Comparable<T>> extends OwnedDataElement<T> {
        private final BoundedStreamDataElementType<?, ?, T> type;

        public Bounded(T value, BlockEntity owner, BoundedStreamDataElementType<?, ?, T> type) {
            super(value, owner);
            this.type = type;
        }

        @Override
        public synchronized void set(T value) {
            super.set(Util.clamp(value, type.min(), type.max()));
        }
    }

    public static class BoundedRange<T extends Comparable<T>> extends OwnedDataElement<Range<T>> {
        private final BoundedRangeStreamDataElementType<?, ?, T> type;

        public BoundedRange(Range<T> value, BlockEntity owner, BoundedRangeStreamDataElementType<?, ?, T> type) {
            super(value, owner);
            this.type = type;
        }

        @Override
        public synchronized void set(Range<T> value) {
            super.set(value.clamp(type.min(), type.max()));
        }
    }
}
