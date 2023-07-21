package beanvest.lib.testing.asserts;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


public record AssertImage(String path) {
    public static AssertImage assertThatImage(String path) {
        return new AssertImage(path);
    }

    public AssertImage hasSameDimensionsAs(String imagePath) {
        try {
            BufferedImage comparedTo = ImageIO.read(new File(imagePath));
            BufferedImage actual = ImageIO.read(new File(path));
            assertThat(dimensionsAsString(actual))
                    .as("Dimensions of images")
                    .isEqualTo(dimensionsAsString(comparedTo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void isWiderThan(String imagePath) {
        try {
            BufferedImage comparedTo = ImageIO.read(new File(imagePath));
            BufferedImage actual = ImageIO.read(new File(path));
            assertThat(actual.getWidth())
                    .as("width of the image")
                    .isGreaterThan(comparedTo.getWidth());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AssertImage exists() {
         assertThat(Path.of(path)).exists();
         return this;
    }

    public void weightsAtLeastAsMuchAs(String filePath) {
        try {
            var actualSize = Files.size(Path.of(path));
            var expectedMinSize = Files.size(Path.of(filePath));
            assertThat(actualSize).as("file size")
                    .isGreaterThanOrEqualTo(expectedMinSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String dimensionsAsString(BufferedImage myPicture) {
        return String.format("%dx%d", myPicture.getWidth(), myPicture.getHeight());
    }
}
