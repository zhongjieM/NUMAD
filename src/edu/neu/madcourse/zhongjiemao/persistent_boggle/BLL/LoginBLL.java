package edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL;

import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;

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

	private GsonHelper gh;

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

	/**
	 * Default Constructor Initialize Gson, userCollection.
	 */
	public LoginBLL() {

		// initialize Gson for future operation
		gh = new GsonHelper();
	}

	/**
	 * Given a user name and a phone id, first check whether the network is
	 * working; if it is working, then check whether the given user name can log
	 * in the game. If the user name existed under the given phone id, log in
	 * anyway. If the user name existed under the other phones' id, log in
	 * fails. Otherwise, register this user name automatically and log in the
	 * game by using this new user name.
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
		if (gh.isServerAvailable()) {
			check = isThisPhoneHasGivenAccount(uname, pid);
			if (check == NO_SUCH_USER)
				if (!register(uname, pid)) {
					check = UNACCESSIBLE;
				}
		}
		return check;
	}

	/**
	 * This method is to initialize the user Online Information when the user
	 * successfully login.
	 */
	public Boolean loginInitialize(String userName) {
		Boolean check = false;
		// update the Online Table
		// clear all the dirty data of the current user.
		// 1. check the Room Status if there is any Room that was under his
		// name.
		// 2. check the Room Info table to see if there is any game starts under
		// his user name
		// 3. Add a new record to ONLINE table
		check = gh.deleteRecordFromTable(userName, GsonHelper.ROOMSTATUS)
				&& gh.deleteTable(userName)
				&& gh.deleteRecordFromTable(userName, GsonHelper.ONLINEUSER);
		OnLineUser ou = new OnLineUser(userName, false,
				OnLineUser.DEFAULT_INIVITER);
		check = check && gh.addNewRecordToTable(GsonHelper.ONLINEUSER, ou);
		// TODO: how to make sure that all the information have been set
		// correctly at the server
		System.out.println("Login Initialize: " + check);
		return check;
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

		UserInfo ui = (UserInfo) gh.getRecordFromTable(uname,
				GsonHelper.USERINFO);
		if (ui == null)
			return NO_SUCH_USER;
		else if (ui.getPhoneID().intern() == pid)
			return LOGIN_APPROVED;
		else
			return LOGIN_DENIED;
	}

	/**
	 * Register a new user name under the specific phone id.
	 * 
	 * @param uname
	 * @param pid
	 * @return true if register success or false if not.
	 */
	private Boolean register(String uname, String pid) {
		UserInfo ui = new UserInfo(uname, pid, 0);
		Boolean check = gh.addNewRecordToTable(GsonHelper.USERINFO, ui);
		return check;
	}
}
