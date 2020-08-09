package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This model has data about the images on a particular card, including what images or words are
 * on it, and the location of them on the card.
 */
public class Card {
	//	For example, if it's in the landscape set and image num is 2, and imagesMap(2) = 3,
	//	then the 2nd image on the card draws from the file a_3.jpg.
	private List<Integer> imagesMap = new ArrayList<>();

	// x coordinates (origin is at top left corner of an image) of where an image is placed/starts
	public List<Integer> leftMargins = new ArrayList<>();
	public List<Integer> topMargins = new ArrayList<>(); //	y coordinates of ith image

	//	width of ith image in pixels (before scaling)
	public List<Double> imageWidths = new ArrayList<>();

	//	height of ith image in pixels (before scaling)
	public List<Double> imageHeights = new ArrayList<>();
	public List<Boolean> isWord = new ArrayList<>();	//	true if ith "image" is a word

	//	angle of rotation in degrees for ith image
	public List<Double> randRotations = new ArrayList<>();

	//	scalar on ith image's size (scaling range = [0.6, 1.25])
	public List<Double> randScales = new ArrayList<>();

	private int cardNum;

	public Card(List<Integer> imageOrders, List<Boolean> wordBools, List<Double> randRotations,
					List<Double> randScales, int cardNum) {
		this.cardNum = cardNum;

		this.randRotations.addAll(randRotations);
		this.randScales.addAll(randScales);
		this.isWord.addAll(wordBools);
		this.imagesMap.addAll(imageOrders);
	}

	public int getCardNum() {
		return cardNum;
	}

	public List<Integer> getImagesMap() {
		return imagesMap;
	}

	public Integer getImagesByIndex(int index) {
		return imagesMap.get(index);
	}

	public List<Integer> getLeftMargins() {
		return leftMargins;
	}

	public List<Integer> getTopMargins() {
		return topMargins;
	}

	public List<Double> getImageWidths() {
		return imageWidths;
	}

	public List<Double> getImageHeights() {
		return imageHeights;
	}

	public List<Boolean> getIsWord() {
		return isWord;
	}

	public List<Double> getRandRotations() {
		return randRotations;
	}

	public List<Double> getRandScales() {
		return randScales;
	}
}

