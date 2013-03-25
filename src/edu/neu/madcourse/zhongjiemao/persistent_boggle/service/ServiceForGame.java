package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserGameStatus;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.PersistentBoggleGame;

public class ServiceForGame extends Service {

	private static final String TAG = "ServiceForGame";

	public static final String INTENT_ROOMID = "INTENT_ROOMID";
	public static final String INTENT_USERNAME = "INTENT_USERNAME";
	public static final String INTENT_CHARACTER = "INTENT_CHARACTER";
	public static final String INTENT_TIME = "INTENT_TIME";
	private static final int DEFAULT_TIME = 180;

	private static final int TICK_TIMER = 1;
	private static final int PLAYERS_TIMER = 2;

	private GsonHelper gsonHelper;

	private String roomID;
	private String userName;
	private int character;
	private int time = 0;

	private Timer tick_timer;
	private TimerTask tick_timerTask;

	private Timer players_timer;
	private TimerTask players_timerTask;
	private static Handler handler;

	private int Character;
	private String[] players;
	private int numberOfOtherPlayers;
	private UserGameStatus[] previous_ugs;
	private UserGameStatus[] temp_ugs;
	private String messageToBeHandle;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int id) {
		Log.d(TAG, "ServiceForGame Starts");
		try {
			roomID = intent.getStringExtra(INTENT_ROOMID);
			userName = intent.getStringExtra(INTENT_USERNAME);
			character = intent.getIntExtra(INTENT_CHARACTER,
					PersistentBoggleGame.ROOMMEMBER);
			time = intent.getIntExtra(INTENT_TIME, DEFAULT_TIME);
			initaizlie();
		} catch (Exception ex) {
			String errorMessage = "ServiceForGame: can't get extra time";
			System.out.println(errorMessage);
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "ServiceForGame stopped");
		stopTimerTasks();
		super.onDestroy();
	}

	private void initaizlie() {
		gsonHelper = new GsonHelper();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TICK_TIMER:
					getSharedPreferences("GAME_TIME_CURRENT", MODE_PRIVATE)
							.edit().putInt("TIME_CURRENT", time).commit();
					break;
				case PLAYERS_TIMER:
					showNotificationBar(messageToBeHandle);
					break;
				}
			}
		};
		initializeTickTimer();
		initializePlayersListener();
	}

	private void initializeTickTimer() {
		tick_timerTask = new TimerTask() {
			@Override
			public void run() {
				if (time > 0)
					time--;
				Message msg = new Message();
				msg.what = TICK_TIMER;
				handler.sendMessage(msg);
				System.out.println("Continue Ticking:" + String.valueOf(time));
			}
		};
		tick_timer = new Timer();
		tick_timer.schedule(tick_timerTask, 0, 1000);
	}

	private void initializePlayersListener() {
		try {
			findOtherPlayers();
			previous_ugs = new UserGameStatus[numberOfOtherPlayers];
			temp_ugs = new UserGameStatus[numberOfOtherPlayers];
			players_timerTask = new TimerTask() {
				@Override
				public void run() {
					try {
						for (int i = 0; i < numberOfOtherPlayers; i++) {
							temp_ugs[i] = gsonHelper
									.getUserGameStatus(players[i]);
							if (temp_ugs[i] == null)
								continue;
							if (previous_ugs[i] == null) {
								previous_ugs[i] = temp_ugs[i];
								continue;
							}
							if (previous_ugs[i].getCurrentScore() != temp_ugs[i]
									.getCurrentScore()) {
								StringBuilder sb = new StringBuilder(players[i]
										+ "discovered these new words: ");
								for (int j = previous_ugs[i].getCurrentWords()
										.size(); j < temp_ugs[i]
										.getCurrentWords().size(); j++) {
									sb.append(temp_ugs[i].getCurrentWords()
											.get(j));
								}
								previous_ugs[i] = temp_ugs[i];
								messageToBeHandle = sb.toString();
								Message msg = new Message();
								msg.what = PLAYERS_TIMER;
								handler.sendMessage(msg);
							}
						}
					} catch (Exception ex) {
						System.out.println(ex.toString());
					}
				}
			};
			players_timer = new Timer();
			players_timer.schedule(players_timerTask, 3000, 1000);
		} catch (Exception ex) {
			String errorMessage = "ServiceForGame: can't get room information from server";
			System.out.println(errorMessage);
			return;
		}
	}

	private void findOtherPlayers() {
		try {
			int i = 0;
			players = new String[2];
			RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(roomID,
					GsonHelper.ROOMSTATUS);
			if (rs != null) {
				if (rs.getPlayer1().intern() != RoomStatus.DEFAULT_PLAYER
						&& rs.getPlayer1().intern() != userName.intern()) {
					players[i] = rs.getPlayer1();
					i++;
				}
				if (rs.getPlayer2().intern() != RoomStatus.DEFAULT_PLAYER
						&& rs.getPlayer2().intern() != userName.intern()) {
					players[i] = rs.getPlayer2();
					i++;
				}
				if (rs.getPlayer3().intern() != RoomStatus.DEFAULT_PLAYER
						&& rs.getPlayer3().intern() != userName.intern()) {
					players[i] = rs.getPlayer3();
					i++;
				}
				numberOfOtherPlayers = i;
			}
		} catch (Exception ex) {
			String errorMessage = "ServiceForGame: can't get room information from server";
			System.out.println(errorMessage);
			return;
		}
	}

	private void showNotificationBar(String content) {
		new NotificationBarController(this).showNotificationBar(
				NotificationBarController.NOTIFICATION_ID_GAME,
				messageToBeHandle);
	}

	private void stopTimerTasks() {
		try {
			if (tick_timerTask != null)
				tick_timerTask.cancel();
			if (players_timerTask != null)
				players_timerTask.cancel();
			if (tick_timer != null)
				tick_timer.cancel();
			if (players_timer != null)
				players_timer.cancel();
			new NotificationBarController(this)
					.cancelNotification(NotificationBarController.NOTIFICATION_ID_GAME);
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}
}
