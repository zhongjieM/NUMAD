package edu.neu.madcourse.zhongjiemao.exerpacman.game;

import edu.neu.madcourse.zhongjiemao.R;

/**
 * This class contains the enumerations for the moves and the ghosts as well as
 * as the constants of the game. If you should change the constants, bear in
 * mind that this might significantly affect the game and hence the performance
 * of your controller.
 */
public final class Constants {
	/**
	 * Enumeration for the moves that are possible in the game. At each time
	 * step, a controller is required to supply one of the 5 actions available.
	 * If the controller replies NEUTRAL or does not reply in time, the previous
	 * action is repeated. If the previous action is not a legal move, a legal
	 * move is chosen uniformly at random.
	 */
	public enum MOVE {
		UP {
			@Override
			public MOVE opposite() {
				return MOVE.DOWN;
			}

			@Override
			public MOVE left() {
				return MOVE.LEFT;
			}

			@Override
			public MOVE right() {
				return MOVE.RIGHT;
			};
		},
		RIGHT {
			@Override
			public MOVE opposite() {
				return MOVE.LEFT;
			}

			@Override
			public MOVE left() {
				return MOVE.UP;
			}

			@Override
			public MOVE right() {
				return MOVE.DOWN;
			};
		},
		DOWN {
			@Override
			public MOVE opposite() {
				return MOVE.UP;
			}

			@Override
			public MOVE left() {
				return MOVE.RIGHT;
			}

			@Override
			public MOVE right() {
				return MOVE.LEFT;
			};
		},
		LEFT {
			@Override
			public MOVE opposite() {
				return MOVE.RIGHT;
			}

			@Override
			public MOVE left() {
				return MOVE.DOWN;
			}

			@Override
			public MOVE right() {
				return MOVE.UP;
			};
		},
		NEUTRAL {
			@Override
			public MOVE opposite() {
				return MOVE.NEUTRAL;
			}

			@Override
			public MOVE left() {
				return MOVE.NEUTRAL;
			}

			@Override
			public MOVE right() {
				return MOVE.NEUTRAL;
			};
		};

		public abstract MOVE opposite();

		public abstract MOVE left();

		public abstract MOVE right();
	};

	/**
	 * Enumeration for the ghosts. The integer arguments are the initial lair
	 * times.
	 */
	public enum GHOST {
		BLINKY(40), PINKY(60), INKY(80), SUE(100);

		public final int initialLairTime;

		GHOST(int lairTime) {
			this.initialLairTime = lairTime;
		}
	};

	/**
	 * DM stands for Distance Metric, a simple enumeration for use with methods
	 * that require a distance metric. The metric available are as follows:
	 * PATH: the actual path distance (i.e., number of step required to reach
	 * target) EUCLID: Euclidean distance using the nodes' x and y coordinates
	 * MANHATTAN: Manhattan distance (absolute distance between x and y
	 * coordinates)
	 */
	public enum DM {
		PATH, EUCLID, MANHATTAN
	};

	/**
	 * points for a normal pill
	 */
	public static final int PILL = 10;

	/**
	 * points for a power pill
	 */
	public static final int POWER_PILL = 50;

	/**
	 * score for the first ghost eaten (doubles every time for the duration of a
	 * single power pill)
	 */
	public static final int GHOST_EAT_SCORE = 200;

	/**
	 * initial time a ghost is edible for (decreases as level number increases)
	 */
	public static final int EDIBLE_TIME = 200;

	/**
	 * reduction factor by which edible time decreases as level number increases
	 */
	public static final float EDIBLE_TIME_REDUCTION = 0.9f;

	/**
	 * reduction factor by which lair times decrease as level number increases
	 */
	public static final float LAIR_REDUCTION = 0.9f;

	public static final int LEVEL_RESET_REDUCTION = 6;

	/**
	 * time spend in lair after being eaten
	 */
	public static final int COMMON_LAIR_TIME = 40;

	/**
	 * time limit for a level
	 */
	public static final int LEVEL_LIMIT = 4000;

	/**
	 * probability of a global ghost reversal event
	 */
	public static final float GHOST_REVERSAL = 0.0015f;

	/**
	 * maximum time a game can be played for, ms
	 */
	public static int MAX_TIME = 300000; // 60000;
	public static int MAX_TIME_SEC = 300; // 60;

	/**
	 * points awarded for every life left at the end of the game (when time runs
	 * out)
	 */
	public static final int AWARD_LIFE_LEFT = 800;

	/**
	 * extra life is awarded when this many points have been collected
	 */
	public static final int EXTRA_LIFE_SCORE = 10000;

	/**
	 * distance in the connected graph considered close enough for an eating
	 * event to take place
	 */
	public static final int EAT_DISTANCE = 3; // 2

	/**
	 * number of ghosts in the game
	 */
	public static final int NUM_GHOSTS = 4;

	/**
	 * number of different mazes in the game
	 */
	public static final int NUM_MAZES = 4;

	/**
	 * delay (in milliseconds) between game advancements
	 */
	// TODO global variable 20 - 80 best:45
	public static int DELAY = 45;

	// TODO global variable
	public static int[] NUM_PILLS = { 15, 25, 35, 45 };

	// TODO global variable
	public static float GHOST_AGRESSIVITY = 0.05f;

	/**
	 * total number of lives Ms Pac-Man has (current + NUM_LIVES-1 spares)
	 */
	public static final int NUM_LIVES = 50;

	/**
	 * difference in speed when ghosts are edible (every GHOST_SPEED_REDUCTION,
	 * a ghost remains stationary)
	 */
	public static final int GHOST_SPEED_REDUCTION = 2;

	/**
	 * for display only (ghosts turning blue)
	 */
	public static final int EDIBLE_ALERT = 30;

	/**
	 * for quicker execution: check every INTERVAL_WAIT ms to see if controllers
	 * have returned
	 */
	public static final int INTERVAL_WAIT = 1;

	// for Competition
	/**
	 * time limit in milliseconds for the controller to initialize
	 */
	public static final int WAIT_LIMIT = 5000;

	/**
	 * memory limit in MB for controllers (including the game)
	 */
	public static final int MEMORY_LIMIT = 512;

	/**
	 * limit in MB on the files written by controllers
	 */
	public static final int IO_LIMIT = 10;

	// for Maze
	public static final String[] nodeNames = { "map_a", "map_b", "map_c",
			"map_d" };
	public static final String[] distBinNames = { "da_binary_zip",
			"db_binary_zip", "dc_binary_zip", "dd_binary_zip" };

	// for GameView
	public static final int MAG = 2;

	public static final int[] mazeIDs = { R.drawable.maze_a, R.drawable.maze_b,
			R.drawable.maze_c, R.drawable.maze_d };

	public static final int[] unlockMazeIDs = { R.drawable.lv1_unlock,
			R.drawable.lv2_unlock, R.drawable.lv3_unlock, R.drawable.lv4_unlock };

	public static final int[] lockMazeIds = { R.drawable.lv2_lock,
			R.drawable.lv2_lock, R.drawable.lv3_lock, R.drawable.lv4_lock };

	public static final int[] pillIDs = { R.drawable.pill_lv_1,
			R.drawable.pill_lv_2, R.drawable.pill_lv_3, R.drawable.pill_lv_4 };

	public static final int swordID = R.drawable.power_sword;
	public static final int bgID = R.drawable.bg_texture;

	public static String INIT_GAME_STATE[] = {
			"false,0,0,0,0,0,978,LEFT,50,false,1292,0,40,NEUTRAL,1292,0,60,NEUTRAL,1292,0,80,NEUTRAL,1292,0,100,NEUTRAL,0000000000000000000100000000000000000000000001010000000000000000000000000000100000000000000000001000000000010000000000101000000000000000000000000000010010000100000000010000000000100000000000000001100000000000000000000000,1111,-1,false,false,false,false,false,false,false",
			"false,1,0,0,0,0,973,LEFT,50,false,1318,0,40,NEUTRAL,1318,0,60,NEUTRAL,1318,0,80,NEUTRAL,1318,0,100,NEUTRAL,000000000010000100000010000010100000000000000000000100000000010001100010000100000000010000000000000000000000000000000000000000010000000100100001100000010000000000000010000100000000000000001000000010000000000001100000010000000000000000000000,1111,-1,false,false,false,false,false,false,false",
			"false,2,0,0,0,0,1060,LEFT,50,false,1379,0,40,NEUTRAL,1379,0,60,NEUTRAL,1379,0,80,NEUTRAL,1379,0,100,NEUTRAL,0000000000110100001000100001000100000000000001000000000010001000110100000001000000100001000000000110010000000000000110000000010000100010000010000010000000000001000010000100000000000000000000000001000000000011010000001001000000000000000000,1111,-1,false,false,false,false,false,false,false",
			"false,3,0,0,0,0,989,LEFT,50,false,1308,0,40,NEUTRAL,1308,0,60,NEUTRAL,1308,0,80,NEUTRAL,1308,0,100,NEUTRAL,000001110001001000010000100000000000000000001100001000101000001000100000000100000001000000000000000000000000010001001101000000000001010000000000010101010000100000011000000001101001011010000100000100110000000000000010111000000000000000,1111,-1,false,false,false,false,false,false,false"
	};

	public static String INIT_LEVEL_LOCK = "[1]";

	public static final String KEY_MODE = "edu.neu.madcourse.yingquanyuan.exerpacman.key_mode";
	public static int MODE_VALUE = 0;
	public static final int MODE_SENSOR = 0;
	public static final int MODE_SWIPE = 1;
	
	public static boolean SQUAT_ENABLED = true;

	public static boolean USE_ROTATION_HINT = true;
	public static boolean IS_SWIPE_MODE = false;
	public static boolean PLAY_MUSIC = true;

	public static final String KEY_MAZE_ID = "edu.neu.madcourse.yingquanyuan.exerpacman.key_maze_id";

	public static final String KEY_PAUSE_TYPE = "edu.neu.madcourse.yingquanyuan.exerpacman.key_pause_type";
	public static final String PAUSE_GAME_PEND = "edu.neu.madcourse.yingquanyuan.exerpacman.pause_game_pend";
	public static final String PAUSE_GAME_OVER = "edu.neu.madcourse.yingquanyuan.exerpacman.pause_game_over";
	public static final int PAUSE_RESUME = 0;
	public static final int PAUSE_RESTART = 1;
	public static final int PAUSE_QUIT = 2;

	public static final String KEY_GAME_OVER = "edu.neu.madcourse.yingquanyuan.exerpacman.key_game_over";
	public static final String KEY_SCORE = "edu.neu.madcourse.yingquanyuan.exerpacman.key_score";

	public static final String LEVEL_LOCK_FILE = "level_lock.txt";
	public static final String SCORE_FILE = "score_list.txt";
	public static final String FIRST_TIME_CHECK_FILE = "first_time.txt";
	public static final int MSG_UPDATE_LEVEL_LOCK = 1;
	public static final int MSG_SHOW_JUNCTION_HINT = 2;
	public static final int MSG_UPDATE_SCORE = 3;
	public static final int SUB_MSG_EAT_FRUIT = -3;
	public static final int SUB_MSG_EAT_SWORD = -4;
	public static final int SUB_MSG_EAT_GHOST = -5;
	public static final int SUB_MSG_LOSE_LIVES = -6;
	// TODO add sub msg here
	public static final int MSG_UPDATE_LEVEL = 4;
	public static final int MSG_UPDATE_TIME = 5;
	public static final int MSG_UPDATE_LIVES = 6;
	public static final int MSG_SHOW_GAME_OVER = 7;
	public static final int SUB_MSG_GAME_OVER_DIE = -1;
	public static final int SUB_MSG_GAME_OVER_TIME_UP = -2;
	public static final int MSG_UPDATE_ORIENTATION = 8;

	public static final int TURN_LEFT = 0;
	public static final int TURN_RIGHT = 1;
	public static final int TURN_BOTH = 2;
	public static final int TURN_NONE = 3;

	public static final String BC_ACTION_QUIT = "edu.neu.madcourse.yingquanyuan.exerpacman.bc_action_quit_game";

	public static final float[] BT_SELECTED = new float[] { 2, 0, 0, 0, 2, 0,
			2, 0, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 1, 0 };

	public static final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
	
	public static final int SOUND_EAT_FRUIT = 1;
	public static final int SOUND_EAT_SWORD = 2;
	public static final int SOUND_EAT_GHOST = 3;
	public static final int SOUND_EAT_LOSE_LIVES = 4;

	private Constants() {
	}
}