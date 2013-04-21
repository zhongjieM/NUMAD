package edu.neu.madcourse.zhongjiemao.exerpacman.game;

import java.io.Serializable;
import java.util.ArrayList;

public class ScoresWrapper implements Serializable {
	
	private static final long serialVersionUID = 1514687980149255081L;
	
	ArrayList<ScoreBean> beans;
	
	public ScoresWrapper(ArrayList<ScoreBean> beans) {
		this.beans = beans;
	}

	public ArrayList<ScoreBean> getInnerList() {
		return beans;
	}

}
