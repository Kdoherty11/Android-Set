package com.kdoherty.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for finding Sets in a list of Cards
 * 
 * @author Kevin Doherty
 * 
 */
public class SetSolver {

	/** Describes whether to find one set or all the sets */
	enum Find {
		ONE, ALL
	}

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
		List<Set> sets = toSets(cards, Find.ONE);
		if (sets.isEmpty()) {
			// No Set could be found
			return null;
		}
		return sets.get(0);
	}

	/**
	 * Black box helper method for toSets
	 * 
	 * @param cards
	 *            The cards to turn into sets
	 * @return All combinations of possible sets from the input Cards
	 */
	private static List<Set> toSets(List<Card> cards, Find find) {
		return toSets(cards, new ArrayList<Set>(), find);
	}

	/**
	 * Generates all possible Sets the input Cards can make if the input Find
	 * parameter is ALL. If the Find parameter is ONE, it returns a list of the
	 * first Set which is a set as per game rules.
	 * 
	 * @param cards
	 *            The cards to generate the Sets from
	 * @param sets
	 *            The accumulated list of Sets
	 * @param find
	 *            All -> All combinations of 3 card sets are found. One -> Stops
	 *            after finding one real Set as per game rules
	 * @return All combinations of possible sets from the input Cards or a list
	 *         of one real set
	 */
	private static List<Set> toSets(List<Card> cards,
			List<Set> sets, Find find) {
		int numCards = cards.size();
		Card cardA;
		Card cardB;
		Card cardC;
		if (numCards < 3) {
            return sets;
			//throw new RuntimeException("Need 3 cards to make a set");
		} else if (numCards == 3) {
            Set set = new Set(cards.get(0), cards.get(1), cards.get(2));
            if (find == Find.ALL) {
                sets.add(set);
            } else if (set.isSet()) {
                sets.add(set);
            }
			return sets;
		}
		int count = numCards - 2;
		int switchBCount = 0;
		cardA = cards.get(0);
		for (int i = 0; i < count; i++) {
			cardB = cards.get(i + 1);
			for (int j = switchBCount; j < count; j++) {
				cardC = cards.get(j + 2);
				Set set = new Set(cardA, cardB, cardC);
				if (find == Find.ALL) {
					sets.add(set);
				} else if (find == Find.ONE && set.isSet()) {
					sets.add(set);
					return sets;
				}
			}
			switchBCount++;
		}
		// Recur
		cards.remove(0);
		return toSets(cards, sets, find);
	}
}
