package com.github.jakobcornell.tetrisbattles;

public enum PlayerAction {
	P1ROTATE (UP, ROTATE, null),
	P1LEFT   (UP, MOVE, Game.Direction.LEFT),
	P1DOWN   (UP, MOVE, Game.Direction.DOWN),
	P1RIGHT  (UP, MOVE, Game.Direction.RIGHT),
	P2ROTATE (DOWN, ROTATE, null),
	P2LEFT   (DOWN, MOVE, Game.Direction.LEFT),
	P2DOWN   (DOWN, MOVE, Game.Direction.DOWN),
	P2RIGHT  (DOWN, MOVE, Game.Direction.RIGHT);

	public static enum PlayerActionType {
		MOVE, ROTATE
	}

	public static final PlayerActionType MOVE = PlayerActionType.MOVE;
	public static final PlayerActionType ROTATE = PlayerActionType.ROTATE;

	public final Game.Direction perspective;
	public final PlayerActionType type;
	public final Game.Direction moveDirection;

	private PlayerAction(Game.Direction perspective, PlayerActionType type, Game.Direction moveDirection) {
		this.perspective = perspective;
		this.type = type;
		this.moveDirection = moveDirection;
	}
}
