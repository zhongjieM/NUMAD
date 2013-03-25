package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.R;

public class NotificationBarController {

	public static final int NOTIFICATION_ID_GAMEHALL = 1;
	public static final int NOTIFICATION_ID_ROOM = 2;
	public static final int NOTIFICATION_ID_GAME = 3;

	private static final String _NOTIFICATION_GAMEHALL = "edu.neu.madcourse.zhongjiemao.persistent_boggle.GameHall";
	private static final String _NOTIFICATION_ROOM = "edu.neu.madcourse.zhongjiemao.persistent_boggle.Room";
	private static final String _NOTIFICATION_GAME = "edu.neu.madcourse.zhongjiemao.persistent_boggle.PersistentBoggleGame";

	private Context context;
	private NotificationManager notificationManager;

	public NotificationBarController(Context context) {
		this.context = context;
	}

	public void showNotificationBar(int notificationId, String... params) {
		switch (notificationId) {
		case NOTIFICATION_ID_GAMEHALL:
			showNotificationForGameHall(notificationId, params);
			break;
		case NOTIFICATION_ID_ROOM:
			showNotificationForRoom(notificationId, params);
			break;
		case NOTIFICATION_ID_GAME:
			showNotificationForGame(notificationId, params);
			break;
		default:
			return;
		}
	}

	public void cancelNotification(int notificationId) {
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		System.out.println("notificationCancelled");
		notificationManager.cancel(notificationId);
	}

	private void showNotificationForGameHall(int notificationId,
			String... params) {
		StringBuilder title = new StringBuilder("Persistent Boggle");
		StringBuilder content = new StringBuilder();
		int nid = notificationId;
		String className = "";
		String errorMessage = "";

		title.append(" Game Hall");
		content.append(params[0]);
		content.append(" invites you to a game!");
		className = _NOTIFICATION_GAMEHALL;
		errorMessage = "Service for GameHall: notification error";
		showNotification(content.toString(), title.toString(), nid, className,
				errorMessage);
	}

	private void showNotificationForRoom(int notificationId, String... params) {
		StringBuilder title = new StringBuilder("Persistent Boggle");
		StringBuilder content = new StringBuilder();
		int nid = notificationId;
		String className = "";
		String errorMessage = "";

		title.append(" Room");
		content.append("Game Start!!!");
		className = _NOTIFICATION_ROOM;
		errorMessage = "Service for Room: notification error";
		showNotification(content.toString(), title.toString(), nid, className,
				errorMessage);
	}

	private void showNotificationForGame(int notificationId, String... params) {
		StringBuilder title = new StringBuilder("Persistent Boggle");
		StringBuilder content = new StringBuilder(params[0]);
		int nid = notificationId;
		String className = _NOTIFICATION_GAME;
		String errorMessage = "Service for Game: notification error";
		showNotification(content.toString(), title.toString(), nid, className,
				errorMessage);
	}

	@SuppressWarnings("deprecation")
	private void showNotification(String notificationContent,
			String notificationTitle, int notificationID, String className,
			String errorMessage) {
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings({ "deprecation", "deprecation" })
		Notification notification = new Notification(R.drawable.even_icon,
				notificationContent, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		try {
			notificationManager.cancel(notificationID);
			Intent i = new Intent(context, Class.forName(className));
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			PendingIntent pt = PendingIntent.getActivity(context,
					notificationID, i, 0);
			notification.setLatestEventInfo(context, notificationTitle,
					notificationContent, pt);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notificationManager.notify(notificationID, notification);
		} catch (Exception ex) {
			System.out.println(errorMessage);
		}
	}
}
