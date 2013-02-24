package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.R.layout;
import edu.neu.madcourse.zhongjiemao.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PersistentBoggleLogin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_persistent_boggle_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater()
				.inflate(R.menu.activity_persistent_boggle_login, menu);
		return true;
	}

}
