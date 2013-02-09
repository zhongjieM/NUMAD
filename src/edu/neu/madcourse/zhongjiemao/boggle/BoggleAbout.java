package edu.neu.madcourse.zhongjiemao.boggle;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.R.layout;
import edu.neu.madcourse.zhongjiemao.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BoggleAbout extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boggle_about);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_boggle_about, menu);
		return true;
	}

}
