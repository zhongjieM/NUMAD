package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.BC_ACTION_QUIT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.INIT_GAME_STATE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.IS_SWIPE_MODE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.KEY_GAME_OVER;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.KEY_MAZE_ID;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.KEY_MODE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.KEY_SCORE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.LEVEL_LOCK_FILE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MODE_SENSOR;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MODE_SWIPE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_SHOW_GAME_OVER;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_SHOW_JUNCTION_HINT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_LEVEL;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_LEVEL_LOCK;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_LIVES;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_ORIENTATION;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_SCORE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MSG_UPDATE_TIME;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.NUM_LIVES;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.PAUSE_GAME_PEND;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.PAUSE_QUIT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.PAUSE_RESTART;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.PAUSE_RESUME;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SOUND_EAT_FRUIT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SOUND_EAT_GHOST;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SOUND_EAT_LOSE_LIVES;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SOUND_EAT_SWORD;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SQUAT_ENABLED;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_EAT_FRUIT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_EAT_GHOST;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_EAT_SWORD;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_GAME_OVER_DIE;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_GAME_OVER_TIME_UP;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SUB_MSG_LOSE_LIVES;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.TURN_BOTH;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.TURN_LEFT;
import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.TURN_RIGHT;

import java.util.BitSet;
import java.util.EnumMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.TCApplication;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.BaseController;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.SensorController;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.StarterGhosts;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.SwipeController;
import edu.neu.madcourse.zhongjiemao.exerpacman.controllers.SwipeInput;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.GHOST;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.SquatListener;
import edu.neu.madcourse.zhongjiemao.exerpacman.sensors.SquatListener.OnSquatListener;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.FileUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.ScaleUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.Sound;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.GameView;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.Images;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.OrientationView;

public class ExerPacmanGame extends Activity {

	private static String TAG = "EXER_PACMAN_GAME";

	private static Images mImages;
	private static GameEngine mEngine;
	private static Handler mHandler;

	private GameView mGameView;
	private OrientationView mOrientationView;
	private TextView mTimeView;
	private TextView mScoreView;
	private TextView mHPTextView;
	private ImageView mLeftArrow;
	private ImageView mRightArrow;
	private ProgressBar mHPBar;

	private BaseController<MOVE> pacmanController;
	private BaseController<EnumMap<GHOST, MOVE>> ghostController;

	private BroadcastReceiver mReceiver;
	private SquatListener mSquatListener;

	private Vibrator mVibrator;
	// private WakeLock mWakeLock;
	private Sound mSound;

	private int mMode = 0;
	private int initMapID = 0;
	private boolean isSurfaceDestroyed = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMode = getIntent().getIntExtra(KEY_MODE, 0);
		initMapID = getIntent().getIntExtra(KEY_MAZE_ID, 0);
		setupScale();
		initHandler();
		initImages();
		initSound();
		initGameEngine();
		initSquatListener();
		initVibrator();
		setContentView(R.layout.exer_pacman_game);
		initViews();
		initBroadcastReceiver();
		ExerPacmanMusic.playOneByOne(this, R.raw.exer_pacman_beginning,
				R.raw.exer_pacman_bg);
	}

	@Override
	protected void onResume() {
		// TODO del
		if (mGameView != null) {
			if (mGameView.getSurfaceDrawer() != null) {
				mGameView.getSurfaceDrawer().setPause(false);
			}
		}
		// TODO del

		initController();
		ExerPacmanMusic.resume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO del
		if (mGameView != null) {
			if (mGameView.getSurfaceDrawer() != null) {
				mGameView.getSurfaceDrawer().setPause(true);
			}
		}
		// TODO del

		ExerPacmanMusic.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		releaseSensors();
		releaseBroadcastReceiver();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doPause(PAUSE_GAME_PEND);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void doPause(String pauseType) {
		mGameView.getSurfaceDrawer().setPause(true);
		ExerPacmanMusic.pause();
		new AlertDialog.Builder(this)
				.setTitle(null)
				.setItems(R.array.exer_pacman_pause,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == PAUSE_RESUME) {
									ExerPacmanMusic.resume();
									mGameView.getSurfaceDrawer()
											.setPause(false);

								} else if (which == PAUSE_RESTART) {
									ExerPacmanMusic.play(ExerPacmanGame.this,
											R.raw.exer_pacman_bg, true);
									mEngine.setGameState(INIT_GAME_STATE[mEngine
											.getMazeIndex()]);
									mGameView.getSurfaceDrawer()
											.setPause(false);

								} else if (which == PAUSE_QUIT) {
									quit();
								}
							}
						})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						ExerPacmanMusic.resume();
						mGameView.getSurfaceDrawer().setPause(false);
					}
				}).show();
	}

	private void quit() {
		// close game panel
		Intent broadcast = new Intent();
		broadcast.setAction(BC_ACTION_QUIT);
		this.sendBroadcast(broadcast);
		// start a new main board
		Intent intent = new Intent(this, ExerPacman.class);
		startActivity(intent);
		overridePendingTransition(R.anim.grow_from_middle,
				R.anim.shrink_to_middle);
		// destroy self
		finish();
	}

	public void setSurfaceDestroyed(boolean flag) {
		isSurfaceDestroyed = flag;
	}

	public GameEngine getGameEngine() {
		return mEngine;
	}

	public Images getImages() {
		return mImages;
	}

	public BaseController<MOVE> getPacmanController() {
		return pacmanController;
	}

	public BaseController<EnumMap<GHOST, MOVE>> getGhostController() {
		return ghostController;
	}

	private void setupScale() {
		Bitmap template = BitmapFactory.decodeResource(getResources(),
				R.drawable.maze_a);
		ScaleUtil.setLocalMazeW(template.getWidth());
		ScaleUtil.setLocalMazeH(template.getHeight());
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		ScaleUtil.calcRatio(dm.widthPixels, dm.heightPixels);
	}

	private void initImages() {
		if (mImages == null) {
			mImages = new Images(this);
		}
	}

	private void initGameEngine() {
		if (mMode == MODE_SWIPE)
			IS_SWIPE_MODE = true;
		else
			IS_SWIPE_MODE = false;
		mEngine = new GameEngine(0, initMapID, this, mHandler);
	}

	private void initSound() {
		mSound = ((TCApplication) getApplication()).getSound();
	}

	private void initController() {
		if (!isSurfaceDestroyed)
			return;
		if (mMode == MODE_SENSOR) {
			// unregister sensors used in the old controller
			if (pacmanController != null) {
				((SensorController) pacmanController).stopSensor();
			}
			// init a new one
			pacmanController = new SensorController(this, mHandler);
			mOrientationView.setVisibility(View.VISIBLE);
		} else {
			pacmanController = new SwipeController(new SwipeInput(mGameView));
			mOrientationView.setVisibility(View.GONE);
		}
		ghostController = new StarterGhosts();
		isSurfaceDestroyed = false;
	}

	private void initSquatListener() {
		if (SQUAT_ENABLED) {
			mSquatListener = new SquatListener(this);
			mSquatListener.setOnSquatListener(new OnSquatListener() {
				@Override
				public void onSquat() {
					if (!mEngine.isSquatted()) {
						Log.d(TAG, "SQUAT");
						mEngine.setSquatted(true);
					}
				}
			});
		}
	}

	private void releaseSensors() {
		if (mMode == MODE_SENSOR) {
			((SensorController) pacmanController).stopSensor();
		}
		if (mSquatListener != null) {
			mSquatListener.pause();
		}
	}

	// private void initWakeLock() {
	// mWakeLock = ((PowerManager)getSystemService(POWER_SERVICE))
	// .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
	// mWakeLock.acquire();
	// }

	// private void releaseWakeLock() {
	// if (mWakeLock != null) {
	// mWakeLock.release();
	// }
	// }

	private void initVibrator() {
		mVibrator = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
	}

	public void vibrate() {
		mVibrator.vibrate(30);
	}

	private void initViews() {
		mGameView = (GameView) findViewById(R.id.exer_pacman_game_view);
		mOrientationView = (OrientationView) findViewById(R.id.exer_pacman_orientation_view);
		mTimeView = (TextView) findViewById(R.id.exer_pacman_time);
		mScoreView = (TextView) findViewById(R.id.exer_pacman_score);
		mHPTextView = (TextView) findViewById(R.id.exer_pacman_hp);
		mHPTextView.setTextColor(Color.GREEN);
		mLeftArrow = (ImageView) findViewById(R.id.exer_pacman_left_arrow);
		mRightArrow = (ImageView) findViewById(R.id.exer_pacman_right_arrow);
		mHPBar = (ProgressBar) findViewById(R.id.exer_pacman_hp_bar);
		initImageViews(mLeftArrow, mRightArrow);
		initHPBar(mHPBar);
	}

	private void initHPBar(ProgressBar bar) {
		int mw = (int) ((ScaleUtil.getActualScreenW() * 2) / 3);
		LayoutParams params = (LayoutParams) bar.getLayoutParams();
		params.width = mw;
		bar.setLayoutParams(params);
	}

	private void initImageViews(ImageView left, ImageView right) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inPreferredConfig = Config.RGB_565;
		Bitmap originLeftArrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.arwl, options);
		Bitmap originRightArrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.arwr, options);
		float ratio = ((float) (originLeftArrow.getHeight()))
				/ ((float) (originLeftArrow.getWidth()));
		float dstW = ScaleUtil.getActualScreenW() / 6f;
		float dstH = dstW * ratio;
		Bitmap scaledLeftArrow = Bitmap.createScaledBitmap(originLeftArrow,
				(int) dstW, (int) dstH, true);
		Bitmap scaledRightArrow = Bitmap.createScaledBitmap(originRightArrow,
				(int) dstW, (int) dstH, true);
		originLeftArrow.recycle();
		originRightArrow.recycle();
		left.setImageBitmap(scaledLeftArrow);
		left.setScaleType(ScaleType.CENTER_INSIDE);
		left.setAlpha(60);
		right.setImageBitmap(scaledRightArrow);
		right.setScaleType(ScaleType.CENTER_INSIDE);
		right.setAlpha(60);
	}

	private void initBroadcastReceiver() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(BC_ACTION_QUIT);
		this.registerReceiver(mReceiver, filter);
	}

	private void releaseBroadcastReceiver() {
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_UPDATE_LEVEL_LOCK:
					updateLevelLock(msg.arg1);
					break;

				case MSG_SHOW_JUNCTION_HINT:
					showRotationHint(msg.arg1);
					break;

				case MSG_UPDATE_SCORE:
					updateScore(msg.arg1, msg.arg2);
					break;

				case MSG_UPDATE_LEVEL:
					break;

				case MSG_UPDATE_TIME:
					updateTime(msg.arg1);
					break;

				case MSG_UPDATE_LIVES:
					updateLives(msg.arg1, msg.arg2);
					break;

				case MSG_SHOW_GAME_OVER:
					handleGameOver(msg.arg1);
					break;

				case MSG_UPDATE_ORIENTATION:
					updateOrientation(msg.obj);
					break;

				default:
					break;
				}
			}
		};
	}

	private void updateOrientation(Object values) {
		mOrientationView.onOrientationChanged((float[]) values);
	}

	private void updateLevelLock(int levelIndex) {
		if (FileUtil.isInternalFileExist(this, LEVEL_LOCK_FILE)) {
			String plainData = FileUtil.readFileFromInternal(this,
					LEVEL_LOCK_FILE);
			if (plainData == null) {
				Log.e(TAG, "ERROR file reading!");
				return;
			}
			Gson gson = new Gson();
			BitSet bits = gson.fromJson(plainData, BitSet.class);
			if (!bits.get(levelIndex)) {
				bits.set(levelIndex, true);
				plainData = gson.toJson(bits);
				FileUtil.deleteInternalFile(this, LEVEL_LOCK_FILE);
				FileUtil.writeFileToInternal(this, LEVEL_LOCK_FILE, plainData);
				Log.d(TAG, "update level lock file finished");
			}
		}
	}

	private void updateTime(int time) {
		if (time <= 60) {
			mTimeView.setTextColor((time % 2) == 0 ? Color.RED : Color.LTGRAY);
			mTimeView.setText("Time: " + formatedTime(time));
		} else {
			mTimeView.setText("Time: " + formatedTime(time));
		}
	}

	private String formatedTime(int time) {
		int minutes = time / 60;
		int seconds = time - minutes * 60;
		String secStr;
		if (seconds / 10 == 0)
			secStr = "0" + seconds;
		else
			secStr = "" + seconds;
		return minutes + " : " + secStr;
	}

	private void updateScore(int score, int subMsg) {
		mScoreView.setText("Score: " + score);
		switch (subMsg) {
		case SUB_MSG_EAT_FRUIT:
			mSound.play(SOUND_EAT_FRUIT);
			break;

		case SUB_MSG_EAT_SWORD:
			mSound.play(SOUND_EAT_SWORD);
			break;

		case SUB_MSG_EAT_GHOST:
			mSound.play(SOUND_EAT_GHOST);
			break;

		default:
			break;
		}
	}

	private void showRotationHint(int turn) {
		if (turn == TURN_LEFT) {
			mLeftArrow.setAlpha(255);
			mRightArrow.setAlpha(60);
		} else if (turn == TURN_RIGHT) {
			mRightArrow.setAlpha(255);
			mLeftArrow.setAlpha(60);
		} else if (turn == TURN_BOTH) {
			mLeftArrow.setAlpha(255);
			mRightArrow.setAlpha(255);
		} else {
			mLeftArrow.setAlpha(60);
			mRightArrow.setAlpha(60);
		}
	}

	private void updateLives(int lives, int subMsg) {
		// update lives
		mHPBar.setProgress(lives);
		if (lives >= 30) {
			mHPTextView.setTextColor(Color.GREEN);
		} else if (lives > 10 && lives < 30) {
			mHPTextView.setTextColor(Color.YELLOW);
		} else {
			mHPTextView.setTextColor(Color.RED);
		}
		mHPTextView.setText("HP: " + lives + "/" + NUM_LIVES);
		// handle sub msg
		switch (subMsg) {
		case SUB_MSG_LOSE_LIVES:
			mSound.play(SOUND_EAT_LOSE_LIVES);
			break;

		default:
			break;
		}
	}

	private void handleGameOver(int subMsg) {
		playGameOverMusic(subMsg);
		Intent intent = new Intent(this, ExerPacmanGameOver.class);
		intent.putExtra(KEY_SCORE, mEngine.getScore());
		if (subMsg == SUB_MSG_GAME_OVER_DIE) {
			mHPBar.setProgress(0);
			mHPTextView.setText("HP: 0/50");
			intent.putExtra(KEY_GAME_OVER, SUB_MSG_GAME_OVER_DIE);
			startActivity(intent);
		} else {
			intent.putExtra(KEY_GAME_OVER, SUB_MSG_GAME_OVER_TIME_UP);
			startActivity(intent);
		}
	}

	private void playGameOverMusic(int subMsg) {
		if (subMsg == SUB_MSG_GAME_OVER_DIE) {
			ExerPacmanMusic.play(this, R.raw.exer_pacman_death, false);
		} else if (subMsg == SUB_MSG_GAME_OVER_TIME_UP) {
			ExerPacmanMusic.play(this, R.raw.exer_pacman_death, false);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
