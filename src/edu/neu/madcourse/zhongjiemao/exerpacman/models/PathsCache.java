package edu.neu.madcourse.zhongjiemao.exerpacman.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.GameEngine;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

/**
 * Pre-computes paths for more efficient execution of the game.
 */
public class PathsCache {
	public HashMap<Integer, Integer> junctionIndexConverter;
	public DNode[] nodes;
	public Junction[] junctions;
	public GameEngine game;

	public PathsCache(int mazeIndex) {
		junctionIndexConverter = new HashMap<Integer, Integer>();

//		this.game = new GameEngine(0, mazeIndex);
		Maze m = game.getCurrentMaze();

		int[] jctIndices = m.junctionIndices;

		for (int i = 0; i < jctIndices.length; i++)
			junctionIndexConverter.put(jctIndices[i], i);

		nodes = assignJunctionsToNodes(game);
		junctions = junctionDistances(game);

		for (int i = 0; i < junctions.length; i++)
			junctions[i].computeShortestPaths();
	}

	// for Ms Pac-Man
	public int[] getPathFromA2B(int a, int b) {
		// not going anywhere
		if (a == b)
			return new int[] {};

		// junctions near the source
		ArrayList<JunctionData> closestFromJunctions = nodes[a].closestJunctions;

		// if target is on the way to junction, then we are done
		for (int w = 0; w < closestFromJunctions.size(); w++)
			for (int i = 0; i < closestFromJunctions.get(w).path.length; i++)
				if (closestFromJunctions.get(w).path[i] == b) {
					int[] newPath = new int[i + 1];
					System.arraycopy(closestFromJunctions.get(w).path, 0,
							newPath, 0, i + 1);
					return newPath;
					// return Arrays.copyOf(closestFromJunctions.get(w).path, i
					// + 1);
				}

		// junctions near the target
		ArrayList<JunctionData> closestToJunctions = nodes[b].closestJunctions;

		int minFrom = -1;
		int minTo = -1;
		int minDistance = Integer.MAX_VALUE;
		int[] shortestPath = null;

		for (int i = 0; i < closestFromJunctions.size(); i++) {
			for (int j = 0; j < closestToJunctions.size(); j++) {
				// to the first junction
				int distance = closestFromJunctions.get(i).path.length;
				// junction to junction
				int[] tmpPath = junctions[junctionIndexConverter
						.get(closestFromJunctions.get(i).nodeID)].paths[junctionIndexConverter
						.get(closestToJunctions.get(j).nodeID)]
						.get(MOVE.NEUTRAL);
				distance += tmpPath.length;
				// to the second junction
				distance += closestToJunctions.get(j).path.length;

				if (distance < minDistance) {
					minDistance = distance;
					minFrom = i;
					minTo = j;
					shortestPath = tmpPath;
				}
			}
		}

		return concat(closestFromJunctions.get(minFrom).path, shortestPath,
				closestToJunctions.get(minTo).reversePath);
	}

	/** ghosts */

	/**
	 * To be made more efficient shortly.
	 * 
	 * @param a
	 * @param b
	 * @param lastMoveMade
	 * @return
	 */
	public int getPathDistanceFromA2B(int a, int b, MOVE lastMoveMade) {
		return getPathFromA2B(a, b, lastMoveMade).length;
	}

	public int[] getPathFromA2B(int a, int b, MOVE lastMoveMade) {
		// not going anywhere
		if (a == b)
			return new int[] {};

		// first, go to closest junction (there is only one since we can't
		// reverse)
		JunctionData fromJunction = nodes[a].getNearestJunction(lastMoveMade);

		// if target is on the way to junction, then we are done
		for (int i = 0; i < fromJunction.path.length; i++)
			if (fromJunction.path[i] == b) {
				int[] newPath = new int[i + 1];
				System.arraycopy(fromJunction.path, 0, newPath, 0, i + 1);
				return newPath;
				// return Arrays.copyOf(fromJunction.path, i + 1);
			}

		// we have reached a junction, fromJunction, which we entered with
		// moveEnteredJunction
		int junctionFrom = fromJunction.nodeID;
		int junctionFromId = junctionIndexConverter.get(junctionFrom);
		// if we are at a junction, consider last move instead
		MOVE moveEnteredJunction = fromJunction.lastMove.equals(MOVE.NEUTRAL) ? lastMoveMade
				: fromJunction.lastMove;

		// now we need to get the 1 or 2 target junctions that enclose the
		// target point
		ArrayList<JunctionData> junctionsTo = nodes[b].closestJunctions;

		int minDist = Integer.MAX_VALUE;
		int[] shortestPath = null;
		int closestJunction = -1;

		boolean onTheWay = false;

		for (int q = 0; q < junctionsTo.size(); q++) {
			int junctionToId = junctionIndexConverter
					.get(junctionsTo.get(q).nodeID);

			if (junctionFromId == junctionToId) {
				if (!game.getMoveToMakeToReachDirectNeighbour(junctionFrom,
						junctionsTo.get(q).reversePath[0]).equals(
						moveEnteredJunction.opposite())) {
					int[] reversepath = junctionsTo.get(q).reversePath;
					int cutoff = -1;

					for (int w = 0; w < reversepath.length; w++)
						if (reversepath[w] == b)
							cutoff = w;

					shortestPath = new int[cutoff + 1];
					System.arraycopy(reversepath, 0, shortestPath, 0,
							cutoff + 1);
					// shortestPath = Arrays.copyOf(reversepath, cutoff+1);
					minDist = shortestPath.length;
					closestJunction = q;
					onTheWay = true;
				}
			} else {
				EnumMap<MOVE, int[]> paths = junctions[junctionFromId].paths[junctionToId];
				Set<MOVE> set = paths.keySet();

				for (MOVE move : set) {
					if (!move.opposite().equals(moveEnteredJunction)
							&& !move.equals(MOVE.NEUTRAL)) {
						int[] path = paths.get(move);

						// need to take distance from toJunction to target into
						// account
						if (path.length + junctionsTo.get(q).path.length < minDist) {
							minDist = path.length
									+ junctionsTo.get(q).path.length;
							shortestPath = path;
							closestJunction = q;
							onTheWay = false;
						}
					}
				}
			}
		}

		if (!onTheWay)
			return concat(fromJunction.path, shortestPath,
					junctionsTo.get(closestJunction).reversePath);
		else
			return concat(fromJunction.path, shortestPath);
		// return concat(fromJunction.path,
		// junctionsTo.get(closestJunction).reversePath);
	}

	private Junction[] junctionDistances(GameEngine game) {
		Maze m = game.getCurrentMaze();
		int[] indices = m.junctionIndices;

		Junction[] junctions = new Junction[indices.length];

		// from
		for (int q = 0; q < indices.length; q++) {
			// all possible moves
			MOVE[] possibleMoves = m.graph[indices[q]].allPossibleMoves
					.get(MOVE.NEUTRAL);

			junctions[q] = new Junction(q, indices[q], indices.length);

			// to (we need to include distance to itself)
			for (int z = 0; z < indices.length; z++) {
				for (int i = 0; i < possibleMoves.length; i++) {
					int neighbour = game.getNeighbour(indices[q],
							possibleMoves[i]);
					int[] p = m.astar.computePathsAStar(neighbour, indices[z],
							possibleMoves[i], game);
					m.astar.resetGraph();

					junctions[q].addPath(z, possibleMoves[i], p);
				}
			}
		}

		return junctions;
	}

	private DNode[] assignJunctionsToNodes(GameEngine game) {
		Maze m = game.getCurrentMaze();
		int numNodes = m.graph.length;

		DNode[] allNodes = new DNode[numNodes];

		for (int i = 0; i < numNodes; i++) {
			boolean isJunction = game.isJunction(i);
			allNodes[i] = new DNode(i, isJunction);

			if (!isJunction) {
				MOVE[] possibleMoves = m.graph[i].allPossibleMoves
						.get(MOVE.NEUTRAL);

				for (int j = 0; j < possibleMoves.length; j++) {
					ArrayList<Integer> path = new ArrayList<Integer>();

					MOVE lastMove = possibleMoves[j];
					int currentNode = game.getNeighbour(i, lastMove);
					path.add(currentNode);

					while (!game.isJunction(currentNode)) {
						MOVE[] newPossibleMoves = game
								.getPossibleMoves(currentNode);

						for (int q = 0; q < newPossibleMoves.length; q++)
							if (newPossibleMoves[q].opposite() != lastMove) {
								lastMove = newPossibleMoves[q];
								break;
							}

						currentNode = game.getNeighbour(currentNode, lastMove);
						path.add(currentNode);
					}

					int[] array = new int[path.size()];

					for (int w = 0; w < path.size(); w++)
						array[w] = path.get(w);

					allNodes[i].addPath(array[array.length - 1],
							possibleMoves[j], i, array, lastMove);
				}
			}
		}

		return allNodes;
	}

	private int[] concat(int[]... arrays) {
		int totalLength = 0;

		for (int i = 0; i < arrays.length; i++)
			totalLength += arrays[i].length;

		int[] fullArray = new int[totalLength];

		int index = 0;

		for (int i = 0; i < arrays.length; i++)
			for (int j = 0; j < arrays[i].length; j++)
				fullArray[index++] = arrays[i][j];

		return fullArray;
	}
}

class JunctionData {
	public int nodeID, nodeStartedFrom;
	public MOVE firstMove, lastMove;
	public int[] path, reversePath;

	public JunctionData(int nodeID, MOVE firstMove, int nodeStartedFrom,
			int[] path, MOVE lastMove) {
		this.nodeID = nodeID;
		this.nodeStartedFrom = nodeStartedFrom;
		this.firstMove = firstMove;
		this.path = path;
		this.lastMove = lastMove;

		if (path.length > 0)
			this.reversePath = getReversePath(path);
		else
			reversePath = new int[] {};
	}

	public int[] getReversePath(int[] path) {
		int[] reversePath = new int[path.length];

		for (int i = 1; i < reversePath.length; i++)
			reversePath[i - 1] = path[path.length - 1 - i];

		reversePath[reversePath.length - 1] = nodeStartedFrom;

		return reversePath;
	}

	public String toString() {
		return nodeID + "\t" + firstMove.toString() + "\t"
				+ Arrays.toString(path);
	}
}

class DNode {
	public int nodeID;
	public ArrayList<JunctionData> closestJunctions;
	public boolean isJunction;

	public DNode(int nodeID, boolean isJunction) {
		this.nodeID = nodeID;
		this.isJunction = isJunction;

		this.closestJunctions = new ArrayList<JunctionData>();

		if (isJunction)
			closestJunctions.add(new JunctionData(nodeID, MOVE.NEUTRAL, nodeID,
					new int[] {}, MOVE.NEUTRAL));
	}

	public int[] getPathToJunction(MOVE lastMoveMade) {
		if (isJunction)
			return new int[] {};

		for (int i = 0; i < closestJunctions.size(); i++)
			if (!closestJunctions.get(i).firstMove.equals(lastMoveMade
					.opposite()))
				return closestJunctions.get(i).path;

		return null;
	}

	public JunctionData getNearestJunction(MOVE lastMoveMade) {
		if (isJunction)
			return closestJunctions.get(0);

		int minDist = Integer.MAX_VALUE;
		int bestIndex = -1;

		for (int i = 0; i < closestJunctions.size(); i++)
			if (!closestJunctions.get(i).firstMove.equals(lastMoveMade
					.opposite())) {
				int newDist = closestJunctions.get(i).path.length;

				if (newDist < minDist) {
					minDist = newDist;
					bestIndex = i;
				}
			}

		if (bestIndex != -1)
			return closestJunctions.get(bestIndex);
		else
			return null;
	}

	public void addPath(int junctionID, MOVE firstMove, int nodeStartedFrom,
			int[] path, MOVE lastMove) {
		closestJunctions.add(new JunctionData(junctionID, firstMove,
				nodeStartedFrom, path, lastMove));
	}

	public String toString() {
		return "" + nodeID + "\t" + isJunction;
	}
}

/**
 * for each junction, stores paths to all other junctions for all directions
 * 
 * @author robins
 * 
 */
class Junction {
	public int jctId, nodeId;
	public EnumMap<MOVE, int[]>[] paths;

	public void computeShortestPaths() {
		MOVE[] moves = MOVE.values();

		for (int i = 0; i < paths.length; i++) {
			if (i == jctId)
				paths[i].put(MOVE.NEUTRAL, new int[] {});
			else {
				int distance = Integer.MAX_VALUE;
				int[] path = null;

				for (int j = 0; j < moves.length; j++) {
					if (paths[i].containsKey(moves[j])) {
						int[] tmp = paths[i].get(moves[j]);

						if (tmp.length < distance) {
							distance = tmp.length;
							path = tmp;
						}
					}
				}

				paths[i].put(MOVE.NEUTRAL, path);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Junction(int jctId, int nodeId, int numJcts) {
		this.jctId = jctId;
		this.nodeId = nodeId;

		paths = new EnumMap[numJcts];

		for (int i = 0; i < paths.length; i++)
			paths[i] = new EnumMap<MOVE, int[]>(MOVE.class);
	}

	/**
	 * store the shortest path given the last move made
	 * 
	 * @param toJunction
	 * @param firstMoveMade
	 * @param path
	 */
	public void addPath(int toJunction, MOVE firstMoveMade, int[] path) {
		paths[toJunction].put(firstMoveMade, path);
	}

	public String toString() {
		return jctId + "\t" + nodeId;
	}
}