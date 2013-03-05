package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import edu.neu.madcourse.zhongjiemao.R;

public class GameHall extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_hall);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game_hall, menu);
		return true;
	}

}
