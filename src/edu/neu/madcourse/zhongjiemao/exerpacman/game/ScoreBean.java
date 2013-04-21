package edu.neu.madcourse.zhongjiemao.exerpacman.game;

import java.io.Serializable;
import java.util.Date;

public class ScoreBean implements Serializable, Comparable<ScoreBean> {
	
	private static final long serialVersionUID = 756450401982236061L;

	private Integer score;
	
	private Date time;
	
	public ScoreBean(Integer score, Date time) {
		this.score = score;
		this.time = time;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getReadableTime() {
		return (time.getMonth() + 1) + "/" + time.getDate() + "/" + (time.getYear() + 1900);
	}
	
	@Override
	public int compareTo(ScoreBean another) {
		if (another instanceof ScoreBean) {
			if (score > ((ScoreBean) another).getScore()) {
				return 1;
			} else if (score < ((ScoreBean) another).getScore()) {
				return -1;
			} else {
				return 0;
			}
		} else {
			throw new ClassCastException("Wrong class type");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoreBean other = (ScoreBean) obj;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ScoreBean [score=" + score + ", time=" + time + "]";
	}
}
