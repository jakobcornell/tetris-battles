package com.github.jakobcornell.tetrisbattles;

public class Tetromino {
	private Block[][] blocks;
	public final Game.Direction movement;
	private int age; // increment each frame: trigger default movement on specific value

	public Tetromino(Game.Direction movement) {
		blocks = new Block[4][4];
		this.movement = movement;
		age = 0;
	}

	public int getAge() {
		return age;
	}

	public void age() {
		age += 1;
	}
}
