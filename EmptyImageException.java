public class EmptyImageException extends SpriteSheetCreatorException {
    public EmptyImageException(String name) {
        super(String.format("Image \"%s\" is empty.", name));
    }
}
