package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import android.content.Context;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.OldRotationListener;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.OldRotationListener.OnRotationListener;

/**
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class OldSensorController extends BaseController<MOVE> implements OnRotationListener {
	
	private static final String TAG = "EXER_PACMAN_SENSOR_CONTROLLER";
	
	private static final int NO_TURN = 0;
	private static final int TURN_LEFT = 1;
	private static final int TURN_RIGHT = 2;
	
	private Context mContext;
	private OldRotationListener listener;
	private int mRotation = NO_TURN;
	private MOVE lastActiveMove;
	
	public OldSensorController(Context context) {
		mContext = context;
		initListener();
	}
	
	private void initListener() {
		listener = new OldRotationListener(mContext);
		listener.setOnRotationListener(this);
	}
	
	@Override
	public MOVE getMove(GameEngine engine, long timeDue) {
		int currentNodeIndex = engine.getPacmanCurrentNodeIndex();
		MOVE[] possibleMoves = engine.getPossibleMoves(currentNodeIndex);
		MOVE lam = engine.getPacmanLastMoveMade();
		if (!lam.equals(MOVE.NEUTRAL)) {
			lastActiveMove = lam;
		}
		Log.d(TAG, "Last Active Move: " + lastActiveMove.toString());
		switch (mRotation) {
		case TURN_LEFT:
			// TODO improve here
			mRotation = NO_TURN;
			return lastActiveMove.left();
			
		case TURN_RIGHT:
			mRotation = NO_TURN;
			return lastActiveMove.right();

		default:
			return MOVE.NEUTRAL;
		}
	}

	@Override
	public void onRotate(int move) {
		mRotation = move;
	}
	
	@Override
	protected void finalize() throws Throwable {
		listener.pause();
		super.finalize();
	}
	
}