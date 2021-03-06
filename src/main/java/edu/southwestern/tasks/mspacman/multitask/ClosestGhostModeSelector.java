package edu.southwestern.tasks.mspacman.multitask;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A Mode selector which selects modes based on if the closest ghost is a threat
 * or is edible
 * 
 * @author Jacob Schrum
 */
public class ClosestGhostModeSelector extends MsPacManModeSelector {

	public static final int CLOSEST_THREAT = 0;
	public static final int CLOSEST_EDIBLE = 1;

	/**
	 * sets the game mode based on if the closest ghost is a threat or is edible
	 * 0 if the closest ghost is a threat 1 if the closest ghost is edible
	 * 
	 * @return mode
	 */
	public int mode() {
		ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			ghosts.add(i);
		}
		Collections.sort(ghosts, new GhostComparator(gs, true, true));

		int count = 0;
		for (Integer g : ghosts) {
			count++;
			if (gs.isGhostEdible(g)) {
				// Nearest non-returning ghost is edible
				return CLOSEST_EDIBLE;
			} else if (gs.getNumNeighbours(gs.getGhostCurrentNodeIndex(g)) > 0 || count == 4) {
				// If not edible and not returning, then is a threat
				return CLOSEST_THREAT;
			}
		}
		return CLOSEST_THREAT;
	}

	/**
	 * There are 2 modes for this mode selector
	 * 
	 * @return 2
	 */
	public int numModes() {
		return 2;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for if the closest ghost is a
	 *         threat in the first index and the score for if the closest ghost
	 *         is edible in the second index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[CLOSEST_THREAT] = GAME_SCORE;
		result[CLOSEST_EDIBLE] = GHOST_SCORE;
		return result;
	}
}
