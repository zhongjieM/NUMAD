package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

public class GameOverResult {
	private String yourName = "";
	private String player2Name = "";
	private String player3Name = "";
	private int yourScore = 0;
	private int player2Score = 0;
	private int player3Score = 0;

	public GameOverResult() {
	}

	public String getYourName() {
		return yourName;
	}

	public void setYourName(String yourName) {
		this.yourName = yourName;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}

	public String getPlayer3Name() {
		return player3Name;
	}

	public void setPlayer3Name(String player3Name) {
		this.player3Name = player3Name;
	}

	public int getYourScore() {
		return yourScore;
	}

	public void setYourScore(int yourScore) {
		this.yourScore = yourScore;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public int getPlayer3Score() {
		return player3Score;
	}

	public void setPlayer3Score(int player3Score) {
		this.player3Score = player3Score;
	}

}
