package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.ScaleUtil;

public class OnDrawWorker {

	private GameEngine mEngine;
	private Images mImages;
	private static MOVE lastPacManMove;
	private int time;
	
	private Paint pText;
	private Rect mSrc;
	private Rect mDst;
	
	public OnDrawWorker(GameEngine engine, Images images) {
		mEngine = engine;
		mImages = images;
		init();
	}

	private void init() {
		pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setTextSize(40);
		int left = 0;
		int top = (int) ((ScaleUtil.getActualScreenH() - ScaleUtil.getActualMazeH()) / 2f);
		int right = (int) ScaleUtil.getActualScreenW();
		int bottom = (int) (((ScaleUtil.getActualScreenH() - ScaleUtil.getActualMazeH()) / 2f) + ScaleUtil.getActualMazeH());
		mSrc = new Rect(left, top, right, bottom);
		mDst = new Rect(0, 0, (int) ScaleUtil.getActualMazeW(), (int) ScaleUtil.getActualMazeH());
	}
	
	public void draw(Canvas c) {
		time = mEngine.getTotalTime();
		drawBackground(c);
		drawMaze(c);
		drawPills(c);
		drawPowerPills(c);
		drawPacman(c);
		drawGhosts(c);
	}
	
	private void drawBackground(Canvas c) {
		c.drawBitmap(mImages.getBackground(), mSrc, mDst, null);
	}

	/**
	 * draw the maze
	 */
	private void drawMaze(Canvas c) {
		c.drawBitmap(mImages.getMaze(mEngine.getMazeIndex()), 0, 0, null);
	}

	/**
	 * draw the pills
	 */
	private void drawPills(Canvas c) {
		int[] pillIndices = mEngine.getPillIndices();
		for (int i = 0; i < pillIndices.length; i++) {
			if (mEngine.isPillStillAvailable(i)) {
				int baseX = mEngine.getNodeXCood(pillIndices[i]) * MAG + 4;
				int baseY = mEngine.getNodeYCood(pillIndices[i]) * MAG + 8;
				c.drawBitmap(mImages.getPill(mEngine.getMazeIndex()), baseX, baseY, null);
//				c.drawOval(new RectF(baseX, baseY, baseX + 8, baseY + 8), pPills);
			}
		}
	}

	/**
	 * draw the power pills
	 */
	private void drawPowerPills(Canvas c) {
		int[] powerPillIndices = mEngine.getPowerPillIndices();
		for (int i = 0; i < powerPillIndices.length; i++) {
			if (mEngine.isPowerPillStillAvailable(i)) {
				int baseX = mEngine.getNodeXCood(powerPillIndices[i]) * MAG + 1;
				int baseY = mEngine.getNodeYCood(powerPillIndices[i]) * MAG + 5;
				c.drawBitmap(mImages.getSword(), baseX, baseY, null);
//				c.drawOval(new RectF(baseX, baseY, baseX + 16, baseY + 16), pPowerPills);
			}
		}
	}

	/**
	 * draw MS pacman
	 */
	private void drawPacman(Canvas c) {
		int pacLoc = mEngine.getPacmanCurrentNodeIndex();
		MOVE tmpLastPacManMove = mEngine.getPacmanLastMoveMade();
		if (tmpLastPacManMove != MOVE.NEUTRAL)
			lastPacManMove = tmpLastPacManMove;
		c.drawBitmap(mImages.getPacman(lastPacManMove, time),
				mEngine.getNodeXCood(pacLoc) * MAG - 1,
				mEngine.getNodeYCood(pacLoc) * MAG, null); // MAG + 3
	}

	/**
	 * draw the 4 ghosts
	 */
	private void drawGhosts(Canvas c) {
		for (GHOST ghostType : GHOST.values()) {
			int currentNodeIndex = mEngine.getGhostCurrentNodeIndex(ghostType);
			int nodeXCood = mEngine.getNodeXCood(currentNodeIndex);
			int nodeYCood = mEngine.getNodeYCood(currentNodeIndex);
			if (mEngine.getGhostEdibleTime(ghostType) > 0) {
				if (mEngine.getGhostEdibleTime(ghostType) < EDIBLE_ALERT
						&& ((time % 6) / 3) == 0) {
					c.drawBitmap(mImages.getEdibleGhost(true, time), nodeXCood
							* MAG - 1, nodeYCood * MAG, null); // MAG + 3
				} else {
					c.drawBitmap(mImages.getEdibleGhost(false, time), nodeXCood
							* MAG - 1, nodeYCood * MAG, null); // MAG + 3
				}
			} else {
				int index = ghostType.ordinal();
				if (mEngine.getGhostLairTime(ghostType) > 0) {
					c.drawBitmap(mImages.getGhost(ghostType,
							mEngine.getGhostLastMoveMade(ghostType), time),
							nodeXCood * MAG - 1 + (index * 5), nodeYCood * MAG, null); // MAG + 3
				} else {
					c.drawBitmap(mImages.getGhost(ghostType,
							mEngine.getGhostLastMoveMade(ghostType), time),
							nodeXCood * MAG - 1, nodeYCood * MAG, null); // MAG + 3
				}
			}
		}
	}

	/**
	 * draw game over
	 */
	private void drawGameOver(Canvas c) {
		c.drawText("Game Over", ScaleUtil.getActualMazeW() / 2, ScaleUtil.getActualMazeH() / 2, pText);
	}
}
