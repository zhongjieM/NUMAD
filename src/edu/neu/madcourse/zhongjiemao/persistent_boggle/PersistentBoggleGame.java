package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.Score;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.SoundEffect;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.GameOverResult;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserGameStatus;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.PersistentBoggleBLL;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceController;
import edu.neu.madcourse.zhongjiemao.sudoku.Music;

/**
 * This activity is the game part. All the game actions will happened here. It
 * extends from Activity and implements the OnClickListener interface so that
 * all the buttons in it can respond to click event. This activity will do the
 * following jobs: 1. Randomly generate 16 letters; 2. Load dictionary to the
 * application; 3. Allow player to choose letter from word pad; 4. Show the
 * letters that player has chosen; 5. Check the correctness of the word that
 * player has chosen; 6. Show the words that player has chosen and verified by
 * dictionary; 7. Calculate and show the score that player has got; 8. Count
 * down time; 9. Allow player to pause the game; 10. Allow player to exit the
 * current game.
 * 
 * @author kevin
 * 
 */
public class PersistentBoggleGame extends Activity implements OnClickListener {

	private static final String TAG = "Boggle Game";
	public static final String GAMEMODE = "GAMEMODE";
	public static final String CHARACTER = "CHARACTER";
	public static final int ROOMMASTER = 1;
	public static final int ROOMMEMBER = 2;
	// The following constants are for save the data of this activity when
	// the activity is paused.
	public static final String GAMESTATUS = "edu.neu.madcourse.zhongjiemao.boggle";
	private static final String EXISTEDWORD = "EXISTEDWORD";
	public static final String LETTERS = "LETTERS";
	private static final String SCORE = "SCORE";
	private static final String TIMER = "TIMER";
	private static final int OVER = 2;

	// Constants: game period default is 180 seconds
	private static final int GAMETIME = 180;

	// Network tasks message id;
	private static final int NETWORK_TASKS_UPDATE_PLAYER_INFO = 1;
	private static final int NETWORK_TASKS_GAME_OVER = 2;

	// store the screen size
	private int screen_width = 0;
	private int screen_height = 0;
	private int mode = 4;
	private String roomID;
	private String userName;
	private int character;

	// The main view which is to show the random selected words on screen
	private PersistentWordGridView wordGridView;
	// TextView: to show the timer that will count down
	private TextView txt_Timer;
	// ScrollView: to contain TextView whose content is the existed words
	private ScrollView[] sv;
	// TextView: to show the existed words that player has chosen
	private TextView[] txt_ExistedWords;
	private String[] contentsOfTextView;
	private RelativeLayout rl_existed_words;
	// TextView: to show the letters that player has chosen till now
	// its content will be empty once the player get up his finger
	private TextView txt_wording;
	// Button: once tapped, the activity will finish and application will
	// go back to BoggleMain.Class
	private Button btn_Exit;

	// LayoutInflater: to capture the layout inflater of
	// activity_boggle_game.xml
	private LayoutInflater inflater;
	// RelativeLayout: to store the layout of activity_boggle_game.xml as
	// RelativeLayout type
	private RelativeLayout rly;

	// The following variables are used for time count down
	private Timer timer;
	private TimerTask tick_task;
	private RelativeLayout layout_timer;
	// Handler: used for send message from timer thread to UI thread;
	private Handler handler;
	// Integer: to store the remaining seconds. Start from GAMETIME = 180
	private int recLen = GAMETIME;

	// Boolean: record whether the Pause button has been tapped
	// if pause button has been tapped, the activity will hide letters and
	// stop counting down. This is different from the Pause of the activity
	private int isPause = 0;

	// Char[]: to store the letters that player has chosen.
	// Once player's finger leave the screen, its content will clear up.
	protected char[] letters;
	// Stack<String>: to store the words that player has chosen and existed
	// in dictionary
	// private Stack<String> stack_existed_words;
	private LinkedHashSet<String> existed_words;
	// BoggleBLL: the handle the business layer part of boggle game;
	// it will load the dictionary and verify the words' correctness
	private static PersistentBoggleBLL bbll;
	// Score: store the current score player has got
	private Score score;

	private static Boolean isBackExitPressed = false;

	private Timer networkTasksTimer;
	private static Handler networkTasksHandler;
	private TimerTask timertask_NetworkTasks_UpdatePlayers;
	private GameOverResult gor;

	// Service part
	private ServiceController serviceController;

	// -------------------------------------------------------------------------
	// ------------------------Override Methods Part----------------------------
	// -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initialize the screen parameters;
		initializeScreenParameters();
		// initialize layout and views
		initializeLayout();
		// initialize activity data
		initializeActivityParams();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_boggle_game, menu);
		return true;
	}

	@Override
	// implements onClick methods from OnClickListener
	public void onClick(View v) {
		if (v.equals(btn_Exit)) {
			reallyWantToExited();
		}
	}

	@Override
	// automatically called when the activity resume from pause
	protected void onResume() {
		Log.d(TAG, "OnResume Called");
		try {
			serviceController = new ServiceController(this);
			if (serviceController
					.isServiceStarts(ServiceController._SERVICE_FOR_GAME)) {
				serviceController
						.stopServiceById(ServiceController.SERVICE_FOR_GAME);
				recLen = getSharedPreferences("GAME_TIME_CURRENT", MODE_PRIVATE)
						.getInt("TIME_CURRENT", GAMETIME);
			}
			initializeNetworkListener();
			timer();
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggle: onResume Failed";
			System.out.println(errorMessage);
			System.out.println(ex.toString());
		}
		Music.play(this, R.raw.bogglegaming);
		super.onResume();
	}

	@Override
	// automatically called when the activity paused
	protected void onPause() {
		Log.d(TAG, "onPause");
		try {
			stopTimerTasks();
			if (!isBackExitPressed) {
				// Start a service to continue the job.
				serviceController = new ServiceController(this);
				String[] params = { roomID, userName,
						String.valueOf(character), String.valueOf(recLen) };
				serviceController.startServiceForGame(params);
			} else {
				isBackExitPressed = false;
			}
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggle: onPause Failed";
			System.out.println(errorMessage);
			System.out.println(ex.toString());
		}
		Music.stop(this);
		super.onPause();

	}

	// -------------------------------------------------------------------------
	// ------------------------Protected Methods Part---------------------------
	// -------------------------------------------------------------------------

	/**
	 * Show the current letters that player has chosen.
	 * 
	 * @param txt
	 *            the current letter that player has chosen.
	 */
	protected void showText(String txt) {
		txt_wording.setText(txt);
	}

	/**
	 * Show the words that player has chosen and verified by dictionary. Export
	 * all the string elements from stack_existed_words Stack and and reform
	 * them from Object objects to String objects before showing.
	 */
	protected void showCorrectWord() {
		StringBuilder words_builder = new StringBuilder("You:\nScore: "
				+ score.getScore() + "\nExisted Words: \n");
		Iterator<String> iterator = existed_words.iterator();
		while (iterator.hasNext()) {
			words_builder.append(iterator.next().toString());
			words_builder.append('\n');
		}
		txt_ExistedWords[2].setText(words_builder);
	}

	/**
	 * Get possible word that player has just chosen, verify its correctness. If
	 * correct: 1. push the word into stack_existed_words Stack 2. add score to
	 * current score 3. show new score 4. refresh the correct words on show.
	 * 
	 * @param word
	 *            possible word to be examined
	 */
	protected void addPossibleWord(String word) {
		if (bbll.contains(word) && (!existed_words.contains(word))) {
			existed_words.add(word);
			score.wordsPointsAdding(word);
			showCorrectWord();
			AddNewWords anw = new AddNewWords();
			try {
				anw.execute();
			} catch (Exception ex) {
				System.out.println("Update new words to server failed");
			}
			SoundEffect.playCorrectWord();
		} else
			SoundEffect.playError();
	}

	@Override
	public void onBackPressed() {
		reallyWantToExited();
	}

	// -------------------------------------------------------------------------
	// ------------------------Private Methods Part-----------------------------
	// -------------------------------------------------------------------------

	/**
	 * Initialize the activity variables that store game data. If the activity
	 * is resumed from pause, reload data from preferences; If the activity is a
	 * new activity call startNewGame() method. Then initialize BoggleBLL class.
	 */
	private void initializeActivityParams() {
		roomID = this.getIntent().getStringExtra(Room.INTENT_ROOMID);
		if (bbll == null || bbll.isEmpty()) {
			bbll = new PersistentBoggleBLL(mode, roomID);
			try {
				LoacDictionary ld = new LoacDictionary();
				ld.execute();
			} catch (Exception ex) {
				String errorMessage = "Failed to load dictionary!";
				System.out.println(errorMessage);
			}
		}
		int status = this.getIntent().getIntExtra(
				PersistentBoggleGame.GAMESTATUS, 2);
		if (status == 1) {
			restoreData();
			this.showCorrectWord();
		}
		if (status == 2) {
			startNewGame();
		}
		this.getIntent().putExtra(PersistentBoggleGame.GAMESTATUS, 1);
		Music.play(this, R.raw.bogglegaming);
	}

	/**
	 * Save the following data once the activity paused:
	 * stack_existed_words,letters[], score, recLen. They represent the current
	 * words that player has chosen and verified by dictionary, the current
	 * letters on screen, the current score that player has got and the
	 * remaining time the player has.
	 */
	private void saveData() {
		StringBuilder existed_word = new StringBuilder();
		Iterator<String> iterator = existed_words.iterator();
		while (iterator.hasNext())
			existed_word.append(iterator.next() + "#");

		// save userName
		getPreferences(MODE_PRIVATE).edit()
				.putString(Room.INTENT_USERNAME, this.userName).commit();
		// save character
		getPreferences(MODE_PRIVATE).edit()
				.putInt(PersistentBoggleGame.CHARACTER, character).commit();
		// save game mode
		getPreferences(MODE_PRIVATE).edit()
				.putInt(PersistentBoggleGame.GAMEMODE, mode).commit();
		// save roomID
		getPreferences(MODE_PRIVATE).edit().putString("ROOMID", roomID)
				.commit();

		// save existed word
		getPreferences(MODE_PRIVATE)
				.edit()
				.putString(PersistentBoggleGame.EXISTEDWORD,
						existed_word.toString()).commit();
		// save letters
		getPreferences(MODE_PRIVATE)
				.edit()
				.putString(PersistentBoggleGame.LETTERS,
						String.valueOf(this.letters)).commit();
		// save current store
		getPreferences(MODE_PRIVATE).edit()
				.putInt(PersistentBoggleGame.SCORE, this.score.getScore())
				.commit();
	}

	/**
	 * To reload the data from preference.
	 */
	private void restoreData() {
		System.out.println("Reload data");

		// reload userName
		this.userName = getPreferences(MODE_PRIVATE).getString(
				Room.INTENT_USERNAME, "DEF");
		// reload character
		character = getPreferences(MODE_PRIVATE).getInt(CHARACTER, ROOMMEMBER);
		// reload game mode
		mode = getPreferences(MODE_PRIVATE).getInt(GAMEMODE, 4);
		// reload roomID
		roomID = getPreferences(MODE_PRIVATE).getString("ROOMID", "kevin");

		// reload existed words
		String existed_word = getPreferences(MODE_PRIVATE).getString(
				PersistentBoggleGame.EXISTEDWORD, "");
		String word = "";
		existed_words = new LinkedHashSet<String>();
		for (int i = 0; i < existed_word.length(); i++) {
			if (existed_word.charAt(i) == '#') {
				existed_words.add(word);
				word = "";
			} else
				word += existed_word.charAt(i);
		}
		// reload score
		int s = getPreferences(MODE_PRIVATE).getInt(PersistentBoggleGame.SCORE,
				0);
		this.score = new Score(s);
		// reload letters
		letters = new char[mode * mode];
		String l = getPreferences(MODE_PRIVATE).getString(
				PersistentBoggleGame.LETTERS,
				String.valueOf(bbll.generateWords(mode)));
		for (int i = 0; i < l.length(); i++) {
			letters[i] = l.charAt(i);
		}
	}

	/**
	 * Open a new game.
	 */
	private void startNewGame() {
		try {
			this.userName = this.getIntent().getStringExtra(
					Room.INTENT_USERNAME);
			this.character = this.getIntent().getIntExtra(
					PersistentBoggleGame.CHARACTER, ROOMMEMBER);
			this.letters = this.getIntent()
					.getStringExtra(PersistentBoggleGame.LETTERS).toCharArray();
			if (letters.length != mode * mode)
				this.letters = bbll.generateWords(mode);
			this.existed_words = new LinkedHashSet<String>();
			this.score = new Score(0);
			this.recLen = getSharedPreferences("GAME_TIME", MODE_PRIVATE)
					.getInt("TIME", GAMETIME);
			// need to refresh it
			getSharedPreferences("GAME_TIME", MODE_PRIVATE).edit()
					.putInt("TIME", GAMETIME).commit();
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggleGame Start a New Game failed";
			System.out.println(errorMessage);
		}
	}

	/**
	 * Obtain screen size information: screen width and screen height.
	 */
	private void initializeScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screen_width = dm.widthPixels;
		this.screen_height = dm.heightPixels;
		mode = this.getIntent().getIntExtra(GAMEMODE, 4);
	}

	/**
	 * initialize layout and content
	 * 
	 */
	private void initializeLayout() {

		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View ly = inflater.inflate(R.layout.activity_boggle_game, null);
		rly = (RelativeLayout) ly;
		rly.setBackgroundResource(R.drawable.bogglebackground);

		// initialize views
		initializeWordGridViewPosition(rly, mode);
		initializeTextViewTimerPosition(rly, mode);
		initializeTextExsitedWordPosition(rly, mode);
		initializeTextViewWordingPosition(rly, mode);
		initializeButtonExitPosition(rly, mode);
		setContentView(rly);
	}

	/**
	 * Initialize the position of WordGridView object: wordGridView
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeWordGridViewPosition(RelativeLayout rly, int gridMode) {
		wordGridView = new PersistentWordGridView(this, screen_width, mode);
		wordGridView.setId(999);
		RelativeLayout.LayoutParams wgv_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		wgv_rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		wgv_rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		wgv_rllp.rightMargin = screen_width / 32;
		wgv_rllp.bottomMargin = screen_width / 32;
		rly.addView(wordGridView, wgv_rllp);
	}

	/**
	 * Initialize the position of TextView object: txt_Timer
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeTextViewTimerPosition(RelativeLayout rly,
			int gridMode) {
		layout_timer = new RelativeLayout(this);
		layout_timer.setId(993);
		RelativeLayout.LayoutParams ltlp = new RelativeLayout.LayoutParams(
				screen_width / 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
		ltlp.addRule(RelativeLayout.LEFT_OF, wordGridView.getId());
		txt_Timer = new TextView(this);
		txt_Timer.setId(100);
		txt_Timer.setTextColor(getResources().getColor(R.color.boggle_timer));
		txt_Timer.setTextSize(screen_width / 32);
		RelativeLayout.LayoutParams txt_timer_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		txt_timer_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layout_timer.addView(txt_Timer, txt_timer_rllp);
		rly.addView(layout_timer, ltlp);
	}

	/**
	 * Initialize the position of TextView object: txt_existed_words
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeTextExsitedWordPosition(RelativeLayout rly,
			int gridMode) {
		contentsOfTextView = new String[3];
		sv = new ScrollView[3];
		txt_ExistedWords = new TextView[3];
		rl_existed_words = new RelativeLayout(this);
		RelativeLayout.LayoutParams rlewlp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlewlp.addRule(RelativeLayout.BELOW, layout_timer.getId());

		for (int i = 0; i < 3; i++) {
			contentsOfTextView[i] = "Existed Word\n";
			txt_ExistedWords[i] = new TextView(this);
			txt_ExistedWords[i].setWidth(screen_width * 2 / 5);
			txt_ExistedWords[i].setText(contentsOfTextView[i]);
			txt_ExistedWords[i].setSingleLine(false);
			txt_ExistedWords[i].setTextColor(Color.WHITE);
		}
		RelativeLayout.LayoutParams sv_exwds_rllp;
		for (int i = 0; i < 3; i++) {

			sv[i] = new ScrollView(this);
			sv[i].setId(i + 105);
			sv[i].setScrollContainer(true);
			sv[i].setFocusable(true);
			sv[i].addView(txt_ExistedWords[i]);
		}
		sv_exwds_rllp = new RelativeLayout.LayoutParams(screen_width / 6,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		sv_exwds_rllp.addRule(RelativeLayout.RIGHT_OF);
		rl_existed_words.addView(sv[0], sv_exwds_rllp);
		sv_exwds_rllp = new RelativeLayout.LayoutParams(screen_width / 6,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		sv_exwds_rllp.addRule(RelativeLayout.RIGHT_OF, sv[0].getId());
		rl_existed_words.addView(sv[1], sv_exwds_rllp);
		sv_exwds_rllp = new RelativeLayout.LayoutParams(screen_width / 6,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		sv_exwds_rllp.addRule(RelativeLayout.RIGHT_OF, sv[1].getId());
		rl_existed_words.addView(sv[2], sv_exwds_rllp);
		rly.addView(rl_existed_words, rlewlp);
	}

	/**
	 * Initialize the position of TextView object: txt_wording
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeTextViewWordingPosition(RelativeLayout rly,
			int gridMode) {
		txt_wording = new TextView(this);
		txt_wording.setId(16);
		txt_wording.setText(":");
		txt_wording.setTextColor(Color.RED);
		txt_wording.setTextSize(screen_width / 30);
		txt_wording.setSingleLine(true);
		RelativeLayout.LayoutParams txt_wording_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		txt_wording_rllp.addRule(RelativeLayout.RIGHT_OF, this.sv[0].getId());
		txt_wording_rllp.addRule(RelativeLayout.BELOW,
				this.wordGridView.getId());
		txt_wording_rllp.topMargin = screen_width / 7;
		rly.addView(txt_wording, txt_wording_rllp);
	}

	/**
	 * Initialize the position of Button object: btn_Exit
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeButtonExitPosition(RelativeLayout rly, int gridMode) {
		btn_Exit = new Button(this);
		btn_Exit.setText("Exit");
		btn_Exit.setTextSize(screen_width / 50);
		btn_Exit.setTextColor(Color.WHITE);
		btn_Exit.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_exit_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// btn_exit_rllp.setMargins(screen_width * 11 / 20, screen_width
		// + (screen_height - screen_width) * 2 / 4, 0, 0);
		btn_exit_rllp.addRule(RelativeLayout.LEFT_OF, txt_Timer.getId());
		rly.addView(btn_Exit, btn_exit_rllp);
		btn_Exit.setOnClickListener(this);
	}

	/**
	 * Count down implementation
	 */
	private void timer() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					int second = recLen % 60;
					String second_s = String.valueOf(second);
					if (second < 10)
						second_s = String.valueOf("0" + second);
					txt_Timer.setText(String.valueOf(recLen / 60) + " : "
							+ second_s);
					if (recLen <= 30) {
						// game is meeting end
						txt_Timer.setTextColor(Color.RED);
					}
					if (recLen == 0) {
						// game over
						// listener need cancel
						// need to update highest score
						gameOverHandler();
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		tick_task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				System.out.println("Tick working");
				if (isPause == 0 && recLen > 0)
					recLen--;
				if (recLen == 0) {
					// stop the game
				}
				handler.sendMessage(message);
			}
		};
		timer = new Timer(true);
		timer.schedule(tick_task, 1000, 1000);
	}

	private void initializeNetworkListener() {
		Log.d(TAG, "Initialize NetworkListerner");
		// 1. get other player's game status
		networkTasksHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NETWORK_TASKS_UPDATE_PLAYER_INFO:
					for (int i = 0; i < 2; i++) {
						txt_ExistedWords[i].setText(contentsOfTextView[i]);
					}
					break;
				case NETWORK_TASKS_GAME_OVER:

					wordGridView.updateRecord(gor);
					break;
				}
			}
		};
		InitializePlayersInfo ipi = new InitializePlayersInfo();
		try {
			ipi.execute();
		} catch (Exception ex) {
			System.out.println("Initialize player names failed");
		}
	}

	private void initializeNetWorkTimerTasks() {
		Log.d(TAG, "InitializeNetworkTask called");

		timertask_NetworkTasks_UpdatePlayers = new TimerTask() {
			@Override
			public void run() {
				try {
					UserGameStatus[] ugs = bbll.getPlayersStatus();
					gor = new GameOverResult();
					if (isPause == OVER) {
						switch (ugs.length) {
						case 0:
							gor = bbll.formResult(userName, score.getScore(),
									null, null);
							break;
						case 1:
							gor = bbll.formResult(userName, score.getScore(),
									ugs[0], null);
							break;
						case 2:
							gor = bbll.formResult(userName, score.getScore(),
									ugs[0], ugs[1]);
							break;
						}
						Message msg = new Message();
						msg.what = NETWORK_TASKS_GAME_OVER;
						networkTasksHandler.sendMessage(msg);
					} else {
						for (int i = 0; i < ugs.length; i++) {
							if (ugs[i] == null)
								continue;
							ArrayList<String> current_words = ugs[i]
									.getCurrentWords();
							Iterator<String> it = current_words.iterator();
							StringBuilder words_builder = new StringBuilder(
									ugs[i].getUserName() + "\n" + "Score: "
											+ ugs[i].getCurrentScore() + "\n"
											+ "Existed Words:\n");
							while (it.hasNext()) {
								words_builder.append(it.next().toString());
								words_builder.append("\n");
							}
							contentsOfTextView[i] = words_builder.toString();
							// txt_ExistedWords[i].setText(words_builder.toString());
						}
						Message msg = new Message();
						msg.what = NETWORK_TASKS_UPDATE_PLAYER_INFO;
						networkTasksHandler.sendMessage(msg);
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
					return;
				}
			}
		};

		networkTasksTimer = new Timer();
		networkTasksTimer.schedule(timertask_NetworkTasks_UpdatePlayers, 1000,
				3000);
	}

	/**
	 * To start a dialog to ask user whether he really want to exit the game
	 * 
	 * @return true if dialog pressed OK, or false if canceled;
	 */
	private void reallyWantToExited() {
		// start a dialog to ask whether user really want to exit the game
		AlertDialog.Builder exit_ad_builder = new AlertDialog.Builder(this);
		String title = "Exit?";
		String message = "Do you really want to exit game?";
		String positiveButtonString = "Yes";
		String negativeButtonString = "Back";
		exit_ad_builder.setTitle(title);
		exit_ad_builder.setMessage(message);
		exit_ad_builder.setPositiveButton(positiveButtonString,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						isBackExitPressed = true;
						exitGame();
					}
				});
		exit_ad_builder.setNegativeButton(negativeButtonString,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
					}
				});
		exit_ad_builder.show();
	}

	/**
	 * Exit the game after confirmation
	 */
	private void exitGame() {
		Log.d(TAG, "Exit Affirmed!");
		try {
			bbll.exitGame(character, this.userName);
			finish();
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggle: exitGame failed";
			System.out.println(errorMessage);
			System.out.println(ex.toString());
		}
	}

	/**
	 * Stop Timer and TimerTasks
	 */
	private void stopTimerTasks() {
		try {
			if (tick_task != null)
				tick_task.cancel();
			if (timer != null)
				timer.cancel();
			if (timertask_NetworkTasks_UpdatePlayers != null)
				timertask_NetworkTasks_UpdatePlayers.cancel();
			if (networkTasksTimer != null)
				networkTasksTimer.cancel();
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggle: stopTimerTasks Failed";
			System.out.println(errorMessage);
		}
	}

	/**
	 * Doing this methods when the game is over
	 */
	private void gameOverHandler() {
		try {
			isPause = OVER;
			UpdateTopScore uts = new UpdateTopScore();
			uts.execute();
			wordGridView.gameOver(isPause, score.getScore(), existed_words);
			txt_Timer.setText("");
			txt_wording.setText("");
			if (tick_task != null)
				tick_task.cancel();
			if (timer != null)
				timer.cancel();
			System.out.println("Will update top score");
		} catch (Exception ex) {
			String errorMessage = "PersistentBoggle: gameOverHandler Failed!";
			System.out.println(errorMessage);
			System.out.println(ex.toString());
		}
	}

	// ---------------------------- AsyncTasks Part ---------------------------

	/**
	 * LoadDictionary by using AsyncTask
	 * 
	 * @author kevin
	 * 
	 */
	private class LoacDictionary extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				bbll.loadDictionary(getResources().getAssets().open(
						"SimpleBloomFilterFile.txt"));
			} catch (Exception ex) {
				System.out.println("Load Dictionary Failed!");
				System.out.println(ex.toString());
				return null;
			}
			return null;
		}
	}

	/**
	 * Find all the players who enter the game, then initialize Network listener
	 * to detect other players movements
	 * 
	 * @author kevin
	 * 
	 */
	private class InitializePlayersInfo extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean check = false;
			check = bbll.getPlayersNames(userName);
			return check;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				initializeNetWorkTimerTasks();
			}
		}
	}

	private class AddNewWords extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				ArrayList<String> array = new ArrayList<String>();
				array.addAll(existed_words);
				bbll.updateWordsToServer(userName, array, score.getScore());
			} catch (Exception ex) {
				String errorMessage = "PersistentBoggle: Failed to add new words to remote server";
				System.out.println(errorMessage);
				System.out.println(ex.toString());
			}
			return null;
		}
	}

	private class UpdateTopScore extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				bbll.updateTopScore(userName, score.getScore());
				return null;
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
			return null;
		}
	}
}
