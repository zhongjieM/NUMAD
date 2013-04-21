package edu.neu.madcourse.zhongjiemao.exerpacman.models;

import static edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.ScaleUtil;
import edu.neu.madcourse.zhongjiemao.exerpacman.utils.Zip;

/**
 * Stores the actual mazes, each of which is simply a connected graph.
 */
public final class Maze {
	
	private static final String TAG = "EXER_PACMAN_MAZE";

	public AStar astar;

	/**
	 * Information for the controllers
	 */
	public int[] shortestPathDistances, pillIndices, powerPillIndices,
			junctionIndices;

	/**
	 * Maze-specific information
	 */
	public int initialPacManNodeIndex, lairNodeIndex, initialGhostNodeIndex;

	/**
	 * The actual maze, stored as a graph (set of nodes)
	 */
	public Node[] graph;

	/**
	 * Name of the Maze
	 */
	public String name;

	private Context mContext;

	/**
	 * Each maze is stored as a (connected) graph: all nodes have neighbours,
	 * stored in an array of length 4. The index of the array associates the
	 * direction the neighbour is located at: '[up,right,down,left]'. For
	 * instance, if node '9' has neighbours '[-1,12,-1,6]', you can reach node
	 * '12' by going right, and node 6 by going left. The directions returned by
	 * the controllers should thus be in {0,1,2,3} and can be used directly to
	 * determine the next node to go to.
	 */
	public Maze(int index, Context context) {
		mContext = context;
		loadNodes(nodeNames[index]);
		loadZipDistancesBin(distBinNames[index]);

		// create A* graph for shortest paths for the ghosts
		astar = new AStar();
		astar.createGraph(graph);
	}

	/**
	 * Loads all the nodes from files and initialises all maze-specific
	 * information.
	 */
	private void loadNodes(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					mContext.getResources().getAssets()
							.open(fileName, AssetManager.ACCESS_RANDOM)));
			String input = br.readLine();

			// preamble
			String[] pr = input.split("\t");

			this.name = pr[0];
			this.initialPacManNodeIndex = Integer.parseInt(pr[1]);
			this.lairNodeIndex = Integer.parseInt(pr[2]);
			this.initialGhostNodeIndex = Integer.parseInt(pr[3]);
			this.graph = new Node[Integer.parseInt(pr[4])];
			this.pillIndices = new int[Integer.parseInt(pr[5])];
			this.powerPillIndices = new int[Integer.parseInt(pr[6])];
			this.junctionIndices = new int[Integer.parseInt(pr[7])];

			int nodeIndex = 0;
			int pillIndex = 0;
			int powerPillIndex = 0;
			int junctionIndex = 0;

			input = br.readLine();

			while (input != null) {
				String[] nd = input.split("\t");

				// TODO check scale here
				Node node = new Node(Integer.parseInt(nd[0]),
						(int) ScaleUtil.doNodeScaleW(Integer.parseInt(nd[1])),
						(int) ScaleUtil.doNodeScaleH(Integer.parseInt(nd[2])),
						Integer.parseInt(nd[7]), Integer.parseInt(nd[8]),
						new int[] { Integer.parseInt(nd[3]),
								Integer.parseInt(nd[4]),
								Integer.parseInt(nd[5]),
								Integer.parseInt(nd[6]) });

				graph[nodeIndex++] = node;

				if (node.pillIndex >= 0)
					pillIndices[pillIndex++] = node.nodeIndex;
				else if (node.powerPillIndex >= 0)
					powerPillIndices[powerPillIndex++] = node.nodeIndex;

				if (node.numNeighbouringNodes > 2)
					junctionIndices[junctionIndex++] = node.nodeIndex;

				input = br.readLine();
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void loadZipDistancesBin(String fileName) {
		long begin = System.currentTimeMillis();
		try {
			this.shortestPathDistances = Zip.getUnzipData(mContext.getResources().getAssets().open(fileName, AssetManager.ACCESS_RANDOM));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, "Loading Time: " + (end - begin));
	}
}