package bin.ShelfBin;

import bin.AbstractBin;
import bin.InputImage;
import bin.PackedImage;

import java.util.*;

public class ShelfBin extends AbstractBin {
    private List<Shelf> mShelves = new LinkedList<Shelf>();
    private int mWidth, mHeight, mSumOfSquares, mCount;
    private List<InputImage> mUnpackedImages = new LinkedList<InputImage>();

    public ShelfBin(int maxWidth, int maxHeight) {
        super(maxWidth, maxHeight);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    private InputImage[] prepareImages(List<InputImage> images) {
        InputImage[] preparedImages = images.toArray(new InputImage[images.size()]);
        Arrays.sort(preparedImages, new Comparator<InputImage>() {
            @Override
            public int compare(InputImage o1, InputImage o2) {
                Integer h1 = o1.getHeight();
                Integer h2 = o2.getHeight();
                int result = -h1.compareTo(h2);
                if (result == 0) {
                    Integer w1 = o1.getWidth();
                    Integer w2 = o2.getWidth();
                    result = -w1.compareTo(w2);
                }
                return result;
            }
        });
        return preparedImages;
    }

    public void pack(List<InputImage> images) {
        mCount = 0;
        mSumOfSquares = 0;
        mUnpackedImages.clear();
        for (InputImage image : prepareImages(images)) {
            if (image.getWidth() > getMaxWidth()) {
                mUnpackedImages.add(image);
                continue;
            }
            int minFreeSquare = -1;
            Shelf bestShelf = null;
            for (Shelf shelf : mShelves) {
                if (shelf.add(image)) {
                    if (minFreeSquare == -1 || minFreeSquare > shelf.getFreeSquare()) {
                        minFreeSquare = shelf.getFreeSquare();
                        if (bestShelf != null) bestShelf.undoAdd();
                        bestShelf = shelf;
                    } else shelf.undoAdd();
                }
            }
            if (bestShelf == null) {
                if (getHeight() + image.getHeight() > getMaxHeight()) {
                    mUnpackedImages.add(image);
                    continue;
                }
                bestShelf = new Shelf(getMaxWidth(), mHeight);
                bestShelf.add(image);
                mShelves.add(bestShelf);
                mHeight += bestShelf.getHeight();
            }
            mCount++;
            mSumOfSquares += image.getWidth() * image.getHeight();
            if (bestShelf.getWidth() > mWidth) {
                mWidth = bestShelf.getWidth();
            }
        }
    }

    public int getCount() {
        return mCount;
    }

    public int getSumOfSquares() {
        return mSumOfSquares;
    }

    public List<InputImage> getUnpackedImages() {
        return mUnpackedImages;
    }

    public Iterator<PackedImage> iterator() {
        return new ShelfBinIterator(mShelves.iterator());
    }
}
