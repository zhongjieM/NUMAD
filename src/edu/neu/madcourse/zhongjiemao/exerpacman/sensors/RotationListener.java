package edu.neu.madcourse.zhongjiemao.exerpacman.sensors;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class is the sensor listener of Exer Pacman Project. This class is to
 * handle the rotation actions by listening to the ORIENTATION sensor and the
 * squat action by listen to the ACCELEROMETER sensor.
 * 
 * To create an object of this class, you need to provide the current Context of
 * the application and the initial facing direction of your Pacman.
 * 
 * After constructions, the listener will automatically call Resume() method to
 * start listening. You could use pause() public method to Pause the listener
 * from listening. Also you could use resume() public method to resume the
 * listener from pausing.
 * 
 * Before you actually let this listener running, you need to call
 * setOnRotateSquatListener() public method. Also you need to implement the
 * methods in OnRotateSquatListener so you can receive the actions happens in
 * this class.
 * 
 * Have Fun! Good Luck!
 * 
 * Best,
 * 
 * @author Zhongjie Mao
 * 
 */
public class RotationListener implements SensorEventListener {

	// Four possible initial directions you can choose
	public static final int INITIAL_DIRECTION_UP = 0;
	public static final int INITIAL_DIRECTION_RIGHT = 1;
	public static final int INITIAL_DIRECTION_DOWN = 2;
	public static final int INITIAL_DIRECTION_LEFT = 3;

	// Four possible results of rotation actions you will receive
	public static final int NO_TURN = -1;
	public static final int MOVE_UP = 0;
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_DOWN = 2;
	public static final int MOVE_LEFT = 3;

	// -------------------------- Private Members -----------------------------

	private static final String TAG = "ROTATIONLISTENER";
	private static final String SENSOR_SERVICE = Context.SENSOR_SERVICE;
	private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;
	private static final int SENSOR_ORIENTATION = Sensor.TYPE_ORIENTATION;
	private static final int THRESHOLD = 10;

	private OnRotateListener mOnRotateListener;
	private Boolean support;
	private Context mContext;
	private Handler mHandler;
	private SensorManager sensorManager;

	// for rotate
	private Sensor oSensor;
	private int initialFacingDirection = INITIAL_DIRECTION_UP;
	private int up_threshold = 0;
	private int right_threshold = 0;
	private int down_threshold = 0;
	private int left_threshold = 0;
	private int orientation = 0;
	private Boolean state;
	
	private float[] orientationValues = new float[3];
	
	public interface OnRotateListener {

		/**
		 * To deal with the rotation actions. The parameter i has four possible
		 * choices:
		 * 
		 * -- MOVE_UP = 0
		 * 
		 * -- MOVE_RIGHT = 1
		 * 
		 * -- MOVE_DOWN = 2
		 * 
		 * -- MOVE_LEFT = 3
		 * 
		 * @param i
		 */
		public void onRotate(int i);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == SENSOR_ORIENTATION) {
				if (mOnRotateListener != null) {
					int rotate = calculateRotationAmount((int) event.values[0]);
					if (rotate != NO_TURN)
						mOnRotateListener.onRotate(rotate);
					// send message
					System.arraycopy(event.values, 0, orientationValues, 0, 3);
					sendMessage(Constants.MSG_UPDATE_ORIENTATION, -1, -1, orientationValues);
				}
				return;
			}
		}
	}
	
	private void sendMessage(int what, int arg1, int arg2, Object obj) {
		if (mHandler != null) {
			Message message = Message.obtain(mHandler, what, arg1, arg2, obj);
			mHandler.sendMessage(message);
		}
	}

	/**
	 * <pre>
	 * Constructor
	 * 
	 * @param context
	 *            : the Context of the current application
	 * @param initialFacingDirection
	 *            : the original facing direction of the Pacman.
	 * 
	 *            You could choose:
	 * 
	 *            --INITIAL_DIRECTION_UP
	 * 
	 *            --INITIAL_DIRECTION_RIGHT
	 * 
	 *            --INITIAL_DIRECTION_DOWN
	 * 
	 *            --INITIAL_DIRECTION_LEFT
	 * 
	 *            The default value is: INITIAL_DIRECTION_UP
	 * </pre>
	 */
	public RotationListener(Context context, Handler handler, int initialFacingDirection) {
		mContext = context;
		mHandler = handler;
		this.initialFacingDirection = initialFacingDirection;
		resume();
	}

	/**
	 * Listener Operation: Resume the listener from Pause
	 */
	public void resume() {
		Log.d(TAG, "Sensor RESUME");
		registerListener();
	}

	/**
	 * Listener Operation: Pause the listener operation
	 */
	public void pause() {
		Log.d(TAG, "Sensor Pause");
		unregisterListener();
	}

	/**
	 * Set the OnRotateListener
	 * 
	 * @param listener
	 */
	public void setOnRotateListener(OnRotateListener listener) {
		mOnRotateListener = listener;
	}

	// ---------------------------- Private Methods ---------------------------

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
		// for rotate
		orientation = 0;
		state = false;
		up_threshold = 0;
		right_threshold = 0;
		down_threshold = 0;
		left_threshold = 0;
	}

	private void initSensorListener() {
		try {
			sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
			oSensor = sensorManager.getDefaultSensor(SENSOR_ORIENTATION);
			support = sensorManager.registerListener(this, oSensor, SENSOR_DELAY);
			if (!support) {
				// failed to use orientation sensor
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int calculateRotationAmount(int currentValue) {
		try {
			// check if previousValue has been initialized.
			if (!state) {
				state = true;
				return initializeDirections(initialFacingDirection,
						(int) currentValue);
			}
			int temp = -1;
			if (Math.abs(currentValue - up_threshold) < THRESHOLD)
				temp = MOVE_UP;
			else if (Math.abs(currentValue - right_threshold) < THRESHOLD)
				temp = MOVE_RIGHT;
			else if (Math.abs(currentValue - down_threshold) < THRESHOLD)
				temp = MOVE_DOWN;
			else if (Math.abs(currentValue - left_threshold) < THRESHOLD)
				temp = MOVE_LEFT;
			if (temp != orientation && temp != -1) {
				orientation = temp;
				return orientation;
			}
			return NO_TURN;
		} catch (Exception e) {
			e.printStackTrace();
			return NO_TURN;
		}
	}

	private int initializeDirections(int ini_dir, int ini_value) {
		int current_direction = NO_TURN;
		switch (ini_dir) {
		case INITIAL_DIRECTION_UP:
			up_threshold = ini_value;
			right_threshold = (ini_value + 90) > 360 ? (ini_value - 270)
					: ini_value + 90;
			down_threshold = (ini_value + 180) > 360 ? (ini_value - 180)
					: ini_value + 180;
			left_threshold = (ini_value + 270) > 360 ? (ini_value - 90)
					: ini_value + 270;
			current_direction = MOVE_UP;
			break;
		case INITIAL_DIRECTION_RIGHT:
			right_threshold = ini_value;
			down_threshold = (ini_value + 90) > 360 ? (ini_value - 270)
					: ini_value + 90;
			left_threshold = (ini_value + 180) > 360 ? (ini_value - 180)
					: ini_value + 180;
			up_threshold = (ini_value + 270) > 360 ? (ini_value - 90)
					: ini_value + 270;
			current_direction = MOVE_RIGHT;
			break;
		case INITIAL_DIRECTION_DOWN:
			down_threshold = ini_value;
			left_threshold = (ini_value + 90) > 360 ? (ini_value - 270)
					: ini_value + 90;
			up_threshold = (ini_value + 180) > 360 ? (ini_value - 180)
					: ini_value + 180;
			right_threshold = (ini_value + 270) > 360 ? (ini_value - 90)
					: ini_value + 270;
			current_direction = MOVE_DOWN;
			break;
		case INITIAL_DIRECTION_LEFT:
			left_threshold = ini_value;
			up_threshold = (ini_value + 90) > 360 ? (ini_value - 270)
					: ini_value + 90;
			right_threshold = (ini_value + 180) > 360 ? (ini_value - 180)
					: ini_value + 180;
			down_threshold = (ini_value + 270) > 360 ? (ini_value - 90)
					: ini_value + 270;
			current_direction = MOVE_LEFT;
			break;
		default:
			up_threshold = ini_value;
			right_threshold = (ini_value + 90) > 360 ? (ini_value - 270)
					: ini_value + 90;
			down_threshold = (ini_value + 180) > 360 ? (ini_value - 180)
					: ini_value + 180;
			left_threshold = (ini_value + 270) > 360 ? (ini_value - 90)
					: ini_value + 270;
			current_direction = MOVE_RIGHT;
			break;
		}
		return current_direction;
	}
}
