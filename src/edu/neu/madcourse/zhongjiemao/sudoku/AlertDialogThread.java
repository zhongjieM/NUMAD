package edu.neu.madcourse.zhongjiemao.sudoku;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogThread extends Thread implements Runnable {

	Context cont;
	
	public AlertDialogThread(Context cont){
		
		this.cont = cont;
		
	}
	
	public void run()
	{
		if(!this.cont.equals(null))
		{
			try{
				new AlertDialog.Builder(this.cont)
				.setTitle("Oops! Not Public!")
				.setMessage("Sorry! This is a test application not intended for public use. \n" +
							"The application will turn off in 5 seconds.")
				.show();
			}
			catch(Exception ex)
			{
				System.out.print(ex.toString());
			}
		}
	}
}
