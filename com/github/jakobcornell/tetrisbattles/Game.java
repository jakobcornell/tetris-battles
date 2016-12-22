package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

public class Game {
	public static final int width = 12, height = 30;
	public static final int ticksPerStep = 30;

	// direction of block movement or game perspective (right-side-UP or upside-DOWN)
	public static enum Direction {
		RIGHT(0, 1), UP(-1, 0), LEFT(0, -1), DOWN(1, 0);

		public int dr, dc;
		private Direction(int dr, int dc) {
			this.dr = dr;
			this.dc = dc;
		}

		public Direction reverse() {
			switch (this) {
				case RIGHT:
				return LEFT;
				case UP:
				return DOWN;
				case LEFT:
				return RIGHT;
				default:
				return UP;
			}
		}
	}

	private BlockRow[] rows;
	private Set<Tetromino> activeTetrominos = new HashSet<Tetromino>();
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

	private List<Tetromino> newTetrominos = new ArrayList<>(2);
	public void tick() {
		for (PlayerAction action : pendingActions) {
			Tetromino subject = null;
			for (Tetromino t : activeTetrominos) {
				if (t.movement.reverse() == action.perspective) {
					subject = t;
					break;
				}
			}
			if (subject == null) {
				break;
			}
			switch (action.type) {
				case MOVE:
				processMove(subject, action.moveDirection);
				break;
				case ROTATE:
				if (canRotate(subject)) {
					rotate(subject);
				}
			}
		}
		pendingActions.clear();

		for (Tetromino t : activeTetrominos.toArray(new Tetromino[0])) {
			t.age += 1;
			if (t.age % ticksPerStep == 0) {
				processMove(t, t.movement);
			}
		}

		activeTetrominos.addAll(newTetrominos);
		newTetrominos.clear();
	}

	private void processMove(Tetromino t, Direction d) {
		int newR = t.row + d.dr;
		int newC = t.col + d.dc;
		for (int rOff = 0; rOff < t.blocks.length; rOff += 1) {
			for (int cOff = 0; cOff < t.blocks[r].length; cOff += 1) {
				int boardRow = newR + rOff;
				int boardCol = newC + cOff;
				// check against board edges
				if (
					boardRow < 0
					|| boardRow >= height
					|| boardCol < 0
					|| boardCol >= width
				) {
					// TODO: handle
				}
				// check against static blocks
				if (t.blocks[rOff][cOff] != null && rows[boardRow].get(boardCol) != null) {
					// TODO: handle
				}
				// check against other tetrominos
				for (Tetromino other : activeTetrominos.toArray(new Tetromino[0])) {
					if (other != t) {
						int r1 = Math.max(other.row, newR);
						int r2 = Math.min(other.row, newR) + 4;
						int c1 = Math.max(other.col, newC);
						int c2 = Math.min(other.col, newC) + 4;
						for (int r = r1; r < r2; r += 1) {
							for (int c = c1; c < c2; c += 1) {
								if (
									t.blocks[r - newR][c - newC] != null
									&& other.blocks[r - other.row][c - other.col] != null
								) {
									// TODO: handle
								}
							}
						}
					}
				}
			}
		}
		// no collisions
		t.row = newR;
		t.col = newC;
	}

	private boolean canRotate(Tetromino t) {
		// TODO implement
	}

	private void rotate(Tetromino t) {
		// TODO implement
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

	private Tetromino spawn(Direction movement) {
		Tetromino t = new Tetromino(movement);
		// TODO Finish setting up t
		return t;
	}

	// for rendering
	public void getBlocks(Block[][] blocks) {
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				blocks[i][j] = rows[i].get(j);
			}
		}
		for (Tetromino t : activeTetrominos) {
			for (int i = 0; i < t.blocks.length; i += 1) {
				for (int j = 0; j < t.blocks[0].length; j += 1) {
					int boardRow = t.row + i - 2;
					int boardCol = t.col + j - 2;
					if (t.blocks[i][j] != null) {
						blocks[i][j] = t.blocks[i][j];
					}
				}
			}
		}
	}
}
