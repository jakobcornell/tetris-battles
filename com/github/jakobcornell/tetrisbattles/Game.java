package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.HashSet;

public class Game {
	public static final int width = 12, height = 30;
	
	// direction blocks move
	public static enum Direction {
		UP, DOWN
	}

	private BoardRow[] rows;
	private Set<Tetromino> activeTets;

	public Game() {
		rows = new BoardRow[height];
		for (int i = 0; i < rows.length; i += 1) {
			rows[i] = new BoardRow();
		}
	}

	public void tick() {
		
	}

	public void getBlocks(Block[][] blocks) {
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				blocks[i][j] rows[i].get(j);
			}
		}
	}
}
