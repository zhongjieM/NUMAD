package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

/**
 * An object of this class is an entity of a room. It contains the room id,
 * number of players in the room right now, game status: game starts or not,
 * player1's userID, player2's userID and player3's userID.
 * 
 * An object of this class is created when player1, who is the first room master
 * of this room choose to create a new room for playing. When the current room
 * master quit the room, the player2 if there is one, will be new room master.
 * If player2 is empty, then check player3. If there is no player in the room,
 * this object of room will be cleared.
 * 
 * If the game in this room starts, the record of this room will be remained as
 * a ROOMSTATUS table in the remote server. However, a new table whose name is
 * the roomID will be generated. There is only one record in this table. The
 * table will be in the form of {"roomID", "object of this room in RoomStatus"}.
 * 
 * @author kevin
 * 
 */
public class RoomStatus {

	public final static String DEFAULT_PLAYER = "DEF";

	// roomID is a String, representing the room's id, which is correspondent to
	// the room ID in RoomInfo if the game of this room has begun
	private String roomID;
	// numberOfPlayers is an integer, representing the current number of players
	// in this room
	private int numberOfPlayers;
	// isGameStarts is a Boolean, representing the current room status.
	// if the game in this room begins, this value is true; otherwise, false;
	private Boolean isGameStarts;
	// player1 is a String, representing the master's user name of this room.
	private String player1;
	// player2 is a String, the second player's user name.
	private String player2;
	// player3 is a String, the third player's user name.
	private String player3;
	// playing is a String, representing the current playing player's user name
	private String playing;
	// currentString is a String, representing the current characters that the
	// current player has been chosen on his board.
	private String currentString;

	/**
	 * Default constructor
	 */
	public RoomStatus() {
	}

	/**
	 * Constructor with parameters to initialize all the members of this class.
	 * 
	 * @param rid
	 * @param nop
	 * @param gs
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param playing
	 * @param currentString
	 */
	public RoomStatus(String rid, int nop, Boolean gs, String p1, String p2,
			String p3, String playing, String currentString) {
		this.roomID = rid;
		this.numberOfPlayers = nop;
		this.isGameStarts = gs;
		this.player1 = p1;
		this.player2 = p2;
		this.player3 = p3;
		this.playing = playing;
		this.currentString = currentString;

	}

	/**
	 * Constructor with parameters to initialize part members of this class.
	 * This type of constructor can be used when the player generate a new room
	 * for the first time.
	 * 
	 * @param rid
	 * @param p1
	 */
	public RoomStatus(String rid, String p1) {
		this.roomID = rid;
		this.numberOfPlayers = 1;
		this.isGameStarts = false;
		this.player1 = p1;
		this.player2 = DEFAULT_PLAYER;
		this.player3 = DEFAULT_PLAYER;
	}

	// getters and setters of all the members of this class

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public Boolean getIsGameStarts() {
		return isGameStarts;
	}

	public void setIsGameStarts(Boolean isGameStarts) {
		this.isGameStarts = isGameStarts;
	}

	public String getPlayer1() {
		return player1;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	public String getPlayer3() {
		return player3;
	}

	public void setPlayer3(String player3) {
		this.player3 = player3;
	}

	public String getPlaying() {
		return playing;
	}

	public void setPlaying(String playing) {
		this.playing = playing;
	}

	public String getCurrentString() {
		return currentString;
	}

	public void setCurrentString(String currentString) {
		this.currentString = currentString;
	}

	@Override
	/**
	 * -> String
	 * Purpose Statement: return return the String form of this class, which is to
	 * show the values of all the members of this class in String form.
	 * Strategy: domain knowledge
	 */
	public String toString() {
		return "Room ID is:" + this.roomID + ", " + "Number Of Players: "
				+ this.numberOfPlayers + ", " + "Game Starts?: "
				+ this.isGameStarts + ", " + "Player 1:" + this.player1 + ", "
				+ "Player 2: " + this.player2 + ", " + "Player 3: "
				+ this.player3 + ", " + "Playing: " + this.playing + ", "
				+ "Current String: " + this.currentString + " Over";
	}

}
