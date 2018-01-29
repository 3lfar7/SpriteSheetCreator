package bin;

import java.awt.image.BufferedImage;
import java.io.File;

public class InputImage {
    private BufferedImage mBufferedImage;
    private String mName;
    private int mOffsetX, mOffsetY, mWidth, mHeight;

    public InputImage(BufferedImage bufferedImage, String prefix, String path) {
        if (bufferedImage == null || prefix == null || path == null) {
            throw new NullPointerException();
        }
        mBufferedImage = bufferedImage;
        createName(prefix, path);
        trim();
    }

    private void createName(String prefix, String path) {
        if (path.length() - prefix.length() < 2 || !path.startsWith(prefix)) {
            throw new IllegalArgumentException();
        }
        mName = path.substring(prefix.length() + 1);
        int dotIndex = mName.lastIndexOf('.');
        if (dotIndex > -1) {
            mName = mName.substring(0, dotIndex);
        }
        if (File.separatorChar != '/') {
            mName = mName.replace(File.separatorChar, '/');
        }
    }

    private void trim() {
//        Left
        boolean end = false;
        for (int x = 0; x < mBufferedImage.getWidth(); x++) {
            for (int y = 0; y < mBufferedImage.getHeight(); y++) {
                if (mBufferedImage.getRGB(x, y) >> 24 != 0) {
                    end = true;
                    break;
                }
            }
            if (!end) {
                mOffsetX++;
            } else break;
        }
//        Top
        end = false;
        for (int y = 0; y < mBufferedImage.getHeight(); y++) {
            for (int x = mOffsetX; x < mBufferedImage.getWidth(); x++) {
                if (mBufferedImage.getRGB(x, y) >> 24 != 0) {
                    end = true;
                    break;
                }
            }
            if (!end) {
                mOffsetY++;
            } else break;
        }
//        Right
        end = false;
        mWidth = mBufferedImage.getWidth() - mOffsetX;
        for (int x = mBufferedImage.getWidth() - 1; x > mOffsetX; x--) {
            for (int y = mOffsetY; y < mBufferedImage.getHeight(); y++) {
                if (mBufferedImage.getRGB(x, y) >> 24 != 0) {
                    end = true;
                    break;
                }
            }
            if (!end) {
                mWidth--;
            } else break;
        }
//        Bottom
        end = false;
        mHeight = mBufferedImage.getHeight() - mOffsetY;
        for (int y = mBufferedImage.getHeight() - 1; y > mOffsetY; y--) {
            for (int x = mOffsetX; x < mBufferedImage.getWidth(); x++) {
                if (mBufferedImage.getRGB(x, y) >> 24 != 0) {
                    end = true;
                    break;
                }
            }
            if (!end) {
                mHeight--;
            } else break;
        }
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public int getFullWidth() {
        return mBufferedImage.getWidth();
    }

    public int getFullHeight() {
        return mBufferedImage.getHeight();
    }

    public String getName() {
        return mName;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getRGB(int x, int y) {
        return mBufferedImage.getRGB(getOffsetX() + x, getOffsetY() + y);
    }
}
