/**
 * TODO:
 * 
 * 1. have to thread cancel
 * 
 * 2. store userName
 */
package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.GameHallBLL;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.arrayadapter.RoomStatusAdapter;

/**
 * This is the game hall activity. Player who enters this room will have a
 * record at online user table at remote server.
 * 
 * The player can create a room for him/herself. The room will be created under
 * his/her name.
 * 
 * The player can choose an existed room from the list view, where the game
 * hasn't been started and the room is not full.
 * 
 * The player will also receive an invitation from other player who sends
 * invitation to this player.
 * 
 * When the player pressed the HOME button or power button, a service will setup
 * to receive invitation from other players.
 * 
 * When the player leave the game hall, he will unregistered from the remote
 * ONLINEUSER table.
 * 
 * @author kevin
 * 
 */
public class GameHall extends Activity implements OnClickListener {

	//
	private final String TAG = "Persisten Boggle Game Hall";
	public final static String GAMEHALL_USERNAME = "edu.neu.madcourse.zhongjiemao.persistent_boggle.gamehall.username";
	//
	public final static int ILLEGAL_ENTER_WARNING = -1;
	public final static int LOGIN_SUCCESS = 1;
	public final static int NEW_ROOM_CREATED = 2;
	public final static int FAIL_TO_QUIT = 3;
	//
	private final int ROOM_NAME_TEXTVIEW_ID = 1000;
	private final int LISTVIEW_ID = 1001;
	//
	private final int TEXT_SIZE_RATE = 50;
	//
	private final static int MESSAGE_GAMEHALL_GETROOMS = 1;
	private final static int MESSAGE_GAMEHALL_INVITATION = 2;

	//
	private int screenWidth = 0;
	private int screenHeight = 0;

	private LayoutInflater li;
	private RelativeLayout rly;

	private TextView tv_roomName;
	private TextView tv_numberOfPlayers;
	private Button btn_CreateNewRoom;
	private Button btn_Back;
	private ListView lv;
	RelativeLayout rl;

	// TODO: userName have to be stored for resume when pressed HOME button
	private String userName = "";
	private ArrayList<RoomStatus> array_roomstatus;
	private ArrayAdapter<RoomStatus> adapter_roomstatus;
	private GameHallBLL gameHallBLL;

	private static Handler handler;
	private TimerTask task_GetRooms;
	private TimerTask task_GetInvitation;
	private Timer timer;
	private String invitation = "";
	private Boolean backButtonEnabled = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userName = this.getIntent().getStringExtra(GameHall.GAMEHALL_USERNAME);
		if (userName != null) {
			Log.d(TAG, "Successfully enter game hall as user: " + userName);
			showSuccessfullyEnter();
			gameHallBLL.ShowAllTables();
		} else {
			Log.d(TAG, "Fails to enter game hall with a valid user name.");
			showFailureEnter();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game_hall, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(btn_Back)) {
			// quit the game hall, log off the game
			setViewEnabled(false);
			QuitGameHallAsyncTask quitHall = new QuitGameHallAsyncTask();
			quitHall.execute();
		} else if (arg0.equals(btn_CreateNewRoom)) {
			// create a new room for the user himself
			String[] params = { this.userName };
			setViewEnabled(false);
			CreateNewRoomAsyncTask createRoom = new CreateNewRoomAsyncTask();
			createRoom.execute(params);
		}
	}

	@Override
	/**
	 * Deal with BACK button pressed
	 */
	public void onBackPressed() {
		Log.d(TAG, "BACK PRESSED");
		if (backButtonEnabled) {
			QuitGameHallAsyncTask quitHall = new QuitGameHallAsyncTask();
			quitHall.execute();
		}
	}

	@Override
	public void onStart() {
		// When activity starts or goes back, starts threads;
		startThreadListerner();
		setViewEnabled(true);
		super.onStart();
	}

	@Override
	public void onStop() {
		// When press BACK button, HOME button, Quit button or LOCK button, the
		// activity will go out, so all the thread will be canceled;
		timer.cancel();
		super.onStop();
	}

	// -------------------- private methods -----------------------------------

	private void showSuccessfullyEnter() {
		showToast("Login Successfully", LOGIN_SUCCESS);
		gameHallBLL = new GameHallBLL(this.userName);
		initializeScreenParameters();
		initializeComponents();
		buttonOnClickEventRegister();
	}

	/**
	 * Deal with Illegal enter without a userName
	 */
	private void showFailureEnter() {
		showToast("Illegal Enterance, please click back button and try again.",
				ILLEGAL_ENTER_WARNING);
		rl = new RelativeLayout(this);
		btn_Back = new Button(this);
		btn_Back.setText("   Quit   ");
		RelativeLayout.LayoutParams btn_back_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_back_rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rl.addView(btn_Back, btn_back_rllp);
		btn_Back.setOnClickListener(this);
		setContentView(rl);
	}

	/**
	 * Get screen parameters
	 */
	private void initializeScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screenHeight = dm.heightPixels;
		this.screenWidth = dm.widthPixels;
	}

	private void initializeComponents() {
		rl = new RelativeLayout(this);
		initializeRoomName(rl);
		initializeNumberOfPlayers(rl);
		initializeListView(rl);
		initializeBtnCreate(rl);
		initializeBtnBack(rl);
		setContentView(rl);
	}

	private void initializeRoomName(RelativeLayout rl) {
		RelativeLayout.LayoutParams rn_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rn_rllp.topMargin = screenHeight / 40;
		rn_rllp.leftMargin = screenWidth / 30;
		rn_rllp.height = screenHeight / 20;
		tv_roomName = new TextView(this);
		tv_roomName.setId(ROOM_NAME_TEXTVIEW_ID);
		tv_roomName.setText("Room ID");
		tv_roomName.setTextSize(screenHeight / TEXT_SIZE_RATE);
		rl.addView(tv_roomName, rn_rllp);
	}

	private void initializeNumberOfPlayers(RelativeLayout rl) {
		RelativeLayout.LayoutParams nop_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		nop_rllp.topMargin = screenHeight / 40;
		nop_rllp.rightMargin = screenWidth / 30;
		nop_rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		nop_rllp.height = screenHeight / 20;
		tv_numberOfPlayers = new TextView(this);
		tv_numberOfPlayers.setText("Players");
		tv_numberOfPlayers.setTextSize(screenHeight / TEXT_SIZE_RATE);
		rl.addView(tv_numberOfPlayers, nop_rllp);
	}

	// TODO: add background and other things to decorate list view
	private void initializeListView(RelativeLayout rl) {
		lv = new ListView(this);
		lv.setId(LISTVIEW_ID);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// try to join a room
				RoomStatus[] rss = new RoomStatus[1];
				rss[0] = (RoomStatus) lv.getItemAtPosition(position);
				setViewEnabled(false);
				JoinARoom jar = new JoinARoom();
				jar.execute(rss);
			}

		});

		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.BELOW, tv_roomName.getId());
		rllp.rightMargin = screenWidth / 30;
		rllp.leftMargin = screenWidth / 30;
		rllp.height = screenHeight * 13 / 20;
		rl.addView(lv, rllp);
	}

	// TODO: add color or image to the buttons
	private void initializeBtnCreate(RelativeLayout rl) {
		RelativeLayout.LayoutParams btn_create_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_create_rllp.addRule(RelativeLayout.BELOW, lv.getId());
		btn_create_rllp.leftMargin = screenWidth / 30;
		btn_CreateNewRoom = new Button(this);
		btn_CreateNewRoom.setText("Create");
		rl.addView(btn_CreateNewRoom, btn_create_rllp);
	}

	private void initializeBtnBack(RelativeLayout rl) {
		RelativeLayout.LayoutParams btn_back_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_back_rllp.addRule(RelativeLayout.BELOW, lv.getId());
		btn_back_rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btn_back_rllp.rightMargin = screenWidth / 40;
		btn_Back = new Button(this);
		btn_Back.setText("   Quit   ");
		rl.addView(btn_Back, btn_back_rllp);
	}

	/**
	 * Register the onClickListerner for all the buttons on the activity.
	 */
	private void buttonOnClickEventRegister() {
		btn_Back.setOnClickListener(this);
		btn_CreateNewRoom.setOnClickListener(this);
	}

	/**
	 * This method will start threads to listen to ONLINEUSER table and
	 * ROOMSTATUS table at the remote server. In GameHall, threads get data from
	 * remote server 1000ms a time.
	 */
	private void startThreadListerner() {
		timer = new Timer();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_GAMEHALL_GETROOMS:
					adapter_roomstatus = new RoomStatusAdapter(
							getApplicationContext(),
							R.layout.roomstatus_layout, array_roomstatus);
					adapter_roomstatus.notifyDataSetChanged();
					lv.setAdapter(adapter_roomstatus);
					break;
				case MESSAGE_GAMEHALL_INVITATION:
					getInvitation();
					break;
				}
				super.handleMessage(msg);
			}
		};
		startThreadCheckInvitation();
		startThreadGetRoomStatus();
	}

	/**
	 * Check rooms status that will update the listview on the screen. This
	 * thread will run 1 time every 2 minutes
	 */
	private void startThreadGetRoomStatus() {
		task_GetRooms = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Thread1 running");
				array_roomstatus = gameHallBLL.getRoomStatus();
				if (array_roomstatus == null) {
					array_roomstatus = new ArrayList<RoomStatus>();
				}
				Message msg = new Message();
				msg.what = MESSAGE_GAMEHALL_GETROOMS;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(task_GetRooms, 1000, 2000);
	}

	/**
	 * Start a thread to check whether there is an invitation to the player. The
	 * thread will execute 1 time every 2 minutes
	 */
	private void startThreadCheckInvitation() {
		task_GetInvitation = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Thread2 running");
				String temp = gameHallBLL.checkInvitation();
				if (temp != null) {
					invitation = temp.intern();
					Message msg = new Message();
					msg.what = GameHall.MESSAGE_GAMEHALL_INVITATION;
					handler.sendMessage(msg);
				} else {
					// no invitation
					// System.out.println("invitation is empty");
				}
			}
		};
		timer.schedule(task_GetInvitation, 3000, 2000);
	}

	/**
	 * Get invitation and show the invitation in a dialog box
	 */
	private void getInvitation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(invitation + " is inviting you!");
		builder.setTitle("You get an invitation");
		builder.setPositiveButton("Accept",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RoomStatus rs = gameHallBLL.getRoomInfo(invitation);
						if (rs == null) {
							// can't find that room, do nothing
						} else {
							JoinARoom jar = new JoinARoom();
							jar.execute(rs);
						}
					}
				});
		builder.setCancelable(true);
		builder.show();
	}

	/**
	 * Show Words by using Toast
	 * 
	 * @param toastWords
	 *            : the words will be toasted
	 * @param wordsType
	 *            : toasted type
	 */
	private void showToast(String toastWords, int wordsType) {
		int duration = Toast.LENGTH_SHORT;
		switch (wordsType) {
		case GameHall.ILLEGAL_ENTER_WARNING:
			duration = Toast.LENGTH_LONG;
		case GameHall.LOGIN_SUCCESS:
			duration = Toast.LENGTH_SHORT;
		case GameHall.NEW_ROOM_CREATED:
			duration = Toast.LENGTH_LONG;
		case GameHall.FAIL_TO_QUIT:
			duration = Toast.LENGTH_SHORT;
		}
		Toast toast = Toast.makeText(this, toastWords, duration);
		toast.show();
	}

	private void finishActivity() {
		finish();
	}

	private void setViewEnabled(Boolean enabled) {
		backButtonEnabled = enabled;
		btn_Back.setEnabled(enabled);
		btn_CreateNewRoom.setEnabled(enabled);
		lv.setEnabled(enabled);
	}

	// ------------------ AsyncTask Part --------------------------------------

	// create a new room
	private class CreateNewRoomAsyncTask extends
			AsyncTask<String, Object, Boolean> {
		private String userName;

		@Override
		protected Boolean doInBackground(String... arg0) {
			userName = arg0[0];
			return gameHallBLL.startANewRoom();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				// Start a new Room
				showToast("A New Room Created", GameHall.NEW_ROOM_CREATED);
				Intent i = new Intent(getApplicationContext(), Room.class);
				i.putExtra(Room.INTENT_ROOMID, userName);
				i.putExtra(Room.INTENT_IDENTITY, Room.ID_ROOMMASTER);
				i.putExtra(Room.INTENT_USERNAME, userName);
				startActivity(i);
			} else {
				showToast("Fail to create a new Room",
						GameHall.NEW_ROOM_CREATED);
				setViewEnabled(true);
			}
		}
	}

	private class QuitGameHallAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return gameHallBLL.quitGameHall();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				// for test use only
				gameHallBLL.ShowAllTables();
				finishActivity();
			} else {
				// TODO: fails to quit
				showToast(
						"Fails to quit, please try latter or directly go back",
						GameHall.FAIL_TO_QUIT);
				setViewEnabled(true);
			}
		}
	}

	private class JoinARoom extends AsyncTask<RoomStatus, Void, Boolean> {

		private RoomStatus rs;

		@Override
		protected Boolean doInBackground(RoomStatus... rooms) {
			rs = rooms[0];
			return gameHallBLL.setIntoRoom(rs.getRoomID());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				showToast("You can't join that room!", Toast.LENGTH_SHORT);
				setViewEnabled(true);
			} else {
				Intent i = new Intent(getApplicationContext(), Room.class);
				i.putExtra(Room.INTENT_ROOMID, rs.getRoomID());
				i.putExtra(Room.INTENT_IDENTITY, Room.ID_ROOMPLAYER);
				i.putExtra(Room.INTENT_USERNAME, userName);
				startActivity(i);
			}
		}
	}
}
