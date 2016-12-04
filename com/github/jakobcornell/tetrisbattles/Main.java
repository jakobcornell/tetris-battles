package com.github.jakobcornell.tetrisbattles;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import java.awt.Container;
import javax.swing.BoxLayout;

public class Main {
	public static void main(String[] args) {
		final Game game = new Game();
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				PlayerAction action = keyMap.get(e);
				if (action != null) {
					game.queueAction(action);
				}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		};

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("buffy");
				frame.addKeyListener(keyListener);
				Container content = frame.getContentPane();
				content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
				content.add(new GamePanel(game, Game.Direction.Up, 32));
				content.add(new GamePanel(game, Game.Direction.Down, 32));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static Map<Integer, PlayerAction> keyMap = new HashMap();
	static {
		keyMap.put(KeyEvent.VK_W, PlayerAction.P1ROTATE);
		keyMap.put(KeyEvent.VK_A, PlayerAction.P1LEFT);
		keyMap.put(KeyEvent.VK_S, PlayerAction.P1DOWN);
		keyMap.put(KeyEvent.VK_D, PlayerAction.P1RIGHT);
		keyMap.put(KeyEvent.VK_UP, PlayerAction.P2ROTATE);
		keyMap.put(KeyEvent.VK_LEFT, PlayerAction.P2LEFT);
		keyMap.put(KeyEvent.VK_DOWN, PlayerAction.P2DOWN);
		keyMap.put(KeyEvent.VK_RIGHT, PlayerAction.P2RIGHT);
	}
}
