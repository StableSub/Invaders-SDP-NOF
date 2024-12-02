package engine.manager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class InputManager implements KeyListener {

	private static final int NUM_KEYS = 256;
	private static boolean[] keys = new boolean[NUM_KEYS];
	private static InputManager instance;

	private InputManager() {
		keys = new boolean[NUM_KEYS];
	}

	public static InputManager getInstance() {
		if (instance == null)
			instance = new InputManager();
		return instance;
	}

	public boolean isKeyDown(final int keyCode) {
		return keys[keyCode];
	}

	@Override
	public void keyPressed(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS)
			keys[key.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS)
			keys[key.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(final KeyEvent key) {
		// 아무 작업도 하지 않음
	}

	// 추가된 메서드: 모든 키 상태 초기화
	public void resetKeys() {
		for (int i = 0; i < NUM_KEYS; i++) {
			keys[i] = false;
		}
	}
}
