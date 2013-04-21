package edu.neu.madcourse.zhongjiemao.exerpacman.models;

import java.util.EnumMap;

import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants.MOVE;

/**
 * The class is a data structure used to represent a node in the graph.
 */
public final class Node {
	public final int x, y, nodeIndex, pillIndex, powerPillIndex, numNeighbouringNodes;
	public final EnumMap<MOVE, Integer> neighbourhood = new EnumMap<MOVE, Integer>(MOVE.class);
	public EnumMap<MOVE, MOVE[]> allPossibleMoves = new EnumMap<MOVE, MOVE[]>(MOVE.class);
	public EnumMap<MOVE, int[]> allNeighbouringNodes = new EnumMap<MOVE, int[]>(MOVE.class);
	public EnumMap<MOVE, EnumMap<MOVE, Integer>> allNeighbourhoods = new EnumMap<MOVE, EnumMap<MOVE, Integer>>(MOVE.class);

	/**
	 * Instantiates a new node.
	 */
	public Node(int nodeIndex, int x, int y, int pillIndex, int powerPillIndex,
			int[] _neighbourhood) {
		this.nodeIndex = nodeIndex;
		this.x = x;
		this.y = y;
		this.pillIndex = pillIndex;
		this.powerPillIndex = powerPillIndex;

		MOVE[] moves = MOVE.values();

		for (int i = 0; i < _neighbourhood.length; i++)
			if (_neighbourhood[i] != -1)
				neighbourhood.put(moves[i], _neighbourhood[i]);

		numNeighbouringNodes = neighbourhood.size();

		for (int i = 0; i < moves.length; i++)
			if (neighbourhood.containsKey(moves[i])) {
				EnumMap<MOVE, Integer> tmp = new EnumMap<MOVE, Integer>(
						neighbourhood);
				tmp.remove(moves[i]);
				allNeighbourhoods.put(moves[i].opposite(), tmp);
			}

		allNeighbourhoods.put(MOVE.NEUTRAL, neighbourhood);

		int[] neighbouringNodes = new int[numNeighbouringNodes];
		MOVE[] possibleMoves = new MOVE[numNeighbouringNodes];

		int index = 0;

		for (int i = 0; i < moves.length; i++)
			if (neighbourhood.containsKey(moves[i])) {
				neighbouringNodes[index] = neighbourhood.get(moves[i]);
				possibleMoves[index] = moves[i];
				index++;
			}

		// check all moves
		for (int i = 0; i < moves.length; i++) {
			// move is part of neighborhood
			if (neighbourhood.containsKey(moves[i].opposite())) {
				int[] tmpNeighbouringNodes = new int[numNeighbouringNodes - 1];
				MOVE[] tmpPossibleMoves = new MOVE[numNeighbouringNodes - 1];
				index = 0;

				// add all moves to neighborhood except the one above
				for (int j = 0; j < moves.length; j++) {
					if (moves[j] != moves[i].opposite()
							&& neighbourhood.containsKey(moves[j])) {
						tmpNeighbouringNodes[index] = neighbourhood
								.get(moves[j]);
						tmpPossibleMoves[index] = moves[j];
						index++;
					}
				}

				allNeighbouringNodes.put(moves[i], tmpNeighbouringNodes);
				allPossibleMoves.put(moves[i], tmpPossibleMoves);
			}
		}

		allNeighbouringNodes.put(MOVE.NEUTRAL, neighbouringNodes);
		allPossibleMoves.put(MOVE.NEUTRAL, possibleMoves);
	}
}