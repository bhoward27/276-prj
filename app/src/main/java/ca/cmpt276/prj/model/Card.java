package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This model has data about the images on a particular card, including what images or words are
 * on it, and the location of them on the card.
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

	public Card(List<Integer> imageOrders, List<Boolean> wordBools, List<Double> randRotations,
					List<Double> randScales, int num) {
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

