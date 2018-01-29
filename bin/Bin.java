package bin;

import java.util.List;

public interface Bin extends Iterable<PackedImage> {
    int getWidth();
    int getHeight();
    int getPowerOfTwoWidth();
    int getPowerOfTwoHeight();
    int getSumOfSquares();
    int getPowerOfTwoSquare();
    int getSquare();
    int getCount();
    void pack(List<InputImage> images);
    List<InputImage> getUnpackedImages();
    Data getData(String imagePath);
}
