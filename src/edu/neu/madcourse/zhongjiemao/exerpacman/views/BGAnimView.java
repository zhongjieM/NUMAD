package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import edu.neu.madcourse.zhongjiemao.R;

/**
 * This class is to create an animation in your pacman login activity.
 * 
 * To create an instance of this class, you need to use call the constructor of
 * this class. You need to provide the Context of your current activity and the
 * screen width and screen height of your current phone.
 * 
 * Here is an example to get the screen width and screen height of your phone:
 * 
 * DisplayMetrics dm = new DisplayMetrics();
 * 
 * getWindowManager().getDefaultDisplay().getMetrics(dm);
 * 
 * this.screenWidth = dm.widthPixels;
 * 
 * this.screenHeight = dm.heightPixels;
 * 
 * You need to write this in you activity.
 * 
 * Once an object of this class is constructed, the animation will not
 * automatically start up. You need to call onResume() of this class in your
 * onResume() method in your activity to resume the animation from pause to
 * running so that when your activity is running again in the foreground, the
 * animation will keep working. Also you need to call onPause() of this class in
 * your onPause() method in your activity to pause the animation from running,
 * so that when you activity is not running in the foreground, the threads of
 * this class will be canceled.
 * 
 * @author Zhongjie Mao
 * 
 */
public class BGAnimView extends View {

	private static final int ANIMATION_RATE = 100;
	private static final int ANIMATION_SINGLE_RATE = 80;
	private static final int IMG_SMALL_SIZE = 1;
	private static final int IMG_BIG_SIZE = 2;

	private static final int PACMAN_RIGHT_SMALL_NORMAL = 0;
	private static final int PACMAN_RIGHT_SMALL_OPEN = 1;
	private static final int PACMAN_RIGHT_SMALL_CLOSED = 2;
	private static final int PACMAN_RIGHT_BIG_NORMAL = 3;
	private static final int PACMAN_LEFT_BIG_NORMAL = 4;
	private static final int PACMAN_LEFT_BIG_OPEN = 5;
	private static final int PACMAN_LEFT_BIG_CLOSED = 6;

	private static final int GHOST_RIGHT_1 = 0;
	private static final int GHOST_RIGHT_2 = 1;
	private static final int GHOST_LEFT_1 = 2;
	private static final int GHOST_LEFT_2 = 3;
	// HANDLER
	private static final int MSG_PROGRESS = 1;

	private int viewWidth;
	private int viewHeight;
	private int intervals;
	private int radiusOfBean;
	private int progress = 0;

	private Bitmap[] bean_imgs;
	private Bitmap[] pacman_imgs;
	private Bitmap[] ghost_imgs;

	private int current_pacman_img;
	private int current_ghost_img;
	private int current_pacman_location;
	private int current_ghost_location;
	private int current_beans_number;

	private Handler handler;
	private TimerTask timertask_progress;
	private Timer timer_progress;
	private TimerTask timertask_pacmanMouthMove;
	private Timer timer_pacmanMouthMove;
	private TimerTask timertask_ghostimgchange;
	private Timer timer_ghostimgchange;

	public BGAnimView(Context context, int screenWidth, int screenHeight) {
		super(context);

		initializeParams(screenWidth, screenHeight);
		loadImages();
	}

	@Override
	protected void onMeasure(int width, int height) {
		setMeasuredDimension(viewWidth, viewHeight);
	}

	/**
	 * Call this when your activity has an object of this class
	 */
	public void onPause() {
		try {
			if (timertask_progress != null)
				timertask_progress.cancel();
			if (timer_progress != null)
				timer_progress.cancel();
			if (timertask_pacmanMouthMove != null)
				timertask_pacmanMouthMove.cancel();
			if (timer_pacmanMouthMove != null)
				timer_pacmanMouthMove.cancel();
			if (timertask_ghostimgchange != null)
				timertask_ghostimgchange.cancel();
			if (timer_ghostimgchange != null)
				timer_ghostimgchange.cancel();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * Call this if you already has an object of this class and you want to
	 * resume it from pause
	 */
	public void onResume() {
		initializeThreads();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw beans
		drawBeans(canvas);
		// draw pacman
		drawPacman(canvas);
		// draw ghost
		drawGhost(canvas);

	}

	private void drawBeans(Canvas canvas) {
		try {
			Paint paint_bean = new Paint();
			paint_bean.setColor(Color.YELLOW);
			int start_at = 19 - current_beans_number + 2;
			for (int i = start_at; i <= 19; i++) {
				int x = i * intervals - bean_imgs[0].getWidth() / 2;
				int y = viewHeight / 2 - bean_imgs[0].getHeight() / 2;
				canvas.drawBitmap(bean_imgs[0], x, y, paint_bean);
			}
			if (current_beans_number > 0) {
				int x = 20 * intervals - bean_imgs[1].getWidth() / 2;
				int y = viewHeight / 2 - bean_imgs[1].getHeight() / 2;
				paint_bean.setColor(Color.GREEN);
				canvas.drawBitmap(bean_imgs[1], x, y, paint_bean);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	private void drawPacman(Canvas canvas) {
		try {
			if (current_pacman_img < 0 || current_pacman_img > 6)
				return;
			if (current_pacman_location < 0)
				return;
			Paint paint_pacman = new Paint();
			Bitmap temp_pacman_img = pacman_imgs[current_pacman_img];
			float left = intervals * current_pacman_location
					- temp_pacman_img.getWidth() / 2;
			float top = viewHeight / 2 - temp_pacman_img.getHeight() / 2;
			canvas.drawBitmap(temp_pacman_img, left, top, paint_pacman);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	private void drawGhost(Canvas canvas) {
		try {
			if (current_ghost_img < 0 || current_ghost_img > 3)
				return;
			if (current_ghost_location < 0)
				return;
			Paint paint_ghost = new Paint();
			Bitmap temp_ghost_img = ghost_imgs[current_ghost_img];
			float left = intervals * current_ghost_location
					- temp_ghost_img.getWidth() / 2;
			float top = viewHeight / 2 - temp_ghost_img.getHeight() / 2;
			canvas.drawBitmap(temp_ghost_img, left, top, paint_ghost);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * Initialize members
	 * 
	 * @param screenWidth
	 *            : screen width
	 * @param screenHeight
	 *            : screen height
	 */
	private void initializeParams(int screenWidth, int screenHeight) {
		viewWidth = screenWidth * 9 / 10;
		viewHeight = screenHeight / 5;
		intervals = viewWidth / 21;
		radiusOfBean = intervals / 5;
		current_pacman_location = 0;
		current_ghost_location = 0;
		current_beans_number = 20;
		progress = 0;
	}

	private void setAnimationStatus(int pacman_location, int pacman_img,
			int ghost_location, int ghost_img, int bean_number) {
		current_pacman_location = pacman_location;
		current_pacman_img = pacman_img;
		current_ghost_location = ghost_location;
		current_ghost_img = ghost_img;
		current_beans_number = bean_number;
	}

	private void initializeThreads() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_PROGRESS:
					dealWithProgress();
					break;
				}
			}
		};
		threadProgress();
		threadPacmanMouthMove();
		threadGhostImgChange();
	}

	private void threadProgress() {
		timertask_progress = new TimerTask() {
			@Override
			public void run() {
				try {
					progress++;
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception ex) {
					System.out.println(ex.toString());
					return;
				}
			}
		};
		timer_progress = new Timer();
		timer_progress.schedule(timertask_progress, 100, ANIMATION_RATE);
	}

	private void threadPacmanMouthMove() {
		timertask_pacmanMouthMove = new TimerTask() {
			@Override
			public void run() {
				try {
					switch (current_pacman_img) {
					case 0:
						current_pacman_img = PACMAN_RIGHT_SMALL_OPEN;
						break;
					case 1:
						current_pacman_img = PACMAN_RIGHT_SMALL_CLOSED;
						break;
					case 2:
						current_pacman_img = PACMAN_RIGHT_SMALL_NORMAL;
						break;
					case 3:
						current_pacman_img = PACMAN_RIGHT_BIG_NORMAL;
						break;
					case 4:
						current_pacman_img = PACMAN_LEFT_BIG_OPEN;
						break;
					case 5:
						current_pacman_img = PACMAN_LEFT_BIG_CLOSED;
						break;
					case 6:
						current_pacman_img = PACMAN_LEFT_BIG_NORMAL;
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
					return;
				}
			}
		};
		timer_pacmanMouthMove = new Timer();
		timer_pacmanMouthMove.schedule(timertask_pacmanMouthMove, 0,
				ANIMATION_SINGLE_RATE);
	}

	private void threadGhostImgChange() {
		timertask_ghostimgchange = new TimerTask() {
			@Override
			public void run() {
				try {
					switch (current_ghost_img) {
					case 0:
						current_ghost_img = GHOST_RIGHT_2;
						break;
					case 1:
						current_ghost_img = GHOST_RIGHT_1;
						break;
					case 2:
						current_ghost_img = GHOST_LEFT_2;
						break;
					case 3:
						current_ghost_img = GHOST_LEFT_1;
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		};

		timer_ghostimgchange = new Timer();
		timer_ghostimgchange.schedule(timertask_ghostimgchange, 0,
				ANIMATION_SINGLE_RATE);
	}

	private void dealWithProgress() {
		// draw different graph at different progress
		if (progress < 11) {
			// pacman keep going, ghost not visible
			current_pacman_location++;
			current_ghost_location = -1;
			current_beans_number--;
			this.invalidate();
		}
		if (progress == 12) {
			// : ghost comes in
			setAnimationStatus(current_pacman_location, current_pacman_img, 1,
					GHOST_RIGHT_1, current_beans_number);
			invalidate();
		}
		if (progress > 13 && progress <= 23) {
			// : pacman keep going, ghost start going
			current_pacman_location++;
			current_ghost_location += 1;
			current_beans_number--;
			invalidate();
		}
		// When progress == 22
		// pacman at 20th bean and ghost at 18th bean
		if (progress == 24) {
			// pacman stay and ghost stay
			current_pacman_img = PACMAN_RIGHT_SMALL_CLOSED;
			invalidate();
		}
		if (progress == 26) {
			// pacman grow
			current_pacman_img = PACMAN_RIGHT_BIG_NORMAL;
			invalidate();
		}
		if (progress == 28) {
			// pacman turnning
			// ghost turning
			current_pacman_img = PACMAN_LEFT_BIG_NORMAL;
			current_ghost_img = GHOST_LEFT_1;
			invalidate();
		}
		if (progress >= 30 && progress < 40) {
			// pacman and ghost start moving
			current_ghost_location--;
			current_pacman_location -= 2;
			current_beans_number = 0;
			invalidate();
		}
		if (progress == 40) {
			current_pacman_location = 1;
			current_pacman_img = PACMAN_LEFT_BIG_CLOSED;
			current_ghost_location = -1;
			current_ghost_img = GHOST_LEFT_2;
			current_beans_number = 0;
			invalidate();
		}
		if (progress == 41) {
			current_pacman_location = 1;
			current_pacman_img = PACMAN_LEFT_BIG_CLOSED;
			current_ghost_location = -1;
			invalidate();
		}
		if (progress == 45) {
			progress = 0;
			current_pacman_location = 1;
			current_pacman_img = PACMAN_RIGHT_SMALL_NORMAL;
			current_ghost_location = -1;
			current_beans_number = 19;
			invalidate();

		}

	}

	// ------------------------------------------------------------------------

	/**
	 * Load images into the system
	 */
	private void loadImages() {
		loadBeans();
		loadPacmanImgs();
		loadGhostImgs();
	}

	private void loadBeans() {
		try {
			// TODO:
			bean_imgs = new Bitmap[2];
			BitmapDrawable[] bds = new BitmapDrawable[2];
			bds[0] = (BitmapDrawable) getResources().getDrawable(
					R.drawable.icon_lv3);
			bean_imgs[0] = rescaleImage(bds[0].getBitmap(), 0.8F);
			bds[1] = (BitmapDrawable) getResources().getDrawable(
					R.drawable.power_sword);
			bean_imgs[1] = rescaleImage(bds[1].getBitmap(), 1.0F);

		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	private void loadPacmanImgs() {
		try {
			pacman_imgs = new Bitmap[7];
			BitmapDrawable bd;
			// small size pacman : 3 imgs
			// small right normal
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_right_normal);
			pacman_imgs[PACMAN_RIGHT_SMALL_NORMAL] = rescaleImage(
					bd.getBitmap(), IMG_SMALL_SIZE);
			// small right open
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_right_open);
			pacman_imgs[PACMAN_RIGHT_SMALL_OPEN] = rescaleImage(bd.getBitmap(),
					IMG_SMALL_SIZE);
			// small right close
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_right_closed);
			pacman_imgs[PACMAN_RIGHT_SMALL_CLOSED] = rescaleImage(
					bd.getBitmap(), IMG_SMALL_SIZE);
			// Big size pacman : 4 imgs
			// big right normal
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_right_normal);
			pacman_imgs[PACMAN_RIGHT_BIG_NORMAL] = rescaleImage(bd.getBitmap(),
					IMG_BIG_SIZE);
			// big left normal
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_left_normal);
			pacman_imgs[PACMAN_LEFT_BIG_NORMAL] = rescaleImage(bd.getBitmap(),
					IMG_BIG_SIZE);
			// big left open
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_left_open);
			pacman_imgs[PACMAN_LEFT_BIG_OPEN] = rescaleImage(bd.getBitmap(),
					IMG_BIG_SIZE);
			// big left close
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.mspacman_left_closed);
			pacman_imgs[PACMAN_LEFT_BIG_CLOSED] = rescaleImage(bd.getBitmap(),
					IMG_BIG_SIZE);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	private void loadGhostImgs() {
		try {
			ghost_imgs = new Bitmap[4];
			BitmapDrawable bd;
			// Load ghost images : 4 imgs
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.blinky_right_1);
			ghost_imgs[GHOST_RIGHT_1] = rescaleImage(bd.getBitmap(),
					IMG_SMALL_SIZE);
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.blinky_right_2);
			ghost_imgs[GHOST_RIGHT_2] = rescaleImage(bd.getBitmap(),
					IMG_SMALL_SIZE);
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.blinky_left_1);
			ghost_imgs[GHOST_LEFT_1] = rescaleImage(bd.getBitmap(),
					IMG_SMALL_SIZE);
			bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.blinky_left_2);
			ghost_imgs[GHOST_LEFT_2] = rescaleImage(bd.getBitmap(),
					IMG_SMALL_SIZE);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * Rescale the image according to the given scale
	 * 
	 * @param old_map
	 * @param rescaleSize
	 * @return
	 */
	private Bitmap rescaleImage(Bitmap old_map, float rescaleSize) {
		try {
			int oldWidth = old_map.getWidth();
			int oldHeight = old_map.getHeight();
			int newWidth = (int) (intervals * rescaleSize);
			int newHeight = (int) (intervals * rescaleSize);
			double scaleWidth = newWidth * 1.0 / oldHeight;
			double scaleHeight = newHeight * 1.0 / oldHeight;
			Matrix matrix = new Matrix();
			matrix.postScale((float) scaleWidth, (float) scaleHeight);
			Bitmap new_map = Bitmap.createBitmap(old_map, 0, 0, oldWidth,
					oldHeight, matrix, true);
			return new_map;
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}
}
