package edu.neu.madcourse.zhongjiemao.exerpacman.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

public class FileUtil {
	
	private static final String TAG = "EXER_PACMAN_FILE_UTIL";
	private static final String FILE_PATH = "exerpacman";
	private static final int BUFFER_SIZE = 16;
	
	public static File getInternalDirectory(Context context) {
		ContextWrapper contextWrapper = new ContextWrapper(context);
		File directory = contextWrapper.getDir(FILE_PATH, Context.MODE_PRIVATE);
		Log.d(TAG, "get dir: " + directory.getAbsolutePath());
		return directory;
	}
	
	public static boolean isInternalFileExist(Context context, String fileName) {
		File directory = getInternalDirectory(context);
		File file = new File(directory, fileName);
		return file.exists();
	}
	
	public static boolean writeFileToInternal(Context context, String fileName, String data) {
		try {
			File directory = getInternalDirectory(context);
			File file = new File(directory, fileName);
			FileOutputStream out = new FileOutputStream(file);
			out.write(data.getBytes("UTF-8"));
			out.close();
			Log.d(TAG, "file writed: " + file.getAbsolutePath());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String readFileFromInternal(Context context, String fileName) {
		try {
			File directory = getInternalDirectory(context);
			File file = new File(directory, fileName);
			byte[] tmp = new byte[BUFFER_SIZE];
			int len = 0;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(file);
			while ((len = in.read(tmp)) != -1) {
				buffer.write(tmp, 0, len);
			}
			buffer.flush();
			byte[] raw = buffer.toByteArray();
			buffer.close();
			in.close();
			String plain = new String(raw, "UTF-8");
			Log.d(TAG, "file read: " + file.getAbsolutePath());
			return plain;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean deleteInternalFile(Context context, String fileName) {
		File directory = getInternalDirectory(context);
		File file = new File(directory, fileName);
		if (file.exists()) {
			String path = file.getAbsolutePath();
			file.delete();
			Log.d(TAG, "file deleted: " + path);
			return true;
		}
		return false;
	}
	
}
