package com.github.jakobcornell.tetrisbattles;

import java.awt.Color;
import java.util.Random;

public class Tetromino {
	public int dimension;
	public Block[][] blocks;
	public boolean isFrozen = false;
	public int age = 0;
	public int row, col;
	protected static Random random = new Random();

	public Tetromino(TetrominoPrototype prototype, Game.Color color) {
		blocks = prototype.toBlocks();
		dimension = prototype.dimension;
		float ang = (float) Math.PI * (random.nextFloat() * 14 + 1) / 32;
		float h = (color == Game.Color.RED) ? 0f : 0.5f;
		float s = (float) Math.cos(ang);
		float b = (float) Math.sin(ang);
		setColor(Color.getHSBColor(h, s, b));
	}

	public void setColor(Color c) {
		for(Block[] blockRow : blocks) {
			for(Block block : blockRow) {
				if (block != null) {
					block.color = c;
				}
			}
		}
	}

	protected static enum TetrominoPrototype {
		I(0x0F00, 4),
		J(0470, 3),
		L(0170, 3),
		O(0b1111, 2),
		S(0360, 3),
		T(0270, 3),
		Z(0630, 3);

		private final int template;
		private final int dimension;

		private TetrominoPrototype(int template, int dimension) {
			this.template = template;
			this.dimension = dimension;
		}

		private static final TetrominoPrototype[] values = values();

		public static TetrominoPrototype getRandom() {
			return values[random.nextInt(values.length)];
		}

		public Block[][] toBlocks() {
			Block[][] blocks = new Block[dimension][dimension];
			for (int i = 0; i < dimension * dimension; i += 1) {
				if ((template >> (dimension * dimension - 1 - i) & 1) != 0) {
					blocks[i / dimension][i % dimension] = new Block();
				}
			}
			return blocks;
		}
	}
}
