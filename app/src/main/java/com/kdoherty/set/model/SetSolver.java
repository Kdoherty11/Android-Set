package com.kdoherty.set.model;

import java.util.List;

/**
 * This class is responsible for finding Sets in a list of Cards
 * 
 * @author Kevin Doherty
 * 
 */
public class SetSolver {

	/**
	 * Creates a new SetSolver. Should never be called because this class only
	 * contains static methods
	 */
	private SetSolver() {
		// Hide Constructor
	}

	/**
	 * Finds one set in the input cards
	 * 
	 * @param cards
	 *            the cards to find a set in
	 * @return The found set if there is one. Otherwise null.
	 */
	public static Set findSet(List<Card> cards) {
        int cardsLen = cards.size();
        for (int i = 0; i < cardsLen - 2; i++) {
            for (int j = i + 1; j < cardsLen - 1; j++) {
                for (int k = j + 1; k < cardsLen; k++) {
                    Set set = new Set(cards.get(i), cards.get(j), cards.get(k));
                    if (set.isSet()) {
                        return set;
                    }
                }
            }
        }
        return null;
	}
}
