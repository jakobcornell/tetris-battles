package com.github.jakobcornell.tetrisbattles;

import java.awt.Color;
import java.util.Random;

public class Tetromino {
	public final Block[][] blocks;
	public final RotationMode rotationMode;

	public int age = 0; // increment each frame: trigger default movement on specific value
	public Game.Direction movement;
	public int row, col;

	protected Tetromino(Block[][] blocks, RotationMode rotationMode) {
		this.blocks = blocks;
		this.rotationMode = rotationMode;
	}

	public Tetromino(TetrominoPrototype proto, Color blockColor) {
		this(new Block[4][4], proto.rotationMode);
		int mask = 0b1;
		for(Block[] blockRow : blocks) {
			for(int c=3; c>=0; c--) {
				if ((mask & proto.template) != 0) {
					blockRow[c] = new Block();
					blockRow[c].color = blockColor;
				}
				mask <<= 1;
			}
		}
	}

	public Tetromino(Game.Direction movement) {
		this(TetrominoPrototype.getRandom(random), Color.BLUE);
	}

	protected static Random random = new Random();

	protected static enum TetrominoPrototype {
		ITetromino(0x0F00, RotationMode.Even),
		OTetromino(0x0660, RotationMode.Even),
		TTetromino(0x0E40, RotationMode.Odd),
		STetromino(0x0C60, RotationMode.Odd),
		ZTetromino(0x06C0, RotationMode.Odd),
		JTetromino(0x0E80, RotationMode.Odd),
		LTetromino(0x0E20, RotationMode.Odd);

		private final int template;
		private final RotationMode rotationMode;

		private TetrominoPrototype(int template, RotationMode rotationMode) {
			this.template = template;
			this.rotationMode = rotationMode;
		}

		private static final TetrominoPrototype[] values = values();

		public static TetrominoPrototype getRandom(Random random) {
			return values[random.nextInt(values.length)];
		}
	}

	public static enum RotationMode {
		Even, Odd;
	}
}
