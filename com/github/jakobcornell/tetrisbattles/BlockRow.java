package com.github.jakobcornell.tetrisbattles;

public class BlockRow {
	private Block[] blocks;
	private boolean full;

	public BlockRow() {
		
	}

	public Block get(int column) {
		return blocks[column];
	}

	public boolean isFull() {
		return full;
	}
}
