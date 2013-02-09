package edu.neu.madcourse.zhongjiemao.boggle.BLLDAL;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.neu.madcourse.zhongjiemao.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Sound Control
 * 
 * @author kevin
 * 
 */
public class SoundEffect {

	private static SoundPool soundPool;

	private static boolean soundSt = true; // 音效开关
	private static Context context;

	private static final int[] musicId = { R.raw.accept, R.raw.error,R.raw.correctword };
	private static Map<Integer, Integer> soundMap;

	/**
	 * initialize method
	 * 
	 * @param c
	 */
	public static void init(Context c) {
		context = c;

		initSound();
	}

	// 初始化音效播放器
	private static void initSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(R.raw.accept, soundPool.load(context, R.raw.accept, 1));
		soundMap.put(R.raw.error, soundPool.load(context, R.raw.error, 1));
		soundMap.put(R.raw.correctword, soundPool.load(context, R.raw.correctword, 1));
	}

	/**
	 * play sound effect 
	 * 
	 * @param resId
	 *            音效资源id
	 */
	public static void playSound(int resId) {
		if (soundSt == false)
			return;

		Integer soundId = soundMap.get(resId);
		if (soundId != null)
			soundPool.play(soundId, 1, 1, 1, 0, 1);
	}

	/**
	 * 获得音效开关状态
	 * 
	 * @return
	 */
	public static boolean isSoundSt() {
		return soundSt;
	}

	/**
	 * 设置音效开关
	 * 
	 * @param soundSt
	 */
	public static void setSoundSt(boolean soundSt) {
		SoundEffect.soundSt = soundSt;
	}

	/**
	 * 发出‘邦’的声音
	 */
	public static void playAccept() {
		playSound(R.raw.accept);
	}
	
	/**
	 * 发出‘邦’的声音
	 */
	public static void playError() {
		playSound(R.raw.error);
	}
	
	/**
	 * 发出‘邦’的声音
	 */
	public static void playCorrectWord() {
		playSound(R.raw.correctword);
	}
}