package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import static edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.MOVE_DOWN;
import static edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.MOVE_LEFT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.MOVE_RIGHT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.MOVE_UP;
import static edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.NO_TURN;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.RotationListener.OnRotateListener;

/**
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class SensorController extends BaseController<MOVE> implements OnRotateListener {
	
	private static final String TAG = "EXER_PACMAN_NEW_SENSOR_CONTROLLER";
	
	private Context mContext;
	private Handler mHandler;
	private RotationListener listener;
	private int mRotation = NO_TURN;
	private MOVE lastActiveMove = MOVE.LEFT;
	
	public SensorController(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		initListener();
	}
	
	private void initListener() {
		listener = new RotationListener(mContext, mHandler, 3);
		listener.setOnRotateListener(this);
	}
	
	@Override
	public MOVE getMove(GameEngine engine, long timeDue) {
		MOVE lam = engine.getPacmanLastMoveMade();
		if (!lam.equals(MOVE.NEUTRAL))
			lastActiveMove = lam;
		Log.d(TAG, "Last Active Move: " + lastActiveMove.toString());
		switch (mRotation) {
		case MOVE_UP:
			return MOVE.UP;
		case MOVE_RIGHT:
			return MOVE.RIGHT;
		case MOVE_DOWN:
			return MOVE.DOWN;
		case MOVE_LEFT:
			return MOVE.LEFT;
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
		if (listener != null) {
			listener.pause();
		}
		super.finalize();
	}
	
	public void stopSensor() {
		if (listener != null) {
			listener.pause();
		}
	}
}