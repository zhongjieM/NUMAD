package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserGameStatus;

public class ServiceForRoom extends Service {

	private static final String TAG = "ServiceForRoom";
	private static final int GAME_START = 1;
	private static final int GAME_OVER = 2;
	private static final int GAME_EXCEPTION = 0;
	public static final String INTENT_ROOMID = "INTENT_ROOMID";
	public static final String INTENT_USERNAME = "INTENT_USERNAME";

	private String roomID;
	private String userName;

	private GsonHelper gsonHelper;

	private Timer timer;
	private TimerTask listenToStartCommand;
	private TimerTask tick;
	private Handler handler;
	private static Boolean isGameStart = false;
	private Timer timer_tick;
	private int time = 180;
	private SharedPreferences.Editor editor;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "ServiceForRoom Stopped");
		time = 180;
		listenToStartCommand.cancel();
		timer.cancel();
		new NotificationBarController(this)
				.cancelNotification(NotificationBarController.NOTIFICATION_ID_ROOM);
		stopTick();
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "ServiceForRoom Starts");
		time = 180;
		isGameStart = false;
		try {
			this.roomID = intent.getStringExtra(INTENT_ROOMID);
			this.userName = intent.getStringExtra(INTENT_USERNAME);
		} catch (Exception ex) {
			String errorMessage = "Can't get RoomID to start service";
			System.out.println(errorMessage);
		}
		initialize();
		return Service.START_STICKY;
	}

	private void initialize() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GAME_START:
					// TODO: game starts get into the room and start the game;
					new NotificationBarController(getApplicationContext())
							.showNotificationBar(
									NotificationBarController.NOTIFICATION_ID_ROOM,
									roomID);
					break;
				case GAME_OVER:
					break;
				}
			}
		};

		listenToStartCommand = new TimerTask() {
			@Override
			public void run() {
				try {
					System.out.println("Running###");
					int gameStatus = isGameStart();
					if (gameStatus == GAME_START) {
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
						return;
					}
					if (gameStatus == GAME_OVER) {
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}
				} catch (Exception ex) {
					String errorMessage = "ServiceForRoom timer task failed!";
					System.out.println(errorMessage);
					return;
				}
			}
		};

		timer = new Timer();
		timer.schedule(listenToStartCommand, 1000, 1000);
	}

	private int isGameStart() {
		gsonHelper = new GsonHelper();
		try {
			RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(
					this.roomID, GsonHelper.ROOMSTATUS);
			UserGameStatus ugs = gsonHelper.getUserGameStatus(userName);
			if (rs != null && rs.getIsGameStarts() == true
					&& isGameStart == false && ugs != null
					&& ugs.getInGame() == false) {
				isGameStart = true;
				editor = getApplicationContext().getSharedPreferences(
						"GAME_TIME", MODE_PRIVATE).edit();
				startTick();
				return GAME_START;
			}
			if (rs != null && rs.getIsGameStarts() == false) {
				isGameStart = false;
				ugs.setInGame(false);
				gsonHelper.updateUserGameStatus(userName, ugs);
				stopTick();
				return GAME_OVER;
			}

		} catch (Exception ex) {
			String errorMessage = "Network Fails!";
			System.out.println(errorMessage);
			return GAME_EXCEPTION;
		}
		return GAME_EXCEPTION;
	}

	private void startTick() {
		tick = new TimerTask() {

			@Override
			public void run() {
				time--;
				editor.putInt("TIME", time);
				editor.commit();
			}
		};
		timer_tick = new Timer();
		timer_tick.schedule(tick, 0, 1000);
	}

	private void stopTick() {
		time = 180;
		try {
			if (tick != null) {
				tick.cancel();
			}
			if (timer_tick != null) {
				timer_tick.cancel();
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
}
