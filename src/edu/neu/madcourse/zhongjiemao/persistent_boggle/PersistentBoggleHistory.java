package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;

public class PersistentBoggleHistory extends Activity {

	private int screenWidth;
	private int screenHeight;
	private RelativeLayout rl;
	private TextView txt_title;
	private TextView txt_history;

	private GsonHelper gsonHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeLayOut();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_persistent_boggle_history,
				menu);
		return true;
	}

	private void initializeLayOut() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// get screen width
		this.screenWidth = dm.widthPixels;
		// get screen height
		this.screenHeight = dm.heightPixels;

		rl = new RelativeLayout(this);

		txt_title = new TextView(this);
		txt_title.setText("Top Score History");
		txt_title.setTextSize(this.screenWidth / 20);
		txt_title.setId(10);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rllp.topMargin = screenHeight / 20;
		rl.addView(txt_title, rllp);

		txt_history = new TextView(this);
		txt_history.setSingleLine(false);
		txt_history.setText(sortUsersTopScore());
		txt_history.setTextSize(screenWidth / 20);
		rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.BELOW, txt_title.getId());
		rllp.topMargin = screenHeight / 20;
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl.addView(txt_history, rllp);
		setContentView(rl);
	}

	@SuppressWarnings("unchecked")
	private String sortUsersTopScore() {
		try {
			gsonHelper = new GsonHelper();
			ArrayList<UserInfo> array = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.USERINFO);
			Object[] objs = array.toArray();
			Arrays.sort(objs, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					UserInfo u1 = (UserInfo) obj1;
					UserInfo u2 = (UserInfo) obj2;
					return u2.getTopScore() - u1.getTopScore();
				}
			});

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < objs.length; i++) {
				sb.append(String.valueOf(i + 1));
				sb.append("    ");
				sb.append(String.valueOf(((UserInfo) objs[i]).getTopScore()));
				sb.append("    ");
				sb.append(((UserInfo) objs[i]).getUserName());
				sb.append("\n");
			}
			return sb.toString();
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return "NO USER";
		}
	}
}
