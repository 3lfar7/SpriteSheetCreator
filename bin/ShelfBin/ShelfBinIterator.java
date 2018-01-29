package bin.ShelfBin;

import bin.PackedImage;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShelfBinIterator implements Iterator<PackedImage> {
    private Iterator<Shelf> mShelfIterator;
    private Iterator<PackedImage> mImagesIterator;
    private Shelf mShelf;

    public ShelfBinIterator(Iterator<Shelf> shelfIterator) {
        if (shelfIterator == null) {
            throw new NullPointerException();
        }
        mShelfIterator = shelfIterator;
    }

    private Iterator<PackedImage> getImagesIterator() {
        if ((mImagesIterator == null || !mImagesIterator.hasNext()) && mShelfIterator.hasNext()) {
            mShelf = mShelfIterator.next();
            mImagesIterator = mShelf.iterator();
        } else return mImagesIterator;
        return getImagesIterator();
    }

    public boolean hasNext() {
        Iterator<PackedImage> imagesIterator = getImagesIterator();
        if (imagesIterator == null) {
            return false;
        }
        return imagesIterator.hasNext();
    }

    public PackedImage next() {
        Iterator<PackedImage> imagesIterator = getImagesIterator();
        if (imagesIterator == null) {
            throw new NoSuchElementException();
        }
        return imagesIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
