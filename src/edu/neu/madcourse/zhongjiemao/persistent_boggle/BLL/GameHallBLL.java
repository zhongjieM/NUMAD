package edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL;

import java.util.ArrayList;

import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;

/**
 * This class is to deal with the business logical layer of the GameHall
 * Activity
 * 
 * Currently Safe
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
		gsonHelper.deleteRecordFromTable(this.userName, GsonHelper.ROOMSTATUS);
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
		String invitation = "";
		OnLineUser ou = (OnLineUser) gsonHelper.getRecordFromTable(
				this.userName, GsonHelper.ONLINEUSER);
		if (ou != null
				&& ou.getInviter().intern() != OnLineUser.DEFAULT_INIVITER) {
			invitation = ou.getInviter().intern();
			ou.setInviter(OnLineUser.DEFAULT_INIVITER);
			gsonHelper.updateTable(GsonHelper.ONLINEUSER, ou);
			return invitation;
		}
		return null;
	}

	/**
	 * This method is to get the Room Status information from remote server, and
	 * then convert them into an adapter for the ListView of GameHall Activity
	 * to use.
	 * 
	 * @return ArrayList<RoomStatus> if it is not empty
	 */
	public ArrayList<RoomStatus> getRoomStatus() {

		// get the RoomStatus Table back from server
		// convert them into adapter.
		ArrayList<RoomStatus> rsal = gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS);
		if (rsal != null) {
			return rsal;
		}
		return null;
	}

	/**
	 * Create a new Room for the user himself
	 * 
	 * @return true if create successfully, or false if not.
	 */
	public Boolean startANewRoom() {
		Boolean check = true;
		// firstly try to update InGame status to true;
		OnLineUser ou = new OnLineUser(this.userName, true,
				OnLineUser.DEFAULT_INIVITER);
		check = gsonHelper.updateTable(GsonHelper.ONLINEUSER, ou);
		// If update InGame status successfully, then add a new record to
		// ROOMSTATUS table.
		if (check) {
			RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(
					userName, GsonHelper.ROOMSTATUS);
			if (rs == null) {
				rs = new RoomStatus(this.userName, this.userName);
				check = gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS,
						rs);
			} else
				// Already have a room for this player
				return false;

		}
		// if all fail try to set status back
		if (!check) {
			ou = new OnLineUser(this.userName, false,
					OnLineUser.DEFAULT_INIVITER);
			gsonHelper.updateTable(GsonHelper.ONLINEUSER, ou);
		}
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS));
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
			if (rs.getPlayer2().intern() == RoomStatus.DEFAULT_PLAYER) {
				rs.setPlayer2(this.userName);
			} else if (rs.getPlayer3().intern() == RoomStatus.DEFAULT_PLAYER) {
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
		} else {
			// set back to online user table
			gsonHelper.updateTable(GsonHelper.ONLINEUSER, new OnLineUser(
					this.userName, false, OnLineUser.DEFAULT_INIVITER));
		}
		return check;
	}

	public RoomStatus getRoomInfo(String roomID) {
		return (RoomStatus) gsonHelper.getRecordFromTable(roomID,
				GsonHelper.ROOMSTATUS);
	}

	/**
	 * For test use
	 */
	public void ShowAllTables() {
		System.out.println("Game Hall Table TESTs");
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.USERINFO));
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ONLINEUSER));
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS));
		System.out.println("END OF Game Hall Table TESTs");
	}
}
