package edu.neu.madcourse.zhongjiemao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.Sound;

public class TCApplication extends Application {

	private static final String TAG = "TCApplication";

	private static Context context;

	private List<Activity> activities = new ArrayList<Activity>();
	
	private Sound mSound;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		mSound = new Sound(context);
		mSound.loadSrc();
	}
	
	public static Context getAppContext() {
		return context;
	}

	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public void killActivity(Activity activity) {
		activity.finish();
		activities.remove(activity);
	}

	public void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	public void killAllActivities() {
		for (Activity activity : activities)
			activity.finish();
		activities.clear();
	}
	
	public Sound getSound() {
		return mSound;
	}

}
