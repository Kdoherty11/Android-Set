package com.kdoherty.model;

/**
 * Represents a Set Card A Card has a shape, number, color, and fill type
 * 
 * @author Kevin Doherty
 * @version 04/03/2014
 * 
 */
public class Card {

	/** All the Shapes on Cards */
	public enum Shape {
		OVAL, DIAMOND, SQUIGGLE
	}

	/** All the Colors of the Shapes on Cards */
	public enum Color {
		RED, GREEN, PURPLE
	}

	/** All the Fills of the Shapes on Cards */
	public enum Fill {
		EMPTY, SOLID, STRIPED
	}

	/** The minimum number of shapes to have on a Card */
	private static final int MIN_NUM = 1;
	/** The maximum number of shapes to have on a Card */
	private static final int MAX_NUM = 3;

	/** The shapes on this Card */
	private final Shape mShape;
	/** The number of shapes on this Card */
	private final int mNum;
	/** The color of the shapes on this Card */
	private final Color mColor;
	/** The fill type of the shapes on this Card */
	private final Fill mFill;

	/**
	 * Creates a new card
	 * 
	 * @param shape
	 *            The shapes on this Card
	 * @param num
	 *            The number of shapes on this Card
	 * @param color
	 *            The color of the shapes on this Card
	 * @param fill
	 *            The fill type of the shapes on this Card
	 */
	public Card(Shape shape, int num, Color color, Fill fill) {
		if (shape == null || color == null || fill == null) {
			throw new NullPointerException("Card fields can't be null");
		}
		rangeCheck(num, MIN_NUM, MAX_NUM);
		this.mShape = shape;
		this.mNum = num;
		this.mColor = color;
		this.mFill = fill;
	}

	Shape getShape() {
		return mShape;
	}

	int getNum() {
		return mNum;
	}

	Color getColor() {
		return mColor;
	}

	Fill getFill() {
		return mFill;
	}

	/**
	 * Asserts that input number is between minimum and maximum inclusive and
	 * throws an exception if is not
	 * 
	 * @param num
	 *            The number to check the range of
	 * @param min
	 *            The lowest the input number can be
	 * @param max
	 *            The highest the input number can be
	 */
	private static void rangeCheck(int num, int min, int max) {
		if (num < min && num > max) {
			throw new IllegalArgumentException("Can't have a card with number "
					+ num + ". Must be between " + min + " and " + max);
		}
	}

	/**
	 * Generates an integer representation of this Card
	 * 
	 * @return An integer representation of this Card
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + mNum;
		result = 31 * result + mShape.hashCode();
		result = 31 * result + mColor.hashCode();
		result = 31 * result + mFill.hashCode();
		return result;
	}

	/**
	 * Does this card equal the input object
	 * 
	 * @param obj
	 *            The object to check if this card is equal to
	 * @return true if this card is equal to the input object
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Card)) {
			return false;
		}
		Card that = ((Card) obj);
		return that.mShape == mShape && that.mNum == mNum
				&& that.mColor == mColor && that.mFill == mFill;
	}

	/**
	 * @return a String representation of this Card
	 */
	@Override
	public String toString() {
		String numToString = "one ";
		if (mNum == 2) {
			numToString = "two ";
		} else if (mNum == 3) {
			numToString = "three ";
		}
		return numToString + mFill + " " + mColor + " " + mShape;
	}
}
