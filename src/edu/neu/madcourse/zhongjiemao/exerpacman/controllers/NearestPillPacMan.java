package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.DM;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

/**
 * The Class NearestPillPacMan.
 */
public class NearestPillPacMan extends BaseController<MOVE> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.neu.madcourse.zhongjiemao.exerpacman.controllers.BaseController#
	 * getMove(edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine, long)
	 */
	public MOVE getMove(GameEngine engine, long timeDue) {
		int currentNodeIndex = engine.getPacmanCurrentNodeIndex();

		// get all active pills
		int[] activePills = engine.getActivePillsIndices();

		// get all active power pills
		int[] activePowerPills = engine.getActivePowerPillsIndices();

		// create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];

		for (int i = 0; i < activePills.length; i++)
			targetNodeIndices[i] = activePills[i];

		for (int i = 0; i < activePowerPills.length; i++)
			targetNodeIndices[activePills.length + i] = activePowerPills[i];

		// return the next direction once the closest target has been identified
		return engine.getNextMoveTowardsTarget(engine.getPacmanCurrentNodeIndex(), 
				engine.getClosestNodeIndexFromNodeIndex(currentNodeIndex,
						targetNodeIndices, DM.PATH), DM.PATH);
	}
}