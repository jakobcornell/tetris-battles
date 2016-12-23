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

	public void set(int column, Block block) {
		blocks[column] = block;
		if (!full) {
			for (Block b : blocks) {
				if (b == null) {
					return;
				}
			}
			full = true;
		}
	}

	public boolean isFull() {
		return full;
	}
}
