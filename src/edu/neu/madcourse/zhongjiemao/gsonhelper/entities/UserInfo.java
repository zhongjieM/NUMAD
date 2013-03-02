package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

/**
 * An object of this class is an entity of user's specific information. It
 * contains user's id, user's user name which is specified by user when the user
 * register to the game, user's phone id and user's top score.
 * 
 * An object of this class will be generated when a new user register to the
 * game. Remote server will store them.
 * 
 * Each user has a unique userID and userName for himself. One phoneID can map
 * to multiple users.
 * 
 * @author kevin
 * 
 */
public class UserInfo {

	// userID is a String, representing the user's id
	private String userID;
	// userName is a String, representing the user name
	private String userName;
	// phoneID is a String, representing the phone's id
	private String phoneID;
	// topScore is an Integer, storing the this user's best score.
	private int topScore;

	/**
	 * Default Constructor
	 */
	public UserInfo() {
	}

	/**
	 * Constructor with parameters to initialize all the members of this class
	 * 
	 * @param uid
	 * @param uname
	 * @param uphoneid
	 * @param topScore
	 */
	public UserInfo(String uid, String uname, String uphoneid, int topScore) {
		this.userID = uid;
		this.userName = uname;
		this.phoneID = uphoneid;
		this.topScore = topScore;
	}

	// getters and setter of all the members of this class

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneID() {
		return phoneID;
	}

	public void setPhoneID(String phoneID) {
		this.phoneID = phoneID;
	}

	public int getTopScore() {
		return topScore;
	}

	public void setTopScore(int topScore) {
		this.topScore = topScore;
	}

	@Override
	/**
	 * -> String
	 * Purpose Statement: return return the String form of this class, which is to
	 * show the values of all the members of this class in String form.
	 * Strategy: domain knowledge
	 */
	public String toString() {
		return "User ID: " + this.userID + ", " + "User Name: " + this.userName
				+ ", " + "Phone ID: " + this.phoneID + ", " + "Top Score: "
				+ this.topScore + " |OVER|";
	}

}
