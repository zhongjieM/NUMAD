package edu.neu.madcourse.zhongjiemao;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.TextView;

public class TeamMembers extends Activity {

	private TextView imei;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_members); 
		
		/**
		 * Get the unique number of the phone,
		 * and set it to the txt_phone_ID TextView.
		 */
		imei = (TextView)findViewById(R.id.txt_phone_ID);
		TelephonyManager tm = (TelephonyManager)this.
				getSystemService(Context.TELEPHONY_SERVICE);
		imei.setText(tm.getDeviceId());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_team_members, menu);
		return true;
	}

}
