package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.DELAY;

import java.util.EnumMap;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.BaseController;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

public class SurfaceDrawer extends Thread {
	
	private static final String TAG = "EXER_PACMAN_DRAWER_THREAD";
	
	private volatile boolean mRunning = true;
	private volatile boolean mPaused = false;
	
	private SurfaceHolder mHolder;
	private GameEngine mEngine;
	private Images mImages;
	private OnDrawWorker mWorker;
	private BaseController<MOVE> mPacmanController;
	private BaseController<EnumMap<GHOST, MOVE>> mGhostController;
	
	@SuppressWarnings("unused")
	private GameView mView;
	
	public SurfaceDrawer() {}
	
	public SurfaceDrawer(GameView view, GameEngine engine, Images images, BaseController<MOVE> pacmanController, BaseController<EnumMap<GHOST, MOVE>> ghostController) {
		mView = view;
		mHolder = view.getHolder();
		mEngine = engine;
		mImages = images;
		mPacmanController = pacmanController;
		mGhostController = ghostController;
		mWorker = new OnDrawWorker(mEngine, mImages);
	}
	
	@Override
	public void run() {
		Canvas c = null;
		new Thread(mPacmanController).start();
		new Thread(mGhostController).start();
		while (mRunning) {
			if (mEngine.gameOver()) {
				break;
			}
			mPacmanController.update(mEngine.copy(), System.currentTimeMillis()+DELAY);
			mGhostController.update(mEngine.copy(), System.currentTimeMillis()+DELAY);
			synchronized (this) {
				try {
					wait(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mEngine.advanceGame(mPacmanController.getMove(), mGhostController.getMove());
			try {
                c = mHolder.lockCanvas(null);
                synchronized (mHolder) {
                	if (c != null) {
                		mWorker.draw(c);
                	}
                }
            } catch (Exception e) {
            	e.printStackTrace();
			} finally {
                if (c != null) {
                	mHolder.unlockCanvasAndPost(c);
                }
            } // end finally block
			
			if (mPaused) {
				Log.d(TAG, "drawer thread paused");
			}
			while (mPaused) {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!mRunning) {
					mPaused = false;
					Log.d(TAG, "drawer thread terminate");
					break;
				}
			}
		}
		mPacmanController.terminate();
		mGhostController.terminate();
	}

	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}
	
	public void setPause(boolean pause) {
		this.mPaused = pause;
	}
	
}
