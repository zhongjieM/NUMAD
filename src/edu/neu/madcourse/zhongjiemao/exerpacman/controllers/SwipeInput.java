package edu.neu.madcourse.zhongjiemao.exerpacman.controllers;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SwipeInput implements OnTouchListener {

	public static final int NULL = 0;
	public static final int UP = 1;
	public static final int RIGHT = 2;
	public static final int DOWN = 3;
	public static final int LEFT = 4;

	private int mDirection = NULL;
	private float downX;
	private float downY;
	private float upX;
	private float upY;
	private View mSurface;
	
	public SwipeInput(View surface) {
		mSurface = surface;
		mSurface.setOnTouchListener(this);
	}
	
	public int getDirection() {
		return mDirection;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;

		case MotionEvent.ACTION_UP:
			upX = event.getX();
			upY = event.getY();
			float x = Math.abs(upX - downX);
			float y = Math.abs(upY - downY);
			double z = Math.sqrt(x * x + y * y);
			int angle = Math.round((float) (Math.asin(y / z) / Math.PI * 180));
			if (upY < downY && angle > 45)
				mDirection = UP;
			else if (upY > downY && angle > 45)
				mDirection = DOWN;
			else if (upX < downX && angle <= 45)
				mDirection = LEFT;
			else if (upX > downX && angle <= 45)
				mDirection = RIGHT;
			break;

		default:
			break;
		}
		return true;
	}

}
