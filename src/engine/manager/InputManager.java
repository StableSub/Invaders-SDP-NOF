package engine.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.Timer;

/**
 * Manages keyboard input for the provided screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class InputManager implements KeyListener {

	/** Number of recognised keys. */
	private static final int NUM_KEYS = 256;
	/** Array with the keys marked as pressed or not. */
	private static boolean[] keys;
	/** Singleton instance of the class. */
	private static InputManager instance;

	/** Stores custom actions for specific keys. */
	private HashMap<Integer, ActionListener> keyBindings;
	/** Timer to execute key bindings repeatedly while key is held. */
	private Timer keyTimer;

	/**
	 * Private constructor.
	 */
	private InputManager() {
		keys = new boolean[NUM_KEYS];
		keyBindings = new HashMap<>();
	}

	/**
	 * Returns shared instance of InputManager.
	 *
	 * @return Shared instance of InputManager.
	 */
	public static InputManager getInstance() {
		if (instance == null)
			instance = new InputManager();
		return instance;
	}

	/**
	 * Binds a key to an action.
	 *
	 * @param keyCode Key to bind.
	 * @param action  Action to perform when the key is pressed.
	 */
	public void bindKey(int keyCode, ActionListener action) {
		if (keyCode >= 0 && keyCode < NUM_KEYS) {
			keyBindings.put(keyCode, action);
		}
	}

	/**
	 * Checks if the key is currently pressed.
	 *
	 * @param keyCode Key number to check.
	 * @return True if the key is pressed.
	 */
	public boolean isKeyDown(final int keyCode) {
		return keys[keyCode];
	}

	/**
	 * Handles key pressed event.
	 *
	 * @param key Key pressed.
	 */
	@Override
	public void keyPressed(final KeyEvent key) {
		int keyCode = key.getKeyCode();
		if (keyCode >= 0 && keyCode < NUM_KEYS) {
			keys[keyCode] = true;

			if (keyBindings.containsKey(keyCode)) {
				ActionListener action = keyBindings.get(keyCode);
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		}
	}

	/**
	 * Handles key released event.
	 *
	 * @param key Key released.
	 */
	@Override
	public void keyReleased(final KeyEvent key) {
		int keyCode = key.getKeyCode();
		if (keyCode >= 0 && keyCode < NUM_KEYS) {
			keys[keyCode] = false;
		}
	}

	/**
	 * Does nothing for key typed events.
	 *
	 * @param key Key typed.
	 */
	@Override
	public void keyTyped(final KeyEvent key) {
		// No action needed for key typed
	}

	public void setKeyUp(final int keyCode) {
		if (keyCode >= 0 && keyCode < NUM_KEYS) {
			keys[keyCode] = false;
		}
	}

}
