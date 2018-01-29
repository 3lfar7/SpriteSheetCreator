package bin.ShelfBin;

import bin.PackedImage;

import java.util.Iterator;

public class ShelfIterator implements Iterator<PackedImage> {
    private Iterator<PackedImage> mIterator, mFloorIterator;

    public ShelfIterator(Iterator<PackedImage> ceilIterator, Iterator<PackedImage> floorIterator) {
        mIterator = ceilIterator;
        mFloorIterator = floorIterator;
    }

    private Iterator<PackedImage> getIterator() {
        if (mIterator != mFloorIterator && !mIterator.hasNext()) {
            mIterator = mFloorIterator;
        }
        return mIterator;
    }

    public boolean hasNext() {
        return getIterator().hasNext();
    }

    public PackedImage next() {
        return getIterator().next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
