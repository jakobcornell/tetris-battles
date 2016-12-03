package com.github.jakobcornell.tetrisbattles;

public class Tetromino {
	private Block[][] blocks;
	private int age; // increment each frame: trigger default movement on specific value

	public Tetromino() {
		blocks = new Block[4][4];
	}

	public int getAge() {
		return age;
	}

	public void age() {
		age += 1;
	}
}
