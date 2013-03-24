package edu.neu.madcourse.zhongjiemao.persistent_boggle.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserGameStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.GameHallBLL;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ServiceForGameHall;

public class PersistentBoggleTest extends Activity implements OnClickListener {

	private Button btn_InitializeRemoteServer;
	private Button btn_GsonHelperTest;
	private Button btn_ClearRecord;
	private Button btn_GameHallBLLTest;

	private NotificationManager notificationManager;
	private Button btn_NotificationTest;
	private Button btn_NotificationTestCancel;
	private ServiceForGameHall sfg;

	ArrayList<UserInfo> uial;
	ArrayList<OnLineUser> oual;
	ArrayList<RoomStatus> rsal;

	private GsonHelper gsonHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_persistent_boggle_test);
		gsonHelper = new GsonHelper();
		initializeLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_persistent_boggle_test, menu);
		return true;
	}

	private void initializeLayout() {
		btn_InitializeRemoteServer = (Button) findViewById(R.id.btn_InitializeRemoteServer);
		btn_InitializeRemoteServer.setOnClickListener(this);

		btn_GsonHelperTest = (Button) findViewById(R.id.btn_GsonHelperTest);
		btn_GsonHelperTest.setOnClickListener(this);

		btn_ClearRecord = (Button) findViewById(R.id.btn_ClearRemoteRecord);
		btn_ClearRecord.setOnClickListener(this);

		btn_GameHallBLLTest = (Button) findViewById(R.id.btn_GameHallBLLTest);
		btn_GameHallBLLTest.setOnClickListener(this);

		btn_NotificationTest = (Button) findViewById(R.id.btn_NotificationTest);
		btn_NotificationTest.setOnClickListener(this);

		btn_NotificationTestCancel = (Button) findViewById(R.id.btn_NotificationTestCancel);
		btn_NotificationTestCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_ClearRemoteRecord:
			clearRemoteServerData();
			break;
		case R.id.btn_InitializeRemoteServer:
			// Initialize Remote Server
			initializeRemoteServer();
			break;
		case R.id.btn_GsonHelperTest:
			// Test GsonHelper class
			BOTTOMTESTCASES();
			break;
		case R.id.btn_GameHallBLLTest:
			// TODO: Game Hall BLL Tests
			USERGAMESTATUSTEST();
			break;
		case R.id.btn_NotificationTest:
			NOTIFICATIONTEST();
			break;
		case R.id.btn_NotificationTestCancel:
			NOTIFICATIONTESTCANCEL();
			break;
		}
	}

	private Boolean clearRemoteServerData() {
		// Clear All the tables
		gsonHelper.clearAllTables();
		uial = gsonHelper.getTableByTableNameFromServer(GsonHelper.USERINFO);
		oual = gsonHelper.getTableByTableNameFromServer(GsonHelper.ONLINEUSER);
		rsal = gsonHelper.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS);
		if (uial.size() == 0 && oual.size() == 0 && rsal.size() == 0) {
			System.out.println("All the table has been cleared");
			toastResult("All the tables have been cleared");
			return true;
		}
		return false;
	}

	private void initializeRemoteServer() {
		if (clearRemoteServerData()) {
			// Initialize All the tables with some dirty data
			UserInfo ui1 = new UserInfo("kevin", "000000", 0);
			UserInfo ui2 = new UserInfo("robbins", "000000", 0);
			UserInfo ui3 = new UserInfo("chen", "000001", 0);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui1);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui2);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui3);
			uial = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.USERINFO);
			System.out.println(uial.toString());

			OnLineUser olu1 = new OnLineUser("kevin", false,
					OnLineUser.DEFAULT_INIVITER);
			OnLineUser olu2 = new OnLineUser("robbins", false, "chen");
			OnLineUser olu3 = new OnLineUser("chen", true,
					OnLineUser.DEFAULT_INIVITER);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu1);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu2);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu3);
			oual = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.ONLINEUSER);
			System.out.println(oual.toString());

			RoomStatus rs2 = new RoomStatus("chen", "chen");
			gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, rs2);
			rsal = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS);
			System.out.println(rsal.toString());
		}
	}

	private void BOTTOMTESTCASES() {

		// Test Example 0: Empty table
		System.out.println("Test Example 0: empty table");
		ArrayList<RoomStatus> rsal = gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS);
		if (rsal.isEmpty()) {
			System.out.println("Initially, ROOMSTATUS table is empty.");
		}
		System.out.println("Test Example 0 is Over");

		// Test Example 1: check if kevin record exists in USERINFO table
		System.out
				.println("Test Example 1: check if kevin record exists in USERINFO table");
		String example1 = "kevin";
		UserInfo ui = (UserInfo) gsonHelper.getRecordFromTable(example1,
				GsonHelper.USERINFO);
		if (ui == null) {
			System.out.println("Kevin not exists");
		} else {
			System.out.println("Kevin exists: " + ui.toString());
		}
		System.out.println("Test Example 1 Over");

		// Test Example 2: delete a record from ONLINEUSER table
		System.out
				.println("Test Example 2, test of delete record from ONLINEUSER table");
		System.out.println("Show ONLINEUSER table before delete:");
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ONLINEUSER));
		System.out.println("Delete kevin from ONLINEUSER");
		gsonHelper.deleteRecordFromTable("kevin", GsonHelper.ONLINEUSER);
		System.out.println("ONLINEUSER after delete kevein: "
				+ gsonHelper
						.getTableByTableNameFromServer(GsonHelper.ONLINEUSER));
		System.out.println("Test Example 2 Over");

		// Test Example 3: delete a record from ROOMSTATUS table
		System.out
				.println("Test Example 3, test of delete record from ROOMSTATUS table");
		System.out.println("Add a new Record to ROOMSTATUS table");
		gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, new RoomStatus(
				"kevin", "kevin"));
		System.out.println("Print out ROOMSTATUS table");
		System.out.println(gsonHelper.getTableByTableNameFromServer(
				GsonHelper.ROOMSTATUS).toString());
		System.out
				.println("Delete kevin room record from the ROOMSTATUS table");
		gsonHelper.deleteRecordFromTable("kevin", GsonHelper.ROOMSTATUS);
		System.out.println("Show the ROOMSTATUS table after result");
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS));
		System.out.println("Test Example 3 Over");

		// Test Example 5: update a record in ONLINEUSER table
		System.out
				.println("Test Example 5: update a record in ONLINUSER table");
		initializeRemoteServer();
		System.out.println(gsonHelper.getRecordFromTable("kevin",
				GsonHelper.ONLINEUSER));
		gsonHelper.updateTable(GsonHelper.ONLINEUSER, new OnLineUser("kevin",
				true, OnLineUser.DEFAULT_INIVITER));
		System.out.println(gsonHelper.getRecordFromTable("kevin",
				GsonHelper.ONLINEUSER));
		System.out.println("Test Example 5 Over");

	}

	private void GAMEHALLBLLTEST() {
		// TODO: Game Hall BLL Tests
		initializeRemoteServer();
		GameHallBLL ghb = new GameHallBLL("kevin");
		// Game Hall BLL Test Example 1: loginInitialize()
		// System.out.println("Test Example 1: loginInitialize()");
		// gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, new RoomStatus(
		// "kevin", "kevin"));
		// gsonHelper.addNewGame("kevin", new RoomStatus("kevin", "kevin"));
		// gsonHelper.updateTable(GsonHelper.ONLINEUSER, new OnLineUser("kevin",
		// true, OnLineUser.DEFAULT_INIVITER));
		// System.out
		// .println("Show ROOMSTATUS table and Game before initializing");
		// System.out.println(gsonHelper.getRecordFromTable("kevin",
		// GsonHelper.ROOMSTATUS));
		// System.out.println(gsonHelper.getGameByRoomID("kevin"));
		// System.out.println(gsonHelper.getRecordFromTable("kevin",
		// GsonHelper.ONLINEUSER));
		//
		// ghb.loginInitialize();
		// System.out.println("Show ROOMSTATUS table and Game after initializing");
		// System.out.println(gsonHelper.getRecordFromTable("kevin",
		// GsonHelper.ROOMSTATUS));
		// System.out.println(gsonHelper.getGameByRoomID("kevin"));
		// System.out.println(gsonHelper.getRecordFromTable("kevin",
		// GsonHelper.ONLINEUSER));
		// System.out.println("Test Example 1 Over");

		// Game Hall BLL Test Example 2: getRoomStatusAsAdapter()
		initializeRemoteServer();
		System.out.println("Test Example 2: getRoomStatusAsAdapter()");
		gsonHelper.updateTable(GsonHelper.ONLINEUSER, new OnLineUser("kevin",
				true, "robbins"));
		System.out.println("initer is: " + ghb.checkInvitation());
		System.out.println("Test Example 2 Over");

		// Game Hall BLL Test Example 3: getRoomStatusAsAdapter()
		initializeRemoteServer();
		System.out.println("Test Example 3: getRoomStatusAsAdapter()");
		gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, new RoomStatus(
				"kvein", "kevin"));
		gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, new RoomStatus(
				"robbins", "robbins"));
		System.out.println(ghb.getRoomStatus());
		System.out.println("Test Example 3 Over");

		// Game Hall BLL Test Example 5: setIntoRoom("robbins");
		initializeRemoteServer();
		gsonHelper.addNewRecordToTable(GsonHelper.ROOMSTATUS, new RoomStatus(
				"robbins", 2, true, "robbins", "chen",
				RoomStatus.DEFAULT_PLAYER, "", ""));
		System.out.println("Test Example 5: setIntoRoom(roomID)");
		System.out.println("Before set into robbins");
		System.out.println(gsonHelper.getRecordFromTable("robbins",
				GsonHelper.ROOMSTATUS));
		ghb.setIntoRoom("robbins");
		System.out.println("After set into robbins");
		System.out.println(gsonHelper.getRecordFromTable("robbins",
				GsonHelper.ROOMSTATUS));
		System.out.println("Test Example 5 Over");

		// Game Hall BLL Test Example 6: startANewRoom()
		initializeRemoteServer();
		System.out.println("Test Example 6: startANewRoom()");
		ghb.startANewRoom();
		System.out.println(gsonHelper
				.getTableByTableNameFromServer(GsonHelper.ROOMSTATUS));
		System.out.println("Test Example 6 Over");
	}

	private void USERGAMESTATUSTEST() {
		// Test 1: create a record
		UserGameStatus ugs_test1 = new UserGameStatus("Cleng");
		gsonHelper.initializeUserGameStatus("Cleng", false);
		UserGameStatus ugs_test1_expect = gsonHelper.getUserGameStatus("Cleng");
		if (ugs_test1.toString().intern() == ugs_test1_expect.toString()
				.intern())
			System.out.println("Test1 approved");
		else
			System.out.println("Test1 failed");

		// Test 2: update a record
		UserGameStatus ugs_test2 = gsonHelper.getUserGameStatus("Cleng");
		ugs_test2.setInGame(true);
		gsonHelper.updateUserGameStatus("Cleng", ugs_test2);
		ugs_test2 = gsonHelper.getUserGameStatus("Cleng");
		UserGameStatus ugs_test2_expect = new UserGameStatus("Cleng");
		ugs_test2_expect.setInGame(true);
		if (ugs_test2_expect.toString().intern() == ugs_test2.toString()
				.intern()) {
			System.out.println("Test2 approved");
		} else
			System.out.println("Test2 failed");

	}

	private void toastResult(String result) {
		Toast toast = Toast.makeText(this, result, Toast.LENGTH_SHORT);
		toast.show();
	}

	private void NOTIFICATIONTEST() {
		System.out.println("Pressed");
		Intent i = new Intent(this, ServiceForGameHall.class);
		getApplicationContext().startService(i);
	}

	private void NOTIFICATIONTESTCANCEL() {
		getApplicationContext().stopService(
				new Intent(this, ServiceForGameHall.class));
	}
}
