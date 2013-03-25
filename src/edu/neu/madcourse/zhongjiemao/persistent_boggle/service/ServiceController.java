package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ServiceController {

	public static final int SERVICE_FOR_GAMEHALL = 1;
	public static final int SERVICE_FOR_ROOM = 2;
	public static final int SERVICE_FOR_GAME = 3;

	public static final String _SERVICE_FOR_GAMEHALL = "edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceForGameHall";
	public static final String _SERVICE_FOR_ROOM = "edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceForRoom";
	public static final String _SERVICE_FOR_GAME = "edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceForGame";

	private Context context;

	public ServiceController(Context context) {
		this.context = context;
	}

	public Boolean stopServiceById(int serviceId) {
		String serviceName;
		switch (serviceId) {
		case SERVICE_FOR_GAMEHALL:
			serviceName = _SERVICE_FOR_GAMEHALL;
			break;
		case SERVICE_FOR_ROOM:
			serviceName = _SERVICE_FOR_ROOM;
			break;
		case SERVICE_FOR_GAME:
			serviceName = _SERVICE_FOR_GAME;
			break;
		default:
			return false;
		}
		return stopServiceByName(serviceName);
	}

	public Boolean isServiceStarts(String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Iterator<RunningServiceInfo> runningService = ((ArrayList<RunningServiceInfo>) am
				.getRunningServices(3000)).iterator();
		while (runningService.hasNext()) {
			if (runningService.next().service.getClassName().toString()
					.intern() == serviceName)
				return true;
		}
		return false;
	}

	public Boolean startServiceForGameHall(String userName) {
		if (!isServiceStarts(_SERVICE_FOR_GAMEHALL)) {
			try {
				Intent i = new Intent(context,
						Class.forName(_SERVICE_FOR_GAMEHALL));
				i.putExtra(ServiceForGameHall.INTENT_USERNAME, userName);
				context.startService(i);
				return true;
			} catch (Exception ex) {
				System.out.println("Start ServiceForGameHall Failed");
				return false;
			}
		}
		return false;
	}

	public Boolean startServiceForRoom(String... params) {
		if (!isServiceStarts(_SERVICE_FOR_ROOM)) {
			try {
				Intent i = new Intent(context, Class.forName(_SERVICE_FOR_ROOM));
				i.putExtra(ServiceForRoom.INTENT_ROOMID, params[0]);
				i.putExtra(ServiceForRoom.INTENT_USERNAME, params[1]);
				context.startService(i);
				return true;
			} catch (Exception ex) {
				System.out.println("Sart ServiceForRoom Failed");
				return false;
			}
		}
		return false;
	}

	public Boolean startServiceForGame(String... params) {
		if (!isServiceStarts(_SERVICE_FOR_GAME)) {
			try {

				Intent i = new Intent(context, Class.forName(_SERVICE_FOR_GAME));
				i.putExtra(ServiceForGame.INTENT_ROOMID, params[0]);
				i.putExtra(ServiceForGame.INTENT_USERNAME, params[1]);
				i.putExtra(ServiceForGame.INTENT_CHARACTER,
						Integer.valueOf(params[2]));
				i.putExtra(ServiceForGame.INTENT_TIME,
						Integer.valueOf(params[3]));
				context.startService(i);
				return true;
			} catch (Exception ex) {
				System.out.println("Start ServiceForGame Failed!");
				return false;
			}
		}
		return false;
	}

	private Boolean stopServiceByName(String serviceName) {
		if (isServiceStarts(serviceName)) {
			try {
				context.stopService(new Intent(context, Class
						.forName(serviceName)));
				return true;
			} catch (Exception ex) {
				System.out.println("Stop Service: " + serviceName + " Failed");
				return false;
			}
		}
		return false;
	}

}
