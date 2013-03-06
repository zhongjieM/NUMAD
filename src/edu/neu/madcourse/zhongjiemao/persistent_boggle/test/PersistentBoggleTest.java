package edu.neu.madcourse.zhongjiemao.persistent_boggle.test;

import java.util.ArrayList;

import android.app.Activity;
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
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;

public class PersistentBoggleTest extends Activity implements OnClickListener {

	private Button btn_InitializeRemoteServer;
	private Button btn_GsonHelperTest;
	private Button btn_ClearRecord;

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
			// TODO:
			// Test GsonHelper class
			TESTCASES();
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
			UserInfo ui2 = new UserInfo("robboins", "000000", 0);
			UserInfo ui3 = new UserInfo("chen", "000001", 0);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui1);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui2);
			gsonHelper.addNewRecordToTable(GsonHelper.USERINFO, ui3);
			uial = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.USERINFO);
			System.out.println(uial.toString());
			// TODO: initialize OnLineUser Table
			OnLineUser olu1 = new OnLineUser("kevin", false,
					OnLineUser.DEFAULT_INIVITER);
			OnLineUser olu2 = new OnLineUser("robbins", false, "kevin");
			OnLineUser olu3 = new OnLineUser("chen", true,
					OnLineUser.DEFAULT_INIVITER);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu1);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu2);
			gsonHelper.addNewRecordToTable(GsonHelper.ONLINEUSER, olu3);
			oual = gsonHelper
					.getTableByTableNameFromServer(GsonHelper.ONLINEUSER);
			System.out.println(oual.toString());
			// TODO: initialize RoomStatus Table
		}
	}

	private void TESTCASES() {

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
		RoomStatus rs = gsonHelper.getGameByRoomID("kevin");
		if (rs == null)
			System.out.println("kevin room not exists");
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

		// Test Example 4: delete a game
		System.out.println("Test Example 4: test delete a game");
		System.out.println("Add a new game at server");
		RoomStatus test4_rs = new RoomStatus("kevin", 3, true, "kevin",
				"robbins", "chen", "kevin", "");
		gsonHelper.addNewGame("kevin", test4_rs);
		System.out.println("Show the added game");
		System.out.println(gsonHelper.getGameByRoomID("kevin"));
		System.out.println("Delete this game");
		System.out.println("Delete result: " + gsonHelper.deleteTable("kevin"));
		System.out.println("Show the deletion result");
		System.out.println(gsonHelper.getGameByRoomID("kevin"));
		System.out.println("Test Example 4 Over");

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

	private void toastResult(String result) {
		Toast toast = Toast.makeText(this, result, Toast.LENGTH_SHORT);
		toast.show();
	}
}
