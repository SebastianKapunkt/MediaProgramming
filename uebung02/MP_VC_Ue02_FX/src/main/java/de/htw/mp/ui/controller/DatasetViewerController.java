package de.htw.mp.ui.controller;

import de.htw.mp.model.FeatureContainer;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
        switch (featureType) {
            case MeanColor:
                return retrieveMeanColor(query, database);
            case MeanImage:
                return retrieveMeanImage(query, database);
            default:
                return Arrays.stream(database).collect(Collectors.toList());
        }
    }

    private List<FeatureContainer> retrieveMeanImage(FeatureContainer query, FeatureContainer[] database) {
        int width = query.getMeanImage().getWidth();
        int height = query.getMeanImage().getHeight();
        int[][] meanImagePixel = new int[width * height][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                meanImagePixel[y * width + x][0] = (query.getMeanImage().getRGB(x, y) >> 16) & 0xff;
                meanImagePixel[y * width + x][1] = (query.getMeanImage().getRGB(x, y) >> 8) & 0xff;
                meanImagePixel[y * width + x][2] = query.getMeanImage().getRGB(x, y) & 0xff;
            }
        }
        // [pixel position][0 = red 1 = green 2 = blue]

        double[] rgbQ = getRgbFromMeanColor(query);

        System.out.println(String.format("r: %s g: %s b:%s", rgbQ[0], rgbQ[1], rgbQ[2]));

        return Arrays.stream(database)
                .filter(image -> !image.getName().equals(query.getName()))
                .sorted((o1, o2) -> {
                    double featureValueO1 = 0;
                    double featureValueO2 = 0;

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            featureValueO1 = featureValueO1
                                    + Math.abs(meanImagePixel[y * width + x][0] - ((o1.getMeanImage().getRGB(x, y) >> 16) & 0xff))
                                    + Math.abs(meanImagePixel[y * width + x][1] - ((o1.getMeanImage().getRGB(x, y) >> 8) & 0xff))
                                    + Math.abs(meanImagePixel[y * width + x][2] - (o1.getMeanImage().getRGB(x, y) & 0xff));

                            featureValueO2 = featureValueO2
                                    + Math.abs(meanImagePixel[y * width + x][0] - ((o2.getMeanImage().getRGB(x, y) >> 16) & 0xff))
                                    + Math.abs(meanImagePixel[y * width + x][1] - ((o2.getMeanImage().getRGB(x, y) >> 8) & 0xff))
                                    + Math.abs(meanImagePixel[y * width + x][2] - (o2.getMeanImage().getRGB(x, y) & 0xff));
                        }
                    }

                    return calculateSort(featureValueO1, featureValueO2);
                })
                .collect(Collectors.toList());
    }

    private List<FeatureContainer> retrieveMeanColor(FeatureContainer query, FeatureContainer[] database) {
        double[] rgbQ = getRgbFromMeanColor(query);

        System.out.println(String.format("r: %s g: %s b:%s", rgbQ[0], rgbQ[1], rgbQ[2]));

        return Arrays.stream(database)
                .filter(image -> !image.getName().equals(query.getName()))
                .sorted((o1, o2) -> {
                    double[] rgbO1 = getRgbFromMeanColor(o1);
                    double[] rgbO2 = getRgbFromMeanColor(o2);

                    double redQAndO1 = Math.abs(rgbQ[0] - rgbO1[0]);
                    double greenQAndO1 = Math.abs(rgbQ[1] - rgbO1[1]);
                    double blueQAndO1 = Math.abs(rgbQ[2] - rgbO1[2]);

                    double redQAndO2 = Math.abs(rgbQ[0] - rgbO2[0]);
                    double greenQAndO2 = Math.abs(rgbQ[1] - rgbO2[1]);
                    double blueQAnd02 = Math.abs(rgbQ[2] - rgbO2[2]);

                    double sumQAndO1 = redQAndO1 + greenQAndO1 + blueQAndO1;
                    double sumQAndO2 = redQAndO2 + greenQAndO2 + blueQAnd02;

                    return calculateSort(sumQAndO1, sumQAndO2);
                })
                .collect(Collectors.toList());
    }

    private int calculateSort(double featureValueO1, double featureValueO2) {
        if (featureValueO1 > featureValueO2) {
            return 1;
        } else if (featureValueO1 == featureValueO2) {
            return 0;
        } else {
            return -1;
        }
    }

    private double[] getRgbFromMeanColor(FeatureContainer container) {
        return new double[]{
                container.getMeanColor().getRed(),
                container.getMeanColor().getGreen(),
                container.getMeanColor().getBlue()
        };
    }

    /**
     * Make the prediction based on the sorted list of features (images or categories).
     *
     * @param sortedList
     * @param k
     * @return predicted category
     */

    public String classify(List<FeatureContainer> sortedList, int k) {
        Map<String, Integer> map = new HashMap<>();

        sortedList.stream().limit(k).forEach(x -> {
            System.out.println(String.format("r: %s g: %s b:%s", x.getMeanColor().getRed(), x.getMeanColor().getGreen(), x.getMeanColor().getBlue()));
            map.put(x.getCategory(), map.getOrDefault(x.getCategory(), 0) + 1);
        });

        return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}