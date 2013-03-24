package edu.neu.madcourse.zhongjiemao.persistent_boggle.BLL;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.Score;
import edu.neu.madcourse.zhongjiemao.boggle.BLLDAL.SimpleBloomFilter;
import edu.neu.madcourse.zhongjiemao.gsonhelper.GsonHelper;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserGameStatus;
import edu.neu.madcourse.zhongjiemao.persistent_boggle.PersistentBoggleGame;

/**
 * 
 * This class is to handle the data interactions between the UI of the boggle
 * and the dictionary data assets.
 * 
 * @author Kevin
 * 
 */
public class PersistentBoggleBLL {

	private GsonHelper gsonHelper;

	// SimpleBloomFilter Class
	// Used to handle the verification job.
	private SimpleBloomFilter<String> sbf_dictionary;

	// Scoring System
	private Score scoringSystem;
	private int mode = 4;
	private String roomID;

	// other players information
	private String[] playerID;

	/**
	 * 
	 * Constructor of BoggleBLL: Initialize the Bloom Filter; Import the
	 * dictionary to the Bloom Filter.
	 */
	public PersistentBoggleBLL(int mode, String roomID) {
		this.mode = mode;
		gsonHelper = new GsonHelper();
		this.roomID = roomID;
		// initialize the scoring system
		scoringSystem = new Score();
		// initialize the bloom filter
		sbf_dictionary = new SimpleBloomFilter<String>(12000000, 440000);
		// initialize(resource_is);
	}

	/**
	 * String -> Boolean Given a target word; Check if it exists in the
	 * dictionary. If the word exists in the dictionary, add points to current
	 * score automatically. Otherwise, don't do any thing.
	 * 
	 * @param targetWord
	 * @return Boolean
	 */
	public boolean contains(String targetWord) {
		boolean containsWord = sbf_dictionary.contains(targetWord);
		if (containsWord)
			this.scoringSystem.wordsPointsAdding(targetWord);
		return containsWord;
	}

	/**
	 * Returns the current score of the game.
	 * 
	 * @return
	 */
	public int returnCurrentScore() {
		return this.scoringSystem.getScore();
	}

	/**
	 * To Generate n ramdom letters
	 * 
	 * @param n
	 * @return
	 */
	public char[] generateWords(int n) {
		char[] words = new char[n * n];
		char[] vowels = { 'a', 'e', 'i', 'o', 'u' };
		for (int i = 0; i < n * n; i++) {
			int num = (Math.abs(new Random().nextInt())) % 26;
			words[i] = (char) (num + 97);
		}
		for (int i = 0; i < n; i++) {
			int index = (Math.abs((new Random()).nextInt())) % n;
			int num = (Math.abs(new Random().nextInt())) % 5;
			words[i * n + index] = (char) vowels[num];
		}
		return words;
	}

	public boolean isEmpty() {
		return sbf_dictionary.isEmpty();
	}

	public void loadDictionary(InputStream resource_is) {
		try {
			// import the bit files of the dictionary to bloom filter
			sbf_dictionary.readBit(resource_is);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public void exitGame(int character, String userName) {
		if (character == PersistentBoggleGame.ROOMMASTER) {
			RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(roomID,
					GsonHelper.ROOMSTATUS);
			rs.setIsGameStarts(false);
			gsonHelper.updateTable(GsonHelper.ROOMSTATUS, rs);
		}
	}

	/**
	 * get other players names
	 * 
	 * @param userName
	 * @return
	 */
	public Boolean getPlayersNames(String userName) {
		RoomStatus rs = (RoomStatus) gsonHelper.getRecordFromTable(roomID,
				GsonHelper.ROOMSTATUS);
		if (rs == null)
			return false;
		playerID = new String[2];
		int j = 0;
		String[] players = new String[3];
		players[0] = rs.getPlayer1();
		players[1] = rs.getPlayer2();
		players[2] = rs.getPlayer3();
		for (int i = 0; i < 3; i++) {
			if (players[i].intern() != RoomStatus.DEFAULT_PLAYER
					&& players[i].intern() != userName.intern()) {
				playerID[j] = players[i];
				j++;
			}
		}
		return true;
	}

	/**
	 * get other players status
	 * 
	 * @return
	 */
	public UserGameStatus[] getPlayersStatus() {
		int numberOfPlayers = playerID.length;
		UserGameStatus[] ugs = new UserGameStatus[numberOfPlayers];
		for (int i = 0; i < numberOfPlayers; i++) {
			ugs[i] = gsonHelper.getUserGameStatus(playerID[i]);
		}
		return ugs;
	}

	public void updateWordsToServer(String userName, ArrayList<String> words,
			int score) {
		UserGameStatus ugs = new UserGameStatus(userName, true, words, score);
		gsonHelper.updateUserGameStatus(userName, ugs);
	}
}
