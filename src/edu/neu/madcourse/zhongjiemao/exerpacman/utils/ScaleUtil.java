package edu.neu.madcourse.zhongjiemao.exerpacman.utils;

public class ScaleUtil {
	
	protected static float bitmapRatio = 1;
	protected static float nodeRatio = 1;
	protected static float tRatio = 1;
	
	protected static float actualScreenW = -1;
	protected static float actualScreenH = -1;
	
	protected static float actualMazeW = -1;
	protected static float actualMazeH = -1;
	
	/** for nodes */
	protected static final float originMazeW = 224f;
	protected static final float originMazeH = 248f;
	
	/** for images */
	protected static float localMazeW = -1;
	protected static float localMazeH = -1;
	
	private ScaleUtil() {}
	
	public static float doBitmapScaleW(float originW) {
		return originW * bitmapRatio;
	}
	
	public static float doBitmapScaleH(float originH) {
		return originH * bitmapRatio;
	}
	
	public static float doNodeScaleW(float originW) {
		return originW * nodeRatio;
	}
	
	public static float doNodeScaleH(float originH) {
		return originH * nodeRatio;
	}
	
	public static float doScaleT(float textSize) {
		return textSize * tRatio;
	}
	
	public static float getBitmapRatio() {
		return bitmapRatio;
	}
	
	public static float getNodeRatio() {
		return nodeRatio;
	}
	
	/**
	 * calculate the scaled width and height of images
	 * @param baseViewW the actual width of the base view
	 * @param baseViewH the actual height of the base view
	 */
	public static void calcRatio(float baseViewW, float baseViewH) {
		actualScreenW = baseViewW;
		actualScreenH = baseViewH;
		bitmapRatio = actualScreenW / localMazeW;
		tRatio = bitmapRatio;
		actualMazeW = localMazeW * bitmapRatio;
		actualMazeH = localMazeH * bitmapRatio;
		
		nodeRatio = actualScreenW / originMazeW;
	}

	public static float getActualScreenW() {
		return actualScreenW;
	}

	public static float getActualScreenH() {
		return actualScreenH;
	}
	
	public static float getActualMazeW() {
		return actualMazeW;
	}
	
	public static float getActualMazeH() {
		return actualMazeH;
	}

	public static void setLocalMazeW(float localMazeW) {
		ScaleUtil.localMazeW = localMazeW;
	}

	public static void setLocalMazeH(float localMazeH) {
		ScaleUtil.localMazeH = localMazeH;
	}
	
}
