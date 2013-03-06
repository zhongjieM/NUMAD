package edu.neu.madcourse.zhongjiemao.gsonhelper;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;
import edu.neu.mobileclass.apis.KeyValueAPI;

/**
 * This Class is to deal with all the gson operation with the remote server.
 * 
 * This class provides following public methods:
 * 
 * getTableByNameFromServer :
 * 
 * addNewRecordToTable :
 * 
 * isValueExistsInTable:
 * 
 * @author kevin
 * 
 */
public class GsonHelper {

	// usr and pwd to get access to remote server
	private final static String TEAMNAME = "ZJM";
	private final static String PASSWORD = "50019891";

	// four tables names
	public final static String USERINFO = "USERINFO";
	public final static String ONLINEUSER = "ONLINEUSER";
	public final static String ROOMSTATUS = "ROOMSTATUS";

	private Gson gson;

	public GsonHelper() {
		gson = new Gson();
	}

	/**
	 * Check whether the remote server is available or not
	 * 
	 * @return true if it is available or not if it is not available.
	 */
	public Boolean isServerAvailable() {
		return KeyValueAPI.isServerAvailable();
	}

	/**
	 * Get table at remote server by table name.
	 * 
	 * @param classType
	 * @return ArrayList<T> or NULL if get nothing
	 */
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getTableByTableNameFromServer(String tableName) {
		if (tableName == USERINFO) {
			return (ArrayList<T>) getUserInfoFromServer();
		} else if (tableName == ONLINEUSER) {
			return (ArrayList<T>) getOnLineUserFromServer();
		} else if (tableName == ROOMSTATUS) {
			return (ArrayList<T>) getRoomStatusFromServer();
		} else
			return null;
	}

	/**
	 * Given a room id, check if the game in this room has started
	 * 
	 * @param roomID
	 * @return if started, return an object of RoomStatus of this room. or null,
	 *         if the game has not yet started.
	 */
	public RoomStatus getGameByRoomID(String roomID) {
		if (KeyValueAPI.isServerAvailable()) {
			String jString = KeyValueAPI.get(TEAMNAME, PASSWORD, roomID)
					.intern();
			if (jString != "") {
				return gson.fromJson(jString, RoomStatus.class);
			}
		}
		return null;
	}

	/**
	 * Given the primary key of a table, check if it has a matched record in the
	 * given table
	 * 
	 * @param recordKey
	 * @param tableName
	 * @return
	 */
	public Object getRecordFromTable(String recordKey, String tableName) {
		if (tableName == USERINFO)
			return getRecordFromUSERINFO(recordKey);
		if (tableName == ONLINEUSER)
			return getRecordFromONLINEUSER(recordKey);
		if (tableName == ROOMSTATUS)
			return getRecordFromROOMSTATUS(recordKey);
		return false;
	}

	/**
	 * Add a new record to the specific table by its name
	 * 
	 * @param tableName
	 *            : a new record will be added here
	 * @param obj
	 *            : the new record will be added
	 * @return: true if add successfully or false if not
	 */
	public Boolean addNewRecordToTable(String tableName, Object obj) {
		if (tableName == USERINFO) {
			return addNewUserToUSERINFO((UserInfo) obj);
		} else if (tableName == ONLINEUSER) {
			return addNewUserToONLINEUSER((OnLineUser) obj);
		} else {
			// tableName == ROOMSTATUS
			return addNewRoomStatusToROOMSTATUS((RoomStatus) obj);
		}
	}

	/**
	 * Register a new Game to server according to roomID
	 * 
	 * @param roomID
	 * @param rs
	 * @return true if register successfully or false if not.
	 */
	public Boolean addNewGame(String roomID, RoomStatus rs) {
		if (KeyValueAPI.isServerAvailable()) {
			if (getGameByRoomID(roomID) == null) {
				String jString = gson.toJson(rs);
				KeyValueAPI.put(TEAMNAME, PASSWORD, roomID, jString);
				return true;
			}
		}
		return false;
	}

	/**
	 * Clear all the tables. This is just for test use.
	 */
	public void clearAllTables() {
		deleteTable(USERINFO);
		deleteTable(ONLINEUSER);
		deleteTable(ROOMSTATUS);
	}

	/**
	 * 
	 * Given a record's primary key and a table name. Delete the record from the
	 * assigned table.
	 * 
	 * @param recordKey
	 * @param tableName
	 * @return true if delete successfully, or false if not.
	 */
	public Boolean deleteRecordFromTable(String recordKey, String tableName) {
		Boolean check = false;
		if (tableName == ONLINEUSER) {
			return deleteRecordFromONLINEUSER(recordKey);
		} else if (tableName == ROOMSTATUS) {
			return deleteRecordFromROOMSTATUS(recordKey);
		} else {

		}
		return check;
	}

	/**
	 * Given a table Name, delete such table from the remote server
	 * 
	 * @param tableName
	 * @return
	 */
	public Boolean deleteTable(String tableName) {
		if (KeyValueAPI.isServerAvailable()) {
			if (KeyValueAPI.clearKey(TEAMNAME, PASSWORD, tableName).intern() == "true")
				return true;
		}
		return false;
	}

	// TODO: update the table or game

	/**
	 * Update the record of a table by given table name.
	 * 
	 * @param tableName
	 * @param obj
	 * @return
	 */
	public Boolean updateTable(String tableName, Object obj) {
		if (tableName == ONLINEUSER) {
			return updateONLINEUSER((OnLineUser) obj);
		} else if (tableName == ROOMSTATUS) {
			return updateROOMSTATUS((RoomStatus) obj);
		} else {
			return false;
		}
	}

	/**
	 * Update a game's status. This game has been started
	 * 
	 * @param roomID
	 * @param new_game_status
	 * @return
	 */
	public Boolean updateGame(String roomID, RoomStatus new_game_status) {
		if (KeyValueAPI.isServerAvailable()) {
			String jString = KeyValueAPI.get(TEAMNAME, PASSWORD, roomID);
			if (jString.intern() != "") {
				jString = gson.toJson(new_game_status);
				return Boolean.valueOf(KeyValueAPI.put(TEAMNAME, PASSWORD,
						roomID, jString));
			}
		}
		return false;
	}

	// ------------------------ Get table information from server -------------

	/**
	 * Get USERINFO table from the remote server
	 * 
	 * @return
	 */
	private ArrayList<UserInfo> getUserInfoFromServer() {
		return getTableByTableTypeAndName(UserInfo.class, USERINFO);
	}

	/**
	 * Get ONLINEUSER table from the remote server
	 * 
	 * @return
	 */
	private ArrayList<OnLineUser> getOnLineUserFromServer() {
		return getTableByTableTypeAndName(OnLineUser.class, ONLINEUSER);
	}

	/**
	 * Get ROOMSTATUS table from remote server
	 * 
	 * @return
	 */
	private ArrayList<RoomStatus> getRoomStatusFromServer() {
		return getTableByTableTypeAndName(RoomStatus.class, ROOMSTATUS);
	}

	/**
	 * Detailed operation of getting table from remote server by using Gson
	 * 
	 * @param classType
	 * @param tableName
	 * @return
	 */
	private <T> ArrayList<T> getTableByTableTypeAndName(Class<T> classType,
			String tableName) {
		ArrayList<T> tableArray = new ArrayList<T>();
		JsonArray jarray = getJsonTableFromServerByKey(tableName);
		if (jarray.size() != 0) {
			for (int i = 0; i < jarray.size(); i++)
				tableArray.add(gson.fromJson(jarray.get(i), classType));
		}
		return tableArray;
	}

	/**
	 * Get specific table from remote server by key value
	 * 
	 * @param key
	 * @return
	 */
	private JsonArray getJsonTableFromServerByKey(String key) {
		String jsonTable = "";
		JsonParser parser;
		if (KeyValueAPI.isServerAvailable()) {
			parser = new JsonParser();
			jsonTable = KeyValueAPI.get(TEAMNAME, PASSWORD, key);
			if (jsonTable.intern() != "") {
				// System.out.println(jsonTable);
				return parser.parse(jsonTable).getAsJsonArray();
			}
		}
		return new JsonArray();
	}

	// ------------ Search a Specific Table for Specific Record----------------

	/**
	 * Check if the given user name exists in the USERINFO table
	 * 
	 * @param userName
	 * @return
	 */
	private UserInfo getRecordFromUSERINFO(String userName) {
		ArrayList<UserInfo> uial = getUserInfoFromServer();
		Iterator<UserInfo> it = uial.iterator();
		UserInfo ui;
		while (it.hasNext()) {
			ui = it.next();
			System.out.println(ui.toString());
			System.out.println(userName + " : " + ui.getUserName());
			if (ui.getUserName().intern() == userName)
				return ui;
		}
		return null;
	}

	/**
	 * Check if the given user name exists in the ONLINEUSER table
	 * 
	 * @param userName
	 * @return
	 */
	private OnLineUser getRecordFromONLINEUSER(String userName) {
		ArrayList<OnLineUser> oual = getOnLineUserFromServer();
		Iterator<OnLineUser> it = oual.iterator();
		OnLineUser ou;
		while (it.hasNext()) {
			ou = it.next();
			if (ou.getUserName().intern() == userName)
				return ou;
		}
		return null;
	}

	/**
	 * Check if the given room id exists in the ROOMSTATUS table
	 * 
	 * @param roomID
	 * @return
	 */
	private RoomStatus getRecordFromROOMSTATUS(String roomID) {
		ArrayList<RoomStatus> rsal = getRoomStatusFromServer();
		Iterator<RoomStatus> it = rsal.iterator();
		RoomStatus rs;
		while (it.hasNext()) {
			rs = it.next();
			if (rs.getRoomID() == roomID)
				return rs;
		}
		return null;
	}

	// ---------------------- Add Record to Specific Table -----------------

	/**
	 * Add a new User to USERINFO table
	 * 
	 * @param ui
	 * @return
	 */
	private Boolean addNewUserToUSERINFO(UserInfo ui) {
		return addNewRecordToTable(UserInfo.class, USERINFO, ui);
	}

	/**
	 * Add a new User into ONLINEUSER table
	 * 
	 * @param ou
	 * @return
	 */
	private Boolean addNewUserToONLINEUSER(OnLineUser ou) {
		return addNewRecordToTable(OnLineUser.class, ONLINEUSER, ou);
	}

	/**
	 * Add a new Room to ROOMSTATUS table
	 * 
	 * @param rs
	 * @return
	 */
	private Boolean addNewRoomStatusToROOMSTATUS(RoomStatus rs) {
		return addNewRecordToTable(RoomStatus.class, ROOMSTATUS, rs);
	}

	/**
	 * Add a new record to table
	 * 
	 * @param classType
	 * @param tableName
	 * @param obj
	 * @return
	 */
	private <T> Boolean addNewRecordToTable(Class<T> classType,
			String tableName, T obj) {
		Boolean check = false;
		// get table from the server
		// check if the table exists
		// add the obj to the table
		// put the table back to server
		ArrayList<T> table = getTableByTableNameFromServer(tableName);
		if (!table.equals(null)) {
			table.add(obj);
			check = putTableBackToServer(table, tableName);
		} else {
			// create the table and put it back to server
			table.add(obj);
			check = putTableBackToServer(table, tableName);
		}
		return check;
	}

	/**
	 * Put table back to server
	 * 
	 * @param table
	 * @param tableName
	 * @return
	 */
	private <T> Boolean putTableBackToServer(ArrayList<T> table,
			String tableName) {
		Boolean check = KeyValueAPI.isServerAvailable();
		// check network available before put table back to server
		if (check) {
			check = Boolean.valueOf(KeyValueAPI.put(TEAMNAME, PASSWORD,
					tableName, gson.toJson(table)));
		}
		return check;
	}

	// ----------------------- Delete Record From Table -----------------------

	/**
	 * Delete a record from ONLINEUSER table
	 * 
	 * @param userName
	 * @return
	 */
	private Boolean deleteRecordFromONLINEUSER(String userName) {
		Boolean check = false;
		ArrayList<OnLineUser> oual = getOnLineUserFromServer();
		OnLineUser ou;
		Iterator<OnLineUser> iterator = oual.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ou = iterator.next();
			if (ou.getUserName().intern() == userName) {
				oual.remove(count);
				check = putTableBackToServer(oual, ONLINEUSER);
				break;
			}
			count++;
		}
		return check;
	}

	/**
	 * Delete a record from ROOMSTATUS table
	 * 
	 * @param roomID
	 * @return
	 */
	private Boolean deleteRecordFromROOMSTATUS(String roomID) {
		Boolean check = false;
		ArrayList<RoomStatus> rsal = getRoomStatusFromServer();
		RoomStatus rs;
		Iterator<RoomStatus> iterator = rsal.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			rs = iterator.next();
			if (rs.getRoomID().intern() == roomID) {
				rsal.remove(count);
				check = putTableBackToServer(rsal, ROOMSTATUS);
				break;
			}
			count++;
		}
		return check;
	}

	// ---------------------------- Update Table ------------------------------

	/**
	 * Update the record in ONLINEUSERTABLE. If there is a record matched the
	 * new OnLineUser object's userName, replace it with the new one.
	 * 
	 * @param new_ou
	 * @return TRUE if update successfully, or false if not.
	 */
	private Boolean updateONLINEUSER(OnLineUser new_ou) {
		Boolean check = false;
		ArrayList<OnLineUser> oual = getOnLineUserFromServer();
		Iterator<OnLineUser> iterator = oual.iterator();
		OnLineUser ou;
		int count = 0;
		while (iterator.hasNext()) {
			ou = iterator.next();
			if (ou.getUserName().intern() == new_ou.getUserName().intern()) {
				oual.set(count, new_ou);
				check = putTableBackToServer(oual, ONLINEUSER);
				break;
			}
			count++;
		}
		return check;
	}

	/**
	 * Update the record in ROOMSTATUS. If there is a record matched the new
	 * RoomStatus object's room id, replace it with the new one.
	 * 
	 * @param new_rs
	 * @return TRUE if update successfully, or false if not.
	 */
	private Boolean updateROOMSTATUS(RoomStatus new_rs) {
		Boolean check = false;
		ArrayList<RoomStatus> rsal = getRoomStatusFromServer();
		Iterator<RoomStatus> iterator = rsal.iterator();
		RoomStatus rs;
		int count = 0;
		while (iterator.hasNext()) {
			rs = iterator.next();
			if (rs.getRoomID().intern() == new_rs.getRoomID().intern()) {
				rsal.set(count, new_rs);
				check = putTableBackToServer(rsal, ROOMSTATUS);
				break;
			}
			count++;
		}
		return check;
	}
}
