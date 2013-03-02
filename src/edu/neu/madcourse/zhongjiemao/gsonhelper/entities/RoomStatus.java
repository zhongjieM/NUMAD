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
 * If the game in this room has begun, and all the players in this room has
 * quit, then the object of room of RoomInfo class and RoomStatus will be
 * cleared.
 * 
 * @author kevin
 * 
 */
public class RoomStatus {

	// roomID is a String, representing the room's id, which is correspondent to
	// the room ID in RoomInfo if the game of this room has begun
	private String roomID;
	// numberOfPlayers is an integer, representing the current number of players
	// in
	// this room
	private int numberOfPlayers;
	// gameStart is a boolean, representing the room status. If the game in this
	// room has begun, gameStart is TRUE. Otherwise, it is FALSE.
	private Boolean gameStart;
	// player1 is a String, representing the master's userID of this room.
	private String player1;
	// player2 is a String, the second player's userID.
	private String player2;
	// player3 is a String, the third player's userID.
	private String player3;

	/**
	 * Default constructor
	 */
	public RoomStatus() {
	}

	/**
	 * Constructor with parameters to initialize all the memebers of this class.
	 * 
	 * @param rid
	 * @param nop
	 * @param gs
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public RoomStatus(String rid, int nop, Boolean gs, String p1, String p2,
			String p3) {
		this.roomID = rid;
		this.numberOfPlayers = nop;
		this.gameStart = gs;
		this.player1 = p1;
		this.player2 = p2;
		this.player3 = p3;
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
		this.gameStart = false;
		this.player1 = p1;
	}

	// getters and setters of all the members of this class

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public Boolean getGameStart() {
		return gameStart;
	}

	public void setGameStart(Boolean gameStart) {
		this.gameStart = gameStart;
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

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
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
				+ this.numberOfPlayers + ", " + "Game Start?: "
				+ this.gameStart + ", " + "Player 1:" + this.player1 + ", "
				+ "Player 2: " + this.player2 + ", " + "Player 3: "
				+ this.player3 + " Over";
	}

}
