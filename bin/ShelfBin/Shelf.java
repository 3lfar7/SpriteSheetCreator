package bin.ShelfBin;

import bin.InputImage;
import bin.PackedImage;

import java.util.Iterator;
import java.util.LinkedList;

public class Shelf implements Iterable<PackedImage> {
    private int mWidth, mHeight, mY, mFreeSquare, mCeilFreeWidth, mFloorFreeWidth;
    private LinkedList<PackedImage> mLastPlace;
    private LinkedList<PackedImage> mCeil = new LinkedList<PackedImage>();
    private LinkedList<PackedImage> mFloor = new LinkedList<PackedImage>();

    public Shelf(int width, int y) {
        mWidth = width;
        mCeilFreeWidth = width;
        mY = y;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        if (mFloor.size() == 0) {
            return mWidth - mCeilFreeWidth;
        }
        return mWidth;
    }

    public int getFreeSquare() {
        if (mHeight == 0) throw new IllegalStateException();
        return mFreeSquare;
    }

    private boolean addToCeil(InputImage image) {
        int ceilFreeWidth = mCeilFreeWidth - image.getWidth();
        if (ceilFreeWidth >= 0) {
            mCeil.add(new PackedImage(image, mWidth - mCeilFreeWidth, mY));
            mCeilFreeWidth = ceilFreeWidth;
            mLastPlace = mCeil;
            return true;
        }
        return false;
    }

    private boolean addToFloor(InputImage image) {
        int floorFreeWidth = mFloorFreeWidth - image.getWidth();
        if (floorFreeWidth >= 0) {
            int x = mFloor.size() == 0 ? x = mWidth - image.getWidth() : mFloor.getLast().getX() - image.getWidth();
            boolean found = false, bigHeight = false;
            for (Iterator<PackedImage> iterator = mCeil.descendingIterator(); iterator.hasNext(); ) {
                PackedImage ceilImage = iterator.next();
                if (x <= ceilImage.getX() + ceilImage.getWidth() && x + image.getWidth() >= ceilImage.getX()) {
                    found = true;
                    if (image.getHeight() + ceilImage.getHeight() > mHeight) bigHeight = true;
                } else if (found) break;
            }
            if (!bigHeight) {
                mFloor.add(new PackedImage(image, x, mY + mHeight - image.getHeight()));
                mFloorFreeWidth = floorFreeWidth;
                mLastPlace = mFloor;
                return true;
            }
        }
        return false;
    }

    public boolean add(InputImage image) {
        boolean success = false;
        if (mHeight == 0) {
            success = addToCeil(image);
            if (success) {
                mHeight = image.getHeight();
                mFloorFreeWidth = mCeilFreeWidth;
                mFreeSquare = mWidth * mHeight;
            }
        } else if (mHeight >= image.getHeight()) {
            success = addToCeil(image);
            if (!success) success = addToFloor(image);
        }
        if (success) mFreeSquare -= image.getWidth() * image.getHeight();
        return success;
    }

    public boolean undoAdd() {
        if (mLastPlace != null) {
            PackedImage image = mLastPlace.removeLast();
            if (mLastPlace == mCeil) {
                if (mCeil.size() == 0) mHeight = 0;
                mCeilFreeWidth += image.getWidth();
            } else {
                mFloorFreeWidth += image.getWidth();
            }
            mFreeSquare += image.getWidth() * image.getHeight();
            mLastPlace = null;
            return true;
        }
        return false;
    }

    public int getCount() {
        return mCeil.size() + mFloor.size();
    }

    public Iterator<PackedImage> iterator() {
        return new ShelfIterator(mCeil.iterator(), mFloor.iterator());
    }
}
