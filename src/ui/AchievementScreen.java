package ui;

import java.awt.event.KeyEvent;
import java.util.List;
import database.DatabaseManager;
import engine.utility.Score;
import engine.utility.Sound;
import engine.manager.SoundManager;
import entity.Achievement;

/**
 * Implements the achievement screen.
 *
 * Team NOF
 * 
 */
public class AchievementScreen extends Screen {

	/** List of past high scores. */
	private final List<Score> highScores;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private final int totalScore;
	private final int totalPlayTime;
	private final int currentPerfectStage;
	private final int maxCombo;
	private final double highAccuracy;
	private final boolean checkFlawlessFailure;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public AchievementScreen(final int width, final int height, final int fps, final Achievement achievement) {
		super(width, height, fps);
		DatabaseManager db = new DatabaseManager();
		highScores = db.getHighScoreList();
		totalScore = achievement.getTotalScore();
		totalPlayTime = achievement.getTotalPlayTime();
		currentPerfectStage = achievement.getPerfectStage();
		highAccuracy = achievement.getHighAccuracy();
		maxCombo = achievement.getHighMaxCombo();
		checkFlawlessFailure = achievement.getFlawlessFailure();
		this.returnCode = 1;


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
		if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
				&& this.inputDelay.checkFinished()) {
			this.isRunning = false;
			soundManager.playSound(Sound.MENU_BACK);
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawAchievementMenu(this, this.totalScore, this.totalPlayTime,
				this.maxCombo, this.highAccuracy, this.currentPerfectStage, this.currentPerfectStage+1,
				this.checkFlawlessFailure);
		drawManager.drawHighScores(this, this.highScores);
		drawManager.completeDrawing();
	}
}
