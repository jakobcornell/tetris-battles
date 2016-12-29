package com.github.jakobcornell.tetrisbattles;

import java.awt.Color;
import java.util.Random;

public class Tetromino {
	public Block[][] blocks;
	public final RotationMode rotationMode;
	public boolean isFrozen = false;
	public int age = 0; // increment each frame: trigger default movement on specific value
	public int row, col;
	protected static Random random = new Random();

	protected Tetromino(Block[][] blocks, RotationMode rotationMode) {
		this.blocks = blocks;
		this.rotationMode = rotationMode;
	}

	public Tetromino(TetrominoPrototype proto, Game.Direction movement) {
		this(new Block[4][4], proto.rotationMode);
		int bit = 0, step = 0;
		if (movement == Game.Direction.DOWN) {
			bit = 0; step = 1;
		} else if (movement == Game.Direction.UP) {
			bit = 15; step = 15;
		} else {
			throw new IllegalArgumentException("movement must be UP or DOWN");
		}
		for (Block[] blockRow : blocks) {
			for (int c = 0; c < 4; c += 1) {
				if ((1 << bit & proto.template) != 0) {
					blockRow[c] = new Block();
				}
				bit = (bit + step) & 0xF;
			}
		}
	}

	public Tetromino(Game.Direction movement, Game.Color color) {
		this(TetrominoPrototype.getRandom(random), movement);
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
		I(0x0F00, RotationMode.EVEN),
		O(0x0660, RotationMode.EVEN),
		T(0x0720, RotationMode.ODD),
		S(0x0360, RotationMode.ODD),
		Z(0x0630, RotationMode.ODD),
		J(0x0710, RotationMode.ODD),
		L(0x0740, RotationMode.ODD);

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
		EVEN, ODD;
	}
}
