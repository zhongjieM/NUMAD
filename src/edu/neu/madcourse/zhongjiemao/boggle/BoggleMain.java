package edu.neu.madcourse.zhongjiemao.boggle;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.R.layout;
import edu.neu.madcourse.zhongjiemao.R.menu;
import edu.neu.madcourse.zhongjiemao.sudoku.Game;
import edu.neu.madcourse.zhongjiemao.sudoku.Music;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * This is the main activity of the boggle game. There are five buttons in this
 * activity. They represents: Continue, New Game, About, Acknowledge and Exit
 * respectively.
 * 
 * Click Continue button, you can continue your previous game. Click New Game
 * button, you will start a new game. Click About or Acknowledgment button, you
 * will see a dialogue activity showing the information about the application.
 * Click Exit Button, you will finish boggle game.
 * 
 * @author kevin
 * 
 */
public class BoggleMain extends Activity implements OnClickListener {

	private static final String TAG = "Boggle Main";

	// store the screen size for layout views on this activity.
	private int screen_width = 0;
	private int screen_height = 0;

	// five buttons that have described above
	private Button btn_Continue;
	private Button btn_NewGame;
	private Button btn_About;
	private Button btn_Acknowledge;
	private Button btn_Exit;

	// ImageView iv: store the boggle image that will show above the buttons
	private ImageView iv;

	// Layout
	LayoutInflater inflater;
	private RelativeLayout rly;

	// -------------------------------------------------------------------------
	// ------------------------Override Methods Part----------------------------
	// -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialize the screen size information
		initializeScreenParameters();

		// initialize the layout of this activity
		initializeLayout();

		// start the background music
		Music.play(this, R.raw.bogglestart);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_boggle_main, menu);
		return true;
	}

	@Override
	/**
	 * Called automatically when the game has resumed from pause.
	 */
	protected void onResume() {
		super.onResume();
		// continue play the background music
		Music.play(this, R.raw.bogglestart);
	}

	@Override
	/**
	 * Called automatically when the game pauses.
	 */
	protected void onPause() {
		super.onPause();
		// stop the background music
		Music.stop(this);
	}

	/**
	 * initialize the screen size information which will be used to layout
	 * views.
	 */
	private void initializeScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screen_width = dm.widthPixels;
		this.screen_height = dm.heightPixels;
	}

	/**
	 * initialize the activity layout
	 */
	private void initializeLayout() {
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View ly = inflater.inflate(R.layout.activity_boggle_main, null);
		// Drawable backgroundDrawable =
		// getResources().getDrawable(R.drawable.bogglebackground);
		rly = (RelativeLayout) ly;
		rly.setBackgroundResource(R.drawable.bogglebackground);

		// initialize each views' position and appearance on the activity
		initializeImageView(rly);
		initializeBtnCont(rly);
		initializeBtnNewGame(rly);
		initializeBtnAbout(rly);
		initializeBtnAcknowledge(rly);
		initializeBtnExit(rly);

		// set the layout into view
		setContentView(rly);
	}

	/**
	 * Initialize the ImageView iv.
	 * 
	 * @param rly
	 *            the layout that iv will be set into.
	 */
	private void initializeImageView(RelativeLayout rly) {
		iv = new ImageView(this);
		iv.setImageResource(R.drawable.boggleview);
		RelativeLayout.LayoutParams iv_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		iv_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		iv_rllp.topMargin = screen_height / 20;
		rly.addView(iv, iv_rllp);
	}

	/**
	 * Initialize the Button btn_Continue.
	 * 
	 * @param rly
	 *            the layout that btn_Continue will be set into.
	 */
	private void initializeBtnCont(RelativeLayout rly) {
		btn_Continue = new Button(this);
		btn_Continue.setId(1);
		btn_Continue.setText("Continue");
		btn_Continue.setWidth(screen_width * 3 / 4);
		btn_Continue.setTextColor(Color.WHITE);
		btn_Continue.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_cont_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_cont_rllp.topMargin = screen_height / 2 - screen_height / 20;
		btn_cont_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rly.addView(btn_Continue, btn_cont_rllp);

	}

	/**
	 * Initialize the Button btn_NewGame.
	 * 
	 * @param rly
	 *            the layout that btn_NewGame will be set into.
	 */
	private void initializeBtnNewGame(RelativeLayout rly) {
		btn_NewGame = new Button(this);
		btn_NewGame.setId(2);
		btn_NewGame.setText("New Game");
		btn_NewGame.setWidth(screen_width * 3 / 4);
		btn_NewGame.setTextColor(Color.WHITE);
		btn_NewGame.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_newgame_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_newgame_rllp.addRule(RelativeLayout.BELOW, btn_Continue.getId());
		btn_newgame_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rly.addView(btn_NewGame, btn_newgame_rllp);
	}

	/**
	 * Initialize the Button btn_About.
	 * 
	 * @param rly
	 *            the layout that btn_About will be set into.
	 */
	private void initializeBtnAbout(RelativeLayout rly) {
		btn_About = new Button(this);
		btn_About.setId(3);
		btn_About.setText("About");
		btn_About.setWidth(screen_width * 3 / 4);
		btn_About.setTextColor(Color.WHITE);
		btn_About.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_about_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_about_rllp.addRule(RelativeLayout.BELOW, btn_NewGame.getId());
		btn_about_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rly.addView(btn_About, btn_about_rllp);
	}

	/**
	 * Initialize the Button btn_Acknowedge.
	 * 
	 * @param rly
	 *            the layout that btn_Acknowledge will be set into.
	 */
	private void initializeBtnAcknowledge(RelativeLayout rly) {
		btn_Acknowledge = new Button(this);
		btn_Acknowledge.setId(4);
		btn_Acknowledge.setText("Acknowledgement");
		btn_Acknowledge.setWidth(screen_width * 3 / 4);
		btn_Acknowledge.setTextColor(Color.WHITE);
		btn_Acknowledge.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_ack_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_ack_rllp.addRule(RelativeLayout.BELOW, btn_About.getId());
		btn_ack_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rly.addView(btn_Acknowledge, btn_ack_rllp);
	}

	/**
	 * Initialize the Button btn_Exit.
	 * 
	 * @param rly
	 *            the layout that btn_Exit will be set into.
	 */
	private void initializeBtnExit(RelativeLayout rly) {
		btn_Exit = new Button(this);
		btn_Exit.setId(5);
		btn_Exit.setText("Exit");
		btn_Exit.setWidth(screen_width * 3 / 4);
		btn_Exit.setTextColor(Color.WHITE);
		btn_Exit.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_exit_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_exit_rllp.addRule(RelativeLayout.BELOW, btn_Acknowledge.getId());
		btn_exit_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rly.addView(btn_Exit, btn_exit_rllp);
	}

	@Override
	/**
	 * onClick method. Handle the click events on the activity.
	 */
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// Continue a game
		if (v.equals(btn_Continue)) {
			startGame(1);
		}
		// Start a new game
		if (v.equals(btn_NewGame)) {
			startGame(2);
		}
		// See acknowledgment
		if (v.equals(btn_Acknowledge)) {
			Intent intent = new Intent(this, BoggleAcknowledgement.class);
			startActivity(intent);
		}
		// See about
		if (v.equals(btn_About)) {
			Intent intent = new Intent(this, BoggleAbout.class);
			startActivity(intent);
		}
		// Finish the application
		if (v.equals(btn_Exit)) {
			Music.stop(this);
			finish();
		}
	}

	/**
	 * start a game according to its parameter.
	 * 
	 * @param i
	 *            If i equals to 1, continue a game; if i equal to 2, start a
	 *            new game.
	 */
	private void startGame(int i) {
		Log.d(TAG, "clicked on " + i);
		Intent intent = new Intent(this, BoggleGame.class);
		intent.putExtra(BoggleGame.GAMESTATUS, i);
		startActivity(intent);
	}
}
