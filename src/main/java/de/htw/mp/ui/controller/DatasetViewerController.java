package de.htw.mp.ui.controller;

import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * DatasetViewer: Categorizes and lists all image files in a directory.
 * The controller handles all events of the DatasetViewer.fxml view.
 * *
 *
 * @author Nico Hezel
 */
public class DatasetViewerController extends DatasetViewerBase {

    /**
     * TODO Calculate the mean color of all given images. Or return PINK if there are no images.
     *
     * @param imageFiles
     * @return
     */
    public Color getMeanColor(Path... imageFiles) {

        if (imageFiles.length >= 0) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(imageFiles[0].toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (image != null) {
                int red = 0;
                int green = 0;
                int blue = 0;
                int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

                for (int rgb : pixels) {
                    red += (rgb >> 16) & 0xff;
                    green += (rgb >> 8) & 0xff;
                    blue += rgb & 0xff;
                }
                red = red / pixels.length;
                green = green / pixels.length;
                blue = blue / pixels.length;

                System.out.println(String.format("r: %s, g: %s, b: %s", red, green, blue));
                return Color.rgb(red, green, blue);
            }
        }

        return Color.PINK;
    }

    /**
     * TODO Calculate the mean images of all given images. Or return NULL if there are no images.
     *
     * @param imageFiles
     * @return
     */
    public BufferedImage getMeanImage(Path... imageFiles) {
        
        // no images? return null
        if (imageFiles.length == 0) return null;

        return null;
    }
}
