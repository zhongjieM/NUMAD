package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.neu.madcourse.zhongjiemao.exerpacman.activities.ExerPacmanGame;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.ScaleUtil;

/**
 * This class is the view that displays the game. The only thing constants might
 * need to know about are the helper methods that allow one to add colors and
 * lines to the game view. This could be useful for debugging.
 */
public final class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final String TAG = "EXER_PACMAN_GV";
	
	private ExerPacmanGame mGame;
	private SurfaceHolder mHolder;
	private SurfaceDrawer mDrawer;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		mGame = (ExerPacmanGame) context;
		mHolder = getHolder();
		mHolder.addCallback(this);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "w: " + w + ", h: " + h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int) ScaleUtil.getActualScreenW(), (int) ScaleUtil.getActualMazeH());
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		mDrawer = new SurfaceDrawer(this, mGame.getGameEngine(), mGame.getImages(), mGame.getPacmanController(), mGame.getGhostController());
		mDrawer.setRunning(true);
		mDrawer.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		mGame.setSurfaceDestroyed(true);
		mDrawer.setRunning(false);
	}
	
	public SurfaceDrawer getSurfaceDrawer() {
		return mDrawer;
	}
}