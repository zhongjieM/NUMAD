package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	
	private static Toast toast;
	
	private ToastUtil() {}
	
	public static void show(Context context, String msg) {
		toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public static void showLongTime(Context context, String msg) {
		toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
