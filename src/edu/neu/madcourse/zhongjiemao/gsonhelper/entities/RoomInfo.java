package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

/**
 * An object of this class is an entity of a room information. The game in this
 * room has started. The class contains the room's ID, which is correspondent to
 * the room ID in RoomStatus class, the user ID the player who is playing right
 * now and the a list of characters in the form of String that the current
 * player has been chosen on his board of his phone right now.
 * 
 * This class is only used when a game has begun.
 * 
 * @author kevin
 * 
 */
public class RoomInfo {

	// roomID is a String, representing the room's ID where a game has begun.
	private String roomID;
	// playing is a String, representing the player's ID who is on his turn to
	// playing.
	private String playing;
	// currentString is a String, representing the current list of characters
	// that the current player has been chosen on his board
	private String currentString;

	/**
	 * Default Constructor
	 */
	public RoomInfo() {
	}

	/**
	 * Constructor with parameters to initialize all the members of this class.
	 * 
	 * @param rid
	 * @param ping
	 * @param cs
	 */
	public RoomInfo(String rid, String ping, String cs) {
		this.roomID = rid;
		this.playing = ping;
		this.currentString = cs;
	}

	// getter and setter of all the members of this class

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
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
	 * Purpose Statement: return the String form of this class, which is to
	 * show the values of all the members of this class in String form.
	 * Strategy: domain knowledge
	 */
	public String toString() {
		return "Room ID: " + this.roomID + ", " + "Who is Playing: "
				+ this.playing + ", " + "Current String: " + this.currentString
				+ " |OVER|";
	}

}
