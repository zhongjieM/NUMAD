package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import static edu.neu.madcourse.zhongjiemao.exerpacman.controllers.SwipeInput.*;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

public class SwipeController extends BaseController<MOVE> {

	private SwipeInput mInput;

	public SwipeController(SwipeInput input) {
		mInput = input;
	}

	@Override
	public MOVE getMove(GameEngine engine, long timeDue) {
		switch (mInput.getDirection()) {
		case UP:
			return MOVE.UP;
		case RIGHT:
			return MOVE.RIGHT;
		case DOWN:
			return MOVE.DOWN;
		case LEFT:
			return MOVE.LEFT;
		default:
			return MOVE.NEUTRAL;
		}
	}
}
