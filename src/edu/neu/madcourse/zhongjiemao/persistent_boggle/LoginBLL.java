package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;
import java.util.Collection;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;
import edu.neu.mobileclass.apis.KeyValueAPI;

/**
 * This class is to handle business logical operations of login parts. If the
 * user name does not exist in the remote server, create a new account with this
 * user name as the username; if the user name does exist in the remote server
 * under the given phone id, login approved; if the user name does exist in the
 * remote server but under the other phone id, login denied.
 * 
 * @author kevin
 * 
 */
public class LoginBLL {

	private final String TEAMNAME = "ZJM";
	private final String PASSWORD = "50019891";
	private final String USERINFO = "USERINFO";

	/**
	 * A login status is either:
	 * 
	 * NO_SUCH_USER: new user has been created, login approved;
	 * 
	 * LOGIN_APPROVED: user exists, login approved;
	 * 
	 * LOGIN_DENIED: user exists but not belongs to this phone, login denied;
	 * 
	 * UNACCESSIBLE: network fails. Remote server is unaccessible now.
	 */
	public final static int NO_SUCH_USER = 0;
	public final static int LOGIN_APPROVED = 1;
	public final static int LOGIN_DENIED = 2;
	public final static int UNACCESSIBLE = 3;

	private Gson gson;
	private Collection<UserInfo> userCollection;

	/**
	 * Default Constructor Initialize Gson, userCollection.
	 */
	public LoginBLL() {

		// initialize Gson for future operation
		gson = new Gson();

		// initialize userCollection
		this.userCollection = new ArrayList<UserInfo>();
	}

	/**
	 * Try to get USERINFO table from remote server to initialize userCollection
	 * 
	 * @return true if has contents or false if not.
	 */
	private boolean initializeUserInfoCollection() {
		// Get the USERINFO table from remote server
		String userInfoDataBase = KeyValueAPI.get(TEAMNAME, PASSWORD, USERINFO);
		// if the USERINFO table are not empty, initialize that table
		if (userInfoDataBase != "" && userInfoDataBase != null) {
			JsonParser parser = new JsonParser();
			JsonArray jarray = parser.parse(userInfoDataBase).getAsJsonArray();
			UserInfo[] uis = new UserInfo[jarray.size()];
			for (int i = 0; i < jarray.size(); i++) {
				uis[i] = gson.fromJson(jarray.get(i), UserInfo.class);
				this.userCollection.add(uis[i]);
			}
			return true;
		}
		return false;
	}

	/**
	 * Given a user name and a phone id, check whether this user name existed
	 * under the specific phone id. The server has been proved to be accessible.
	 * 
	 * @param uname
	 * @param pid
	 * @return -- NO_SUCH_USER, there is no such user
	 * 
	 *         -- LOGIN_APPROVED, there is a such user name at remote server
	 *         under the specific phone id
	 * 
	 *         -- LOGIN_DENIED, there is a such user name at remote server but
	 *         not under the specific phone id
	 */
	private int isThisPhoneHasGivenAccount(String uname, String pid) {

		// check is an integer. Initially, the default value is 0.
		int check = 0;

		Object[] uis;
		if (userCollection.size() != 0) {
			System.out.println("Size of UserCollection: "
					+ userCollection.size());
			uis = userCollection.toArray();
			for (int i = 0; i < userCollection.size(); i++) {
				if (uname.equals(((UserInfo) uis[i]).getUserName())) {
					if (pid.equals(((UserInfo) uis[i]).getPhoneID()))
						check = LOGIN_APPROVED;
					else
						check = LOGIN_DENIED;
					break;
				} else
					check = NO_SUCH_USER;
			}
		}
		return check;
	}

	/**
	 * Register a new user name under the specific phone id.
	 * 
	 * @param uname
	 * @param pid
	 * @return true if register success or false if not.
	 */
	private Boolean register(String uname, String pid) {

		Boolean check = false;
		// if there is no record in USERINFO table, then the one create here is
		// the first record of that table
		int uid = 1;
		// Initially, the top score of a fresh user is 0.
		int topScore = 0;

		// register operation ......
		if (userCollection.size() != 0) {
			// get a new user id
			Object[] uis = userCollection.toArray();
			uid = Integer.parseInt(((UserInfo) uis[userCollection.size() - 1])
					.getUserID()) + 1;
		}
		// create a new user account
		UserInfo newUser = new UserInfo(String.valueOf(uid), uname, pid,
				topScore);
		// add this new account to the collection
		userCollection.add(newUser);
		// update this collection to the remote server
		String userInfoDataBase = gson.toJson(userCollection);
		// check again whether the server is available
		if (KeyValueAPI.isServerAvailable()) {
			KeyValueAPI.put(TEAMNAME, PASSWORD, USERINFO, userInfoDataBase);
			check = true;
		}
		return check;
	}

	/**
	 * Given a user name and a phone id, first check whether the network is
	 * working; if it is working, then check whether the given user name can log
	 * in the game. If the user name existed under the given phone id, log in
	 * anyway. If the user name existed under the other phones' id, log in
	 * fails. Otherwise, register this user name automatically and log in the
	 * game by using this new name.
	 * 
	 * @param uname
	 * @param pid
	 * @return -- NO_SUCH_USER: there is no such user, register it
	 * 
	 *         -- LOGIN_APPROVED: there is a such user under the given phone id
	 * 
	 *         -- LOGGIN_DENIED: there is a such user but under other phone id
	 * 
	 *         -- UNACCESSIBLE: the Server is not accessible right now.
	 */
	public int isLoginApproved(String uname, String pid) {
		int check = UNACCESSIBLE;
		// check whether the server is available or not
		if (KeyValueAPI.isServerAvailable()) {
			// try to get USERINFO table from remote server to initialize
			// userCollection
			initializeUserInfoCollection();
			check = isThisPhoneHasGivenAccount(uname, pid);
			if (check == NO_SUCH_USER)
				if (!register(uname, pid))
					check = UNACCESSIBLE;
			userCollection.clear();
		}
		return check;
	}
}
