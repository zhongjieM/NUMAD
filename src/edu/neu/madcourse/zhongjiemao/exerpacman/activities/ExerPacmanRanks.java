package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.SCORE_FILE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.google.gson.Gson;

import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ExerPacmanMusic;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ScoreBean;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.ScoresWrapper;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.FileUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter.TableCell;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.TableAdapter.TableRow;

public class ExerPacmanRanks extends Activity {

	private ListView mListView;
	private TableAdapter mAdapter;

	private boolean isBackToMain = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exer_pacman_ranks);
		List<ScoreBean> sortedScores = retrieveData();
		List<TableRow> table = renderData(sortedScores);
		mAdapter = new TableAdapter(this, table);
		mListView = (ListView) findViewById(R.id.exer_pacman_top_score_list);
		mListView.setAdapter(mAdapter);
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

	private List<TableRow> renderData(List<ScoreBean> data) {
		List<TableRow> table = new ArrayList<TableRow>();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int unitWidth = dm.widthPixels / 20;
		TableCell[] titles = new TableCell[3];
		titles[0] = new TableCell("Rank", unitWidth * 5,
				LayoutParams.MATCH_PARENT, TableCell.STRING, 20);
		titles[1] = new TableCell("Score", unitWidth * 5,
				LayoutParams.MATCH_PARENT, TableCell.STRING, 20);
		titles[2] = new TableCell("Date", unitWidth * 5,
				LayoutParams.MATCH_PARENT, TableCell.STRING, 20);
		table.add(new TableRow(titles));
		for (int i = 0; i < data.size(); i++) {
			TableCell[] cells = new TableCell[3];
			cells[0] = new TableCell(i + 1, unitWidth * 5,
					LayoutParams.MATCH_PARENT, TableCell.STRING, 15);
			cells[1] = new TableCell(data.get(i).getScore(), unitWidth * 5,
					LayoutParams.MATCH_PARENT, TableCell.STRING, 15);
			cells[2] = new TableCell(data.get(i).getReadableTime(),
					unitWidth * 5, LayoutParams.MATCH_PARENT, TableCell.STRING,
					15);
			table.add(new TableRow(cells));
		}
		return table;
	}

	private List<ScoreBean> retrieveData() {
		Gson gson = new Gson();
		if (FileUtil.isInternalFileExist(this, SCORE_FILE)) {
			String plain = FileUtil.readFileFromInternal(this, SCORE_FILE);
			ScoresWrapper wrapper = gson.fromJson(plain, ScoresWrapper.class);
			List<ScoreBean> beans = wrapper.getInnerList();
			Collections.sort(beans);
			Collections.reverse(beans);
			return beans;
		} else {
			return new ArrayList<ScoreBean>();
		}
	}

}
