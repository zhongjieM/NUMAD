package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.google.gson.Gson;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.service.ToastUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.FileUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter.TableCell;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter.TableRow;

public class ExerPacmanMapNav extends Activity implements OnItemClickListener {
	
	private static final String TAG = "EXER_PACMAN_MAP_NAV";
	
	private ListView mListView;
	private TableAdapter mAdapter;
	
	private BitSet mLevelLock;

	private int mMode = 0;
	private int initMapID = 0;
	private boolean isBackToMain = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMode = getIntent().getIntExtra(KEY_MODE, 0);
		setContentView(R.layout.exer_pacman_map_nav);
		retrieveData();
		List<TableRow> table = renderData();
		mAdapter = new TableAdapter(this, table);
		mListView = (ListView) findViewById(R.id.exer_pacman_lv_map_nav);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		isBackToMain = false;
		ExerPacmanMusic.resume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if (!isBackToMain) {
			ExerPacmanMusic.pause();
		}
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isBackToMain = true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mLevelLock.get((int) id)) {
			initMapID = (int) id;
			startGame(initMapID);
		} else {
			ToastUtil.show(this, "Sorry, Level " + ((int) (id + 1)) + " is locked now");
		}
		
	}
	
	private void startGame(int mapId) {
		// start game activity
		Intent intent = new Intent(this, ExerPacmanGame.class);
		intent.putExtra(KEY_MODE, mMode);
		intent.putExtra(KEY_MAZE_ID, mapId);
		startActivity(intent);
		overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
		// destroy self
		this.finish();
	}
	
	private void retrieveData() {
		if (!FileUtil.isInternalFileExist(this, LEVEL_LOCK_FILE)) {
			FileUtil.writeFileToInternal(this, LEVEL_LOCK_FILE, INIT_LEVEL_LOCK);
		}
		String plainData = FileUtil.readFileFromInternal(this, LEVEL_LOCK_FILE);
		Gson gson = new Gson();
		mLevelLock = gson.fromJson(plainData, BitSet.class);
	}
	
	private List<TableRow> renderData() {
		List<TableRow> table = new ArrayList<TableAdapter.TableRow>();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int unitWidth = dm.widthPixels / 5;
		for (int i = 0; i < NUM_MAZES; i++) {
			if (mLevelLock.get(i)) {
				TableCell[] cells = new TableCell[2];
				cells[0] = new TableCell(unlockMazeIDs[i], unitWidth * 2,
						LayoutParams.WRAP_CONTENT, TableCell.IMAGE);
				cells[1] = new TableCell("Level " + (i + 1), unitWidth * 2,
						LayoutParams.MATCH_PARENT, TableCell.STRING, 25);
				table.add(new TableRow(cells));
			} else {
				TableCell[] cells = new TableCell[2];
				cells[0] = new TableCell(lockMazeIds[i], unitWidth * 2,
						LayoutParams.WRAP_CONTENT, TableCell.IMAGE);
				cells[1] = new TableCell("Level " + (i + 1), unitWidth * 2,
						LayoutParams.MATCH_PARENT, TableCell.STRING, 25);
				table.add(new TableRow(cells));
			}
		}
		return table;
	}

}
