package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.util.EnumMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.ScaleUtil;

public class Images {

	private static final String TAG = "EXER_PACMAN_IMAGES";

	private Context mContext;
	private EnumMap<MOVE, Bitmap[]> pacman;
	private EnumMap<GHOST, EnumMap<MOVE, Bitmap[]>> ghosts;
	private Bitmap[] edibleGhosts, edibleBlinkingGhosts, mazes;
	private Bitmap[] pills;
	private Bitmap sword;
	private Bitmap bg;

	private BitmapFactory.Options mOptions;

	public Images(Context context) {
		mContext = context;
		mOptions = new BitmapFactory.Options();
		mOptions.inPurgeable = true;
		mOptions.inPreferredConfig = Config.RGB_565;
		load();
	}

	private void load() {
		// init all images of pacman
		pacman = new EnumMap<MOVE, Bitmap[]>(MOVE.class);
		pacman.put(MOVE.UP, new Bitmap[] {
				_loadImage(R.drawable.mspacman_up_normal),
				_loadImage(R.drawable.mspacman_up_open),
				_loadImage(R.drawable.mspacman_up_closed) });
		pacman.put(MOVE.RIGHT, new Bitmap[] {
				_loadImage(R.drawable.mspacman_right_normal),
				_loadImage(R.drawable.mspacman_right_open),
				_loadImage(R.drawable.mspacman_right_closed) });
		pacman.put(MOVE.DOWN, new Bitmap[] {
				_loadImage(R.drawable.mspacman_down_normal),
				_loadImage(R.drawable.mspacman_down_open),
				_loadImage(R.drawable.mspacman_down_closed) });
		pacman.put(MOVE.LEFT, new Bitmap[] {
				_loadImage(R.drawable.mspacman_left_normal),
				_loadImage(R.drawable.mspacman_left_open),
				_loadImage(R.drawable.mspacman_left_closed) });

		// init all images of ghosts
		ghosts = new EnumMap<GHOST, EnumMap<MOVE, Bitmap[]>>(GHOST.class);
		// init blinky
		ghosts.put(GHOST.BLINKY, new EnumMap<MOVE, Bitmap[]>(MOVE.class));
		ghosts.get(GHOST.BLINKY).put(
				MOVE.UP,
				new Bitmap[] { _loadImage(R.drawable.blinky_up_1),
						_loadImage(R.drawable.blinky_up_2) });
		ghosts.get(GHOST.BLINKY).put(
				MOVE.RIGHT,
				new Bitmap[] { _loadImage(R.drawable.blinky_right_1),
						_loadImage(R.drawable.blinky_right_2) });
		ghosts.get(GHOST.BLINKY).put(
				MOVE.DOWN,
				new Bitmap[] { _loadImage(R.drawable.blinky_down_1),
						_loadImage(R.drawable.blinky_down_2) });
		ghosts.get(GHOST.BLINKY).put(
				MOVE.LEFT,
				new Bitmap[] { _loadImage(R.drawable.blinky_left_1),
						_loadImage(R.drawable.blinky_left_2) });
		// init pinky
		ghosts.put(GHOST.PINKY, new EnumMap<MOVE, Bitmap[]>(MOVE.class));
		ghosts.get(GHOST.PINKY).put(
				MOVE.UP,
				new Bitmap[] { _loadImage(R.drawable.pinky_up_1),
						_loadImage(R.drawable.pinky_up_2) });
		ghosts.get(GHOST.PINKY).put(
				MOVE.RIGHT,
				new Bitmap[] { _loadImage(R.drawable.pinky_right_1),
						_loadImage(R.drawable.pinky_right_2) });
		ghosts.get(GHOST.PINKY).put(
				MOVE.DOWN,
				new Bitmap[] { _loadImage(R.drawable.pinky_down_1),
						_loadImage(R.drawable.pinky_down_2) });
		ghosts.get(GHOST.PINKY).put(
				MOVE.LEFT,
				new Bitmap[] { _loadImage(R.drawable.pinky_left_1),
						_loadImage(R.drawable.pinky_left_2) });
		// init inky
		ghosts.put(GHOST.INKY, new EnumMap<MOVE, Bitmap[]>(MOVE.class));
		ghosts.get(GHOST.INKY).put(
				MOVE.UP,
				new Bitmap[] { _loadImage(R.drawable.inky_up_1),
						_loadImage(R.drawable.inky_up_2) });
		ghosts.get(GHOST.INKY).put(
				MOVE.RIGHT,
				new Bitmap[] { _loadImage(R.drawable.inky_right_1),
						_loadImage(R.drawable.inky_right_2) });
		ghosts.get(GHOST.INKY).put(
				MOVE.DOWN,
				new Bitmap[] { _loadImage(R.drawable.inky_down_1),
						_loadImage(R.drawable.inky_down_2) });
		ghosts.get(GHOST.INKY).put(
				MOVE.LEFT,
				new Bitmap[] { _loadImage(R.drawable.inky_left_1),
						_loadImage(R.drawable.inky_left_2) });
		// init sue
		ghosts.put(GHOST.SUE, new EnumMap<MOVE, Bitmap[]>(MOVE.class));
		ghosts.get(GHOST.SUE).put(
				MOVE.UP,
				new Bitmap[] { _loadImage(R.drawable.sue_up_1),
						_loadImage(R.drawable.sue_up_2) });
		ghosts.get(GHOST.SUE).put(
				MOVE.RIGHT,
				new Bitmap[] { _loadImage(R.drawable.sue_right_1),
						_loadImage(R.drawable.sue_right_2) });
		ghosts.get(GHOST.SUE).put(
				MOVE.DOWN,
				new Bitmap[] { _loadImage(R.drawable.sue_down_1),
						_loadImage(R.drawable.sue_down_2) });
		ghosts.get(GHOST.SUE).put(
				MOVE.LEFT,
				new Bitmap[] { _loadImage(R.drawable.sue_left_1),
						_loadImage(R.drawable.sue_left_2) });

		// init all images of edible ghosts
		edibleGhosts = new Bitmap[2];
		edibleGhosts[0] = _loadImage(R.drawable.edible_ghost_1);
		edibleGhosts[1] = _loadImage(R.drawable.edible_ghost_2);

		// init all images of blinking edible ghosts
		edibleBlinkingGhosts = new Bitmap[2];
		edibleBlinkingGhosts[0] = _loadImage(R.drawable.edible_ghost_blink_1);
		Log.d(TAG, "eghost.width: " + edibleBlinkingGhosts[0].getWidth()
				+ ", eghost.height: " + edibleBlinkingGhosts[0].getHeight());
		edibleBlinkingGhosts[1] = _loadImage(R.drawable.edible_ghost_blink_2);

		// init all images of mazes
		mazes = new Bitmap[4];
		for (int i = 0; i < mazes.length; i++) {
			mazes[i] = _loadImage(mazeIDs[i]);
			Log.d(TAG, "mazes[" + i + "].width: " + mazes[i].getWidth()
					+ ", mazes[" + i + "].height: " + mazes[i].getHeight());
		}

		// init all pills images for 4 levels
		pills = new Bitmap[4];
		for (int i = 0; i < pills.length; i++) {
			pills[i] = _loadImage(pillIDs[i]);
		}
		// init sword image
		sword = _loadImage(swordID);

		// init bg image
		bg = _loadBackground(bgID);
	}

	public Bitmap getPacman(MOVE move, int time) {
		return pacman.get(move)[(time % 6) / 2];
	}

	public Bitmap getPacmanForExtraLives() {
		return pacman.get(MOVE.RIGHT)[0];
	}

	public Bitmap getGhost(GHOST ghost, MOVE move, int time) {
		if (move == MOVE.NEUTRAL)
			return ghosts.get(ghost).get(MOVE.UP)[(time % 6) / 3];
		else
			return ghosts.get(ghost).get(move)[(time % 6) / 3];
	}

	public Bitmap getEdibleGhost(boolean blinking, int time) {
		if (!blinking)
			return edibleGhosts[(time % 6) / 3];
		else
			return edibleBlinkingGhosts[(time % 6) / 3];
	}

	public Bitmap getMaze(int mazeIndex) {
		return mazes[mazeIndex];
	}

	public Bitmap getPill(int pillIndex) {
		return pills[pillIndex];
	}

	public Bitmap getSword() {
		return sword;
	}

	public Bitmap getBackground() {
		return bg;
	}

	/**
	 * private method for loading resource images
	 * 
	 * @param id
	 *            the corresponding res id of the images
	 * @return a wrapped bitmap
	 */
	private Bitmap _loadImage(int id) {
		Bitmap originImage = BitmapFactory.decodeResource(
				mContext.getResources(), id, mOptions);
		Bitmap scaledImage = null;
		// scale the image according to the current screen resolution
		float dstWidth = originImage.getWidth();
		float dstHeight = originImage.getHeight();
		dstWidth = ScaleUtil.doBitmapScaleW(dstWidth);
		dstHeight = ScaleUtil.doBitmapScaleH(dstHeight);
		if (dstWidth != originImage.getWidth()
				|| dstHeight != originImage.getHeight()) {
			scaledImage = Bitmap.createScaledBitmap(originImage,
					(int) dstWidth, (int) dstHeight, true);
		}
		// add to the image list
		if (scaledImage != null) {
			// explicit call to avoid out of memory
			originImage.recycle();
			return scaledImage;
		} else {
			return originImage;
		}
	}

	private Bitmap _loadBackground(int id) {
		Bitmap originImage = BitmapFactory.decodeResource(
				mContext.getResources(), id, mOptions);
		Bitmap scaledImage = null;
		float dstWidth = ScaleUtil.getActualScreenW();
		float dstHeight = ScaleUtil.getActualScreenH();
		scaledImage = Bitmap.createScaledBitmap(originImage, (int) dstWidth,
				(int) dstHeight, true);
		originImage.recycle();
		return scaledImage;
	}
}
