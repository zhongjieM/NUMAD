package edu.neu.madcourse.zhongjiemao.boggle;

import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.BoggleBLL;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.Score;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.SoundEffect;
import edu.neu.madcourse.zhongjiemao.sudoku.Music;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Color;
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
public class BoggleGame extends Activity implements OnClickListener {

	private static final String TAG = "Boggle Game";
	// The following constants are for save the data of this activity when
	// the activity is paused.
	public static final String GAMESTATUS = "edu.neu.madcourse.zhongjiemao.boggle";
	private static final String EXISTEDWORD = "EXISTEDWORD";
	private static final String LETTERS = "LETTERS";
	private static final String SCORE = "SCORE";
	private static final String TIMER = "TIMER";
	private static final int PAUSE = 1;
	private static final int OVER = 2;
	private static final int ON = 0;

	// Constants: game period default is 180 seconds
	private static final int GAMETIME = 180;

	// store the screen size
	private int screen_width = 0;
	private int screen_height = 0;

	// The main view which is to show the random selected words on screen
	private WordGridView wordGridView;
	// TextView: to show the timer that will count down
	private TextView txt_Timer;
	// ScrollView: to contain TextView whose content is the existed words
	private ScrollView sv;
	// TextView: to show the existed words that player has chosen
	private TextView txt_existed_words;
	// TextView: to show the current score that the player gets
	private TextView txt_score;
	// TextView: to show the letters that player has chosen till now
	// its content will be empty once the player get up his finger
	private TextView txt_wording;
	// Button: once tapped, the wordGridView will only show Pause and will
	// no more response to any touch action and timer will stop.
	// Tap again, everything will be back.
	private Button btn_Pause;
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
	private Stack<String> stack_existed_words;
	// BoggleBLL: the handle the business layer part of boggle game;
	// it will load the dictionary and verify the words' correctness
	private static BoggleBLL bbll;
	// Score: store the current score player has got
	private Score score;

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
		// TODO Auto-generated method stub
		// responds btn_Pause Button
		if (v.equals(btn_Pause)) {
			switch (isPause) {
			case 1:
				isPause = ON;
				wordGridView.pause(BoggleGame.ON);
				btn_Pause.setText("Pause");
				break;
			case 2:
				this.isPause = ON;
				this.wordGridView.pause(BoggleGame.ON);
				this.stack_existed_words = new Stack<String>();
				this.score = new Score(0);
				this.recLen = GAMETIME;
				this.btn_Pause.setText("Pause");
				break;
			case 0:
				isPause = PAUSE;
				wordGridView.pause(BoggleGame.PAUSE);
				btn_Pause.setText("Resume");
				break;
			}
		}
		// responds btn_Exit Button
		if (v.equals(btn_Exit)) {
			Music.stop(this);
			finish();
		}
	}

	@Override
	// automatically called when the activity resume from pause
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.bogglegaming);
	}

	@Override
	// automatically called when the activity paused
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		Music.stop(this);
		// Save the current game
		// Save existed words
		saveData();
		System.out.println("On Pause Called!");
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
		String words = "";
		Object[] words_obj = stack_existed_words.toArray();
		for (int i = 0; i < words_obj.length; i++)
			words += words_obj[i].toString() + "\n";
		txt_existed_words.setText("Existed Words: \n" + words);
	}

	/**
	 * Show the current score that player has got.
	 * 
	 * @param score
	 *            the current score that player has got
	 */
	protected void showScore(int score) {
		txt_score.setText("Score: " + String.valueOf(score));
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
		if (bbll.contains(word) && (!stack_existed_words.contains(word))) {
			stack_existed_words.push(word);
			score.wordsPointsAdding(word);
			showScore(score.getScore());
			showCorrectWord();
			SoundEffect.playCorrectWord();
		} else
			SoundEffect.playError();
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
		if (bbll == null || bbll.isEmpty()) {
			try {
				bbll = new BoggleBLL(getResources().getAssets().open(
						"SimpleBloomFilterFile.txt"));
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		int status = this.getIntent().getIntExtra(BoggleGame.GAMESTATUS, 2);
		if (status == 1) {
			restoreData();
			this.showCorrectWord();
			this.showScore(this.score.getScore());
		}
		if (status == 2) {
			startNewGame();
		}
		this.getIntent().putExtra(BoggleGame.GAMESTATUS, 1);
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
		while (!this.stack_existed_words.isEmpty())
			existed_word.append(this.stack_existed_words.pop() + "#");
		getPreferences(MODE_PRIVATE).edit()
				.putString(BoggleGame.EXISTEDWORD, existed_word.toString())
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString(BoggleGame.LETTERS, String.valueOf(this.letters))
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(BoggleGame.SCORE, this.score.getScore()).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(BoggleGame.TIMER, this.recLen).commit();
	}

	/**
	 * To reload the data from preference.
	 */
	private void restoreData() {
		// reload existed words
		String existed_word = getPreferences(MODE_PRIVATE).getString(
				BoggleGame.EXISTEDWORD, "");
		String word = "";
		stack_existed_words = new Stack<String>();
		for (int i = 0; i < existed_word.length(); i++) {
			if (existed_word.charAt(i) == '#') {
				stack_existed_words.push(word);
				word = "";
			} else
				word += existed_word.charAt(i);
		}
		// reload score
		int s = getPreferences(MODE_PRIVATE).getInt(BoggleGame.SCORE, 0);
		this.score = new Score(s);
		// reload timer
		recLen = getPreferences(MODE_PRIVATE)
				.getInt(BoggleGame.TIMER, GAMETIME);
		// reload letters
		letters = new char[16];
		String l = getPreferences(MODE_PRIVATE).getString(BoggleGame.LETTERS,
				String.valueOf(bbll.generateWords(16)));
		for (int i = 0; i < l.length(); i++) {
			letters[i] = l.charAt(i);
		}
	}

	/**
	 * Open a new game. Initialize: letters[], stack_existed_words, score and
	 * recLen
	 */
	private void startNewGame() {
		this.letters = bbll.generateWords(16);
		this.stack_existed_words = new Stack<String>();
		this.score = new Score(0);
		this.recLen = GAMETIME;
	}

	/**
	 * Obtain screen size information: screen width and screen height.
	 */
	private void initializeScreenParameters() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screen_width = dm.widthPixels;
		this.screen_height = dm.heightPixels;
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
		initializeWordGridViewPosition(rly, 4);
		initializeTextViewTimerPosition(rly, 4);
		initializeTextViewScorePosition(rly, 4);
		initializeTextExsitedWordPosition(rly, 4);
		initializeTextViewWordingPosition(rly, 4);
		initializeButtonPausePosition(rly, 4);
		initializeButtonExitPosition(rly, 4);
		setContentView(rly);
	}

	/**
	 * Initialize the position of WordGridView object: wordGridView
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeWordGridViewPosition(RelativeLayout rly, int gridMode) {
		wordGridView = new WordGridView(this);
		wordGridView.setId(999);
		RelativeLayout.LayoutParams wgv_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		int leftMargin = (screen_width < screen_height ? screen_width
				: screen_height) / ((gridMode + 1) * 2);
		int topMargin = leftMargin;
		wgv_rllp.setMargins(leftMargin, topMargin, -leftMargin, -topMargin);
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
		txt_Timer = new TextView(this);
		txt_Timer.setId(100);
		txt_Timer.setTextColor(getResources().getColor(R.color.boggle_timer));
		txt_Timer.setTextSize(screen_width / 25);
		RelativeLayout.LayoutParams txt_timer_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		timer();
		txt_timer_rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		txt_timer_rllp.topMargin = 0;
		rly.addView(txt_Timer, txt_timer_rllp);
	}

	/**
	 * Initialize the position of TextView object: txt_score
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeTextViewScorePosition(RelativeLayout rly,
			int gridMode) {
		txt_score = new TextView(this);
		txt_score.setId(18);
		txt_score.setText("Score: 0");
		txt_score.setTextColor(Color.WHITE);
		txt_score.setTextSize(screen_width / 30);
		RelativeLayout.LayoutParams txt_score_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		txt_score_rllp.addRule(RelativeLayout.BELOW, wordGridView.getId());
		txt_score_rllp.topMargin = screen_width / 7;
		txt_score_rllp.leftMargin = screen_width / 10;
		rly.addView(txt_score, txt_score_rllp);
	}

	/**
	 * Initialize the position of TextView object: txt_existed_words
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeTextExsitedWordPosition(RelativeLayout rly,
			int gridMode) {
		sv = new ScrollView(this);
		sv.setId(15);
		sv.setScrollContainer(true);
		sv.setFocusable(true);
		txt_existed_words = new TextView(this);
		RelativeLayout.LayoutParams sv_exwds_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		sv_exwds_rllp.addRule(RelativeLayout.BELOW, txt_score.getId());
		sv_exwds_rllp.leftMargin = screen_width / 10;
		txt_existed_words.setWidth(screen_width * 2 / 5);
		txt_existed_words.setText("Existed Word\n");
		txt_existed_words.setSingleLine(false);
		txt_existed_words.setTextColor(Color.WHITE);
		sv.addView(txt_existed_words);
		rly.addView(sv, sv_exwds_rllp);
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
		txt_wording_rllp.addRule(RelativeLayout.RIGHT_OF, this.sv.getId());
		txt_wording_rllp.addRule(RelativeLayout.BELOW,
				this.wordGridView.getId());
		txt_wording_rllp.topMargin = screen_width / 7;
		rly.addView(txt_wording, txt_wording_rllp);
	}

	/**
	 * Initialize the position of Button object: btn_Pause
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeButtonPausePosition(RelativeLayout rly, int gridMode) {
		btn_Pause = new Button(this);
		btn_Pause.setId(17);
		btn_Pause.setWidth(screen_width * 3 / 10);
		btn_Pause.setText("Pause");
		btn_Pause.setTextColor(Color.WHITE);
		RelativeLayout.LayoutParams btn_pause_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		btn_pause_rllp.addRule(RelativeLayout.BELOW, txt_wording.getId());
		btn_pause_rllp.addRule(RelativeLayout.RIGHT_OF, sv.getId());
		btn_pause_rllp.topMargin = screen_width / 10;
		rly.addView(btn_Pause, btn_pause_rllp);
		btn_Pause.setOnClickListener(this);
	}

	/**
	 * Initialize the position of Button object: btn_Exit
	 * 
	 * @param rly
	 * @param gridMode
	 */
	private void initializeButtonExitPosition(RelativeLayout rly, int gridMode) {
		btn_Exit = new Button(this);
		btn_Exit.setWidth(screen_width * 3 / 10);
		btn_Exit.setText("Exit");
		btn_Exit.setTextColor(Color.WHITE);
		btn_Exit.setOnClickListener(this);
		RelativeLayout.LayoutParams btn_exit_rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// btn_exit_rllp.setMargins(screen_width * 11 / 20, screen_width
		// + (screen_height - screen_width) * 2 / 4, 0, 0);
		btn_exit_rllp.addRule(RelativeLayout.BELOW, btn_Pause.getId());
		btn_exit_rllp.addRule(RelativeLayout.RIGHT_OF, sv.getId());
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
						isPause = OVER;
						wordGridView.gameOver(isPause, score.getScore(),
								stack_existed_words);
						txt_Timer.setText("");
						txt_wording.setText("");
						btn_Pause.setText("Restart");
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				if (isPause == 0 && recLen > 0)
					recLen--;
				if (recLen == 0) {
					// stop the game
				}
				handler.sendMessage(message);
			}
		};
		timer = new Timer(true);
		timer.schedule(task, 1000, 1000);
	}
}
