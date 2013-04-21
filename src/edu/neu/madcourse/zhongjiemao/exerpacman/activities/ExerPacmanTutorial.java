package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;

public class ExerPacmanTutorial extends FragmentActivity implements
		OnTouchListener {

	private static final String TAG = "PacmanTutorial";
	private static final int VP_MPAGER_ID = 1000;
	private static final int BTN_START_ID = 1001;
	private static final int BTN_EXIT_ID = 1002;

	// Screen Width and Height
	private int screenWidth;
	private int screenHeight;
	private int numberOfPagers;

	// Pager Parameters
	private MyAdapter mAdapter;
	private ViewPager mPager;
	// Other views
	private RelativeLayout rl;
	private PositionView pv;
	private ImageButton btn_start;
	private Bitmap img_play;
	private Paint paint;
	
	private boolean isBackToMain = false;

	private static Drawable[] tutorial_imgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		numberOfPagers = 3;
		initPaint();
		getScreenParameters();
		loadImages();
		initializViews();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "Pacman Tutorial Pause");
		if (!isBackToMain) {
			ExerPacmanMusic.pause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "Pacman Tutorial Resume");
		isBackToMain = false;
		if (tutorial_imgs == null)
			loadTutorialImgs();
		ExerPacmanMusic.resume();
		super.onResume();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getId() == btn_start.getId()) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v.getId() == btn_start.getId()) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				isBackToMain = true;
				this.finish();
			}
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isBackToMain = true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// -------------------------- Private Methods -----------------------------

	private void initPaint() {
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	/**
	 * Initialize Views layout
	 */
	private void initializViews() {
		rl = new RelativeLayout(this);
		initializePagers(rl);
		initializePositionView(rl);
		initializeBtnStart(rl);
		setContentView(rl);
	}

	/**
	 * Get screen width and height
	 */
	private void getScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void initializePagers(RelativeLayout rl) {
		// create adapter
		mAdapter = new MyAdapter(getSupportFragmentManager());
		// create pager
		mPager = new ViewPager(this);
		// set adapter
		mPager.setAdapter(mAdapter);
		// set pager id
		mPager.setId(VP_MPAGER_ID);
		// set pager OnPageChangeListener
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				pv.pagePositionChanged(arg0 + 1);
				if (arg0 == numberOfPagers - 1) {
					btn_start.setVisibility(View.VISIBLE);
				} else {
					btn_start.setVisibility(View.INVISIBLE);
				}

			}
		});
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		rl.addView(mPager, rllp);
	}

	private void initializePositionView(RelativeLayout rl) {
		pv = new PositionView(this, this.screenWidth, this.screenHeight,
				this.numberOfPagers);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// TODO: set pv location
		rllp.topMargin = screenHeight * 9 / 10;
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl.addView(pv, rllp);
	}

	private void initializeBtnStart(RelativeLayout rl) {
		btn_start = new ImageButton(this);
		btn_start.setId(BTN_START_ID);
		btn_start.setOnTouchListener(this);
		btn_start.setBackgroundDrawable(new BitmapDrawable(img_play));
		btn_start.setVisibility(View.INVISIBLE);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.topMargin = screenHeight * 8 / 10;
		rl.addView(btn_start, rllp);
	}

	/**
	 * Load images
	 */
	private void loadImages() {
		BitmapDrawable bd_play = (BitmapDrawable) getResources().getDrawable(
				R.drawable.tutorial_play);
		img_play = rescaleImage(bd_play.getBitmap());
	}

	private Bitmap rescaleImage(Bitmap old_map) {
		int oldWidth = old_map.getWidth();
		int oldHeight = old_map.getHeight();
		int newWidth = screenWidth / 4;
		int newHeight = screenWidth / 4;
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

	private void loadTutorialImgs() {
		tutorial_imgs = new Drawable[3];
		tutorial_imgs[0] = getResources().getDrawable(
				R.drawable.tutorial_swipe_mode);
		tutorial_imgs[1] = getResources().getDrawable(
				R.drawable.tutorial_sensor_mode);
		tutorial_imgs[2] = getResources().getDrawable(
				R.drawable.tutorial_settings);
	}

	// ----------------------- Private Classes --------------------------------

	/**
	 * FragmentPagerAdapter
	 * 
	 * @author Zhongjie Mao
	 * 
	 */
	private class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return numberOfPagers;
		}

		@Override
		public Fragment getItem(int position) {
			return (new ArrayListFragment(position));
		}
	}

	@SuppressLint("ValidFragment")
	/**
	 * ListFragment
	 * @author Zhongjie Mao
	 *
	 */
	private class ArrayListFragment extends ListFragment {
		private int mNum;

		public ArrayListFragment(int num) {
			mNum = num;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstancesState) {
			View v = inflater.inflate(R.layout.fragment_pager_lists, container,
					false);
			ImageView iv = (ImageView) v
					.findViewById(R.id.exer_pacman_tutorial_images);
			iv.setBackgroundDrawable(selectImage(mNum));
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			String[] examples = { "Grap", "Hello", "Sandwishes", "Hotdogs" };
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, examples));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);
		}

		private Drawable selectImage(int mNum) {
			Drawable res;
			if (tutorial_imgs == null)
				loadTutorialImgs();
			res = tutorial_imgs[mNum];
			return res;
		}
	}

	/**
	 * PositionView to show the position of tutorial activity
	 * 
	 * @author Zhongjie Mao
	 * 
	 */
	private class PositionView extends View {
		private static final int NORMAL_COLOR = 0x50FFFFFF;
		private static final int CURRENT_COLOR = 0x90FFFFFF;
		private int pagePos = 0;
		private int viewWidth;
		private int viewHeight;
		private int numberOfPagers;
		private int gap;
		private int radius;

		public PositionView(Context context, int width, int height,
				int numberOfPages) {
			super(context);
			initializeParams(width, height, numberOfPages);
		}

		@Override
		public void onMeasure(int width, int height) {
			setMeasuredDimension(viewWidth, viewHeight);
		}

		@Override
		public void onDraw(Canvas canvas) {
			// TODO: draw circles
			paint.setColor(NORMAL_COLOR);
			for (int i = 1; i <= this.numberOfPagers; i++) {
				canvas.drawCircle(i * gap, viewHeight / 2, this.radius, paint);
			}
			paint.setColor(CURRENT_COLOR);
			canvas.drawCircle(pagePos * gap, viewHeight / 2, this.radius, paint);
		}

		public void pagePositionChanged(int newPosition) {
			if (newPosition < 1 || newPosition > this.numberOfPagers)
				return;
			pagePos = newPosition;
			invalidate();
		}

		private void initializeParams(int screenWidth, int screenHeight,
				int numberOfPagers) {
			this.pagePos = 1;
			this.numberOfPagers = numberOfPagers;
			this.viewWidth = screenWidth * 4 / 5;
			this.viewHeight = screenHeight / 15;
			this.gap = this.viewWidth / (this.numberOfPagers + 1);
			this.radius = this.viewHeight / 5;
		}
	}
}