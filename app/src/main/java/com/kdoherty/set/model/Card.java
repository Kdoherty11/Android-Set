package com.kdoherty.set.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Set Card A Card has a mShape, number, color, and fill type
 * 
 * @author Kevin Doherty
 * @version 04/03/2014
 * 
 */
public class Card implements Parcelable {

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
	private Shape shape;
	/** The number of shapes on this Card */
	private int num;
	/** The color of the shapes on this Card */
	private Color color;
	/** The fill type of the shapes on this Card */
	private Fill fill;

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
		this.shape = shape;
		this.num = num;
		this.color = color;
		this.fill = fill;
	}

    public Card(String shape, int num, String color, String fill) {
        this(Shape.valueOf(shape.toUpperCase()),
                num,
                Color.valueOf(color.toUpperCase()),
                Fill.valueOf(fill.toUpperCase()));
    }

    public Card(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(shape);
        out.writeInt(num);
        out.writeSerializable(color);
        out.writeSerializable(fill);
    }

    private void readFromParcel(Parcel in) {
        shape = (Shape) in.readSerializable();
        num = in.readInt();
        color = (Color) in.readSerializable();
        fill = (Fill) in.readSerializable();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {

        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }

    };

    Shape getShape() {
		return shape;
	}

	int getNum() {
		return num;
	}

	Color getColor() {
		return color;
	}

	Fill getFill() {
		return fill;
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
		result = 31 * result + num;
		result = 31 * result + shape.hashCode();
		result = 31 * result + color.hashCode();
		result = 31 * result + fill.hashCode();
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
		return that.shape == shape && that.num == num
				&& that.color == color && that.fill == fill;
	}

	/**
	 * @return a String representation of this Card
	 */
	@Override
	public String toString() {
		String numToString = "ONE ";
		if (num == 2) {
			numToString = "TWO ";
		} else if (num == 3) {
			numToString = "THREE ";
		}
		return numToString + fill + " " + color + " " + shape;
	}
}
