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
		for (int rOff = 0; rOff < t.blocks.length; rOff += 1) {
			for (int cOff = 0; cOff < t.blocks[rOff].length; cOff += 1) {
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
						int r2 = Math.min(other.row, newR) + 4;
						int c1 = Math.max(other.col, newC);
						int c2 = Math.min(other.col, newC) + 4;
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
		switch (t.rotationMode) {
		case EVEN:
			
			// check against board edges and existing blocks
			for (int tr = 0; tr < 4; tr += 1) {
				for (int tc = 0; tc < 4; tc += 1) {
					if (t.blocks[tr][tc] != null) {
						int br = t.row + tc;
						int bc = t.col + 3 - tr;
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
					int r2 = Math.min(other.row, t.row) + 4;
					int c1 = Math.max(other.col, t.col);
					int c2 = Math.min(other.col, t.col) + 4;
					for (int r = r1; r < r2; r += 1) {
						for (int c = c1; c < c2; c += 1) {
							int tRow = 3 - (c - t.col);
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
		case ODD:
			
			// check against board edges and existing blocks
			for (int tr = 1; tr < 4; tr += 1) {
				for (int tc = 0; tc < 3; tc += 1) {
					if (t.blocks[tr][tc] != null) {
						int br = t.row + tc + 1;
						int bc = t.col + (2 - (tr - 1));
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
					int r1 = Math.max(other.row, t.row);
					int r2 = Math.min(other.row + 4, t.row + 3);
					int c1 = Math.max(other.col, t.col + 1);
					int c2 = Math.min(other.col + 4, t.col + 4);
					for (int r = r1; r < r2; r += 1) {
						for (int c = c1; c < c2; c += 1) {
							int tRow = 2 - (c - t.col);
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
		throw new IllegalArgumentException("rotationMode is null or something");
	}

	private void rotate(Tetromino t) {
		Block[][] blocks = new Block[4][4];
		switch (t.rotationMode) {
		case EVEN:
			for (int r = 0; r < 4; r += 1) {
				for (int c = 0; c < 4; c += 1) {
					blocks[c][3 - r] = t.blocks[r][c];
				}
			}
			break;
		case ODD:
			for (int r = 1; r < 4; r += 1) {
				for (int c = 0; c < 3; c += 1) {
					blocks[c + 1][2 - (r - 1)] = t.blocks[r][c];
				}
			}
			break;
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
		for (int r = 0; r < t.blocks.length; r += 1) {
			if (rows[r].isFull()) {
				switch (players.get(t).playDirection) {
				case UP:
					while (r < height - 1) {
						rows[r] = rows[r + 1];
						r += 1;
					}
					break;
				case DOWN:
					while (r > 0) {
						rows[r] = rows[r - 1];
						r -= 1;
					}
				}
				rows[r] = new BlockRow(width);
			}
		}
	}

	private Tetromino spawn(Player owner) {
		Tetromino t = new Tetromino(owner.playDirection, owner.color);
		t.col = width / 2 - 2;
		int i;
		switch (owner.playDirection) {
		case DOWN:
			for (i = 0; i < 16; i += 1) {
				if (t.blocks[i / 4][i % 4] != null) {
					break;
				}
			}
			t.row = -(i / 4);
			break;
		case UP:
			for (i = 15; i >= 0; i -= 1) {
				if (t.blocks[i / 4][i % 4] != null) {
					break;
				}
			}
			t.row = height - 1 - (i / 4);
			break;
		}

		// check whether placement is possible
		for (int r = 0; r < 4; r += 1) {
			for (int c = 0; c < 4; c += 1) {
				if (t.blocks[r][c] != null && rows[t.row + r].get(t.col + c) != null) {
					return null;
				}
			}
		}
		for (Tetromino other : tetrominos) {
			int r1 = Math.max(t.row, other.row);
			int r2 = Math.min(t.row, other.row) + 4;
			int c1 = Math.max(t.col, other.col);
			int c2 = Math.min(t.col, other.col) + 4;
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
			for (int r = 0; r < t.blocks.length; r += 1) {
				for (int c = 0; c < t.blocks[0].length; c += 1) {
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
