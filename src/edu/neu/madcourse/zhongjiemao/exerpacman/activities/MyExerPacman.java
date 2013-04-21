package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import edu.neu.madcourse.zhongjiemao.R;
import android.R.anim;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class MyExerPacman extends Activity implements OnClickListener {

	private static final String TAG = "EXER_PACMAN";

	private WebView mLogoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exer_pacman);
		mLogoView = (WebView) findViewById(R.id.exer_pacman_web_view);
		mLogoView.loadUrl("file:///android_asset/logo.html");
		mLogoView.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 0:

			break;

		default:
			break;
		}
	}

}
