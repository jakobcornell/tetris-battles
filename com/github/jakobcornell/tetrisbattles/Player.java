package com.github.jakobcornell.tetrisbattles;

public class Player {
	public final String name;
	public final Game.Direction perspective;
	public final Game.Direction playDirection;
	public final Game.Color color;

	public Player(String name, Game.Direction perspective, Game.Color color) {
		this.name = name;
		this.perspective = perspective;
		playDirection = perspective.reverse();
		this.color = color;
	}

	public String toString() {
		return name;
	}
}
