package bin;

public class PackedImage {
    private int mX, mY;
    private InputImage mInputImage;

    public PackedImage(InputImage inputImage, int x, int y) {
        if (inputImage == null) {
            throw new NullPointerException();
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        mInputImage = inputImage;
        mX = x;
        mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getWidth() {
        return mInputImage.getWidth();
    }

    public int getHeight() {
        return mInputImage.getHeight();
    }

    public InputImage getInputImage() {
        return mInputImage;
    }

    public String getJson(String imagePath) {
        if (imagePath == null) {
            throw new NullPointerException();
        }
        return String.format("{\"imagePath\":\"%s\",\"x\":%d,\"y\":%d,\"width\":%d,\"height\":%d,\"offsetX\":%d,\"offsetY\":%d,\"fullWidth\":%d,\"fullHeight\":%d}",
                imagePath, getX(), getY(),
                getInputImage().getWidth(), getInputImage().getHeight(),
                getInputImage().getOffsetX(), getInputImage().getOffsetY(),
                getInputImage().getFullWidth(), getInputImage().getFullHeight());
    }
}
