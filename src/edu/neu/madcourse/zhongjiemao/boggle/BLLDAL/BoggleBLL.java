package edu.neu.madcourse.zhongjiemao.boggle.BLLDAL;

import java.io.InputStream;
import java.util.Random;

/**
 * 
 * This class is to handle the data interactions between the UI of the boggle
 * and the dictionary data assets.
 * 
 * @author Kevin
 * 
 */
public class BoggleBLL {

	// SimpleBloomFilter Class
	// Used to handle the verification job.
	private SimpleBloomFilter<String> sbf_dictionary;

	// Scoring System
	private Score scoringSystem;

	/**
	 * 
	 * Constructor of BoggleBLL: Initialize the Bloom Filter; Import the
	 * dictionary to the Bloom Filter.
	 */
	public BoggleBLL(InputStream resource_is) {
		try {
			// initialize the scoring system
			scoringSystem = new Score();

			// initialize the bloom filter
			sbf_dictionary = new SimpleBloomFilter<String>(12000000, 440000);
			// import the bit files of the dictionary to bloom filter
			sbf_dictionary.readBit(resource_is);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
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
		char[] words = new char[n];
		char[] vowels = { 'a', 'e', 'i', 'o', 'u' };
		for (int i = 0; i < n; i++) {
			int num = (Math.abs(new Random().nextInt())) % 26;
			words[i] = (char) (num + 97);
		}
		for (int i = 0; i < 4; i++) {
			int index = (Math.abs((new Random()).nextInt())) % 4;
			int num = (Math.abs(new Random().nextInt())) % 5;
			words[i * 4 + index] = (char) vowels[num];
		}
		return words;
	}

	public boolean isEmpty() {
		return sbf_dictionary.isEmpty();
	}
}
