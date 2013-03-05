package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.GsonHelper;

/**
 * This class is to deal with the business logical layer of the GameHall
 * Activity
 * 
 * @author kevin
 * 
 */
public class GameHallBLL {

	// gsonHelper is an object of GsonHelper which is to deal with data process
	// with the remote server
	private GsonHelper gsonHelper;

	/**
	 * Default constructor
	 */
	public GameHallBLL() {

		// initialize the object of GsonHelper
		gsonHelper = new GsonHelper();
	}

	/**
	 * This method is to initialize the user Online Information when the user
	 * successfully login.
	 */
	public void loginInitialize() {
		// TODO:
		// update the Online Table
		// clear all the dirty data of the current user.
		// 1. check the Room Status if there is any Room that was under his
		// name.
		// 2. check the Room Info table to see if there is any game starts under
		// his user name
		// 3. Add a new record to ONLINE table
	}

	/**
	 * This method is to finish all the tasks left before quitting the game
	 * hall. Delete its online record. Delete its room and game.
	 * 
	 * @return TRUE if quit finished tasks successfully, or FALSE if not
	 */
	public boolean quitGameHall() {
		Boolean check = false;
		// TODO:
		// 1. Delete its record at ONLINE table
		// 2. check if there is a room at ROOMSTATUS table; if there is one,
		// delete it.
		// 3. check if there is a room at ROOMINFO table, if there is one,
		// delete it
		return check;
	}

	/**
	 * Check the current user's online record on the OnLine Table to see if
	 * there is a new invitation of himself
	 * 
	 * @return
	 */
	public String checkInvitation() {
		String inviter = "";
		// TODO:
		// Check the OnLine Table for his inviter fields
		// if there is a new value, get it and then set it to be default value.
		return inviter;
	}

	/**
	 * This method is to get the Room Status information from remote server, and
	 * then convert them into an adapter for the ListView of GameHall Activity
	 * to use.
	 */
	public void getRoomStatusAsAdapter() {

		// TODO:
		// get the RoomStatus Table back from server
		// convert them into adapter.
	}

	/**
	 * Given a roomID, try to set the player into the room.
	 * 
	 * @param roomID
	 * @return TRUE if set successfully, or FALSE if not.
	 */
	public Boolean setIntoRoom(String roomID) {
		Boolean check = false;
		// TODO:
		// 1. check if the roomID exists first
		// 2. if the roomID exists, check if the room is full.
		// 3. if the roomID's room is not full, find a place to set into.
		// 4. if set successfully, set its online InGame field to be true;
		// if set successfully, be prepared to get start room activity
		return check;
	}

}
