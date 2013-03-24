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

	private Timer timer;
	private TimerTask listenerForInvitation;
	private static Handler handler;
	private GsonHelper gsonHelper;
	private NotificationManager notificationManager;

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
		if (notificationManager != null)
			notificationManager.cancel(1);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		Log.d(TAG, "Start Service by onStartCommand");
		initialization();
		return Service.START_STICKY;
	}

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
				OnLineUser olu = (OnLineUser) gsonHelper.getRecordFromTable(
						"kevin", GsonHelper.ONLINEUSER);
				if (olu != null) {
					if (olu.getInviter().intern() != OnLineUser.DEFAULT_INIVITER) {
						olu.setInviter(OnLineUser.DEFAULT_INIVITER);
						gsonHelper.updateTable(GsonHelper.ONLINEUSER, olu);
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
			}

		};

		timer = new Timer();
		timer.schedule(listenerForInvitation, 1000, 1000);
	}

	@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	private void showNotificationBar() {
		System.out.println("You got an invitaion");
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings({ "deprecation", "deprecation" })
		Notification notification = new Notification(R.drawable.even_icon,
				"You have an invitation", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		Intent i = new Intent(this, GameHall.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent pt = PendingIntent.getActivity(this, 1, i, 0);
		notification.setLatestEventInfo(this, " title", "content", pt);
		notificationManager.notify(1, notification);
	}
}
