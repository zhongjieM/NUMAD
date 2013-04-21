package edu.neu.madcourse.zhongjiemao.exerpacman.models;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

/**
 * Data structure to hold all information pertaining to the ghosts.
 */
public final class Ghost {

	public int currentNodeIndex, edibleTime, lairTime;

	public GHOST type;

	public MOVE lastMoveMade;

	public Ghost(GHOST type, int currentNodeIndex, int edibleTime,
			int lairTime, MOVE lastMoveMade) {
		this.type = type;
		this.currentNodeIndex = currentNodeIndex;
		this.edibleTime = edibleTime;
		this.lairTime = lairTime;
		this.lastMoveMade = lastMoveMade;
	}

	public Ghost copy() {
		return new Ghost(type, currentNodeIndex, edibleTime, lairTime,
				lastMoveMade);
	}
}