package bin;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractBin implements Bin {
    private int mMaxWidth, mMaxHeight;

    public AbstractBin(int maxWidth, int maxHeight) {
        if (maxWidth < 1 || maxHeight < 1) {
            throw new IllegalArgumentException();
        }
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public int getPowerOfTwoWidth() {
        return (int)Math.pow(2, Math.ceil(Math.log(getWidth()) / Math.log(2)));
    }

    public int getPowerOfTwoHeight() {
        return (int)Math.pow(2, Math.ceil(Math.log(getHeight()) / Math.log(2)));
    }

    public int getPowerOfTwoSquare() {
        return getPowerOfTwoWidth() * getPowerOfTwoHeight();
    }

    public int getSquare() {
        return getWidth() * getHeight();
    }

    public Data getData(String imagePath) {
        class Item {
            private InputImage image;
            private String json;
        }
        int capacity = getCount() * 4 - 1;
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        List<Item> items = new LinkedList<Item>();
        for (PackedImage packedImage : this) {
            Item item = new Item();
            InputImage inputImage = item.image = packedImage.getInputImage();
            item.json = packedImage.getJson(imagePath);
            capacity += item.image.getName().length() + item.json.length();
            items.add(item);
            for (int x = 0; x < inputImage.getWidth(); x++) {
                for (int y = 0; y < inputImage.getHeight(); y++) {
                    image.setRGB(packedImage.getX() + x, packedImage.getY() + y, inputImage.getRGB(x, y));
                }
            }
        }
        StringBuilder json = new StringBuilder(capacity);
        int i = 0;
        for (Item item : items) {
            json.append('"');
            json.append(item.image.getName());
            json.append("\":");
            json.append(item.json);
            if (i < getCount() - 1) json.append(',');
            i++;
        }
        return new Data(image, json.toString());
    }
}
