package edu.neu.madcourse.zhongjiemao;

import org.apache.http.client.entity.UrlEncodedFormEntity;

import edu.neu.madcourse.zhongjiemao.boggle.BoggleGame;
import edu.neu.madcourse.zhongjiemao.boggle.BoggleMain;
import edu.neu.madcourse.zhongjiemao.sudoku.*;
import edu.neu.mobileClass.PhoneCheckAPI;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;

public class Assignment1 extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment1);
		
		// Check whether the phone is permitted to run the program.
		boolean authorized = checkAuthorized();
		// Set click listener for all the buttons.
		setButtonClickListener(authorized);
		//System.out.print(authorized);
		if(!authorized)
		{
			// Turn off the app in FIVE SECONDS.
			try{
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(5000);
							System.exit(0);
						}
						catch(Exception ex)
						{
							System.out.print(ex.toString());
						}
					}
				}).start();	
			}
			catch(Exception ex){
				System.out.print(ex.toString());
			}
		}	
	}
	
	/**
	 * Check whether the current phone is authorized to run the program.
	 * @return
	 * -- true : authorized to run the program;
	 * -- false: forbidden to run the program.
	 * 			 the method will setup an AlertDialog to show the warning.
	 */
	private boolean checkAuthorized(){
		
		/**
		 * Check whether the phone is permitted to run the program.
		 */
		boolean authorizedToRun = PhoneCheckAPI.doAuthorization(this);
		if(authorizedToRun)
			return true;
		else{
			/**
			 * This phone is not allowed to run the program!
			 * Show the warning message.
			 */
			new AlertDialog.Builder(this)
			.setTitle("Oops! Not Public!")
			.setMessage("Sorry! This is a test application not intended for public use. \n" +
						"The application will turn off in 5 seconds.").show();
			return false;
		}
	}
	
	/**
	 * Set click listener for all the buttons.
	 * @param authorized
	 * If the current phone is unauthorized to run the program:
	 * all the button will be set to be invisible.
	 * otherwise, nothing else is going to happen.
	 */
	private void setButtonClickListener(boolean authorized){
		
		View btn_team_members = findViewById(R.id.btn_team_members);
		btn_team_members.setOnClickListener(this);
		
		View btn_sudoku_game = findViewById(R.id.btn_sudoku_game);
		btn_sudoku_game.setOnClickListener(this);
		
		View btn_boggle = findViewById(R.id.btn_boggle_game);
		btn_boggle.setOnClickListener(this);
		
		View btn_error_check = findViewById(R.id.btn_error);
		btn_error_check.setOnClickListener(this);
		
		View btn_exit = findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
		
		/** 
		 * Authorized to run?
		 * Authorized  : Visible;
		 * Unauthorized: invisible.
		 */
		if(!authorized)
		{
			btn_team_members.setVisibility(4);
			btn_sudoku_game.setVisibility(4);
			btn_error_check.setVisibility(4);
			btn_exit.setVisibility(4);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_assignment1, menu);
		return true;
	}

	public void onClick(View v) {

		Intent i;

		switch (v.getId()) {
		case R.id.btn_team_members:
			i = new Intent(this, TeamMembers.class);
			startActivity(i);
			break;
		case R.id.btn_sudoku_game:
			i = new Intent(this, Sudoku.class);
			startActivity(i);
			break;
		case R.id.btn_error:
			i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: 12345"));
			startActivity(i);
			break;
		case R.id.btn_boggle_game:
			i = new Intent(this, BoggleMain.class);
			startActivity(i);
			break;
		case R.id.btn_exit:
			System.exit(0);
			break;
		default:
			return;
		}
		
	}

}
