package com.github.jakobcornell.tetrisbattles;

public class BlockRow {
	private Block[] blocks;
	private boolean full;

	public BlockRow(int size) {
		blocks = new Block[size];
	}

	public Block get(int column) {
		return blocks[column];
	}

	public boolean isFull() {
		return full;
	}
}
