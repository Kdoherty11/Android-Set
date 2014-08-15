package com.kdoherty.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the Deck of 81 cards needed to play the game Set
 * 
 * @author Kevin Doherty
 * 
 */
public class Deck {

	/** The Cards contained in this Deck */
	private final List<Card> mCards = new ArrayList<>();

	/**
	 * Creates a new Deck of 81 Cards and shuffles them
	 */
	public Deck() {
		initDeck();
		shuffle();
	}

	/**
	 * Initializes all 81 Cards in this deck
	 */
	private void initDeck() {
		Card.Shape shape;
		int num;
		Card.Color color;
		Card.Fill fill;
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				shape = Card.Shape.OVAL;
			} else if (i == 1) {
				shape = Card.Shape.DIAMOND;
			} else {
				shape = Card.Shape.SQUIGGLE;
			}
			for (int j = 0; j < 3; j++) {
				num = j + 1;
				for (int k = 0; k < 3; k++) {
					if (k == 0) {
						color = Card.Color.RED;
					} else if (k == 1) {
						color = Card.Color.GREEN;
					} else {
						color = Card.Color.PURPLE;
					}
					for (int l = 0; l < 3; l++) {
						if (l == 0) {
							fill = Card.Fill.EMPTY;
						} else if (l == 1) {
							fill = Card.Fill.SOLID;
						} else {
							fill = Card.Fill.STRIPED;
						}
						mCards.add(new Card(shape, num, color, fill));
					}
				}
			}
		}
	}
	
	/**
	 * Shuffles the cards in this Deck
	 */
	private void shuffle() {
		Collections.shuffle(mCards);
	}

	/**
	 * Getter for all the cards contained in this Deck
	 * @return All the cards contained in this Deck
	 */
	List<Card> getCards() {
		return mCards;
	}

	/**
	 * Deals a given number of cards from this Deck
	 * 
	 * @param num
	 *            The number of cards to deal
	 * @return The dealt cards
	 */
	List<Card> deal(int num) {
		List<Card> dealtCards = new ArrayList<>();
		if (mCards.size() < num) {
			return dealtCards;
		}
		for (int i = 0; i < num; i++) {
			dealtCards.add(mCards.remove(0));
		}
		return dealtCards;
	}

    @Override
    public String toString() {
        return mCards.toString();
    }
}
