package de.htw.mp.ui.controller;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.htw.mp.model.FeatureContainer;
import javafx.scene.paint.Color;

/**
 * DatasetViewer: Categorizes and lists all image files in a directory.
 * The controller handles all events of the DatasetViewer.fxml view.
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
	public Color getMeanColor(Path ... imageFiles) {
		
		// no images? return PINK
		if(imageFiles.length == 0) return Color.PINK;
	
		return Color.PINK;
	}
	
	/**
	 * TODO Calculate the mean images of all given images. Or return NULL if there are no images.
	 * 
	 * @param imageFiles
	 * @return
	 */
	public BufferedImage getMeanImage(Path ... imageFiles) {
		
		// no images? return null
		if(imageFiles.length == 0) return null;		
		
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
	public List<FeatureContainer> retrieve(FeatureContainer query, FeatureContainer[] database, FeatureType featureType) {
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