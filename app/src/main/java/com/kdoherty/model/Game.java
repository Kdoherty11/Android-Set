package com.kdoherty.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the general rules for the Set! game
 * Wrapper for the Deck class
 * @author Kevin Doherty
 *
 */
public class Game {
	
	/** The number of starting Cards */
	public static final int NUM_START_CARDS = 12;

	/** The Deck this Game uses */
	private final Deck mDeck = new Deck();

    private final List<Card> activeCards = new ArrayList<>();

    public Game() {
        deal(NUM_START_CARDS);
    }

    public List<Card> getActiveCards() {
        return activeCards;
    }

	/**
	 * Deals the input number of Cards from this Game's Deck
	 * @param num The number of Cards to deal
	 * @return The list of dealt Cards
	 */
	public List<Card> deal(int num) {
        List<Card> dealt = mDeck.deal(num);
		activeCards.addAll(dealt);
        return dealt;
	}

    /**
     * Removes all input cards from the active cards
     * @param cards The cards to remove
     */
    public void remove(List<Card> cards) {
        for (Card card : cards) {
            activeCards.remove(card);
        }
    }

	/**
	 * The game is over when the deck has less than 3 cards
	 * and there are no sets on the table
	 * @return Is this Game over?
	 */
	public boolean isOver() {
		return mDeck.getCards().size() < Set.SIZE
				&& SetSolver.findSet(activeCards) == null;
	}
}
