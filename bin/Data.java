package bin;

import java.awt.image.RenderedImage;

public class Data {
    private RenderedImage mImage;
    private String mJson;

    public Data(RenderedImage image, String json) {
        mImage = image;
        mJson = json;
    }

    public RenderedImage getImage() {
        return mImage;
    }

    public String getJson() {
        return mJson;
    }
}
