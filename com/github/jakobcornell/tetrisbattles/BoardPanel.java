package com.github.jakobcornell.tetrisbattles;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;

public class BoardPanel extends JPanel {
    private final Game game;
    private final Game.Direction perspective;
    private final int zoomLevel;
	private final Block[][] gameState;

	public BoardPanel(Game game, Game.Direction perspective, int zoomLevel) {
		this.perspective = perspective;
		this.zoomLevel = zoomLevel;
		this.gameState = new Block[game.height][game.width];
		this.setPreferredSize(new Dimension(game.width * zoomLevel, game.height * zoomLevel));
	}

	public void paintComponent(Graphics graphics) {
		Graphics2D ctx = (Graphics2D) graphics;
		ctx.scale(((float) zoomLevel) / 100, ((float) zoomLevel) / 100);
		game.getBlocks(gameState);
		for(int r=0; r<game.height; r+=1) {
			for(int c=0; c<game.width; c+=1) {
				Block block;
				if (perspective == Game.Direction.UP) {
					block = gameState[r][c];
				}
				else if (perspective == Game.Direction.DOWN) {
					block = gameState[game.height - r - 1][game.width - c - 1];
				}
				if (block != null) {
					Shape rectangle = new Rectangle(100*r + 2, 100*c + 2, 96, 96);
					ctx.setColor(Color.WHITE);
					ctx.fill(rectangle);
					ctx.setColor(new Color(block.color == Block.Color.Red ? 0 : 2.0f/3, 1.0f, ((float) shade)/256));
					ctx.draw(rectangle);
				}
			}
		}
	}
}
