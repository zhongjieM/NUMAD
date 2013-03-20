package edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL;

import java.util.ArrayList;

import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;

/**
 * This class is to deal with the room BLL. All the public methods will be used
 * in Room Activity.
 * 
 * @author kevin
 * 
 */
public class RoomBLL {

	public final static int STATUS_NO_CHANGE = -1;
	public final static int STATUS_GAME_START = 1;
	public final static int STATUS_ROOM_NOT_EXIST = 0;
	public final static int STATUS_YOU_ARE_MASTER = 2;

	private RoomStatus roomStatus;
	private String roomID;
	private String userName;
	private GsonHelper gsonHelper;

	public RoomBLL(String roomID, String userName) {
		this.roomID = roomID;
		this.userName = userName;
		gsonHelper = new GsonHelper();
	}

	/**
	 * Send invitation to a target user by his userName
	 * 
	 * @param targetUserName
	 */
	public void invitation(String targetUserName) {
		OnLineUser ou = (OnLineUser) gsonHelper.getRecordFromTable(
				targetUserName, GsonHelper.ONLINEUSER);
		if (ou != null) {
			ou.setInviter(roomID);
			gsonHelper.updateTable(GsonHelper.ONLINEUSER, ou);
		}
	}

	/**
	 * Get all the users from ONLINETABLE
	 * 
	 * @return
	 */
	public ArrayList<OnLineUser> getAllAvailableUser() {
		return gsonHelper.getTableByTableNameFromServer(GsonHelper.ONLINEUSER);
	}

	/**
	 * Quit the room.
	 * 
	 * @return TRUE if quit successfully, or false if not.
	 */
	public Boolean quitRoom() {
		Boolean check = false;
		RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(roomID,
				GsonHelper.ROOMSTATUS);
		if (rs != null) {
			if (rs.getPlayer1().intern() == this.userName.intern()) {
				check = quitRoomAsMaster(rs);
			} else {
				check = quitRoomAsMember(rs);
			}
			if (check)
				check = updateONLINEUSERtoNotInGame();
		}
		return check;
	}

	/**
	 * This method must be used together with checkCurrentRoomStatus;
	 * 
	 * @return
	 */
	public RoomStatus getRoomStatus() {
		this.roomStatus = (RoomStatus) gsonHelper.getRecordFromTable(
				this.roomID, GsonHelper.ROOMSTATUS);
		return this.roomStatus;
	}

	public int checkCurrentRoomStatus() {
		// if room not exists, return without a word
		if (this.roomStatus == null)
			return STATUS_ROOM_NOT_EXIST;
		// if game starts, return immediately
		if (this.roomStatus.getIsGameStarts())
			return STATUS_GAME_START;
		// if you change to be the master of the room, return
		if (this.roomStatus.getPlayer1().intern() == this.userName)
			return STATUS_YOU_ARE_MASTER;
		return STATUS_NO_CHANGE;
	}

	/**
	 * Quit the room as room master
	 * 
	 * @param rs
	 * @return
	 */
	private Boolean quitRoomAsMaster(RoomStatus rs) {
		// room master, if there is a player 2, get him to your
		// position and if there is a player 3, get him to player 2
		System.out.println("Quit as Room Master");
		return gsonHelper.deleteRecordFromTable(this.roomID,
				GsonHelper.ROOMSTATUS);
	}

	/**
	 * Quit room as room member
	 * 
	 * @param rs
	 * @return
	 */
	private Boolean quitRoomAsMember(RoomStatus rs) {
		// you are not the room master
		if (rs.getPlayer2().intern() == this.userName.intern()) {
			rs.setPlayer2(RoomStatus.DEFAULT_PLAYER);
			rs.setNumberOfPlayers(rs.getNumberOfPlayers() - 1);
		} else if (rs.getPlayer3().intern() == this.userName.intern()) {
			rs.setPlayer3(RoomStatus.DEFAULT_PLAYER);
			rs.setNumberOfPlayers(rs.getNumberOfPlayers() - 1);
		} else {
			// TODO:
			// You are not actually in the room.
			// Quit the room anyway
			System.out.println("Do nothing here");
		}
		System.out.println("Get Player2:" + rs.getPlayer2());
		System.out.println("Get userName" + this.userName);
		return gsonHelper.updateTable(GsonHelper.ROOMSTATUS, rs);
	}

	/**
	 * Change user's condition to not in game.
	 * 
	 * @return
	 */
	public Boolean updateONLINEUSERtoNotInGame() {
		OnLineUser ou = (OnLineUser) gsonHelper.getRecordFromTable(userName,
				GsonHelper.ONLINEUSER);
		if (ou != null) {
			ou.setInGame(false);
			ou.setInviter(OnLineUser.DEFAULT_INIVITER);
			return gsonHelper.updateTable(GsonHelper.ONLINEUSER, ou);
		}
		return false;
	}
}
