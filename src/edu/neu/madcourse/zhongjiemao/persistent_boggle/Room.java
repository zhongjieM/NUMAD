package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.RoomBLL;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceController;

/**
 * This class is to the Room Activity. There are two characters in this room:
 * room Master, and room member. Everyone who enters into a room will set up two
 * thread: one is to check whether the game in this room has been started; the
 * other is to check whether the room exists.
 * 
 * If the room master quit the room, s/he will delete the room from the remote
 * server and all the other room member who catch this message will quit the
 * room automatically.
 * 
 * @author kevin
 * 
 */
public class Room extends Activity implements OnClickListener {

	private final String TAG = "Persistent Boggle Room";

	public final static String INTENT_ROOMID = "INTENT_ROOMID";
	public final static String INTENT_IDENTITY = "INTENT_IDENTITY";
	public final static String INTENT_USERNAME = "INTENT_USERNAME";
	public final static int ID_ROOMMASTER = 1;
	public final static int ID_ROOMPLAYER = 0;

	private final static int TEXTVIEW_STARTID = 10001;
	private final static int BUTTON_STARTID = 10011;
	private final static int BUTTON_INVITEID = 10012;
	private final static int BUTTON_QUITID = 10013;

	private final static int TEXT_SIZE_RATE = 50;

	private final static int TASK_CHECK_ROOM_STATUS = 1;
	private final String NO_PLAYER = "No Player";

	private int identity = 1;
	private String userName;

	private int screenWidth = 0;
	private int screenHeight = 0;

	private String roomID = "";
	private int numberOfPlayers = 0;
	private String player1;
	private String player2;
	private String player3;

	private RelativeLayout rl;

	private TextView[] textView;

	private Button btn_Start;
	private Button btn_Quit;
	private Button btn_Invite;

	private TimerTask task_GetRoomStatus;
	private Timer timer;
	private Handler handler;

	private RoomBLL roomBLL;
	private RoomStatus room;
	private int roomStatus;

	private Boolean backButtonEnabled = true;

	// service part
	private static Boolean isHomeNotPressed = false;
	private ServiceController serviceController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeRoomParameters();
		initializeScreenParameters();
		initializeViewComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_room, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		try {
			isHomeNotPressed = true;
			switch (arg0.getId()) {
			case BUTTON_STARTID:
				openNewGameDialog();
				break;
			case BUTTON_INVITEID:
				Intent i = new Intent(this, InvitationDialog.class);
				i.putExtra(InvitationDialog.ROOMID, this.roomID);
				i.putExtra(InvitationDialog.USERNAME, this.userName);
				startActivity(i);
				break;
			case BUTTON_QUITID:
				quitRoom();
				break;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	@Override
	public void onPause() {
		try {
			if (!isHomeNotPressed) {
				String[] params = { roomID, userName };
				serviceController = new ServiceController(this);
				serviceController.startServiceForRoom(params);
				serviceController
						.stopServiceById(ServiceController.SERVICE_FOR_BACKGROUND_MUSIC);
			} else
				isHomeNotPressed = false;
			timer.cancel();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		super.onStop();
	}

	@Override
	public void onResume() {
		try {
			serviceController = new ServiceController(this);
			serviceController
					.stopServiceById(ServiceController.SERVICE_FOR_ROOM);
			serviceController.startServiceForBackgroundMusic();
			startThreadListener();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "Back Button Pressed");
		if (backButtonEnabled) {
			quitRoom();
		}
	}

	// ------------------ private methods -------------------------------------

	// ------------------ Activity Initialization -----------------------------
	private void initializeRoomParameters() {
		roomID = getIntent().getStringExtra(Room.INTENT_ROOMID);
		identity = getIntent().getIntExtra(Room.INTENT_IDENTITY,
				Room.ID_ROOMMASTER);
		userName = getIntent().getStringExtra(Room.INTENT_USERNAME);
		roomBLL = new RoomBLL(roomID, userName);
	}

	private void initializeScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void initializeViewComponents() {
		rl = new RelativeLayout(this);
		rl.setBackgroundResource(R.drawable.persistentbogglebackground);
		initializeTextViews(rl);
		initializeBtnStart(rl);
		initializeBtnInvite(rl);
		initializeBtnQuit(rl);
		onClickListenerRegister();
		setContentView(rl);
	}

	private void initializeTextViews(RelativeLayout rl) {
		LayoutParams[] textView_lp = new LayoutParams[10];
		String[] _string_textView_text = { "Room ID: ", "Players: ",
				"Player1: ", "Player2: ", "Player3: " };
		String[] string_textView_text = { roomID,
				String.valueOf(numberOfPlayers), player1, player2, player3 };
		textView = new TextView[10];
		for (int i = 0; i < 5; i++) {
			textView[i * 2] = new TextView(this);
			textView[i * 2].setId(TEXTVIEW_STARTID + i * 2);
			textView[i * 2].setText(_string_textView_text[i]);
			textView[i * 2].setTextSize(screenHeight / TEXT_SIZE_RATE);
			textView[i * 2].setTextColor(Color.WHITE);
			textView_lp[i * 2] = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			textView_lp[i * 2].addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textView_lp[i * 2].leftMargin = screenWidth / 7;
			textView_lp[i * 2].topMargin = screenHeight * (1 + i) / 10;
			rl.addView(textView[i * 2], textView_lp[i * 2]);
		}
		for (int i = 0; i < 5; i++) {
			textView[i * 2 + 1] = new TextView(this);
			textView[i * 2 + 1].setId(TEXTVIEW_STARTID + i * 2 + 1);
			textView[i * 2 + 1].setText(string_textView_text[i]);
			textView[i * 2 + 1].setTextSize(screenHeight / TEXT_SIZE_RATE);
			textView[i * 2 + 1].setTextColor(Color.WHITE);
			textView_lp[i * 2 + 1] = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			textView_lp[i * 2 + 1].leftMargin = screenWidth * 9 / 14;
			textView_lp[i * 2 + 1].topMargin = screenHeight * (1 + i) / 10;
			rl.addView(textView[i * 2 + 1], textView_lp[i * 2 + 1]);
		}

	}

	private void initializeBtnStart(RelativeLayout rl) {
		btn_Start = new Button(this);
		btn_Start.setText("Start");
		btn_Start.setId(BUTTON_STARTID);
		btn_Start.setTextColor(Color.WHITE);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.BELOW, textView[9].getId());
		param.topMargin = screenHeight / 10;
		param.leftMargin = screenWidth / 7;
		if (identity == Room.ID_ROOMPLAYER)
			btn_Start.setEnabled(false);
		rl.addView(btn_Start, param);
	}

	private void initializeBtnInvite(RelativeLayout rl) {
		btn_Invite = new Button(this);
		btn_Invite.setText("Invite");
		btn_Invite.setId(BUTTON_INVITEID);
		btn_Invite.setTextColor(Color.WHITE);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.BELOW, textView[9].getId());
		param.addRule(RelativeLayout.RIGHT_OF, btn_Start.getId());
		param.topMargin = screenHeight / 10;
		param.leftMargin = screenWidth / 10;
		if (identity == Room.ID_ROOMPLAYER)
			btn_Invite.setEnabled(false);
		rl.addView(btn_Invite, param);
	}

	private void initializeBtnQuit(RelativeLayout rl) {
		btn_Quit = new Button(this);
		btn_Quit.setText(" Quit ");
		btn_Quit.setId(BUTTON_QUITID);
		btn_Quit.setTextColor(Color.WHITE);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.BELOW, textView[9].getId());
		param.addRule(RelativeLayout.RIGHT_OF, btn_Invite.getId());
		param.topMargin = screenHeight / 10;
		param.leftMargin = screenWidth / 10;
		rl.addView(btn_Quit, param);
	}

	private void onClickListenerRegister() {
		btn_Start.setOnClickListener(this);
		btn_Invite.setOnClickListener(this);
		btn_Quit.setOnClickListener(this);
	}

	private void updateTextView(RoomStatus room) {
		try {
			if (room != null) {
				textView[1].setText(room.getRoomID());
				textView[3].setText(String.valueOf(room.getNumberOfPlayers()));
				textView[5]
						.setText(room.getPlayer1().intern() == RoomStatus.DEFAULT_PLAYER ? this.NO_PLAYER
								: room.getPlayer1());
				textView[7]
						.setText(room.getPlayer2().intern() == RoomStatus.DEFAULT_PLAYER ? this.NO_PLAYER
								: room.getPlayer2());
				textView[9]
						.setText(room.getPlayer3().intern() == RoomStatus.DEFAULT_PLAYER ? this.NO_PLAYER
								: room.getPlayer3());
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	private void checkStatus(int status) {
		try {
			switch (status) {
			case RoomBLL.STATUS_ROOM_NOT_EXIST:
				// go back to game hall and toast
				showToast("Sorry the room no longer exists!");
				if (timer != null)
					timer.cancel();
				roomBLL.updateONLINEUSERtoNotInGame();
				finish();
				break;
			case RoomBLL.STATUS_GAME_START:
				// TODO: start a new game
				startGameAsMember();
				break;
			case RoomBLL.STATUS_YOU_ARE_MASTER:
				// activate btn_Start
				break;
			default:
				// MEMBER, but IDENTITY NO CHANGE
				break;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	private void startThreadListener() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TASK_CHECK_ROOM_STATUS:
					// update all the TextView and button
					updateTextView(room);
					checkStatus(roomStatus);
					break;
				}
				super.handleMessage(msg);
			}
		};
		startThreadCheckRoomStatus();
	}

	private void startThreadCheckRoomStatus() {
		task_GetRoomStatus = new TimerTask() {
			@Override
			public void run() {
				try {
					room = roomBLL.getRoomStatus();
					roomStatus = roomBLL.checkCurrentRoomStatus();
					Message msg = new Message();
					msg.what = Room.TASK_CHECK_ROOM_STATUS;
					handler.sendMessage(msg);
				} catch (Exception ex) {
					System.out.println(ex.toString());
					return;
				}
			}
		};
		timer = new Timer();
		timer.schedule(task_GetRoomStatus, 2000, 1000);

	}

	private void quitRoom() {
		try {
			if (timer != null)
				timer.cancel();
			setViewEnabled(false);
			QuitRoomAsyncTask qrat = new QuitRoomAsyncTask();
			qrat.execute();
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	private void setViewEnabled(Boolean enabled) {
		btn_Invite.setEnabled(enabled);
		btn_Quit.setEnabled(enabled);
		btn_Start.setEnabled(enabled);
		backButtonEnabled = enabled;
	}

	/**
	 * Show Words by using Toast
	 * 
	 * @param toastWords
	 *            : the words will be toasted
	 * @param wordsType
	 *            : toasted type
	 */
	private void showToast(String toastWords) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, toastWords, duration);
		toast.show();
	}

	/**
	 * Ask the user what difficulty level they want
	 * */
	private void openNewGameDialog() {
		try {
			new AlertDialog.Builder(this)
					.setTitle("Start a New Game")
					.setItems(R.array.board,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									if (roomBLL.createNewGame(i + 4, roomID)) {
										startNewGame(i + 4, roomID,
												roomBLL.getLetters(),
												PersistentBoggleGame.ROOMMASTER);
									} else {
										showToast("Sorry! You can't start right now!");
									}
								}
							}).show();
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	/**
	 * Start a new Game--persistent boggle game
	 * 
	 * @param mode
	 * @param roomID
	 */
	private void startNewGame(int mode, String roomID, String letters,
			int character) {
		try {
			if (timer != null)
				timer.cancel();
			if (roomBLL.startGameOperation(userName)) {
				new ServiceController(this)
						.stopServiceById(ServiceController.SERVICE_FOR_BACKGROUND_MUSIC);
				isHomeNotPressed = true;
				Intent in = new Intent(getApplicationContext(),
						PersistentBoggleGame.class);
				in.putExtra(Room.INTENT_USERNAME, this.userName);
				in.putExtra(PersistentBoggleGame.GAMEMODE, mode);
				in.putExtra(INTENT_ROOMID, roomID);
				in.putExtra(PersistentBoggleGame.LETTERS, letters);
				in.putExtra(PersistentBoggleGame.CHARACTER, character);
				startActivity(in);
			} else {
				showToast("Sorry! You can't start right now!");
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	private void startGameAsMember() {
		try {
			serviceController
					.stopServiceById(ServiceController.SERVICE_FOR_ROOM);
			if (!roomBLL.startable())
				return;
			String letters = roomBLL.getLettersFromServer(roomID);
			while (letters == null) {
				letters = roomBLL.getLettersFromServer(roomID);
			}
			startNewGame((int) Math.sqrt(letters.length()), roomID, letters,
					PersistentBoggleGame.ROOMMEMBER);
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}
	}

	private class QuitRoomAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			isHomeNotPressed = true;
			return roomBLL.quitRoom();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				finish();
			} else {
				// TODO you can't quit room right now
			}
		}

	}

}
