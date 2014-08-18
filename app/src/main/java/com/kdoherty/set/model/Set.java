package com.kdoherty.set.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.List;

/**
 * Contains 3 Cards. Does not have to actually be a set as far as the mGame is
 * concerned.
 * 
 * @author Kevin Doherty
 * 
 */
public class Set implements Iterable<Card>, Parcelable {

	/** The size of the Set */
	public static final int SIZE = 3;

	/** The first Card in this Set */
	private Card a;

	/** The second Card in this Set */
	private Card b;

	/** The third Card in this Set */
	private Card c;

	/**
	 * Creates a new Set which contains the input cards
	 * 
	 * @param a
	 *            The first Card in this Set
	 * @param b
	 *            The second Card in this Set
	 * @param c
	 *            The third Card in this Set
	 */
	public Set(Card a, Card b, Card c) {
		if (a == null || b == null || c == null) {
			throw new NullPointerException("Can't make a set with a null Card");
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * Alternate list constructor which takes a list of 3 cards
	 * @param cards The cards to create a set out of
	 */
	public Set(List<Card> cards) {
		if (cards.size() != SIZE) {
			throw new RuntimeException("Can't create a set with a list of "
					+ cards.size() + " cards");
		}
		this.a = cards.get(0);
		this.b = cards.get(1);
		this.c = cards.get(2);
	}

    public Set(Parcel in) {
        readFromParcel(in);
    }

	@Override
	public String toString() {
		return a + ", " + b + ", " + c;
	}

	/**
	 * A Sets is a set if 1. They are 3 unique cards 2. The colors are the same
	 * or all different 3. The shapes are the same or all different 4. The
	 * numbers are the same or all different 5. The fills are the same or all
	 * different
	 * 
	 * @return is this Set a set
	 */
	public boolean isSet() {
		return areUnique() &&
                (sameColors() || diffColors())
                && (sameShapes() || diffShapes())
                && (sameNumbers() || diffNumbers())
                && (sameFills() || diffFills());
	}

	/**
	 * 
	 * @return Are all the cards in this Set unique?
	 */
	private boolean areUnique() {
		return !a.equals(b) && !a.equals(c) && !b.equals(c);
	}

	/**
	 * @return Are all the colors the same in this Set?
	 */
	private boolean sameColors() {
		Card.Color aColor = a.getColor();
		return aColor == b.getColor() && aColor == c.getColor();
	}

	/**
	 * @return Are all the shapes the same in this Set?
	 */
	private boolean sameShapes() {
		Card.Shape aShape = a.getShape();
		return aShape == b.getShape() && aShape == c.getShape();
	}

	/**
	 * @return Are all the numbers the same in this Set?
	 */
	private boolean sameNumbers() {
		int aNum = a.getNum();
		return aNum == b.getNum() && aNum == c.getNum();
	}

	/**
	 * @return Are all the fills the same in this Set?
	 */
	private boolean sameFills() {
		Card.Fill aFill = a.getFill();
		return aFill == b.getFill() && aFill == c.getFill();
	}

	/**
	 * @return Are all the colors different in this Set?
	 */
	private boolean diffColors() {
		Card.Color aColor = a.getColor();
		Card.Color bColor = b.getColor();
		Card.Color cColor = c.getColor();
		return aColor != bColor && aColor != cColor
				&& bColor != cColor;
	}

	/**
	 * @return Are all the shapes different in this Set?
	 */
	private boolean diffShapes() {
		Card.Shape aShape = a.getShape();
		Card.Shape bShape = b.getShape();
		Card.Shape cShape = c.getShape();
		return aShape != bShape && aShape != cShape
				&& bShape != cShape;
	}

	/**
	 * @return Are all the numbers different in this Set?
	 */
	private boolean diffNumbers() {
		int aNum = a.getNum();
		int bNum = b.getNum();
		int cNum = c.getNum();
		return aNum != bNum && aNum != cNum && bNum != cNum;
	}

	/**
	 * @return Are all the fills different in this Set?
	 */
	private boolean diffFills() {
		Card.Fill aFill = a.getFill();
		Card.Fill bFill = b.getFill();
		Card.Fill cFill = c.getFill();
		return aFill != bFill && aFill != cFill
				&& bFill != cFill;
	}

	/**
	 * @return an Iterator for this Set
	 */
	public Iterator<Card> iterator() {
		return new SetIterator();
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(a, flags);
        out.writeParcelable(b, flags);
        out.writeParcelable(c, flags);
    }

    private void readFromParcel(Parcel in) {
        a = (Card) in.readParcelable(Card.class.getClassLoader());
        b = (Card) in.readParcelable(Card.class.getClassLoader());
        c = (Card) in.readParcelable(Card.class.getClassLoader());
    }

    public static final Creator<Set> CREATOR = new Creator<Set>() {

        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

    /**
	 * Describes how to iterate through a Set
	 * 
	 * @author kdoherty
	 * 
	 */
	private class SetIterator implements Iterator<Card> {

		/** Index of the iterator */
		int index = 0;

		/**
		 * Does this iterator have a next element
		 */
		public boolean hasNext() {
			return index < 3;
		}

		/**
		 * @return the next card in this Iterator
		 */
		public Card next() {
			index++;
			if (index == 1) {
				return a;
			}
			if (index == 2) {
				return b;
			}
			return c;
		}

		/**
		 * Removing from this Iterator is not supported
		 */
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
