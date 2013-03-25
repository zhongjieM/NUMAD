package edu.neu.madcourse.zhongjiemao.persistent_boggle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.OnLineUser;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL.RoomBLL;

public class InvitationDialog extends Activity {

	private final String TAG = "Persistent Boggle Invitation";

	public final static String USERNAME = "USERNAME";
	public final static String ROOMID = "ROOMID";

	private static final int THREAD_INVITE = 1;

	private ListView lv;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> array_OnlineUsers;
	private RelativeLayout rl;

	private String roomID;
	private String userName;

	private static Handler handler;
	private TimerTask thread_invite;
	private Timer timer;
	private ArrayList<OnLineUser> array_ous;

	private RoomBLL roomBLL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Invitation dialog Activated");
		initializeParameters();
		initializeViews();
		startOnlineUserListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_invitation_dialog, menu);
		return true;
	}

	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		return null;
	}

	@Override
	public void onPause() {
		try {
			if (thread_invite != null)
				thread_invite.cancel();
			if (timer != null)
				timer.cancel();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		super.onPause();
	}

	// ------------------ Private Methods --------------------------
	private void initializeParameters() {
		userName = this.getIntent().getStringExtra(InvitationDialog.USERNAME)
				.intern();
		roomID = this.getIntent().getStringExtra(InvitationDialog.ROOMID)
				.intern();
		roomBLL = new RoomBLL(roomID, userName);
		array_ous = new ArrayList<OnLineUser>();
	}

	private void initializeViews() {
		rl = new RelativeLayout(this);
		lv = new ListView(this);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String invite = (String) lv.getItemAtPosition(position);
				try {
					InviteSomeOne iso = new InviteSomeOne();
					iso.execute(invite);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		});
		rl.addView(lv);
		setContentView(rl);
	}

	private void startOnlineUserListener() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case THREAD_INVITE:
					updateListView();
					break;
				default:
					break;
				}
			}
		};

		thread_invite = new TimerTask() {
			@Override
			public void run() {
				try {
					array_ous = roomBLL.getAllAvailableUser();
					Message msg = new Message();
					msg.what = THREAD_INVITE;
					handler.sendMessage(msg);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		};
		timer = new Timer();
		timer.schedule(thread_invite, 500, 1000);

	}

	private void updateListView() {
		try {
			if (array_ous == null)
				return;
			Iterator<OnLineUser> it_avals = array_ous.iterator();
			array_OnlineUsers = new ArrayList<String>();
			while (it_avals.hasNext()) {
				OnLineUser ou = it_avals.next();
				if (ou.getInGame().equals(false))
					array_OnlineUsers.add(ou.getUserName());
			}
			adapter = new ArrayAdapter<String>(this,
					R.layout.invitationlist_layout, array_OnlineUsers);
			lv.setAdapter(adapter);
		} catch (Exception ex) {
			String errorMessage = "Invitation Dialog Update ListView Failed";
			System.out.println(errorMessage);
			System.out.println(ex.toString());
		}
	}

	// ------------------ AsyncTask Part ---------------------------

	private class InviteSomeOne extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... userNames) {
			roomBLL.invitation(userNames[0]);
			return null;
		}
	}
}
