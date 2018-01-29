public class BigImageSizeException extends SpriteSheetCreatorException {
    public BigImageSizeException(String name, String measure, int value, int maxValue) {
        super(String.format("Image \"%s\" %s %dpx more than %dpx.", name, measure, value, maxValue));
    }
}
