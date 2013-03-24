package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.SoundEffect;

/*
 * The approach of having one view for the entire puzzle and
 * and drawing lines and numbers inside that prove prove to
 * be the fastest and easiest way for this application.  
 */

/**
 * 
 * @author kevin
 * 
 */
public class PersistentWordGridView extends View {

	private final String TAG = "Boggle Word View";
	private final String VIEW_STATE = "View State";

	// the activity size information
	private int view_width = 0;
	private int view_height = 0;
	private int mode = 4;
	// to store the square length of each small grid
	private float squareSideLength;
	// to store the center position value of each grid
	private float[][] centerOfGrid_x;// = new float[4][4];
	private float[][] centerOfGrid_y;// = new float[4][4];
	// to store the reachable information of each grid to every other grids
	private boolean[][] reachable;// = new boolean[16][16];
	// to store the effective center radius in a grid.
	// if the touch position is not within this value area, it will not
	// be an effective touch like ACTION_DOWN or ACTION_MOVE.
	private float effectCenterRadius;

	// to store this view's parent BoggleGame context methods of BoggleGame
	// Class will be called through this context variable to show information
	// in its parent boggleGame.
	private final PersistentBoggleGame boggleGame;

	// to record the previous ACTION_DOWN or ACTION_MOVE position
	private static int previous_position = -1;
	// to store all the selected letters in each complete ACTION_DOWN or
	// ACTION_MOVE in one round.
	private boolean[] existed_letters;
	// to form all the letters that have been chosen in one round action.
	private String possible_word = "";
	// to store the index of the letters that have been chosen in one round
	// action in sequence.
	private Stack<Integer> stack_existed_letters;

	// Integer gamePaused:
	// 0: default value, game is going on;
	// 1: game pauses;
	// 2: game is over.
	private int gamePaused = 0;

	// to store the finalized score when the game is over.
	private int score;
	// to store the longest word's length when the game is over.
	private int longestLength;
	// to store the longest word then the game is over.
	private String longestword;

	// -------------------------------------------------------------------------
	// -------------------------- Constructor ----------------------------------
	// -------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public PersistentWordGridView(Context context) {
		super(context);
		this.boggleGame = (PersistentBoggleGame) context;
		// initialize a new view
		initializeView();
	}

	public PersistentWordGridView(Context context, int screenWidth, int mode) {
		super(context);
		view_width = screenWidth * 7 / 16;
		view_height = screenWidth * 7 / 16;
		this.mode = mode;
		this.centerOfGrid_x = new float[mode][mode];
		this.centerOfGrid_y = new float[mode][mode];
		this.reachable = new boolean[mode * mode][mode * mode];
		this.boggleGame = (PersistentBoggleGame) context;
		initializeView();
	}

	// -------------------------------------------------------------------------
	// ------------------------Override Methods Part----------------------------
	// -------------------------------------------------------------------------

	@Override
	protected void onMeasure(int width, int height) {
		Log.d(TAG, "onMeasure:" + this.getWidth() + " : " + this.getHeight());
		setMeasuredDimension(view_width, view_height);
		initializeCenterOfEachGrid(mode);
		this.squareSideLength = this.getWidth() / mode;
		this.effectCenterRadius = this.squareSideLength * 3 / mode;
	}

	@Override
	/**
	 * This method is called after the view is created and Android
	 * knows how big everything is.
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "onSizeChanged: squareSideLength " + squareSideLength);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "Drawing Boggle Word View");
		// Draw the background
		drawBackground(canvas);

		switch (this.gamePaused) {
		case 1:
			// game pauses
			drawLetters(canvas, this.gamePaused);
			break;
		case 2:
			// game stops
			drawLetters(canvas, this.gamePaused);
			break;
		default:
			// game is going on
			// Draw the grid lines
			drawGridLines(canvas);
			// Draw the letters;
			drawLetters(canvas, this.gamePaused);
			// Draw the selection...
			drawSelection(canvas);
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int letter_index = -1;
		// if the current state is paused, then do nothing in onTouchEvent
		if (this.gamePaused != 0)
			return true;
		// Else do actions according to the event type
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			letter_index = getCharacter(event.getX(), event.getY());
			if (letter_index != -1) {
				addLetterToWord(letter_index);
				previous_position = letter_index;
				showText();
				SoundEffect.playAccept();
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			letter_index = getCharacter(event.getX(), event.getY());
			if (letter_index != -1 && letter_index != previous_position
					&& reachable(letter_index)) {
				addLetterToWord(letter_index);
				previous_position = letter_index;
				showText();
				SoundEffect.playAccept();
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			stack_existed_letters.clear();
			for (int i = 0; i < mode * mode; i++)
				existed_letters[i] = false;
			boggleGame.addPossibleWord(this.possible_word);
			showText();
		}
		invalidate();
		return true;
	}

	// -------------------------------------------------------------------------
	// ------------------------Public Methods Part------------------------------
	// -------------------------------------------------------------------------

	/**
	 * Pause the game. This method will be called in BoggleGame Class
	 * 
	 * @param pause
	 *            to change the gamePause State
	 */
	public void pause(int pause) {
		this.gamePaused = pause;
		this.invalidate();
	}

	public void gameOver(int over, int score,
			LinkedHashSet<String> existed_words) {
		this.gamePaused = over;
		this.score = score;
		Iterator<String> iterator = existed_words.iterator();
		this.longestword = "";
		this.longestLength = 0;
		for (int i = 0; i < mode * mode; i++) {
			this.existed_letters[i] = false;
		}
		stack_existed_letters.clear();
		while (iterator.hasNext()) {
			String temp = iterator.next().toString();
			if (this.longestLength < temp.length())
				this.longestLength = temp.length();
			this.longestword = temp;
		}
		this.invalidate();
	}

	// -------------------------------------------------------------------------
	// ------------------------Private Methods Part-----------------------------
	// -------------------------------------------------------------------------

	/**
	 * Initialize the view and private global variables.
	 */
	private void initializeView() {

		int total = mode * mode;
		setFocusable(true);
		setFocusableInTouchMode(true);
		existed_letters = new boolean[total];
		stack_existed_letters = new Stack<Integer>();
		// Initialize the sound effect.
		SoundEffect.init(boggleGame);
		int y_p = -1;
		int x_p = -1;
		int y_c = -1;
		int x_c = -1;
		// Initialize the existed_letters[] and reachable[][]
		for (int i = 0; i < total; i++) {
			existed_letters[i] = false;
			y_p = i / mode;
			x_p = i % mode;
			for (int j = 0; j < total; j++) {
				y_c = j / mode;
				x_c = j % mode;
				if (y_p == y_c && x_p == x_c)
					reachable[i][j] = false;
				else {
					if (y_p - 1 == y_c || y_p == y_c || y_p + 1 == y_c) {
						if (x_p - 1 == x_c || x_p == x_c || x_p + 1 == x_c)
							reachable[i][j] = true;
						else
							reachable[i][j] = false;
					} else {
						reachable[i][j] = false;
					}
				}
			}
		}
	}

	// Draw background
	private void drawBackground(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(this.getResources().getColor(
				R.color.boggle_background));
		canvas.drawRect(0, 0, view_width, view_height, background);

	}

	// Draw grid lines, this method will not be called when the game is pause or
	// over
	private void drawGridLines(Canvas canvas) {
		// Draw the board
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(Color.WHITE);
		Paint hilite = new Paint();
		hilite.setColor(Color.WHITE);
		Paint light = new Paint();
		light.setColor(Color.WHITE);
		light.setStrokeWidth(3);
		for (int i = 0; i < mode + 1; i++) {
			canvas.drawLine(0, i * squareSideLength, this.getWidth(), i
					* squareSideLength, light);
			canvas.drawLine(i * squareSideLength, 0, i * squareSideLength,
					this.getHeight(), light);
		}
	}

	/**
	 * Draw the content of this view 1. when the game is going on, just draw the
	 * letters 2. when the game is pause, just draw text "Pause"; 3. when the
	 * game is over, draw text "Game Over", "Score", "longest length",
	 * "longest word" and comments. This is according to gameStatus
	 * 
	 * @param canvas
	 * @param gameStatus
	 */
	private void drawLetters(Canvas canvas, int gameStatus) {
		// Draw the letters ....
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(squareSideLength * 0.75f);
		foreground.setTextScaleX(1);
		foreground.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = foreground.getFontMetrics();
		int[] color = new int[6];
		color[0] = R.color.boggle_word_color1;
		color[1] = R.color.boggle_word_color2;
		color[2] = R.color.boggle_word_color3;
		color[3] = R.color.boggle_word_color4;
		color[4] = R.color.boggle_word_color5;
		color[5] = R.color.boggle_word_color6;
		float x = 0;
		float y = 0;
		// Draw the letters
		switch (gameStatus) {
		case 2:
			// game is over, draw score
			drawGameOverLetter(canvas, foreground, x, y);
			break;
		default:
			// game is going on, draw letters
			x = squareSideLength / 2;
			y = squareSideLength / 2 - (fm.ascent + fm.descent) / 2;
			for (int i = 0; i < mode; i++) {
				foreground.setColor(getResources().getColor(color[i]));
				for (int j = 0; j < mode; j++) {
					canvas.drawText(
							String.valueOf(boggleGame.letters[i * mode + j]), j
									* squareSideLength + x, i
									* squareSideLength + y, foreground);
				}
			}
			break;
		}
	}

	/**
	 * This is a helper methods, which will be called when the game is over. Its
	 * task is to draw the score and result of the passed game. It is only
	 * called in drawLetter method
	 * 
	 * @param canvas
	 * @param foreground
	 * @param x
	 * @param y
	 */
	private void drawGameOverLetter(Canvas canvas, Paint foreground, float x,
			float y) {

		foreground.setTextSize(squareSideLength / 2);
		foreground.setColor(Color.RED);
		// Draw Game Over Text
		drawGameOverText(canvas, foreground, x, y);

		x = this.getWidth() / 8;
		foreground.setTextAlign(Paint.Align.LEFT);
		foreground.setTextSize(squareSideLength / 4);
		foreground.setColor(Color.GREEN);
		// Draw Score Text
		drawScoreText(canvas, foreground, x, y);
		// Draw Longest Length Text
		drawLongestLengthText(canvas, foreground, x, y);
		// Draw Longest Word Text
		drawLongestwordText(canvas, foreground, x, y);

		foreground.setTextSize(squareSideLength / 3);
		foreground.setTextAlign(Paint.Align.CENTER);
		// Draw Comments
		drawCommentsText(canvas, foreground, x, y);

	}

	private void drawSelection(Canvas canvas) {
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.boggle_selected));
		Object[] v = stack_existed_letters.toArray();
		int index = -1;
		for (int i = 0; i < v.length; i++) {
			if (i == v.length - 1)
				selected.setColor(getResources().getColor(
						R.color.boggle_current_selected));
			index = Integer.parseInt(String.valueOf(v[i]));
			canvas.drawCircle(centerOfGrid_x[index / mode][index % mode],
					centerOfGrid_y[index / mode][index % mode],
					this.squareSideLength / 3, selected);
		}
	}

	private void drawGameOverText(Canvas canvas, Paint foreground, float x,
			float y) {
		x = this.getWidth() / 2;
		y = this.getHeight() / 4;
		String result = "GAME OVER";
		canvas.drawText(result, x, y, foreground);
	}

	private void drawScoreText(Canvas canvas, Paint foreground, float x, float y) {
		y = this.getHeight() / 4 + this.getHeight() * 3 / 16;
		String result = "Score : " + this.score;
		canvas.drawText(result, x, y, foreground);
	}

	private void drawLongestLengthText(Canvas canvas, Paint foreground,
			float x, float y) {
		y = this.getHeight() / 4 + this.getHeight() * 5 / 16;
		String result = "Longest Length : " + this.longestLength;
		canvas.drawText(result, x, y, foreground);
	}

	private void drawLongestwordText(Canvas canvas, Paint foreground, float x,
			float y) {
		y = this.getHeight() / 4 + this.getHeight() * 7 / 16;
		String result = "Longest Word : " + this.longestword;
		canvas.drawText(result, x, y, foreground);
	}

	private void drawCommentsText(Canvas canvas, Paint foreground, float x,
			float y) {
		x = this.getWidth() / 2;
		y = this.getHeight() / 4 + this.getHeight() * 10 / 16;
		String result = "";
		if (this.score > 10) {
			result = "GOOD JOB!";
		} else if (this.score < 3) {
			result = "You Can Do Better!";
			foreground.setColor(Color.RED);
		} else {
			result = "Mediam";
			foreground.setColor(Color.YELLOW);
		}
		canvas.drawText(result, x, y, foreground);
	}

	/**
	 * Get the index of the character by calculating the its x and y position
	 * 
	 * @param x
	 * @param y
	 * @return the index of the character
	 */
	private int getCharacter(float x, float y) {
		System.out.println("x : y= " + x + " : " + y);
		int letter_index = -1;
		int index_x = (int) (x / squareSideLength);
		int index_y = (int) (y / squareSideLength);
		if (index_x <= mode - 1 && index_y <= mode - 1 && index_x >= 0
				&& index_y >= 0) {
			if (Math.abs(x - centerOfGrid_x[index_y][index_x])
					+ Math.abs(y - centerOfGrid_y[index_y][index_x]) < effectCenterRadius)
				letter_index = index_x + index_y * mode;
		}
		return letter_index;
	}

	/**
	 * Judge whether the current node can be reached from the previous node
	 * 
	 * @param current_index
	 * @return true if it can be reached from the previous node
	 */
	private boolean reachable(int current_index) {
		return reachable[previous_position][current_index];
	}

	/**
	 * add the letter to word. If the letter index already exist, drop the cycle
	 * and make that node to be false. (not visited)
	 * 
	 * @param letter_index
	 * @return
	 */
	private boolean addLetterToWord(int letter_index) {
		// a cycle?
		int index = -1;
		if (existed_letters[letter_index] == true) {
			// already existed a cycle formed.
			while (!stack_existed_letters.empty()) {
				index = stack_existed_letters.pop();
				if (index == letter_index) {
					// if find the matched index, the cycle back to the start
					// point; so push this nodes back
					stack_existed_letters.push(index);
					break;
				}
				existed_letters[index] = false;
			}
			return false;
		} else {
			// not a cycle
			existed_letters[letter_index] = true;
			stack_existed_letters.push(letter_index);
			return true;
		}
	}

	/**
	 * Once the size of the screen is known, this method will calculate the
	 * center x and center y position of each grid of the 4*4 grids.
	 */
	private void initializeCenterOfEachGrid(int mode) {
		// TODO: problems here
		float current_x = this.squareSideLength / 2;
		float current_y = this.squareSideLength / 2;
		for (int i = 0; i < mode; i++) {
			for (int j = 0; j < mode; j++) {
				centerOfGrid_x[i][j] = current_x;
				centerOfGrid_y[i][j] = current_y;
				current_x += this.squareSideLength;
			}
			current_y += this.squareSideLength;
			current_x = this.squareSideLength / 2;
		}
	}

	private void showText() {
		Object[] v = stack_existed_letters.toArray();
		String txt = "";
		for (int i = 0; i < stack_existed_letters.size(); i++) {
			txt += boggleGame.letters[Integer.parseInt(String.valueOf(v[i]))];
		}
		this.possible_word = txt;
		boggleGame.showText(this.possible_word);
	}
}
