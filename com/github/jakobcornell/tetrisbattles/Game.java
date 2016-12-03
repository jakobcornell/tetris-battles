package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class Game {
	public static final int width = 12, height = 30;
	public static final int ticksPerStep = 10;

	// direction blocks move
	public static enum Direction {
		UP, DOWN
	}

	private BlockRow[] rows;
	private Tetromino one;
	private Tetromino two;
	private Random tieBreaker = new Random();

	public Game() {
		rows = new BlockRow[height];
		for (int i = 0; i < rows.length; i += 1) {
			rows[i] = new BlockRow();
		}
	}

	public void tick() {
		boolean oneMove = /* player one moves */ || one.getAge() % ticksPerStep == 0;
		boolean twoMove = /* player two moves */ || two.getAge() % ticksPerStep == 0;
		if (oneMove && twoMove && collide(one, two)) {
			// break tie
		}
		else {
			if (oneMove) {
				if (canMove(one)) {
					move(one);
				}
				else {
					freeze(one);
					// one is stale
				}
			}
			if (twoMove) {
				if (canMove(two)) {
					move(two);
				}
				else {
					freeze(two);
					// two is stale
				}
			}
		}


		one.age();
		two.age();
	}

	private boolean canRotate(Tetromino t) {
	
	}
	private boolean canMove(Tetromino t) {
	
	}

	// one block separation collision check
	private boolean collide(Tetromino one, Tetromino two) {
		
	}

	private void rotate(Tetromino t) {
	
	}
	private void move(Tetromino t) {
		
	}

	// move tetromino blocks to the static board
	private void freeze(Tetromino t) {
		
	}

	public void getBlocks(Block[][] blocks) {
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				blocks[i][j] rows[i].get(j);
			}
		}
	}
}
