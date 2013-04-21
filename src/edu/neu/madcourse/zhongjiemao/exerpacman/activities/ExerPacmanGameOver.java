package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ScoreBean;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ScoresWrapper;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.FileUtil;

public class ExerPacmanGameOver extends Activity {

	private static final String TAG = "EXER_PACMAN_GAME_OVER";
	private static final int TITLE_ID = 10000;
	private static final int SHOWSCORE_ID = 10001;
	private static final int MIDDLE_RL_ID = 10002;

	private int screenWidth;
	private int screenHeight;

	private RelativeLayout rl;
	private RelativeLayout middle_rl;
	private ImageButton btn_exit;
	private ImageView img_title;
	private TextView tv_ShowScore;
	private TextView tv_Score;
	private Bitmap bm_showscore;
	private Bitmap bm_title;
	private Bitmap bm_exit;

	private int mGameOverType;
	private int mScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGameOverType = getIntent().getIntExtra(KEY_GAME_OVER, SUB_MSG_GAME_OVER_DIE);
		mScore = getIntent().getIntExtra(KEY_SCORE, 0);
		getScreenParams();
		loadImages();
		initializeViews();
		ExerPacmanMusic.play(this, R.raw.exer_pacman_game_over, false);
		saveScore();
	}
	
	@Override
	protected void onResume() {
		ExerPacmanMusic.resume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		ExerPacmanMusic.pause();
		super.onPause();
	}
	
	// --------------------------------------------------------------
	
	private void backToMainBoard() {
		// close game panel
		Intent broadcast = new Intent();
		broadcast.setAction(BC_ACTION_QUIT);
		this.sendBroadcast(broadcast);
		// start a new main board
		Intent intent = new Intent(this, ExerPacman.class);
		startActivity(intent);
		overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
		// destroy self
		finish();
	}
	
	private void saveScore() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Gson gson = new Gson();
				ScoreBean bean = new ScoreBean(mScore, new Date());
				if (FileUtil.isInternalFileExist(ExerPacmanGameOver.this, SCORE_FILE)) {
					String plain = FileUtil.readFileFromInternal(ExerPacmanGameOver.this, SCORE_FILE);
					ScoresWrapper wrapper = gson.fromJson(plain, ScoresWrapper.class);
					wrapper.getInnerList().add(bean);
					FileUtil.deleteInternalFile(ExerPacmanGameOver.this, SCORE_FILE);
					FileUtil.writeFileToInternal(ExerPacmanGameOver.this, SCORE_FILE, gson.toJson(wrapper));
				} else {
					ScoresWrapper wrapper = new ScoresWrapper(new ArrayList<ScoreBean>());
					wrapper.getInnerList().add(bean);
					FileUtil.writeFileToInternal(ExerPacmanGameOver.this, SCORE_FILE, gson.toJson(wrapper));
				}
			}
		}).start();
	}

	private void getScreenParams() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void initializeViews() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		rl = new RelativeLayout(this);
		rl.setBackgroundResource(R.drawable.bg_texture);
		initializeTitle(rl);
		initializeScorePart(rl);
		initializeBtnExit(rl);
		setContentView(rl);
	}

	private void initializeTitle(RelativeLayout rl) {
		img_title = new ImageView(this);
		img_title.setImageBitmap(bm_title);
		img_title.setId(TITLE_ID);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl.addView(img_title, rllp);

	}

	private void initializeScorePart(RelativeLayout rl) {
		middle_rl = new RelativeLayout(this);
		middle_rl.setId(MIDDLE_RL_ID);
		initializeShowScore(middle_rl);
		initializeScore(middle_rl);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addView(middle_rl, rllp);
	}

	private void initializeShowScore(RelativeLayout rl) {
		tv_ShowScore = new TextView(this);
		tv_ShowScore.setMaxHeight(screenHeight / 5);
		tv_ShowScore.setMaxWidth(screenWidth / 10);
		tv_ShowScore.setBackgroundDrawable(new BitmapDrawable(bm_showscore));
		tv_ShowScore.setId(SHOWSCORE_ID);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addView(tv_ShowScore, rllp);
	}

	private void initializeScore(RelativeLayout rl) {
		tv_Score = new TextView(this);
		tv_Score.setText(mScore + "");
		tv_Score.setTextColor(Color.RED);
		tv_Score.setTextSize(screenWidth / 20);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.RIGHT_OF, tv_ShowScore.getId());
		rllp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addView(tv_Score, rllp);

	}

	private void initializeBtnExit(RelativeLayout rl) {
		btn_exit = new ImageButton(this);
		btn_exit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
					backToMainBoard();
				}
				return true;
			}

		});
		btn_exit.setBackgroundDrawable(new BitmapDrawable(bm_exit));
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		// rllp.addRule(RelativeLayout.CENTER_VERTICAL);
		rllp.topMargin = this.screenHeight / 2;
		rl.addView(btn_exit, rllp);
	}

	/**
	 * Load images
	 */
	private void loadImages() {
		BitmapDrawable bd_showscore = (BitmapDrawable) getResources().getDrawable(R.drawable.game_over_score);
		BitmapDrawable bd_title;
		if (mGameOverType == SUB_MSG_GAME_OVER_DIE)
			bd_title = (BitmapDrawable) getResources().getDrawable(R.drawable.game_over_die);
		else
			bd_title = (BitmapDrawable) getResources().getDrawable(R.drawable.game_over_timeup);
		BitmapDrawable bd_back = (BitmapDrawable) getResources().getDrawable(R.drawable.game_over_back);
		bm_showscore = rescaleImage(bd_showscore.getBitmap(), 0.75F);
		bm_title = rescaleImage(bd_title.getBitmap(), 1.0F);
		bm_exit = rescaleImage(bd_back.getBitmap(), 0.5F);
	}

	private Bitmap rescaleImage(Bitmap old_map, float scale) {
		int oldWidth = old_map.getWidth();
		int oldHeight = old_map.getHeight();
		int newWidth = (int) (screenWidth * scale);
		int newHeight = oldHeight * newWidth / oldWidth;
		float scaleWidth = newWidth * 1.0F / oldWidth;
		float scaleHeight = newHeight * 1.0F / oldHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
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
	
	/**
	 * ?·å????ä½?????æ³?	 * @param bitmap ???è½?????è§??ä½??
	 * @param pixels ?????º¦?°ï??°å?è¶?¤§ï¼??è§??å¤?	 * @return å¤????????ä½??
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

}
