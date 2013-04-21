package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import edu.neu.madcourse.zhongjiemao.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ExerPacmanInstruction extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exer_pacman_instruction);
		View startButton = findViewById(R.id.exer_pacman_start);
		startButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exer_pacman_start:
			startActivity(new Intent(this, ExerPacman.class));
			finish();
			break;

		default:
			break;
		}
	}
	
}
