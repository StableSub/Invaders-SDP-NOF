package ui;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.core.Core;
import engine.core.GameState;
import database.AchievementManager;
import engine.manager.SoundManager;
import engine.utility.Score;
import engine.utility.Sound;
import entity.Wallet;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	/** Current score. */
	private int score;
	/** Player lives left. */
	private int livesRemaining;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Checks if current score is a new high score. */
	private double accuracy;
	/** Number of coins earned in the game */
	private int coinsEarned;

	// Set ratios for each coin_lv - placed in an array in the order of lv1, lv2, lv3, lv4, and will be used accordingly,
	// e.g., lv1; score 100 * 0.1
	private static final double[] COIN_RATIOS = {0.1, 0.13, 0.16, 0.19};

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final int width, final int height, final int fps,
					   final GameState gameState, final Wallet wallet, final AchievementManager achievementManager) {
		super(width, height, fps);

		this.score = gameState.getScore();
		this.livesRemaining = gameState.getLivesRemaining();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		// Get the user's coin_lv
		int coin_lv = wallet.getCoin_lv();

		// Apply different ratios based on coin_lv
		double coin_ratio = COIN_RATIOS[coin_lv-1 < 0 ? 0 : coin_lv-1];

		// Adjust coin earning ratios based on the game level upgrade stage score
		// Since coins are in integer units, round the decimal points and convert to int
		this.coinsEarned = (int)Math.round(gameState.getScore() * coin_ratio);
		this.coinsEarned += achievementManager.getAchievementReward();

		// deposit the earned coins to wallet
		this.accuracy = gameState.getAccuracy();
		wallet.deposit(coinsEarned);

		soundManager.loopSound(Sound.BGM_GAMEOVER);


	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.returnCode = 1;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAMEOVER);
				soundManager.playSound(Sound.MENU_BACK);
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				this.returnCode = 2;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAMEOVER);
			}
		}

	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished());
		drawManager.drawResults(this, this.score, this.livesRemaining,
				this.shipsDestroyed, this.accuracy, this.coinsEarned);

		drawManager.completeDrawing(this);
	}
}
