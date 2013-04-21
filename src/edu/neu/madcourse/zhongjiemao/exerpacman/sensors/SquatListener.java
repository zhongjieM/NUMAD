package edu.neu.madcourse.zhongjiemao.exerpacman.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SquatListener implements SensorEventListener {

	private static final String SENSOR_SERVICE = Context.SENSOR_SERVICE;
	private static final int SENSOR_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
	private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;

	private Context mContext;
	private OnSquatListener mOnSquatListener;
	private SensorManager sensorManager;
	private Sensor aSensor;
	private boolean support;

	private boolean back;
	private long previousTime = 0;

	public interface OnSquatListener {
		public void onSquat();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() != SENSOR_ACCELEROMETER)
				return;
			if (mOnSquatListener == null)
				return;
			boolean isSquat = checkSquat(event.values[2]);
			if (isSquat)
				mOnSquatListener.onSquat();
			return;
		}

	}

	public SquatListener(Context context) {
		mContext = context;
		resume();
	}

	public void setOnSquatListener(OnSquatListener onSquatListener) {
		mOnSquatListener = onSquatListener;
	}

	public void resume() {
		registerListener();
	}

	public void pause() {
		unregisterListener();
	}

	private void registerListener() {
		try {
			initParams();
			initSensorListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterListener() {
		try {
			sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
			if (this != null)
				sensorManager.unregisterListener(this);
			sensorManager = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initParams() {
		support = false;
		back = false;
	}

	private void initSensorListener() {
		try {
			sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
			aSensor = sensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
			support = sensorManager.registerListener(this, aSensor, SENSOR_DELAY);
			if (!support) {
				// failed to use accelerometer
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Boolean checkSquat(float zValue) {
		if (zValue < 11) {
			back = false;
		}
		if (zValue > 12 && back == false) {
			long temp = System.currentTimeMillis();
			if ((temp - previousTime) > 1000) {
				previousTime = temp;
				back = true;
				return true;
			}
			previousTime = temp;
		}
		return false;
	}

}
