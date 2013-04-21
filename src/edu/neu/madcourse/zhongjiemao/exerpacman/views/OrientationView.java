package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OrientationView extends View {

	private static final String TAG = "OrientationView";

	private Bitmap mBitmap;
	private Paint mPaint = new Paint();
	private Canvas mCanvas = new Canvas();
	private Path mPath = new Path();
	private RectF mRect = new RectF();
	private float mOrientationValues[] = new float[3];
	private float mWidth;
	private float mHeight;

	public OrientationView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);

		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mRect.set(-0.5f, -0.5f, 0.5f, 0.5f);
		mPath.arcTo(mRect, 0, 180);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = measureWidth(widthMeasureSpec);
		int measuredHeight = measureHeight(heightMeasureSpec, measuredWidth);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureWidth(int widthMeasureSpec) {
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize / 4;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize / 4;
		}
		return result;
	}

	private int measureHeight(int heightMeasureSpec, int widthValue) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = widthValue;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = widthValue;
		}
		return result;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawColor(Color.TRANSPARENT);
		mWidth = w;
		mHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (this) {
			if (mBitmap != null) {
				final Paint paint = mPaint;
				final Path path = mPath;
				final int outer = 0xFFC0C0C0;
				final int inner = 0xFFff7010;

				canvas.drawBitmap(mBitmap, 0, 0, null);
				float[] values = mOrientationValues;
				float w0 = mWidth;
				float w = w0 - 5;
				float x = w0 * 0.5f;
				for (int i = 0; i < 1; i++) {
					canvas.save(Canvas.MATRIX_SAVE_FLAG);
					canvas.translate(x, w * 0.5f + 4.0f);
					canvas.save(Canvas.MATRIX_SAVE_FLAG);
					paint.setColor(outer);
					canvas.scale(w, w);
					canvas.drawOval(mRect, paint);
					canvas.restore();
					canvas.scale(w - 5, w - 5);
					paint.setColor(inner);
					canvas.rotate(-values[i]);
					canvas.drawPath(path, paint);
					canvas.restore();
					x += w0;
				}
			}
		}
	}

	public void onOrientationChanged(float[] values) {
		Log.d(TAG, "sensor: " + ", x: " + values[0] + ", y: " + values[1]
				+ ", z: " + values[2]);
		synchronized (this) {
			if (mBitmap != null) {
				for (int i = 0; i < 3; i++) {
					mOrientationValues[i] = values[i];
				}
				invalidate();
			}
		}
	}

}