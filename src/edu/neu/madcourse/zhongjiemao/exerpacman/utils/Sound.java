package edu.neu.madcourse.zhongjiemao.exerpacman.utils;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import edu.neu.madcourse.zhongjiemao.R;

public class Sound {

	private Context mContext;
	private SoundPool mSoundPool;
	private Map<Integer, Integer> mSoundMap;
	private float mVolume;

	public Sound(Context context) {
		mContext = context;
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap = new HashMap<Integer, Integer>();
		// Getting the user sound settings
		AudioManager audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVolume = maxVolume;
	}

	public void loadSrc() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mSoundMap.put(SOUND_EAT_FRUIT,
						mSoundPool.load(mContext, R.raw.exer_pacman_chomp, 1));
				mSoundMap.put(SOUND_EAT_SWORD, mSoundPool.load(mContext,
						R.raw.exer_pacman_eatsword, 1));
				mSoundMap.put(SOUND_EAT_GHOST, mSoundPool.load(mContext,
						R.raw.exer_pacman_eatghost, 1));
				mSoundMap.put(SOUND_EAT_LOSE_LIVES, mSoundPool.load(mContext,
						R.raw.exer_pacman_lose_lives, 1));
			}
		}).start();
	}

	public void play(int soundID) {
		mSoundPool.play(soundID, mVolume, mVolume, 1, 0, 1f);
	}

	public void release() {
		mSoundPool.release();
	}

}
