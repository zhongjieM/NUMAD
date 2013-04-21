package edu.neu.madcourse.zhongjiemao.exerpacman.game;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import edu.neu.madcourse.zhongjiemao.exerpacman.activities.ExerPacmanPrefs;

/**
 * For background music only
 * 
 * @author Yingquan Yuan
 *
 */
public class ExerPacmanMusic {
	
	private static MediaPlayer mpStartup = null;
	
	private static MediaPlayer mpBg = null;
	
	public static void playOneByOne(Context context, int resFirst, int resSecond) {
		final Context c = context;
		final int resBg = resSecond;
		stop(context);
		// Start music only if not disabled in preferences
		if (Constants.PLAY_MUSIC) {
			mpStartup = MediaPlayer.create(context, resFirst);
			mpStartup.setLooping(false);
			mpStartup.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					play(c, resBg, true);
				}
			});
			mpStartup.start();
		}
	}
	
	/**
	 * Stop old song and start new song
	 * @param context
	 * @param resource
	 */
	public static void play(Context context, int resource, boolean isLoop) {
		stop(context);
		mpBg = MediaPlayer.create(context, resource);
		mpBg.setLooping(isLoop);
		// Start music only if not disabled in preferences
		if (Constants.PLAY_MUSIC) {
			mpBg.start();
		}
	}
	
	public static void pause() {
		if (mpBg != null && mpBg.isPlaying()) {
			mpBg.pause();
		}
	}
	
	public static void resume() {
		if (mpBg != null && !mpBg.isPlaying() && Constants.PLAY_MUSIC) {
			mpBg.start();
		}
	}
	
	/**
	 * Stop the music
	 * @param context
	 */
	public static void stop(Context context) {
		if (mpBg != null) {
			mpBg.stop();
			mpBg.release();
			mpBg = null;
		}
	}
}
