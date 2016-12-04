package com.github.jakobcornell.tetrisbattles;

public class Tetromino {
	public Block[][] blocks;
	public int row, col;
	public final Game.Direction movement;
	private int age; // increment each frame: trigger default movement on specific value

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
}
