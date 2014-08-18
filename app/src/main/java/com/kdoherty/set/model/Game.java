package com.kdoherty.set.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the general rules for the Set! mGame
 * Wrapper for the Deck class
 * @author Kevin Doherty
 *
 */
public class Game {
	
	/** The number of starting Cards */
	public static final int NUM_START_CARDS = 12;

	/** The Deck this Game uses */
	private final Deck deck = new Deck();

    private final List<Card> activeCards = new ArrayList<>();

    private final List<Player> players = new ArrayList<>();

    private String id = null;

    public Game() {
        deal(NUM_START_CARDS);
        while (SetSolver.findSet(activeCards) == null) {
            deal(Set.SIZE);
        }
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
        List<Card> dealt = deck.deal(num);
		activeCards.addAll(dealt);
        return dealt;
	}

    /**
     * Removes all input cards from the active cards
     * @param cards The cards to remove
     */
    public void remove(Iterable<Card> cards) {
        for (Card card : cards) {
            activeCards.remove(card);
        }
    }

	/**
	 * The mGame is over when the deck has less than 3 cards
	 * and there are no sets on the table
	 * @return Is this Game over?
	 */
	public boolean isOver() {
		return deck.getCards().size() < Set.SIZE
				&& SetSolver.findSet(activeCards) == null;
	}

    @Override
    public String toString() {
        return "Game Id: " + id + '\n' + "Players: " + players + '\n' + "Active Cards: "
                + activeCards + '\n' + " Deck " + deck;
    }

    public String getId() {
        return id;
    }

    public boolean containsSet(Set set) {
        for (Card card : set) {
            if (!activeCards.contains(card)) {
                return false;
            }
        }
        return true;
    }

    public boolean isOpen() {
        return true;
        // TODO: get from server
       // return players.size() < 4;
    }

    public boolean isStarted() {
       // return !(deck.getCards().size() + activeCards.size() == 81);
        return false;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
