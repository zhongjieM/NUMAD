package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.util.EnumMap;
import java.util.Random;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.DM;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

public final class StarterGhosts extends BaseController<EnumMap<GHOST, MOVE>> {
	// attack Ms Pac-Man with this probability
	private float CONSISTENCY = GHOST_AGRESSIVITY;
	// if Ms Pac-Man is this close to a power pill, back away
	private final static int PILL_PROXIMITY = 15;

	Random rnd = new Random();
	EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	
	public StarterGhosts() {
		super();
		CONSISTENCY = GHOST_AGRESSIVITY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.neu.madcourse.zhongjiemao.exerpacman.controllers.BaseController#
	 * getMove(edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine, long)
	 */
	public EnumMap<GHOST, MOVE> getMove(GameEngine engine, long timeDue) {
		// for each ghost
		for (GHOST ghost : GHOST.values()) {
			// if ghost requires an action
			if (engine.doesGhostRequireAction(ghost)) {
				// retreat from Ms Pac-Man if edible or if Ms Pac-Man is close
				// to power pill
				if (engine.getGhostEdibleTime(ghost) > 0
						|| closeToPower(engine))
					myMoves.put(
							ghost,
							engine.getApproximateNextMoveAwayFromTarget(
									engine.getGhostCurrentNodeIndex(ghost),
									engine.getPacmanCurrentNodeIndex(),
									engine.getGhostLastMoveMade(ghost), DM.PATH));
				else {
					// attack Ms Pac-Man otherwise (with certain probability)
					if (rnd.nextFloat() < CONSISTENCY)
						myMoves.put(ghost, engine
								.getApproximateNextMoveTowardsTarget(
										engine.getGhostCurrentNodeIndex(ghost),
										engine.getPacmanCurrentNodeIndex(),
										engine.getGhostLastMoveMade(ghost),
										DM.PATH));
					else {
						// else take a random legal action (to be less
						// predictable)
						MOVE[] possibleMoves = engine.getPossibleMoves(
								engine.getGhostCurrentNodeIndex(ghost),
								engine.getGhostLastMoveMade(ghost));
						myMoves.put(ghost, possibleMoves[rnd.nextInt(possibleMoves.length)]);
					}
				}
			}
		}

		return myMoves;
	}

	/**
	 * This helper function checks if Ms Pac-Man is close to an available power
	 * pill
	 * 
	 * @param engine
	 * @return
	 */
	private boolean closeToPower(GameEngine engine) {
		int[] powerPills = engine.getPowerPillIndices();

		for (int i = 0; i < powerPills.length; i++)
			if (engine.isPowerPillStillAvailable(i)
					&& engine.getShortestPathDistance(powerPills[i],
							engine.getPacmanCurrentNodeIndex()) < PILL_PROXIMITY)
				return true;

		return false;
	}
}