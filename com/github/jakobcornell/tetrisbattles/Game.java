package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class Game {
	public static final int width = 12, height = 30;
	public static final int ticksPerStep = 10;

	// direction blocks move
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	private BlockRow[] rows;
	private Tetromino one;
	private Tetromino two;
	private Random tieBreaker = new Random();
	private List<PlayerAction> pendingActions;

	public Game() {
		rows = new BlockRow[height];
		for (int i = 0; i < rows.length; i += 1) {
			rows[i] = new BlockRow();
		}
		// finish constructing
	}

	public enqueueAction(PlayerAction action) {
		pendingActions.add(action);
	}

	public void tick() {
		for (PlayerAction action : pendingActions) {
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
		}

		one.age();
		two.age();
	}

	private boolean canRotate(Tetromino t) {
	
	}

	private boolean canMove(Tetromino t, Direction dir) {
		int dr, dc;
		switch (dir) {
			case UP:
			dr = -1;
			dc = 0;
			break;
			case DOWN:
			dr = 1;
			dc = 0;
			break;
			case LEFT:
			dr = 0;
			dc = -1;
			break;
			case RIGHT:
			dr = 0;
			dc = 1;
		}
		for (int i = 0; i < t.blocks.length; i += 1) {
			for (int j = 0; j < t.blocks[0].length; j += 1) {
				int boardRow = t.row + i + dr;
				int boardCol = t.col + j + dc;
				if (
					t.blocks[i][j] != null &&
					boardRow >= 0 &&
					boardRow < height &&
					boardCol >= 0 &&
					boardCol < width &&
					rows[boardRow].get(boardCol) != null
				) {
					return false;
				}
			}
		}
		return true;
	}

	// one block separation collision check
	private boolean collide(Tetromino one, Tetromino two) {
		if (
	}

	private void rotate(Tetromino t) {
	
	}

	private void move(Tetromino t, Direction dir) {
		switch (dir) {
			case UP:
			t.row -= 1;
			break;
			case DOWN:
			t.row += 1;
			break;
			case LEFT:
			t.col -= 1;
			break;
			case RIGHT:
			t.col += 1;
		}
	}

	// move tetromino blocks to the static board
	private void freeze(Tetromino t) {
		
	}

	public void getBlocks(Block[][] blocks) {
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				blocks[i][j] = rows[i].get(j);
			}
		}
	}
}
