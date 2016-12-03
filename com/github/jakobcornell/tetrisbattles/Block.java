package com.github.jakobcornell.tetrisbattles;

public class Block {
	public static enum Color {
		BLUE, RED
	}
	
	public final Color color;
	public final int shade;

	public Block(Color color, int shade) {
		this.color = color;
		this.shade = shade;
	}
}
