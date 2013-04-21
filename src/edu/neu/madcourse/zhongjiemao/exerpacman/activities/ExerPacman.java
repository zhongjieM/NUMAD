package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.FileUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.BGAnimView;

public class ExerPacman extends Activity implements OnTouchListener {

	private static final int BTN_PLAY_SENSOR = 10;
	private static final int BTN_PLAY_SWIPE = 11;
	private static final int NUMBER_OF_BUTTONS = 4;

	private int screenWidth;
	private int screenHeight;
	private RelativeLayout rl;

	private BroadcastReceiver mReceiver;

	private WebView wv_title;
	private Button btnPlaySwipeMode;
	private Button btnPlaySensorMode;
	private Bitmap btnBg;
	private ImageButton[] imgBtns;
	private Bitmap[] bitMapForImgBtns;
	private BGAnimView epbv;

	private static boolean pause = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getScreenParams();
		initializeLayout();
		ExerPacmanMusic.play(this, R.raw.exer_pacman_mainboard_bg, true);
		initBroadcastReceiver();
	}

	@Override
	protected void onPause() {
		if (epbv != null) {
			epbv.onPause();
		}
		if (pause) {
			ExerPacmanMusic.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (epbv != null) {
			epbv.onResume();
		}
		ExerPacmanMusic.resume();
		pause = true;
		super.onResume();
		checkFirstTimeUse();
	}

	@Override
	protected void onDestroy() {
		ExerPacmanMusic.stop(this);
		releaseBroadcastReceiver();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.exer_pacman_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exer_pacman_menu_settings:
			startActivity(new Intent(this, ExerPacmanPrefs.class));
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (viewId >= 0 && viewId <= 3 || viewId == BTN_PLAY_SENSOR
					|| viewId == BTN_PLAY_SWIPE) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				return true;
			}
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			v.getBackground().setColorFilter(
					new ColorMatrixColorFilter(BT_NOT_SELECTED));
			v.setBackgroundDrawable(v.getBackground());
			switch (viewId) {
			case 0:
				// settings
				pause = true;
				startActivity(new Intent(this, ExerPacmanPrefs.class));
				return true;
			case 1:
				// top score list
				pause = false;
				startActivity(new Intent(this, ExerPacmanRanks.class));
				return true;
			case 2:
				// tutorial
				pause = false;
				startActivity(new Intent(this, ExerPacmanTutorial.class));
				return true;
			case 3:
				// acknowledgement
				pause = false;
				startActivity(new Intent(this, ExerPacmanAck.class));
				return true;
			case BTN_PLAY_SENSOR:
				pause = false;
				forwardToMapNav(MODE_SENSOR);
				return true;
			case BTN_PLAY_SWIPE:
				pause = false;
				forwardToMapNav(MODE_SWIPE);
				return true;
			default:
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			checkQuit();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void checkFirstTimeUse() {
		if (!FileUtil.isInternalFileExist(this, FIRST_TIME_CHECK_FILE)) {
			FileUtil.writeFileToInternal(this, FIRST_TIME_CHECK_FILE,
					"first_time");
			startActivity(new Intent(this, ExerPacmanTutorial.class));
		}
	}

	private void forwardToMapNav(int mode) {
		Intent intent = new Intent(this, ExerPacmanMapNav.class);
		intent.putExtra(KEY_MODE, mode);
		startActivity(intent);
	}

	private void checkQuit() {
		new AlertDialog.Builder(this)
				.setTitle("Exit")
				.setMessage("Are sure to quit Exer Pacman?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
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

	// ------------------ Private Methods -------------------------------------

	private void getScreenParams() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screenWidth = dm.widthPixels;
		this.screenHeight = dm.heightPixels;
	}

	private void initializeLayout() {
		rl = new RelativeLayout(this);
		rl.setBackgroundColor(Color.BLACK);
		rl.setBackgroundResource(R.drawable.bg_texture);
		loadImages();
		initializeWebViewTitle(rl);
		initializeBtnSwipeMode(rl);
		initializeBtnSensorMode(rl);
		initializeImgBtns(rl);
		initializeAnimationView(rl);
		setContentView(rl);
	}

	private void initializeWebViewTitle(RelativeLayout rl) {
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.addRule(RelativeLayout.ALIGN_TOP);
		rllp.topMargin = screenHeight / 10;

		wv_title = new WebView(this);
		wv_title.loadUrl("file:///android_asset/logo.html");
		wv_title.setBackgroundColor(Color.TRANSPARENT);
		rl.addView(wv_title, rllp);
	}

	private void initializeBtnSwipeMode(RelativeLayout rl) {
		// initialize play swipe mode button
		btnPlaySwipeMode = new Button(this);
		btnPlaySwipeMode.setId(BTN_PLAY_SWIPE);
		btnPlaySwipeMode.setOnTouchListener(this);
		btnPlaySwipeMode.setText("Swipe Mode");
		initButtonStyle(btnPlaySwipeMode);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.addRule(RelativeLayout.CENTER_VERTICAL);

		rl.addView(btnPlaySwipeMode, rllp);
	}

	private void initializeBtnSensorMode(RelativeLayout rl) {
		// initialize play sensor mode button
		btnPlaySensorMode = new Button(this);
		btnPlaySensorMode.setId(BTN_PLAY_SENSOR);
		btnPlaySensorMode.setOnTouchListener(this);
		btnPlaySensorMode.setText("Sensor Mode");
		initButtonStyle(btnPlaySensorMode);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.BELOW, btnPlaySwipeMode.getId());
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		rl.addView(btnPlaySensorMode, rllp);
	}

	private void initButtonStyle(Button btn) {
		btn.setWidth(screenWidth / 3);
		btn.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn.setTypeface(Typeface.SERIF);
		btn.setTextColor(Color.WHITE);
		btn.setTextSize(13);
		btn.setBackgroundDrawable(new BitmapDrawable(btnBg));
	}

	private void initializeAnimationView(RelativeLayout rl) {
		epbv = new BGAnimView(this, screenWidth, screenHeight);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.topMargin = screenHeight * 7 / 10 + screenWidth * 1 / 20;
		rl.addView(epbv, rllp);
	}

	private void initializeImgBtns(RelativeLayout rl) {
		RelativeLayout.LayoutParams rllp;

		imgBtns = new ImageButton[NUMBER_OF_BUTTONS];
		int intervals = screenWidth / (NUMBER_OF_BUTTONS + 1);
		// TODO
		if (bitMapForImgBtns == null)
			return;
		for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
			// initialize buttons
			imgBtns[i] = new ImageButton(this);
			imgBtns[i].setBackgroundDrawable(new BitmapDrawable(
					bitMapForImgBtns[i]));
			imgBtns[i].setId(i);
			imgBtns[i].setOnTouchListener(this);
			// add layout rule for this button
			rllp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			// rllp.addRule(RelativeLayout.BELOW, imgBtn_play.getId());
			rllp.topMargin = screenHeight * 7 / 10;

			rllp.leftMargin = (i + 1) * intervals
					- bitMapForImgBtns[i].getWidth() / 4;

			rl.addView(imgBtns[i], rllp);
		}
	}

	private void loadImages() {
		BitmapDrawable[] bds = new BitmapDrawable[NUMBER_OF_BUTTONS];
		this.bitMapForImgBtns = new Bitmap[NUMBER_OF_BUTTONS];
		bds[0] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.exer_pacman_settings);
		bds[1] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.exer_pacman_ranking);
		bds[2] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.exer_pacman_tutorials);
		bds[3] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.exer_pacman_ack);
		for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
			bitMapForImgBtns[i] = rescaleImage(bds[i].getBitmap());
		}
		BitmapDrawable swBg = (BitmapDrawable) getResources().getDrawable(
				R.drawable.btn_bg);
		btnBg = rescaleImage(swBg.getBitmap(), 2, 4);
	}

	private Bitmap rescaleImage(Bitmap old_map) {
		int oldWidth = old_map.getWidth();
		int oldHeight = old_map.getHeight();
		int newWidth = screenWidth / 5;
		int newHeight = screenWidth / 5;
		double scaleWidth = newWidth * 1.0 / oldWidth;
		double scaleHeight = newHeight * 1.0 / oldHeight;
		Matrix matrix = new Matrix();
		matrix.postScale((float) scaleWidth, (float) scaleHeight);
		System.out.println("OldWidth:" + oldWidth + " : " + "OldHeight: "
				+ oldHeight + " NewWidth: " + newWidth + " : " + "NewHeight: "
				+ newHeight);
		System.out.println("ScaleWidth: " + scaleWidth + " : "
				+ "ScaleHeight: " + scaleHeight);
		System.out.println(matrix.toString());
		Bitmap new_map = Bitmap.createBitmap(old_map, 0, 0, oldWidth,
				oldHeight, matrix, true);
		return new_map;
	}

	private Bitmap rescaleImage(Bitmap old_map, int widthScale, int heightScale) {
		int oldWidth = old_map.getWidth();
		int oldHeight = old_map.getHeight();
		int newWidth = screenWidth / widthScale;
		int newHeight = screenWidth / heightScale;
		double scaleWidth = newWidth * 1.0 / oldWidth;
		double scaleHeight = newHeight * 1.0 / oldHeight;
		Matrix matrix = new Matrix();
		matrix.postScale((float) scaleWidth, (float) scaleHeight);
		System.out.println("OldWidth:" + oldWidth + " : " + "OldHeight: "
				+ oldHeight + " NewWidth: " + newWidth + " : " + "NewHeight: "
				+ newHeight);
		System.out.println("ScaleWidth: " + scaleWidth + " : "
				+ "ScaleHeight: " + scaleHeight);
		System.out.println(matrix.toString());
		Bitmap new_map = Bitmap.createBitmap(old_map, 0, 0, oldWidth,
				oldHeight, matrix, true);
		return new_map;
	}

	private Bitmap rescaleImage1(Bitmap oldImage) {
		int newWidth = screenWidth;
		int newHeight = screenWidth / 3;
		Bitmap new_map = Bitmap.createScaledBitmap(oldImage, newWidth,
				newHeight, true);
		return new_map;
	}
}
