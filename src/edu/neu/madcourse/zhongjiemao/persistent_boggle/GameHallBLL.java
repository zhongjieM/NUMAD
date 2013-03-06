package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;

import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;

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

	private String userName = "";

	/**
	 * Default constructor, must have a user name.
	 * 
	 * @param uname
	 */
	public GameHallBLL(String uname) {

		// initialize the object of GsonHelper
		gsonHelper = new GsonHelper();
		this.userName = uname;
	}

	/**
	 * This method is to initialize the user Online Information when the user
	 * successfully login.
	 */
	public void loginInitialize() {
		// update the Online Table
		// clear all the dirty data of the current user.
		// 1. check the Room Status if there is any Room that was under his
		// name.
		// 2. check the Room Info table to see if there is any game starts under
		// his user name
		// 3. Add a new record to ONLINE table
		if (gsonHelper.isServerAvailable()) {
			gsonHelper.deleteRecordFromTable(this.userName,
					GsonHelper.ROOMSTATUS);
			gsonHelper.deleteRecordFromTable(this.userName,
					GsonHelper.ONLINEUSER);
			OnLineUser ou = new OnLineUser(this.userName, false,
					OnLineUser.DEFAULT_INIVITER);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, ou);
		}
		// TODO: how to make sure that all the information have been set
		// correctly at the server
	}

	/**
	 * This method is to finish all the tasks left before quitting the game
	 * hall. Delete its online record. Delete its room and game.
	 * 
	 * @return TRUE if quit finished tasks successfully, or FALSE if not
	 */
	public boolean quitGameHall() {
		Boolean check = false;
		// 1. Delete its record at ONLINE table
		// 2. check if there is a room at ROOMSTATUS table; if there is one,
		// delete it.
		// 3. check if there is a room at ROOMINFO table, if there is one,
		// delete it
		gsonHelper.deleteRecordFromTable(this.userName, GsonHelper.ONLINEUSER);
		gsonHelper.deleteTable(this.userName);
		check = gsonHelper.deleteRecordFromTable(this.userName,
				GsonHelper.ONLINEUSER);
		return check;
	}

	/**
	 * Check the current user's online record on the OnLine Table to see if
	 * there is a new invitation of himself
	 * 
	 * @return the inivter's user name if there is one or null if the user
	 *         doesn't have a record at ONLINEUSER table or the inviter is the
	 *         default value
	 */
	public String checkInvitation() {
		// Check the OnLine Table for his inviter fields
		// if there is a new value, get it and then set it to be default value.
		OnLineUser ou = (OnLineUser) gsonHelper.getRecordFromTable(
				this.userName, GsonHelper.ONLINEUSER);
		if (ou != null
				&& ou.getInviter().intern() != OnLineUser.DEFAULT_INIVITER) {
			return ou.getInviter();
		}
		return null;
	}

	/**
	 * This method is to get the Room Status information from remote server, and
	 * then convert them into an adapter for the ListView of GameHall Activity
	 * to use.
	 */
	public void getRoomStatusAsAdapter() {

		// get the RoomStatus Table back from server
		// convert them into adapter.
		ArrayList<RoomStatus> rsal = gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS);
		if (!rsal.isEmpty()) {
			// TODO: turn them into adapter in a form which can be accepted by
			// ListView.
		}
	}

	/**
	 * Create a new Room for the user himself
	 * 
	 * @return true if create successfully, or false if not.
	 */
	public Boolean startANewRoom() {
		Boolean check = false;
		RoomStatus rs = new RoomStatus(this.userName, this.userName);
		check = gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, rs);
		return check;
	}

	/**
	 * Given a roomID, try to set the player into the room.
	 * 
	 * @param roomID
	 * @return TRUE if set successfully, or FALSE if not.
	 */
	public Boolean setIntoRoom(String roomID) {
		Boolean check = false;
		// 1. check if the roomID exists first
		// 2. if the roomID exists, check if the room is full.
		// 3. if the roomID's room is not full, find a place to set into.
		// 4. if set successfully, set its online InGame field to be true;
		// if set successfully, be prepared to get start room activity
		RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(roomID,
				GsonHelper.ROOMSTATUS);
		if (rs != null && !rs.getIsGameStarts()) {
			if (rs.getPlayer2().intern() != "") {
				rs.setPlayer2(this.userName);
			} else if (rs.getPlayer3().intern() != "") {
				rs.setPlayer3(this.userName);
			} else {
				return false;
			}
			rs.setNumberOfPlayers(rs.getNumberOfPlayers() + 1);
			check = gsonHelper.updateTable(GsonHelper.ONLINEUSER,
					new OnLineUser(this.userName, true,
							OnLineUser.DEFAULT_INIVITER));
			if (check) {
				check = gsonHelper.updateTable(GsonHelper.ROOMSTATUS, rs);
			}
		}
		return check;
	}
}
