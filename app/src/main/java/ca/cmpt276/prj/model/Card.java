package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This model has data about the three images on a particular card. Its constructor ensures that a
 * card does not have any duplicate images on it, and that its images are all from the same image
 * set / theme (either all predators or all landscapes). It has public methods for determining if
 * two cards have a matching image.
 */
public class Card {
	private List<Integer> imagesMap = new ArrayList<>();
	public List<Integer> leftMargins = new ArrayList<>();
	public List<Integer> topMargins = new ArrayList<>();
	public List<Double> imageWidths = new ArrayList<>();
	public List<Double> imageHeights = new ArrayList<>();
	public List<Boolean> isWord = new ArrayList<>();
	public List<Double> randRotations = new ArrayList<>();
	public List<Double> randScales = new ArrayList<>();

	private int cardNum;

	public Card(List<Integer> imageOrders, List<Boolean> wordBools, List<Double> randRotations, List<Double> randScales, int num) {
		this.cardNum = num;

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

}

