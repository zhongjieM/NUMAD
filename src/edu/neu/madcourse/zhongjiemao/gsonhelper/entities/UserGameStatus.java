package edu.neu.madcourse.zhongjiemao.gsonhelper.entities;

import java.util.ArrayList;

public class UserGameStatus {

	public static final String DEFAULT_USER = "DEF";

	private String userName;
	private Boolean inGame;
	ArrayList<String> currentWords;
	private int currentScore;

	public UserGameStatus() {
		this.userName = DEFAULT_USER;
		this.inGame = false;
		this.currentWords = new ArrayList<String>();
		this.currentScore = 0;
	}

	public UserGameStatus(String userName) {
		this.userName = userName;
		inGame = false;
		currentWords = new ArrayList<String>();
		currentScore = 0;
	}

	public UserGameStatus(String userName, Boolean inGame,
			ArrayList<String> currentWords, int score) {
		this.userName = userName;
		this.inGame = inGame;
		this.currentWords = currentWords;
		this.currentScore = score;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getInGame() {
		return inGame;
	}

	public void setInGame(Boolean inGame) {
		this.inGame = inGame;
	}

	public ArrayList<String> getCurrentWords() {
		return currentWords;
	}

	public void setCurrentWords(ArrayList<String> currentWords) {
		this.currentWords = currentWords;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	@Override
	public String toString() {
		return "User Name: " + this.userName + "; inGame?: " + this.inGame;
	}
}
