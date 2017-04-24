package de.htw.mp.ui.controller;

import de.htw.mp.model.FeatureContainer;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * DatasetViewer: Categorizes and lists all image files in a directory.
 * The controller handles all events of the DatasetViewer.fxml view.
 *
 * @author Nico Hezel
 */
public class DatasetViewerController extends DatasetViewerBase {

    /**
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
                return Color.rgb(red, green, blue);
            }
        }

        return Color.PINK;
    }

    /**
     * @param imageFiles
     * @return
     */
    public BufferedImage getMeanImage(Path... imageFiles) {
        if (imageFiles.length >= 0) {
            List<BufferedImage> imageList = new ArrayList<>();

            for (Path imageFile : imageFiles) {
                try {
                    imageList.add(ImageIO.read(imageFile.toFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int width = imageList.get(0).getWidth();
            int height = imageList.get(0).getHeight();

            BufferedImage resultImage = new BufferedImage(width, height, TYPE_INT_RGB);
            int red, green, blue;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    red = 0;
                    green = 0;
                    blue = 0;

                    for (BufferedImage image : imageList) {
                        red += (image.getRGB(x, y) >> 16) & 0xff;
                        green += (image.getRGB(x, y) >> 8) & 0xff;
                        blue += image.getRGB(x, y) & 0xff;
                    }
                    red = red / imageList.size();
                    blue = blue / imageList.size();
                    green = green / imageList.size();
                    resultImage.setRGB(x, y, (red << 16) | (green << 8) | blue);
                }
            }

            return resultImage;
        }
        
        return null;
    }

    /**
     * TODO Sort the elements in the database based on the similarity to the search query.
     * The similarity will be calculated between to features. Features are are stored in
     * the FeatureContainer and the FeatureType specifies which feature should be used.
     *
     * @param query
     * @param database
     * @param featureType
     * @return sorted list of database elements
     */
    public List<FeatureContainer> retrieve(FeatureContainer query, FeatureContainer[] database, FeatureType
            featureType) {
        return Arrays.stream(database).collect(Collectors.toList());
    }

    /**
     * TODO Predict the category.
     * Make the prediction based on the sorted list of features (images or categories).
     *
     * @param sortedList
     * @param k
     * @return predicted category
     */

    public String classify(List<FeatureContainer> sortedList, int k) {
        return sortedList.get(0).getCategory();
    }
}