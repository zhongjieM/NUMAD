package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.boggle.BoggleMain;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.GameHall;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.InvitationDialog;

public class ServiceForGameHall extends Service {

	private static final String TAG = "ServiceForGameHall";
	public static final String INTENT_USERNAME = "INTENT_USERNAME";
	private String inviter = "";
	private Timer timer;
	private TimerTask listenerForInvitation;
	private static Handler handler;
	private GsonHelper gsonHelper;

	private String userName;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service will be destroryed");
		listenerForInvitation.cancel();
		timer.cancel();
		new NotificationBarController(getApplicationContext())
				.cancelNotification(NotificationBarController.NOTIFICATION_ID_GAMEHALL);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		Log.d(TAG, "Start Service by onStartCommand");
		try {
			this.userName = intent.getStringExtra(INTENT_USERNAME);
		} catch (Exception ex) {
			String errorMessage = "ServiceForGameHall can't get userName";
			System.out.println(errorMessage);
		}
		initialization();
		return Service.START_STICKY;
	}

	/**
	 * Start a thread to check the ONLINEUSER table to find whether there is an
	 * invitation for this user
	 */
	private void initialization() {

		gsonHelper = new GsonHelper();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					// Have an invitation
					showNotificationBar();
					break;
				}
			}
		};

		listenerForInvitation = new TimerTask() {

			@Override
			public void run() {
				System.out.println("Timer ticked");
				try {
					OnLineUser olu = (OnLineUser) gsonHelper
							.getRecordFromTable(userName, GsonHelper.ONLINEUSER);
					if (olu != null) {
						inviter = olu.getInviter();
						if (inviter.intern() != OnLineUser.DEFAULT_INIVITER) {
							olu.setInviter(OnLineUser.DEFAULT_INIVITER);
							gsonHelper.updateTable(GsonHelper.ONLINEUSER, olu);
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception ex) {
					String errorMessage = "Game Hall Service: initialization: TimerTask Failed";
					System.out.println(errorMessage);
					return;
				}
			}

		};

		timer = new Timer();
		timer.schedule(listenerForInvitation, 1000, 1000);
	}

	@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	private void showNotificationBar() {
		NotificationBarController nbc = new NotificationBarController(this);
		nbc.showNotificationBar(
				NotificationBarController.NOTIFICATION_ID_GAMEHALL, inviter);
	}
}
