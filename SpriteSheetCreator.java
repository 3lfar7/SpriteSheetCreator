import bin.Bin;
import bin.Data;
import bin.InputImage;
import bin.ShelfBin.ShelfBin;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SpriteSheetCreator {
    public final static int MAX_BIN_WIDTH = 1024;
    public final static int MAX_BIN_HEIGHT = 1024;

    private File mInput, mOutput;

    public SpriteSheetCreator(String inputPath, String outputPath) throws SpriteSheetCreatorException {
        mInput = new File(inputPath);
        mOutput = new File(outputPath);
        if (!mInput.exists()) {
            throw new SpriteSheetCreatorException(String.format("Path \"%s\" does not exists.", mInput.getPath()));
        }
        if (!mInput.isDirectory()) {
            throw new SpriteSheetCreatorException(String.format("Path \"%s\" is not a directory.", mInput.getPath()));
        }
        File parent = mOutput.getParentFile();
        if (parent != null && !parent.exists()) {
            throw new SpriteSheetCreatorException(String.format("Path \"%s\" does not exists.", parent.getPath()));
        }
    }

    private void load(File directory, List<InputImage> images, ImageReader imageReader, int[] maxImageWidth, int[] maxImageHeight, int[] imageWidthSum) throws IOException, SpriteSheetCreatorException {
        File[] files = directory.listFiles();
        if (files == null) throw new IOException();
        for (File file : files) {
            if (file.isDirectory()) {
                load(file, images, imageReader, maxImageWidth, maxImageHeight, imageWidthSum);
            } else {
                imageReader.setInput(ImageIO.createImageInputStream(file));
                InputImage image;
                try {
                    image = new InputImage(imageReader.read(0), mInput.getPath(), file.getPath());
                } catch (IIOException e) {
                    continue;
                }
                if (image.getWidth() == 0 && image.getHeight() == 0) {
                    throw new EmptyImageException(file.getPath());
                }
                if (image.getWidth() > maxImageWidth[0]) {
                    maxImageWidth[0] = image.getWidth();
                    if (maxImageWidth[0] > MAX_BIN_WIDTH) {
                        throw new BigImageSizeException(file.getPath(), "width", maxImageWidth[0], MAX_BIN_WIDTH);
                    }
                }
                if (image.getHeight() > maxImageHeight[0]) {
                    maxImageHeight[0] = image.getHeight();
                    if (maxImageHeight[0] > MAX_BIN_HEIGHT) {
                        throw new BigImageSizeException(file.getPath(), "height", maxImageHeight[0], MAX_BIN_HEIGHT);
                    }
                }
                imageWidthSum[0] += image.getWidth();
                images.add(image);
            }
        }
    }

    private void load(List<InputImage> images, int[] maxImageWidth, int[] imageWidthSum, int[] step) throws IOException, SpriteSheetCreatorException {
        ImageIO.setUseCache(false);
        ImageReader imageReader = ImageIO.getImageReadersBySuffix("png").next();
        load(mInput, images, imageReader, maxImageWidth, new int[1], imageWidthSum);
        if (images.size() == 0) throw new ImagesNotFoundException();
        int imageWidth = images.get(0).getWidth();
        if ((double)imageWidthSum[0] / images.size() == imageWidth)  {
            step[0] = imageWidth;
        } else {
            step[0] = 1;
        }
    }

    private void getValues(List<InputImage> images, int[] maxImageWidth, int[] imageWidthSum, int[] step) {
        for (InputImage image : images) {
            imageWidthSum[0] += image.getWidth();
            if (image.getWidth() > maxImageWidth[0])
                maxImageWidth[0] = image.getWidth();
        }
        int imageWidth = images.get(0).getWidth();
        if ((double)imageWidthSum[0] / images.size() == imageWidth) {
            step[0] = imageWidth;
        } else {
            step[0] = 1;
        }
    }

    private void pack(List<InputImage> images, List<Bin> bins, int maxImageWidth, int imageWidthSum, int step) {
        Bin bestBin = null;
        int maxBinWidth = imageWidthSum < MAX_BIN_WIDTH ? imageWidthSum : MAX_BIN_WIDTH;
        for (int maxWidth = maxImageWidth; maxWidth <= maxBinWidth; maxWidth += step) {
//            System.out.println(maxWidth);
            Bin bin = new ShelfBin(maxWidth, MAX_BIN_HEIGHT);
            bin.pack(images);
            if (bestBin == null
                    || bin.getSumOfSquares() > bestBin.getSumOfSquares()
                    || (bin.getSumOfSquares() == bestBin.getSumOfSquares()
                    && bin.getPowerOfTwoSquare() < bestBin.getPowerOfTwoSquare())) {
                bestBin = bin;
            }
        }
        bins.add(bestBin);
        if (bestBin.getUnpackedImages().size() > 0) {
            int[] newMaxImageWidth = new int[1], newImageWidthSum = new int[1], newStep = new int[1];
            getValues(bestBin.getUnpackedImages(), newMaxImageWidth, newImageWidthSum, newStep);
            pack(bestBin.getUnpackedImages(), bins, newMaxImageWidth[0], newImageWidthSum[0], newStep[0]);
        }
    }

    public void create() throws IOException, SpriteSheetCreatorException {
        class Item {
            private File file;
            private Data data;
        }
        List<InputImage> images = new LinkedList<InputImage>();
        int[] maxImageWidth = new int[1], imageWidthSum = new int[1], step = new int[1];
        load(images, maxImageWidth, imageWidthSum, step);
        List<Bin> bins = new LinkedList<Bin>();
        pack(images, bins, maxImageWidth[0], imageWidthSum[0], step[0]);
        int capacity = (bins.size() - 1) + 2;
        List<Item> items = new LinkedList<Item>();
        int i = 0;
        for (Bin bin : bins) {
            Item item = new Item();
            if (bins.size() == 1) {
                item.file = new File(mOutput.getPath() + ".png");
            } else {
                item.file = new File(String.format("%s_%d.png", mOutput.getPath(), i));
            }
            item.data = bin.getData(item.file.getPath());
            capacity += item.data.getJson().length();
            items.add(item);
            i++;
        }
        StringBuilder json = new StringBuilder(capacity);
        json.append('{');
        i = 0;
        for (Item item : items) {
            ImageIO.write(item.data.getImage(), "png", item.file);
            json.append(item.data.getJson());
            if (i < items.size() - 1) json.append(',');
            i++;
        }
        json.append('}');
        FileWriter out = null;
        try {
            out = new FileWriter(mOutput + ".json");
            out.write(json.toString());
        } finally {
            if (out != null) out.close();
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            if (args.length != 2) {
                throw new SpriteSheetCreatorException("Wrong syntax.");
            }
            SpriteSheetCreator spriteSheetCreator = new SpriteSheetCreator(args[0], args[1]);
            spriteSheetCreator.create();
        } catch (SpriteSheetCreatorException e) {
            System.out.print(e.getMessage());
        }
    }
}
