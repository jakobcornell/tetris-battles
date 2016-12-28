package com.github.jakobcornell.tetrisbattles;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.Timer;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

public class Main {
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		final Game game = new Game();
		final KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				PlayerAction action = keyMap.get(e.getKeyCode());
				if (action != null) {
					game.enqueueAction(action);
				}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		};

		final Timer tickTimer = new Timer(0, null);
		
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				game.tick();
				if (game.isFinished()) {
					tickTimer.stop();
				}
			}
		};

		tickTimer.addActionListener(timerListener);
		tickTimer.setDelay(65);
		tickTimer.setRepeats(true);

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				final JFrame frame = new JFrame("buffy");
				frame.addKeyListener(keyListener);
				Container content = frame.getContentPane();
				content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
				content.add(new BoardPanel(game, Game.Direction.UP, 24));
				content.add(new BoardPanel(game, Game.Direction.DOWN, 24));
				frame.pack();
				frame.setVisible(true);
			}
		});

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				tickTimer.start();
			}
		});
	}

	private static Map<Integer, PlayerAction> keyMap = new HashMap<>();
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
