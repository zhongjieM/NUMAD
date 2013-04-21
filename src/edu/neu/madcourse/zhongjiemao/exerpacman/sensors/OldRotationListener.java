package edu.neu.madcourse.zhongjiemao.exerpacman.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * sensor class used to detect rotation
 */
public class OldRotationListener implements SensorEventListener {

	private static final String SENSOR_SERVICE = Context.SENSOR_SERVICE;
	private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
	private static final int SENSOR_ORIENTATION = Sensor.TYPE_ORIENTATION;
	private static final int TURN_LEFT = 1;
	private static final int TURN_RIGHT = 2;
	private static final int NO_TURN = 0;
	private static final int THRESHOLD = 20;

	private OnRotationListener mOnRotationListener;
	private Boolean support;
	private Context mContext;
	private SensorManager sensorManager;
	private Sensor oSensor;
	private float previousValue;
	private float amount;
	private Boolean state;

	public interface OnRotationListener {
		public void onRotate(int rotation);
	}
	
	public class RotationEvent {
		public static final int TURN_LEFT = 1;
		public static final int TURN_RIGHT = 2;
	}

	// --------------------------- Constructor --------------------------------

	public OldRotationListener(Context context) {
		this.mContext = context;
		resume();
	}

	// ------------------------- Override Methods -----------------------------

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION)
			return;
		synchronized (this) {
			if (mOnRotationListener != null) {
				int rotate = calculateRotationAmount(event.values[0]);
				if (rotate == TURN_RIGHT)
					mOnRotationListener.onRotate(RotationEvent.TURN_RIGHT);
				else if (rotate == TURN_LEFT)
					mOnRotationListener.onRotate(RotationEvent.TURN_LEFT);
			}
		}
	}

	// ------------------------- Public Methods -------------------------------

	public void setOnRotationListener(OnRotationListener listener) {
		mOnRotationListener = listener;
	}

	public void pause() {
		unregisterListener();
	}

	public void resume() {
		registerListener();
	}

	// ------------------------- Private Helper Methods -----------------------

	private void unregisterListener() {
		try {
			sensorManager = (SensorManager) mContext
					.getSystemService(SENSOR_SERVICE);
			if (this != null)
				sensorManager.unregisterListener(this);
			sensorManager = null;
			// TODO: test use, need to delete before publish
			System.out.println("Unregister SensorListener");
		} catch (Exception ex) {
			String methodName = "unregisterListener";
			showErrorMessage(methodName, ex.toString());
		}
	}

	private void registerListener() {
		try {
			initializeParams();
			initializeSensorListener();
		} catch (Exception ex) {
			String methodName = "registerListener";
			showErrorMessage(methodName, ex.toString());
		}
	}

	private void initializeParams() {
		support = false;
		previousValue = 0;
		amount = 0;
		state = false;
	}

	private void initializeSensorListener() {
		try {
			sensorManager = (SensorManager) mContext
					.getSystemService(SENSOR_SERVICE);
			oSensor = sensorManager.getDefaultSensor(SENSOR_ORIENTATION);
			support = sensorManager.registerListener(this, oSensor,
					SENSOR_DELAY);
			if (!support) {
				// TODO: failed to use orientation sensor
				// Need to do something
			}
		} catch (Exception ex) {
			String methodName = "initiazlizeSensorListener";
			showErrorMessage(methodName, ex.toString());
		}
	}

	private int calculateRotationAmount(float currentValue) {
		try {
			// check if previousValue has been initialized.
			if (!state) {
				previousValue = currentValue;
				state = true;
				return NO_TURN;
			}
			// get the diff between the previous Azimuth value and current
			// Azimuth value.
			float diff = getDiff(currentValue);
			// amount updates and invalidation check;
			if (!updateRotationAmount(diff)) {
				return NO_TURN;
			}
			// rotation check
			return turningCheck();
		} catch (Exception ex) {
			String methodName = "calculateRotationAmount";
			showErrorMessage(methodName, ex.toString());
			return NO_TURN;
		}
	}

	/**
	 * Get the difference between the previous angle value and the current angle
	 * value
	 * 
	 * @param currentValue
	 * @return
	 */
	private float getDiff(float currentValue) {
		// get the diff between the previous Azimuth value and current
		// Azimuth value.
		float diff = currentValue - previousValue;
		// check whether the movement has just passed 0 degree
		// if yes, recalculate the diff.
		if (diff > 270) {
			// counter-clockwise, over the 0 degree
			diff = -(360 - currentValue + previousValue);
		} else if (diff < -270) {
			// Clockwise, over the 0 degree.
			diff = 360 + currentValue - previousValue;
		}
		// give previousValue the currentValue.
		previousValue = currentValue;
		return diff;
	}

	/**
	 * Try to update amount value by checking its validation first.
	 * 
	 * @param diff
	 * @return
	 */
	private Boolean updateRotationAmount(float diff) {
		amount += diff;
		// if the value of amount is invalid.
		// just return directly
		if (amount > 360 || amount < -360) {
			amount = 0;
			// TODO: test use, delete it before publish
			System.out.println("Amount invalidate! DROPPED!");
			return false;
		}
		return true;
	}

	/**
	 * Check whether has get enough rotation angle to rotate.
	 * 
	 * @return TURN_RIGHT | TURN_LEFT | NO_TURN
	 */
	private int turningCheck() {
		// if amount value has reached over + 85 degree, which means has
		// enough rotation clockwise, show right.
		if (amount > THRESHOLD) {
			// TODO turn right; need to delete before publish
			// System.out.println("Turn right!");
			amount = 0;
			return TURN_RIGHT;
		}
		// if amount value has reached ove -85 degree, which means has
		// enough rotation counter-clockwise, show left
		if (amount < -THRESHOLD) {
			// TODO turn left; need to delete before publish
			// System.out.println("Turn left");
			amount = 0;
			return TURN_LEFT;
		}
		return NO_TURN;
	}

	/**
	 * Show Exception Error Message
	 * 
	 * @param methodName
	 * @param errorTrack
	 */
	private void showErrorMessage(String methodName, String errorTrack) {
		StringBuilder sb = new StringBuilder();
		sb.append("RotationListener: ");
		sb.append(methodName);
		sb.append(" Failed");
		System.out.println(sb.toString());
		System.out.println(errorTrack);
	}
}