package edu.neu.madcourse.zhongjiemao.exerpacman.models;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

/**
 * Data structure to hold all information pertaining to Pacman.
 */
public final class Pacman {

	public int currentNodeIndex, numberOfLivesRemaining;

	public MOVE lastMoveMade;

	public boolean hasReceivedExtraLife;

	public Pacman(int currentNodeIndex, MOVE lastMoveMade,
			int numberOfLivesRemaining, boolean hasReceivedExtraLife) {
		this.currentNodeIndex = currentNodeIndex;
		this.lastMoveMade = lastMoveMade;
		this.numberOfLivesRemaining = numberOfLivesRemaining;
		this.hasReceivedExtraLife = hasReceivedExtraLife;
	}

	public Pacman copy() {
		return new Pacman(currentNodeIndex, lastMoveMade, numberOfLivesRemaining, hasReceivedExtraLife);
	}
}