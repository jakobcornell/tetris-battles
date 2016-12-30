package com.github.jakobcornell.tetrisbattles;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Game {
	public static final int width = 12, height = 30;
	public static final int ticksPerStep = 30;
	protected final Set<View> views = new HashSet<>(2);
	private BlockRow[] rows;
	private Map<Tetromino, Player> players = new HashMap<>();
	private Set<Tetromino> tetrominos = players.keySet();
	private List<PlayerAction> pendingActions = new ArrayList<>();
	private boolean isFinished = false;
	private Player winner;

	public Game(Player[] players) {
		rows = new BlockRow[height];
		for (int r = 0; r < rows.length; r += 1) {
			rows[r] = new BlockRow(width);
		}
		for (Player p : players) {
			this.players.put(spawn(p), p);
		}
	}

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
			case DOWN:
				return UP;
			}
			return null;
		}
	}

	public static enum Color {
		RED, BLUE;
	}

	public static interface View {
		public void refresh();
	}

	public void registerView(View v) {
		views.add(v);
	}

	public void unregisterView(View v) {
		views.remove(v);
	}

	public void enqueueAction(PlayerAction action) {
		pendingActions.add(action);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public Player getWinner() {
		return winner;
	}

	public void tick() {
		boolean needsRefresh = false;
		for (PlayerAction action : pendingActions) {
			Tetromino subject = null;
			for (Tetromino t : tetrominos) {
				if (!t.isFrozen && players.get(t).perspective == action.perspective) {
					subject = t;
					break;
				}
			}
			if (subject == null) {
				break;
			}
			needsRefresh = true;
			switch (action.type) {
			case MOVE:
				processMove(subject, action.moveDirection);
				break;
			case ROTATE:
				if (canRotate(subject)) {
					rotate(subject);
				}
				break;
			}
		}
		pendingActions.clear();

		for (Tetromino t : tetrominos) {
			t.age += 1;
			if (t.age % ticksPerStep == 0) {
				needsRefresh = true;
				processMove(t, players.get(t).playDirection);
			}
		}

		for (Tetromino t : tetrominos.toArray(new Tetromino[0])) {
			if (t.isFrozen) {
				needsRefresh = true;
				freeze(t);
				Player owner = players.get(t);
				tetrominos.remove(t);
				Tetromino newTetromino = spawn(owner);
				if (newTetromino == null) {
					isFinished = true;
					
					// assumes two players;
					for (Player p : players.values()) {
						if (p != owner) {
							winner = p;
							break;
						}
					}
				} else {
					players.put(newTetromino, owner);
				}
			}
		}

		if (needsRefresh) {
			for (View v : views) {
				v.refresh();
			}
		}
	}

	private void processMove(Tetromino t, Direction d) {
		int newR = t.row + d.dr;
		int newC = t.col + d.dc;
		for (int rOff = 0; rOff < t.dimension; rOff += 1) {
			for (int cOff = 0; cOff < t.dimension; cOff += 1) {
				int boardRow = newR + rOff;
				int boardCol = newC + cOff;

				// check against board edges
				if (t.blocks[rOff][cOff] != null) {
					if (
						d == Direction.DOWN && boardRow >= height
						|| d == Direction.UP && boardRow < 0
					) {
						t.isFrozen = true;
						return;
					} else if (boardCol < 0 || boardCol >= width) {
						return;
					}
				}

				// check against static blocks
				if (t.blocks[rOff][cOff] != null && rows[boardRow].get(boardCol) != null) {
					if (d == players.get(t).playDirection) {
						t.isFrozen = true;
					}
					return;
				}
				
				// check against other tetrominos
				for (Tetromino other : tetrominos) {
					if (other != t) {
						int r1 = Math.max(other.row, newR);
						int r2 = Math.min(other.row + other.dimension, newR + t.dimension);
						int c1 = Math.max(other.col, newC);
						int c2 = Math.min(other.col + other.dimension, newC + t.dimension);
						for (int r = r1; r < r2; r += 1) {
							for (int c = c1; c < c2; c += 1) {
								if (
									t.blocks[r - newR][c - newC] != null
									&& other.blocks[r - other.row][c - other.col] != null
								) {
									if (d == players.get(t).playDirection) {
										t.isFrozen = true;
										other.isFrozen = true;
									}
									return;
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

		// check against board edges and existing blocks
		for (int tr = 0; tr < t.dimension; tr += 1) {
			for (int tc = 0; tc < t.dimension; tc += 1) {
				if (t.blocks[tr][tc] != null) {
					int br = t.row + tc;
					int bc = t.col + t.dimension - 1 - tr;
					boolean validRow = br >= 0 && br < height;
					boolean validCol = bc >= 0 && bc < width;
					if (!validRow || !validCol || rows[br].get(bc) != null) {
						return false;
					}
				}
			}
		}

		// check against other tetrominos
		for (Tetromino other : tetrominos) {
			if (other != t) {
				
				// these define the range of possible overlap
				int r1 = Math.max(other.row, t.row);
				int r2 = Math.min(other.row + other.dimension, t.row + t.dimension);
				int c1 = Math.max(other.col, t.col);
				int c2 = Math.min(other.col + other.dimension, t.col + t.dimension);
				
				for (int r = r1; r < r2; r += 1) {
					for (int c = c1; c < c2; c += 1) {
						int tRow = t.dimension - 1 - (c - t.col);
						int tCol = r - t.row;
						int oRow = r - other.row;
						int oCol = c - other.col;
						if (t.blocks[tRow][tCol] != null && other.blocks[oRow][oCol] != null) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void rotate(Tetromino t) {
		Block[][] blocks = new Block[t.dimension][t.dimension];
		for (int r = 0; r < t.dimension; r += 1) {
			for (int c = 0; c < t.dimension; c += 1) {
				blocks[c][t.dimension - 1 - r] = t.blocks[r][c];
			}
		}
		t.blocks = blocks;
	}

	// move tetromino blocks to the static board
	private void freeze(Tetromino t) {
		for (int r = 0; r < t.blocks.length; r += 1) {
			int boardRow = t.row + r;
			if (boardRow >= 0 && boardRow < height) {
				for (int c = 0; c < t.blocks[0].length; c += 1) {
					int boardCol = t.col + c;
					if (t.blocks[r][c] != null && boardCol >= 0 && boardCol < width) {
						rows[boardRow].set(boardCol, t.blocks[r][c]);
					}
				}
			}
		}

		// delete filled rows
		int src, dest;
		switch (players.get(t).playDirection) {
		case UP:
			src = Math.max(t.row, 0);
			dest = src;
			while (src < height) {
				if (!rows[src].isFull()) {
					rows[dest] = rows[src];
					dest += 1;
				}
				src += 1;
			}
			while (dest < height) {
				rows[dest] = new BlockRow(width);
				dest += 1;
			}
			break;
		case DOWN:
			src = Math.min(t.row, height - 1);
			dest = src;
			while (src >= 0) {
				if (!rows[src].isFull()) {
					rows[dest] = rows[src];
					dest -= 1;
				}
				src -= 1;
			}
			while (dest >= 0) {
				rows[dest] = new BlockRow(width);
				dest += 1;
			}
			break;
		}
	}

	private Tetromino spawn(Player owner) {
		Tetromino t = new Tetromino(owner.color);
		int d = t.dimension;

		// apply appropriate position and rotation
		int i;
		switch (owner.playDirection) {
		case DOWN:
			for (i = 0; i < d * d; i += 1) {
				if (t.blocks[i / d][i % d] != null) {
					break;
				}
			}
			t.row = -(i / d);
			t.col = width / 2 - d + d / 2;
			break;
		case UP:
			rotate(t);
			rotate(t);
			for (i = d * d - 1; i >= 0; i -= 1) {
				if (t.blocks[i / d][i % d] != null) {
					break;
				}
			}
			t.row = height - 1 - (i / d);
			t.col = width / 2 - d / 2;
			break;
		}

		// check whether placement is possible
		for (int r = 0; r < d; r += 1) {
			for (int c = 0; c < d; c += 1) {
				if (t.blocks[r][c] != null && rows[t.row + r].get(t.col + c) != null) {
					return null;
				}
			}
		}
		for (Tetromino other : tetrominos) {
			int r1 = Math.max(t.row, other.row);
			int r2 = Math.min(t.row + t.dimension, other.row + other.dimension);
			int c1 = Math.max(t.col, other.col);
			int c2 = Math.min(t.col + t.dimension, other.col + other.dimension);
			for (int r = r1; r < r2; r += 1) {
				for (int c = c1; c < c2; c += 1) {
					int tr = r - t.row;
					int tc = c - t.col;
					int or = r - other.row;
					int oc = c - other.col;
					if (t.blocks[tr][tc] != null && other.blocks[or][oc] != null) {
						return null;
					}
				}
			}
		}
		
		return t;
	}

	// for rendering
	public void getBlocks(Block[][] blocks) {
		for (int r = 0; r < height; r += 1) {
			for (int c = 0; c < width; c += 1) {
				blocks[r][c] = rows[r].get(c);
			}
		}
		for (Tetromino t : tetrominos) {
			for (int r = 0; r < t.dimension; r += 1) {
				for (int c = 0; c < t.dimension; c += 1) {
					int boardRow = t.row + r;
					int boardCol = t.col + c;
					if (t.blocks[r][c] != null) {
						blocks[boardRow][boardCol] = t.blocks[r][c];
					}
				}
			}
		}
	}
}
