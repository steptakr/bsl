package nl.debijenkorf.bsl.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for image resizer service. It contains list of image definitions and their properties.
 *
 * Created by Daniel on 18/04/15.
 */
public interface ImageResizerService {

    void process (ImageProfile profile, InputStream image, OutputStream os) throws IOException;

    enum ImageProfile {

        thumbnail (160, 160, 99, ScalingStrategy.FILL, 0xFFF0FFF0),
        detail_small (400, 400, 95, ScalingStrategy.FILL, 0xFFF0FFF0),
        detail_large (800, 800, 90, ScalingStrategy.FILL, 0xFFF0FFF0),
        original (0, 0, -1, null, null);

        public final int height;
        public final int width;
        public final int quality;
        public final ScalingStrategy scalingStrategy;
        public final Integer fill;

        ImageProfile (int height, int width, int quality, ScalingStrategy scalingStrategy, Integer fill) {
            this.height = height;
            this.width = width;
            this.quality = quality;
            this.scalingStrategy = scalingStrategy;
            this.fill = fill;
        }
        }

    enum ScalingStrategy {
        CROP,
        FILL,
        SKEW
    }
}
