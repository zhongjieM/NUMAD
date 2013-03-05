package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.neu.mobileclass.apis.KeyValueAPI;

/**
 * This Class is to deal with all the gson operation with the remote server.
 * 
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
	public final static String ROOMINFO = "ROOMINFO";
	public final static String ROOMSTATUS = "ROOMSTATUS";

	private Gson gson;

	public GsonHelper() {
		gson = new Gson();
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
		if (!jarray.equals(null))
			for (int i = 0; i < jarray.size(); i++)
				tableArray.add(gson.fromJson(jarray.get(i), classType));
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
			if (jsonTable != "" && jsonTable != null) {
				// System.out.println(jsonTable);
				return parser.parse(jsonTable).getAsJsonArray();
			}
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
		// add the obj to the table
		// put the table back to server
		ArrayList<T> table = getTableByTableNameFromServer(tableName);
		table.add(obj);
		check = putTableBackToServer(table, tableName);
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
			KeyValueAPI.put(TEAMNAME, PASSWORD, tableName, gson.toJson(table));
		}
		return check;
	}

	// ------------ Search a Specific Table for Specific Record----------------

	/**
	 * Given the primary key of a table, check if it has a matched record in the
	 * given table
	 * 
	 * @param recordKey
	 * @param tableName
	 * @return
	 */
	public Boolean isValueExistsInTable(String recordKey, String tableName) {
		if (tableName == USERINFO)
			return isExistsInUSERINFO(recordKey);
		if (tableName == ONLINEUSER)
			return isExistsInONLINEUSER(recordKey);
		if (tableName == ROOMSTATUS)
			return isExistsInROOMSTATUS(recordKey);
		return false;
	}

	/**
	 * Check if the given user name exists in the USERINFO table
	 * 
	 * @param userName
	 * @return
	 */
	private Boolean isExistsInUSERINFO(String userName) {
		ArrayList<UserInfo> uial = getUserInfoFromServer();
		Iterator<UserInfo> it = uial.iterator();
		UserInfo ui;
		while (it.hasNext()) {
			ui = it.next();
			if (ui.getUserName() == userName)
				return true;
		}
		return false;
	}

	/**
	 * Check if the given user name exists in the ONLINEUSER table
	 * 
	 * @param userName
	 * @return
	 */
	private Boolean isExistsInONLINEUSER(String userName) {
		ArrayList<OnLineUser> oual = getOnLineUserFromServer();
		Iterator<OnLineUser> it = oual.iterator();
		OnLineUser ou;
		while (it.hasNext()) {
			ou = it.next();
			if (ou.getUserName() == userName)
				return true;
		}
		return false;
	}

	/**
	 * Check if the given room id exists in the ROOMSTATUS table
	 * 
	 * @param roomID
	 * @return
	 */
	private Boolean isExistsInROOMSTATUS(String roomID) {
		ArrayList<RoomStatus> rsal = getRoomStatusFromServer();
		Iterator<RoomStatus> it = rsal.iterator();
		RoomStatus rs;
		while (it.hasNext()) {
			rs = it.next();
			if (rs.getRoomID() == roomID)
				return true;
		}
		return false;
	}
}
