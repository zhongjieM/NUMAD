package edu.neu.madcourse.zhongjiemao.boggle.BLLDAL;

/**
 * 
 * Scoring System of Boggle Game
 * 
 * @author Kevin
 * 
 */
public class Score {

	// record the current score.
	private int score = 0;

	private final int threeLetterWordPoints = 1;
	private final int fourLetterWordPoints = 2;
	private final int fiveLetterWordPoints = 5;
	private final int sixLetterWordPoints = 8;
	private final int moreThanSixLetterWordPoint = 10;

	public Score() {
		this.score = 0;
	}

	public Score(int score) {
		this.score = score;
	}

	/**
	 * get score property
	 * 
	 * @return
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * set score property
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * A existed word is found. Given the word, calculate the length of it and
	 * then add the points to the score according to the length of the word.
	 * 
	 * @param word
	 */
	public void wordsPointsAdding(String word) {
		int wordLength = word.length();
		addPoints(wordLength);

	}

	/**
	 * A existed word is found. Given the length of this word, add the points to
	 * the score according to the length of the word.
	 * 
	 * @param wordLength
	 */
	public void wordsPointsAdding(int wordLength) {
		addPoints(wordLength);
	}

	/**
	 * Given the length of the word. Add points to the score according to the
	 * length of word.
	 * 
	 * @param wordLength
	 */
	private void addPoints(int wordLength) {
		switch (wordLength) {
		case 1:
		case 2:
			break;
		case 3:
			this.score += this.threeLetterWordPoints;
			break;
		case 4:
			this.score += this.fourLetterWordPoints;
			break;
		case 5:
			this.score += this.fiveLetterWordPoints;
			break;
		case 6:
			this.score += this.sixLetterWordPoints;
			break;
		default:
			this.score += this.moreThanSixLetterWordPoint;
			break;
		}
	}

}
