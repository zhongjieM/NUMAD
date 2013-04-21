package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;

public class ExerPacmanAck extends Activity {

	private boolean isBackToMain = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exer_pacman_ack);
	}

	@Override
	protected void onResume() {
		isBackToMain = false;
		ExerPacmanMusic.resume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (!isBackToMain) {
			ExerPacmanMusic.pause();
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isBackToMain = true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
