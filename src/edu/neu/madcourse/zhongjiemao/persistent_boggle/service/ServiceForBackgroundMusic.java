package edu.neu.madcourse.zhongjiemao.persistent_boggle.service;

import edu.neu.madcourse.zhongjiemao.sudoku.Music;
import edu.neu.madcourse.zhongjiemao.R;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceForBackgroundMusic extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int id) {
		startBackgroundMusic();
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopBackgroundMusic();
		super.onDestroy();
	}

	private void startBackgroundMusic() {
		Music.play(this, R.raw.bogglestart);
	}

	private void stopBackgroundMusic() {
		Music.stop(this);
	}
}
