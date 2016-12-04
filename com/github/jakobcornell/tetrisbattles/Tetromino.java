package com.github.jakobcornell.tetrisbattles;

public class Tetromino {
	public Block[][] blocks;
	public int row, col;
	public final Game.Direction movement;
	private int age = 0; // increment each frame: trigger default movement on specific value

	public Tetromino(Game.Direction movement) {
		blocks = new Block[4][4];
		this.movement = movement;
		age = 0;
		// finish constructing
	}

	public int getAge() {
		return age;
	}

	public void age() {
		age += 1;
	}

	public static Tetromino getRandom(Game.Direction movement, int row, int col) {
		Tetromino r = new Tetromino(movement);
		r.row = row;
		r.col = col;
		int shade = 128;
		r.blocks[1][1] = new Block();
		r.blocks[1][2] = new Block();
		r.blocks[2][1] = new Block();
		r.blocks[2][2] = new Block();
		return r;
	}
}
