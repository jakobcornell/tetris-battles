package com.github.jakobcornell.tetrisbattles;

public enum PlayerAction {
	P1ROTATE (Game.Direction.UP, PlayerActionType.ROTATE, null),
	P1LEFT   (Game.Direction.UP, PlayerActionType.MOVE, Game.Direction.LEFT),
	P1DOWN   (Game.Direction.UP, PlayerActionType.MOVE, Game.Direction.DOWN),
	P1RIGHT  (Game.Direction.UP, PlayerActionType.MOVE, Game.Direction.RIGHT),
	P2ROTATE (Game.Direction.DOWN, PlayerActionType.ROTATE, null),
	P2LEFT   (Game.Direction.DOWN, PlayerActionType.MOVE, Game.Direction.LEFT),
	P2DOWN   (Game.Direction.DOWN, PlayerActionType.MOVE, Game.Direction.DOWN),
	P2RIGHT  (Game.Direction.DOWN, PlayerActionType.MOVE, Game.Direction.RIGHT);

	public final Game.Direction perspective;
	public final PlayerActionType type;
	public final Game.Direction moveDirection;

	private PlayerAction(Game.Direction perspective, PlayerActionType type, Game.Direction moveDirection) {
		this.perspective = perspective;
		this.type = type;
		this.moveDirection = moveDirection;
	}
}
