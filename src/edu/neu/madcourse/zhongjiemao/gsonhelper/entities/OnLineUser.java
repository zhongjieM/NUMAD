package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

/**
 * An object of this class is an entity of a user who is online now. This class
 * contains the user's ID, user's status: already in a game or not, and the
 * inviter to this user.
 * 
 * When a user gets online, an object of this class will be generated as an
 * entity of this user. When a user log off, his related object of this class
 * will be cleared.
 * 
 * @author kevin
 * 
 */
public class OnLineUser {

	// userID is a String, representing the user's ID who is just getting online
	// this value is correspondent to the userID member of UserInfo class
	private String userID;
	// inGame is a Boolean, representing whether this online user is already in
	// a room or not. If inGame is TRUE, then other players can't send him
	// invitations any more.
	private Boolean inGame;
	// inviter is a String, representing the inviter player's userID which is
	// corresponding to the userID member of UserInfo class.
	// If there are many other players send this player invitation, only the
	// latest invitation will be shown to this user.
	private String inviter;

	/**
	 * Default Constructor
	 */
	public OnLineUser() {
	}

	/**
	 * Constructor with parameters to initialize all the members of this class.
	 * 
	 * @param uid
	 * @param ig
	 * @param intor
	 */
	public OnLineUser(String uid, Boolean ig, String intor) {
		this.userID = uid;
		this.inGame = ig;
		this.inviter = intor;
	}

	// getters and setters of all the members of this class

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Boolean getInGame() {
		return inGame;
	}

	public void setInGame(Boolean inGame) {
		this.inGame = inGame;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}

	@Override
	/**
	 * -> String
	 * Purpose Statement: return all the values of this online user in String formation.
	 * Strategy: Domain Knowledge
	 */
	public String toString() {
		return "Online Player: " + this.userID + ", " + "In Game?: "
				+ this.inGame + "Latest Inviter: " + this.inviter + " |OVER|";
	}
}
