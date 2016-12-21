package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

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
	private List<PlayerAction> pendingActions = new ArrayList<>();

	public Game() {
		rows = new BlockRow[height];
		for (int i = 0; i < rows.length; i += 1) {
			rows[i] = new BlockRow(width);
		}
		// finish constructing
	}

	public void enqueueAction(PlayerAction action) {
		pendingActions.add(action);
	}

	public void tick() {
		for (PlayerAction action : pendingActions) {
			switch (action.type) {
				case MOVE:
				if (action.moveDirection == Direction.DOWN) {
					
				}
				break;
				case ROTATE:
				// rotate
			}
		}
		pendingActions.clear();

		if (one != null) {
			one.age();
		}
		if (two != null) {
			two.age();
		}
	}

	private boolean canRotate(Tetromino t) {
		return false;
	}

	// collision detection
	private boolean canMove(Tetromino t, Direction dir) {
		// (t.row + dr, t.col + dc) will be new position
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
			default:
			return false;
		}
		// check against static blocks
		for (int i = 0; i < t.blocks.length; i += 1) {
			for (int j = 0; j < t.blocks[0].length; j += 1) {
				int boardRow = t.row + i + dr;
				int boardCol = t.col + j + dc;
				if (
					t.blocks[i][j] != null
					&& boardRow >= 0
					&& boardRow < height
					&& boardCol >= 0
					&& boardCol < width
					&& rows[boardRow].get(boardCol) != null
				) {
					return false;
				}
			}
		}
		// check against opponent tetromino
		Tetromino o = t == one ? two : one;
		int r1 = Math.max(o.row, t.row + dr) - 2;
		int r2 = Math.min(o.row, t.row + dr) + 2;
		int c1 = Math.max(o.col, t.col + dc) - 2;
		int c2 = Math.min(o.col, t.col + dc) + 2;
		for (int r = r1; r < r2; r += 1) {
			for (int c = c1; c < c2; c += 1) {
				if (
					r >= 0
					&& r < height
					&& c >= 0
					&& c < width
					&& t.blocks[r - (t.row + dr) + 2][c - (t.col + dc) + 2] != null
					&& o.blocks[r - o.row + 2][c - o.col + 2] != null
				) {
					return false;
				}
			}
		}
		return true;
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
		for (int i = 0; i < t.blocks.length; i += 1) {
			for (int j = 0; j < t.blocks[0].length; j += 1) {
				int boardRow = t.row + i - 2;
				int boardCol = t.col + j - 2;
				if (
					t.blocks[i][j] != null
					&& boardRow >= 0
					&& boardRow < height
					&& boardCol >= 0
					&& boardCol < width
				) {
					rows[boardRow].set(boardCol, t.blocks[i][j]);
				}
			}
		}
	}

	// for rendering
	public void getBlocks(Block[][] blocks) {
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				blocks[i][j] = rows[i].get(j);
			}
		}
		for (Tetromino t : new Tetromino[] { one, two }) {
			for (int i = 0; i < t.blocks.length; i += 1) {
				for (int j = 0; j < t.blocks[0].length; j += 1) {
					int boardRow = t.row + i - 2;
					int boardCol = t.col + j - 2;
					if (
						t.blocks[i][j] != null
						&& boardRow >= 0
						&& boardRow < height
						&& boardCol >= 0
						&& boardCol < width
					) {
						blocks[i][j] = t.blocks[i][j];
					}
				}
			}
		}
	}
}
