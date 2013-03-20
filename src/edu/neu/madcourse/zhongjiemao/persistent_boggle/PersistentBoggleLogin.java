package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.LoginBLL;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.test.PersistentBoggleTest;

/**
 * This activity is the login activity of the persistent boggle game. The player
 * who want to player persistent boggle game should type his/her user name and
 * password and verify them before login into the game. Or if the player doesn't
 * have an account right now, he/ she can click the register button to start
 * register activity to generate a new account for himself/herself. If the
 * player doesn't want to play, he/she can click back button to go back to the
 * main activity of this application.
 * 
 * @author kevin
 * 
 */
public class PersistentBoggleLogin extends Activity implements OnClickListener {

	// To specify the application's state location
	// Now is at Persistent Boggle Login Activity
	private final String TAG = "Persistent Boggle Login";

	// Integer: Store the screen size information
	private int screenWidth = 0;
	private int screenHeight = 0;

	// Button: login button, click to verify usr and pwd to try to login
	private Button btn_login;
	// Button: quit the game
	private Button btn_back;

	// Button: show the test activity of persistent boggle.
	private Button btn_test;

	// EditText: to get the input of user name from the player
	private EditText et_username;
	// TextView: to show player the place to type his user name
	private TextView tv_usr;

	// To get the layout of this activity from its xml file
	private LayoutInflater layoutInflater;
	private RelativeLayout rly;

	private LoginBLL lbll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Login Activated");
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_persistent_boggle_login);
		// get screen size
		initializeScreenParameter();
		// arrange views' position, layout of this activity
		initializeComponent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater()
				.inflate(R.menu.activity_persistent_boggle_login, menu);
		return true;
	}

	/**
	 * Get screen width and height values for arranging layout.
	 */
	private void initializeScreenParameter() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// get screen width
		this.screenWidth = dm.widthPixels;
		// get screen height
		this.screenHeight = dm.heightPixels;
	}

	/**
	 * Initialize the layout of this activity
	 */
	private void initializeComponent() {

		layoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View ly = layoutInflater.inflate(
				R.layout.activity_persistent_boggle_login, null);

		rly = (RelativeLayout) ly;
		initializeTextViewUSR(rly);
		initializeETUser(rly);
		initializeBtnLogin(rly);
		initializeBtnBack(rly);
		initializeBtnTest(rly);
		initializeBtnOnClickEvents();

		this.setContentView(rly);
	}

	/**
	 * Initialize TextView tv_usr layout
	 * 
	 * @param rly
	 */
	private void initializeTextViewUSR(RelativeLayout rly) {

		tv_usr = new TextView(this);
		tv_usr.setText("User Name: ");
		tv_usr.setTextSize(screenWidth / 35);
		tv_usr.setId(1);
		RelativeLayout.LayoutParams tv_usr_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tv_usr_rllp.topMargin = this.screenHeight / 5 + 13;
		tv_usr_rllp.leftMargin = this.screenWidth / 10;
		rly.addView(tv_usr, tv_usr_rllp);
	}

	/**
	 * Initialize EditText et_username layout
	 * 
	 * @param rly
	 */
	private void initializeETUser(RelativeLayout rly) {
		et_username = new EditText(this);
		et_username.setSingleLine();
		et_username.setTextSize(this.screenWidth / 35);
		et_username.setMinWidth(this.screenWidth * 2 / 5);
		et_username.setId(3);
		RelativeLayout.LayoutParams et_username_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		et_username_rllp.topMargin = this.screenHeight / 5;
		et_username_rllp.addRule(RelativeLayout.RIGHT_OF, tv_usr.getId());
		rly.addView(et_username, et_username_rllp);
	}

	/**
	 * Initialize Button btn_login layout
	 * 
	 * @param rly
	 */
	private void initializeBtnLogin(RelativeLayout rly) {
		btn_login = new Button(this);
		btn_login.setText("Login");
		btn_login.setWidth(this.screenWidth * 2 / 5);
		btn_login.setId(10);
		RelativeLayout.LayoutParams btn_login_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_login_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btn_login_rllp.addRule(RelativeLayout.BELOW, et_username.getId());
		btn_login_rllp.topMargin = this.screenHeight / 10;
		rly.addView(btn_login, btn_login_rllp);
	}

	/**
	 * Initialize Button btn_back layout
	 * 
	 * @param rly
	 */
	private void initializeBtnBack(RelativeLayout rly) {
		btn_back = new Button(this);
		btn_back.setText("Back");
		btn_back.setWidth(this.screenWidth * 2 / 5);
		btn_back.setId(12);
		RelativeLayout.LayoutParams btn_bakc_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_bakc_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btn_bakc_rllp.addRule(RelativeLayout.BELOW, btn_login.getId());
		btn_bakc_rllp.topMargin = this.screenHeight / 20;
		rly.addView(btn_back, btn_bakc_rllp);
	}

	private void initializeBtnTest(RelativeLayout rly) {
		btn_test = new Button(this);
		btn_test.setText("Test");
		btn_test.setWidth(this.screenWidth * 2 / 5);
		btn_test.setId(13);
		RelativeLayout.LayoutParams btn_test_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_test_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btn_test_rllp.addRule(RelativeLayout.BELOW, btn_back.getId());
		btn_test_rllp.topMargin = this.screenHeight / 20;
		rly.addView(btn_test, btn_test_rllp);
	}

	/**
	 * Register all buttons to click listener
	 */
	private void initializeBtnOnClickEvents() {
		btn_login.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_test.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(btn_login)) {
			// check whether the user name and password match.
			Log.d(TAG, "Login Attempt");
			loginAttempt();
		}
		if (arg0.equals(btn_back)) {
			Log.d(TAG, "Quit the game");
			this.finish();
		}
		if (arg0.equals(btn_test)) {
			Log.d(TAG, "Test the game");
			startActivity(new Intent(this, PersistentBoggleTest.class));
		}
	}

	/**
	 * Login operation responds to btn_login click event
	 */
	private void loginAttempt() {
		String usr = et_username.getText().toString();
		LoginAsyncTask login = new LoginAsyncTask();
		if (usr.length() < 5) {
			// here should send a toast to notify person that user name should
			// not be less than 5 letters.
			System.out.println("User Name Should be more than 5 letters");
			login.showResult("User Name Should be more than 5 letters");
			return;
		}
		// ...... Test Only
		// Before commercial edition, uncomment the following code
		// TelephonyManager tm = (TelephonyManager) this
		// .getSystemService(Context.TELEPHONY_SERVICE);
		String[] params = { usr, "000000" };
		login.execute(params);

	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Deal with login task by using async-task
	 * 
	 * @author kevin
	 * 
	 */
	private class LoginAsyncTask extends AsyncTask<String, Void, Integer> {

		private String userName;
		private String phoneID;

		@Override
		protected Integer doInBackground(String... params) {
			lbll = new LoginBLL();
			Boolean check = false;
			int login_result;
			try {
				userName = params[0];
				phoneID = params[1];
				login_result = lbll.isLoginApproved(userName, phoneID);
				if (login_result != LoginBLL.LOGIN_DENIED) {
					check = lbll.loginInitialize(userName);
					if (check) {
						return login_result;
					} else
						return LoginBLL.UNACCESSIBLE;
				}
				return login_result;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch (result) {
			case LoginBLL.NO_SUCH_USER:
			case LoginBLL.LOGIN_APPROVED:
				loginSuccess();
				break;
			case LoginBLL.LOGIN_DENIED:
				showResult("Sorry! Your username is not correct. Please try another one!");
				break;
			case LoginBLL.UNACCESSIBLE:
				showResult("Sorry! Network Unreachable. Please check your network!");
				break;
			default:
				showResult("Oops, some other error occur. We are trying to fix them.");
				break;
			}
		}

		/**
		 * Show up only when login fails.
		 */
		private void showResult(String result) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, result, duration);
			toast.show();
		}

		/**
		 * Login success, get onto Game Hall activity
		 */
		private void loginSuccess() {
			// create a new intent of Game Hall
			Intent i = new Intent(getApplicationContext(), GameHall.class);
			// Put the user ID into new Intent
			i.putExtra(GameHall.GAMEHALL_USERNAME, this.userName);
			startActivity(i);
		}
	}
}
