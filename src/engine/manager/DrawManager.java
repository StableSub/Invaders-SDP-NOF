package engine.manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import blackjack.*;

import database.DatabaseManager;
import engine.utility.Cooldown;
import engine.core.Core;
import engine.utility.Score;
import entity.*;
import ui.Frame;
import ui.Screen;
import ui.ShopScreen;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class DrawManager {
	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	private static Graphics backBufferGraphics;
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Small sized font. */
	private static Font fontSmall;
	/** Small sized font properties. */
	private static FontMetrics fontSmallMetrics;
	/** Normal sized font. */
	private static Font fontRegular;
	/** Normal sized font properties. */
	private static FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** For Shopscreen image */
	private static BufferedImage img_additionallife;
	private static BufferedImage img_bulletspeed;
	private static BufferedImage img_coin;
	private static BufferedImage img_coingain;
	private static BufferedImage img_shotinterval;


	/** Sprite types. */
	public enum SpriteType {
		/** Player ship. */
		Ship,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		/** Enemy bullet. */
		EnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** Bonus ship. */
		EnemyShipSpecial,
		/** Destroyed enemy special ship. */
		SpecialExplosion,
		/** Destroyed enemy ship. */
		Explosion,
		/** Barrier. */
		Barrier,
		/** Item Box. */
		ItemBox,
		/** Spider webs restricting player movement */
		Web,
		/** Obstacles preventing a player's bullet */
		Block,
		/** Obstruction 1 (satellite) */
		Blocker1,
		/** Obstruction 2 (Astronaut) */
		Blocker2,
		/** 2nd player ship. */
		Ship2,
		/** 3rd player ship. */
		Ship3,
		/** 4th player ship. */
		Ship4,
		/** Fourth enemy ship - first form. */
		EnemyShipD1,
		/** Fourth enemy ship - second form. */
		EnemyShipD2,
		/** Fifth enemy ship - first form. */
		EnemyShipE1,
		/** Fifth enemy ship - second form. */
		EnemyShipE2,
		/** Elite enemy ship - first form. */
		EnemyShipF1
	}

	/**
	 * Private constructor.
	 */
	private DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap = new LinkedHashMap<SpriteType, boolean[][]>();

			spriteMap.put(SpriteType.Ship, new boolean[13][8]);
			spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
			spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[62][28]);
			spriteMap.put(SpriteType.SpecialExplosion, new boolean[60][26]);
			spriteMap.put(SpriteType.Explosion, new boolean[13][7]);
			spriteMap.put(SpriteType.Barrier, new boolean[39][11]);
			spriteMap.put(SpriteType.ItemBox, new boolean[7][7]);
			spriteMap.put(SpriteType.Web, new boolean[12][8]);
			spriteMap.put(SpriteType.Block, new boolean[20][7]);
			spriteMap.put(SpriteType.Blocker1, new boolean[182][93]); // artificial satellite
			spriteMap.put(SpriteType.Blocker2, new boolean[82][81]); // astronaut
			spriteMap.put(SpriteType.Ship2, new boolean[13][8]);
			spriteMap.put(SpriteType.Ship3, new boolean[13][8]);
			spriteMap.put(SpriteType.Ship4, new boolean[13][8]);
			spriteMap.put(SpriteType.EnemyShipD1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipD2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipE1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipE2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipF1, new boolean[16][7]);

			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontSmall = fileManager.loadFont(10f);
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
			e.printStackTrace();
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}

		/** Shop image load*/
		try{
			img_additionallife = ImageIO.read(new File("res/image/additional life.jpg"));
			img_bulletspeed = ImageIO.read(new File("res/image/bullet speed.jpg"));
			img_coin = ImageIO.read(new File("res/image/coin.jpg"));
			img_coingain = ImageIO.read(new File("res/image/coin gain.jpg"));
			img_shotinterval = ImageIO.read(new File("res/image/shot interval.jpg"));
		} catch (IOException e) {
			logger.info("Shop image loading failed");
		}

	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	public static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}

	/**
	 * First part of the drawing process. Initializes buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontSmallMetrics = backBufferGraphics.getFontMetrics(fontSmall);
		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

		//drawBorders(screen);
		//drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 */
	public void completeDrawing() {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the appropriate image.
	 *
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
						   final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		backBufferGraphics.setColor(entity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}

	//Drawing an Entity (Blocker) that requires angle setting
	public void drawRotatedEntity(Entity entity, int x, int y, double angle) {
		Graphics2D g2d = (Graphics2D) backBufferGraphics; // Convert to Graphics2D
		AffineTransform oldTransform = g2d.getTransform(); // Save previous conversion

		//Set center point to rotate
		int centerX = x + entity.getWidth() / 2;
		int centerY = y + entity.getHeight() / 2;

		//rotate by a given angle
		g2d.rotate(Math.toRadians(angle), centerX, centerY);

		//Drawing entities
		drawEntity(entity, x, y);

		g2d.setTransform(oldTransform); // Restore to original conversion state
	}

	/**
	 * For debugging purposes, draws the canvas borders.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purposes, draws a grid over the canvas.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */
	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
	}
	/**
	 * Draws level on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Current level.
	 */
	public void drawLevel(final Screen screen, final int level) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("lv.%d", level);
		backBufferGraphics.drawString(scoreString, screen.getWidth() / 2 - 60, 25);
	}

	/**
	 * Draws elapsed time on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param elapsedTime
	 *            Elapsed time.
	 */
	public void drawElapsedTime(final Screen screen, final int elapsedTime) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.LIGHT_GRAY);

		int cent = (elapsedTime % 1000)/10;
		int seconds = elapsedTime / 1000;
		int sec = seconds % 60;
		int min = seconds / 60;

		String elapsedTimeString;
		if (min < 1){
			elapsedTimeString = String.format("%d.%02d", sec, cent);
		} else {
			elapsedTimeString = String.format("%d:%02d.%02d", min, sec, cent);
		}
		backBufferGraphics.drawString(elapsedTimeString, screen.getWidth()/2, 25);
	}

	/**

	 * Draws alert message on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param alertMessage
	 *            Alert message.
	 */
	public void drawAlertMessage(final Screen screen, final String alertMessage) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.RED);
		backBufferGraphics.drawString(alertMessage,
				(screen.getWidth() - fontRegularMetrics.stringWidth(alertMessage))/2, 65);
	}

	/**

	 * Draws number of remaining lives on screen.
	 *
	 * @param lives
	 *            Current lives.
	 */
	public void drawLives(final int lives, final Ship.ShipType shipType) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = ShipFactory.create(shipType, 0, 0);
		for (int i = 0; i < lives; i++)
			drawEntity(dummyShip, 40 + 35 * i, 10);
	}

	/**
	 * Draws launch trajectory on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionX
	 *            X coordinate of the line.
	 */
	public void drawLaunchTrajectory(final Screen screen, final int positionX) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 140; i += 20){
			backBufferGraphics.drawRect(positionX + 13, screen.getHeight() - 100 - i,1,10);
		}
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws game title.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
		String titleString = "Invaders";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5 * 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 5);
	}

	public void drawGameTitle(final Screen screen) {
		String titleString = "Invaders";
		backBufferGraphics.setColor(Color.DARK_GRAY);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 2);
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawMenu(final Screen screen, final int option, final int coin) {
		String playString = "Play";
		String shopString = "SHOP";
		String coinString = "YOUR COIN: " + coin;
		String achievementString = "ACHIEVEMENT";
		String settingString = "SETTING";
		String exitString = "EXIT";


		if (option == 6) /*option2 => Game Settings */
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, playString,
				screen.getHeight() / 7 * 4);

		if (option == 3) /*option3 => Shop */
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, shopString, screen.getHeight()
				/ 7 * 4 + fontRegularMetrics.getHeight() * 2);

		backBufferGraphics.setColor(Color.ORANGE);
		drawCenteredSmallString(screen, coinString, screen.getHeight()
				/ 7 * 4 + fontRegularMetrics.getHeight() * 3);

		if (option == 4) /*option4 => Achievement */
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, achievementString, screen.getHeight()
				/ 7 * 4 + fontRegularMetrics.getHeight() * 5);


		if (option == 5) /*option5 => Setting */
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, settingString, screen.getHeight()
				/ 7 * 4 + fontRegularMetrics.getHeight() * 7);

		if (option == 0) /*option0 => exit */
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight()
				/ 7 * 4 + fontRegularMetrics.getHeight() * 9);
	}

	/**
	 * Draws game results.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining
	 *            Lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param Accuracy
	 *            Total accuracy.
	 */
	public void drawResults(final Screen screen, final int score,
							final int livesRemaining, final int shipsDestroyed,
							final double Accuracy, final int coinsEarned) {
		String scoreString = String.format("score %04d", score);
		String livesRemainingString = "lives remaining " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
		String accuracyString = String
				.format("accuracy %.2f%%", Accuracy);
		String coinsEarnedString = "EARNED COIN " + coinsEarned;

		int height = 2;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		drawCenteredRegularString(screen, accuracyString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
		backBufferGraphics.setColor(Color.YELLOW);
		drawCenteredRegularString(screen, coinsEarnedString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 9);
	}

	/**
	 * Draws basic content of game over screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 */
	public void drawGameOver(final Screen screen, final boolean acceptsInput) {
		String gameOverString = "Game Over";
		String continueOrExitString =
				"Press Space to play again, Escape to exit";

		int height = 2;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameOverString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}


	/**
	 * Draws achievement screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawAchievementMenu(final Screen screen, final int totalScore, final int totalPlayTime, final int maxCombo,
									double highAccuracy, final int currentPerfectStage, final int nextPerfectStage, boolean checkFlawlessFailure) {
		//high score section
		String highScoreTitle = "High Scores";

		//cumulative section
		String totalScoreTitle = "Total Score";
		String totalPlayTimesTitle = "-Total  Playtime-";

		// centered strings
		String achievementTitle = "Achievement";
		String instructionsString = "Press ESC to return";
		String achievementsStatusTitle = "Achievements Status";

		// Achievements names
		String maxComboTitle = " Combo Mastery ";
		String perfectClearTitle = "Perfect Clear";
		String accuracyTitle = "Hawk Eye";
		String flawlessFailureTitle = "Flawless Failure";
		String eternityTimeTitle = "A time of eternity";

		// draw "achievement"
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, achievementTitle, screen.getHeight() / 8-10);

		// draw instruction
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 8 + fontRegularMetrics.getHeight()-10);


		// draw "high score"
		backBufferGraphics.setColor(Color.GREEN);
		drawLeftSideScoreRegularString(screen, highScoreTitle,
				screen.getHeight() / 5+ fontBigMetrics.getHeight()-10);

		// draw total score
		backBufferGraphics.setColor(Color.yellow);
		drawRightSideCumulativeRegularString(screen, totalScoreTitle,
				screen.getHeight() / 5 + fontBigMetrics.getHeight()-10);

		// draw "Total play-time"
		backBufferGraphics.setColor(Color.yellow);
		drawRightSideCumulativeRegularString(screen, totalPlayTimesTitle,
				screen.getHeight() / 5 + 2*fontRegularMetrics.getHeight()+2* fontBigMetrics.getHeight());

		// draw "Total Score"
		backBufferGraphics.setColor(Color.WHITE);
		String totalScoreString = String.format("%s", totalScore);
		drawRightSideCumulativeBigString(screen, totalScoreString, screen.getHeight() / 3
				- fontRegularMetrics.getHeight());

		// draw "achievement status"
		backBufferGraphics.setColor(Color.MAGENTA);
		drawCenteredBigString(screen, achievementsStatusTitle,
				screen.getHeight() / 2 + fontBigMetrics.getHeight()-10 );



		// draw "max combo"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, maxComboTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight() - 13);

		// draw "Perfect clear"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, perfectClearTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*2 - 13);

		// draw "high accuracy"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, accuracyTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*3 - 15);

		// draw "Flawless Failure"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, flawlessFailureTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*6 - 8+fontBigMetrics.getHeight()*4 - 15);

		// draw "Eternity Time"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, eternityTimeTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*7-10 +fontBigMetrics.getHeight()*5-22);

		int totalHours = totalPlayTime / 3600;
		int remainHours = totalPlayTime % 3600;

		int totalMinutes = remainHours / 60;
		int remainMinutes = remainHours % 60;

		int totalSeconds = remainMinutes % 60;

		// draw total play time record
		String totalPlayTimeeString = String.format("%02dH %02dm %02ds",totalHours,totalMinutes,totalSeconds);
		backBufferGraphics.setColor(Color.WHITE);
		drawRightSideCumulativeBigString(screen, totalPlayTimeeString, screen.getHeight() / 2
				- fontRegularMetrics.getHeight() - 25);

		// draw combo reward
		final String[] MAX_COMBO_REWARD = {"500", "1500", "2000", "2500"};

		// draw max combo achievement
		if (maxCombo >= 25) {
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, MAX_COMBO_REWARD[3],
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*2-20);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementSmallEventString(screen, "You record high combo",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()-12);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, "You are crazy!",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*2-20);
		} else {
			backBufferGraphics.setColor(Color.orange);

			drawRightSideAchievementComboString_1(screen, "combo",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5-15);
			drawRightSideAchievementComboString_2(screen, "combo",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5-15);


			backBufferGraphics.setColor(Color.green);
			drawRightSideAchievementSmallString_1(screen, "current",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 4 - 22);
			backBufferGraphics.setColor(Color.red);
			drawRightSideAchievementSmallString_2(screen, "target",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 4 - 22);
			if (maxCombo < 10) {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, MAX_COMBO_REWARD[0],
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2 + fontBigMetrics.getHeight() * 2-20);

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format("             %d", maxCombo) + "  => " + "       10";
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5 - 15);
			} else {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, MAX_COMBO_REWARD[maxCombo <= 9 ? 0 : maxCombo / 5 - 1],
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2 + fontBigMetrics.getHeight() * 2-20);

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format("             %d", maxCombo) + " =>" + String.format("         %d", maxCombo <= 9 ? 10 : (((( (maxCombo - 10) / 5) + 1) * 5 ) + 10));
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5 - 15);
			}
		}


		// draw "perfect clear"
		if (currentPerfectStage <= 6) {
			String[] PERFECT_COIN_REWARD = { "200", "400", "800", "2000", "3000", "4000", "5000"}; // 퍼펙트 스테이지 리워드
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, PERFECT_COIN_REWARD[currentPerfectStage],
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3-20);

			backBufferGraphics.setColor(Color.green);
			drawRightSideAchievementSmallString_1(screen,"current",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*2-13);
			backBufferGraphics.setColor(Color.red);
			drawRightSideAchievementSmallString_2(screen,"target",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*2-13);

			String sampleAchievementsString2 = "lv." + currentPerfectStage + "   =>  lv." +
					nextPerfectStage;
			backBufferGraphics.setColor(Color.WHITE);
			drawRightSideAchievementBigString(screen, sampleAchievementsString2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3-20);

		}
		else{
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, "5000",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3-20);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementSmallEventString2(screen, "You clear all levels perfectly",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*3-25);

			String sampleAchievementsString2 = " 100% Clear !! ";
			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, sampleAchievementsString2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3-20);

		}

		// draw accuracy reward
		final String[] HIGH_ACCURACY_REWARD = {"100", "200", "300", "500"};

		int nextAccuracy = 0;

		if (highAccuracy  < 50) {
			nextAccuracy = 0;
		} else if (highAccuracy < 70) {
			nextAccuracy = 1;
		} else if (highAccuracy < 90) {
			nextAccuracy = 2;
		} else if(highAccuracy < 100) {
			nextAccuracy = 3;
		}
		//draw accuracy achievement
		if (highAccuracy >= 100) {
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, HIGH_ACCURACY_REWARD[3],
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7+fontBigMetrics.getHeight()*2-20);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementSmallEventString(screen, "You record high accuracy",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7+fontBigMetrics.getHeight()-12);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, "You are insane!",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7+fontBigMetrics.getHeight()*2-20);
		} else {
			backBufferGraphics.setColor(Color.orange);


			backBufferGraphics.setColor(Color.green);
			drawRightSideAchievementSmallString_1(screen, "current",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10 - 33);

			backBufferGraphics.setColor(Color.red);
			drawRightSideAchievementSmallString_2(screen, "target",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10 - 33);

			if (highAccuracy < 50) {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, HIGH_ACCURACY_REWARD[0],
						screen.getHeight() /2 + fontRegularMetrics.getHeight()*2 + 4 +fontBigMetrics.getHeight()*5-20);
				// 얘를 기반으로 위치 조정 (코인) 인덱스 오류 잘 찾기.

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format(" %.2f%%", highAccuracy) + "  =>  " + " 50%";
				// 명중률 달성 하나도 안 했을때 다음 목표 정하기
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 11 - 26);
			} else {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, HIGH_ACCURACY_REWARD[nextAccuracy],
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2 + 4 + fontBigMetrics.getHeight() * 5-20);

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format("  %.2f%%", highAccuracy) + " => " + String.format("  %d%%", nextAccuracy == 0 ? 50 : nextAccuracy == 1 ? 70 : nextAccuracy == 2 ? 90 : 100);
				// 목표를 1개라도 달성 시 그 다음 목표 설정, 소수점 설정하기
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 11 - 26);
			}
		}




		// draw flawless failure achievement
		String flawlessFailureReward = "1000";
		if (checkFlawlessFailure) {
			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, "Complete!",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+fontBigMetrics.getHeight()*4-22);
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, flawlessFailureReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+fontBigMetrics.getHeight()*4-22);

		} else {
			String explainFlawlessFailure_1 = "    Achieved when the game ends";
			String explainFlawlessFailure_2 = "                with 0% accuracy.";
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString_3(screen, explainFlawlessFailure_1,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+5+fontBigMetrics.getHeight()*3+3+fontSmallMetrics.getHeight()-20);

			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString_3(screen, explainFlawlessFailure_2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+5+fontBigMetrics.getHeight()*3+3+fontSmallMetrics.getHeight()*2-20);
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, flawlessFailureReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+5+fontBigMetrics.getHeight()*4-22);
		}


		// draw play time achievement
		String eternityTimeReward = "1000";
		String sampleAchievementsString = "complete!";
		String explainEternityTime_1 = "              Total play time ";
		String explainEternityTime_2 = "        must exceed 10 minutes...";
		if (totalPlayTime >= 600) {
			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, sampleAchievementsString,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7 - 3+fontBigMetrics.getHeight()*5-25);
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, eternityTimeReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7 - 3+fontBigMetrics.getHeight()*5-25);

		} else {
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString_3(screen, explainEternityTime_1,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7 - 3+fontBigMetrics.getHeight()*4+fontSmallMetrics.getHeight()-20);
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString_3(screen, explainEternityTime_2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7 - 3+fontBigMetrics.getHeight()*4+fontSmallMetrics.getHeight()*2-20);
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, eternityTimeReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*7 - 3+fontBigMetrics.getHeight()*5-20);

		}
	}

	/**
	 * Draws high scores.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawHighScores(final Screen screen, List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		final int limitDrawingScore = 3;
		int countDrawingScore = 0;

		// 상위 3개의 점수를 출력합니다.
		for (Score score : highScores) {
			scoreString = String.format("%s        %04d", score.getUserId(), score.getScore());
			drawLeftSideScoreRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2-20);
			i++;
			countDrawingScore++;
			if(countDrawingScore >= limitDrawingScore) {
				break;
			}
		}
	}

	/**
	 * Draws a centered string on small font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	private void drawCenteredSmallString(final Screen screen, final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontSmallMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws credit screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawEndingCredit(final Screen screen, List<String> creditlist, int currentFrame)  {
		backBufferGraphics.setColor(Color.WHITE);
		final int startPoint = screen.getHeight() / 2;

		for (int i = 0;i < creditlist.size(); i++) {
			String target = creditlist.get(i);
			drawCenteredRegularString(screen, target,startPoint + (fontRegularMetrics.getHeight() * 2) * i - currentFrame);
		}


	}

	/**
	 * Draws a centered string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a centered string on big font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	// left side score
	public void drawLeftSideScoreRegularString(final Screen screen,
											   final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 4
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}
	public void drawLeftSideScoreSmallString(final Screen screen,
											 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 4
				- fontRegularMetrics.stringWidth(string) / 3, height);
	}

	//right side Cumulative score
	public void drawRightSideCumulativeRegularString(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() *71/ 100
				- fontRegularMetrics.stringWidth(string)/2 , height);
	}
	public void drawRightSideCumulativeBigString(final Screen screen,
												 final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() *71/ 100
				- fontBigMetrics.stringWidth(string)/2, height);
	}
	public void drawLeftSideAchievementRegularString(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() *22/ 100
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}
	public void drawLeftSideAchievementSmallString(final Screen screen,
												   final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *26/ 100
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}
	public void drawRightSideAchievementSmallEventString(final Screen screen,
														 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *65/100-
				fontRegularMetrics.stringWidth(string)/2, height);
	}
	public void drawRightSideAchievementSmallEventString2(final Screen screen,
														  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *68/100-
				fontRegularMetrics.stringWidth(string)/2, height);
	}
	public void drawRightSideAchievementBigString(final Screen screen,
												  final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() *63/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementComboString_1(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *52/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementComboString_2(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *74/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementSmallString_1(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *59/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementSmallString_2(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *77/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementSmallString_3(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 2-
				fontRegularMetrics.stringWidth(string) / 7, height);
	}
	public void drawRightSideAchievementCoinBigString(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth()*81/100 , height);
	}
	/**
	 * Countdown to game start.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */

	public void drawCountDown(final Screen screen, final int level,
							  final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4)
			if (!bonusLife) {
				drawCenteredBigString(screen, "Level " + level,
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			} else {
				drawCenteredBigString(screen, "Level " + level
								+ " - Bonus life!",
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}
	/**
	 * Draws ReloadTimer on screen.
	 *
	 * @param ship
	 *            player's ship.
	 * @param remainingTime
	 *            remaining reload time.
	 */
	public void drawReloadTimer(final Ship ship,final long remainingTime) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		if(remainingTime > 0){

			int shipX = ship.getPositionX();
			int shipY = ship.getPositionY();
			int shipWidth = ship.getWidth();
			int circleSize = 16;
			int startAngle = 90;
			int endAngle = 0;
			switch(Core.BASE_SHIP){
				case Ship.ShipType.VoidReaper:
					endAngle = 360 * (int)remainingTime / (int)(750 * 0.4);
					break;
				case Ship.ShipType.CosmicCruiser:
					endAngle = 360 * (int)remainingTime / (int)(750 * 1.6);
					break;
				case Ship.ShipType.StarDefender:
					endAngle = 360 * (int)remainingTime / (int)(750 * 1.0);
					break;
				case Ship.ShipType.GalacticGuardian:
					endAngle = 360 * (int)remainingTime / (int)(750 * 1.2);
					break;


			}

			backBufferGraphics.fillArc(shipX + shipWidth/2 - circleSize/2, shipY - 3*circleSize/2,
					circleSize, circleSize, startAngle, endAngle);
		}
	}

	/**
	 * Draws Combo on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param combo
	 *            Number of enemies killed in a row.
	 */
	public void drawCombo(final Screen screen, final int combo) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		if (combo >= 2) {
			String comboString = String.format("Combo %03d", combo);
			backBufferGraphics.drawString(comboString, screen.getWidth() - 100, 85);
		}
	}

	/**
	 * Draws intermediate aggregation on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param maxCombo
	 *            Value of maxCombo.
	 * @param elapsedTime
	 *            Value of elapsedTime.
	 * @param lapTime
	 *            Value of lapTime/prevTime.
	 * @param score
	 *            Value of score/prevScore.
	 * @param tempScore
	 *            Value of tempScore.
	 */
	public void interAggre(final Screen screen, final int level, final int maxCombo,
						   final int elapsedTime, final int lapTime,
						   final int score, final int tempScore) {

		int prevTime = elapsedTime - lapTime;
		int prevScore = score - tempScore;

		int pcent = (prevTime % 1000)/10;
		int pseconds = prevTime / 1000;
		int psec = pseconds % 60;
		int pmin = pseconds / 60;

		String timeString;
		if (pmin < 1){
			timeString = String.format("Elapsed Time: %d.%02d", psec, pcent);
		} else {
			timeString = String.format("Elapsed Time: %d:%02d.%02d", pmin, psec, pcent);
		}

		String levelString = String.format("Statistics at Level %d", level);
		String comboString = String.format("MAX COMBO: %03d", maxCombo);
		String scoreString = String.format("Scores earned: %04d", prevScore);

		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawString(levelString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(levelString))/2,
				5*screen.getHeight()/7);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(comboString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(comboString))/2,
				5*screen.getHeight()/7 + 21);
		backBufferGraphics.drawString(timeString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(timeString))/2,
				5*screen.getHeight()/7 + 42);
		backBufferGraphics.drawString(scoreString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(scoreString))/2,
				5*screen.getHeight()/7 + 63);
	}

	/**
	 * Draws the game setting screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawGameSetting(final Screen screen) {
		String titleString = "Game Setting";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 100 * 25);
	}

	public void drawSettingsScreen(final Screen screen) {
		String settingsTitle = "Settings"; // 타이틀

		// 타이틀을 초록색으로 중앙에 그리기
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, settingsTitle, screen.getHeight() / 8);
	}

	/** 볼륨 바를 그리는 메서드 */
	public void drawVolumeBar(int x, int y, int totalWidth, int filledWidth, boolean isSelected) {
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		backBufferGraphics.fillRect(x, y, filledWidth, 10); // 채워진 부분

		// 나머지 부분은 회색으로 표시
		backBufferGraphics.setColor(Color.GRAY);
		backBufferGraphics.fillRect(x + filledWidth, y, totalWidth - filledWidth, 10); // 바의 나머지 부분
	}

	/** 퍼센트 값을 그리는 메서드 */
	public void drawVolumePercentage(Screen screen, int x, int y, int volume, boolean isSelected) {
		String volumeText = volume + "%";
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		drawCenteredRegularString(screen, volumeText, y); // 퍼센트 값을 중앙에 표시
	}

	/** Almost same function as drawVolumeBar to draw a bar to select the ship*/
	public void drawShipBoxes(int x, int y, boolean isSelected, int index, boolean current) {
		if(current){
			// Ship box
			backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
			backBufferGraphics.fillRect(x + index*60, y+index*20, isSelected ? 0 : 10, 10);
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.fillRect(x + index*60, y+index*20, (isSelected ? 10 : 0), 10);
			// Ship name
			backBufferGraphics.setFont(fontRegular);
			backBufferGraphics.drawString(Ship.ShipType.values()[index].name(), x + index*60 + 15, y+index*20);
		} else {
			// Ship box
			backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
			backBufferGraphics.fillRect(x + index*60, y+index*20, isSelected ? 0 : 10, 10);
			backBufferGraphics.setColor(Color.GRAY);
			backBufferGraphics.fillRect(x + index*60, y+index*20, (isSelected ? 10 : 0), 10);
			// Ship name
			backBufferGraphics.setFont(fontRegular);
			backBufferGraphics.drawString(Ship.ShipType.values()[index].name(), x + index*60 + 15, y + index*20);
		}

	}

	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height, boolean isSelected) {
		backBufferGraphics.setFont(fontRegular);
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws the game setting row.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param selectedRow
	 *            Selected row.
	 *
	 * @author <a href="mailto:dayeon.dev@gmail.com">Dayeon Oh</a>
	 *
	 */
	public void drawGameSettingRow(final Screen screen, final int selectedRow) {
		int y = 0;
		int height = 0;
		int screenHeight = screen.getHeight();

		if (selectedRow == 0) {
			y = screenHeight / 100 * 35;
			height = screen.getHeight() / 100 * 28;
		} else if (selectedRow == 1) {
			y = screenHeight / 100 * 63;
			height = screen.getHeight() / 100 * 18;
		} else if (selectedRow == 2) {
			y = screenHeight / 100 * 92;
			height = screen.getHeight() / 100 * 10;
		}

		backBufferGraphics.setColor(Color.DARK_GRAY);
		backBufferGraphics.fillRect(0, y, screen.getWidth(), height);
	}

	/**
	 * Draws the game setting elements.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param selectedRow
	 *            Selected row.
	 * @param name
	 *            Player 1 name.
	 * @param difficultyLevel
	 *            Difficulty level.
	 *
	 * @author <a href="mailto:dayeon.dev@gmail.com">Dayeon Oh</a>
	 *
	 */
	public void drawGameSettingElements(final Screen screen, final int selectedRow,
										final String name, final int difficultyLevel) {
		String spaceString = " ";
		String playerString = "Player";
		String levelEasyString = "Easy";
		String levelNormalString = "Normal";
		String levelHardString = "Hard";
		String startString = "Start";

		backBufferGraphics.setColor(Color.GREEN);

		drawCenteredRegularString(screen, playerString, screen.getHeight() / 100 * 43);
		drawCenteredRegularString(screen, name , screen.getHeight() / 100 * 58);

		if (difficultyLevel==0) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, levelEasyString + spaceString.repeat(60), screen.getHeight() / 100 * 73);

		if (difficultyLevel==1) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, levelNormalString, screen.getHeight() / 100 * 73);

		if (difficultyLevel==2) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, spaceString.repeat(60) + levelHardString, screen.getHeight() / 100 * 73);

		if (selectedRow == 2) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, startString, screen.getHeight() / 100 * 98);
	}

	/**
	 *  draw shop
	 * @param screen
	 * 				Screen to draw on.
	 * @param option
	 * 				selected shop item
	 * @param wallet
	 * 				player's wallet
	 * @param money_alertcooldown
	 * 				cooldown for insufficient coin alert
	 * @param max_alertcooldown
	 * 				cooldown for max level alert
	 */
	public void drawShop(final Screen screen, final int option, final Wallet wallet, final Cooldown money_alertcooldown, final Cooldown max_alertcooldown, final Cooldown max_bl_alertcooldown, final Cooldown needBl_alertcooldown) {

		String shopString = "Shop";
		int shopStringY = Math.round(screen.getHeight() * 0.15f);


		String coinString = ":  " + wallet.getCoin();
		String exitString = "PRESS \"ESC\" TO RETURN TO MAIN MENU";
		String[] costs = new String[]{"1000", "2000", "4000", "8000", "16000", "32000", "64000", "128000", "256000", "MAX LEVEL"};
		String[] breakLimitCosts = new String[]{"5000", "10000", "30000", "120000", "600000", "MAX LEVEL"};
		boolean isBreakLimitMode = ShopScreen.isBreakLimitMode;
		String GamblingString = "PRESS \"ENTER\" TO GO TO GAMBLING SPACE";

//		String[] itemString = new String[]{"BULLET SPEED", "SHOT INTERVAL", "ADDITIONAL LIFE", "COIN GAIN"};
//		int[] walletLevel = new int[]{wallet.getBullet_lv(), wallet.getShot_lv(), wallet.getLives_lv(), wallet.getCoin_lv()};
		int[] breakLimitLevel = new int[]{wallet.getBullet_bl(), wallet.getShot_bl(), wallet.getLives_bl(), wallet.getCoin_bl()};
		String[] itemString = new String[]{"BULLET SPEED", "SHOT INTERVAL", "ADDITIONAL LIFE","COIN GAIN", "GAMBLING"};
		int[] walletLevel = new int[]{wallet.getBullet_lv(), wallet.getShot_lv(), wallet.getLives_lv(), wallet.getCoin_lv(), 0};

		BufferedImage[] itemImages = new BufferedImage[]{img_bulletspeed, img_shotinterval, img_additionallife, img_coingain};

		int imgstartx = screen.getWidth() / 80 * 20;
		int imgstarty = screen.getHeight() / 80 * 27;
		int imgdis = screen.getHeight() / 80 * 12;
		int coinstartx = screen.getWidth() / 80 * 54;
		int coinstarty = screen.getHeight() / 160 * 73;
		int coindis = screen.getHeight() / 80 * 12;
		int coinSize = 20;
		int cointextstartx = screen.getWidth() / 80 * 58;
		int cointextstarty = screen.getHeight() / 160 * 78;
		int cointextdis = screen.getHeight() / 80 * 12;
		int breaklimitstartx = screen.getWidth() / 80 * 68;
		int breaklimitstarty = screen.getHeight() / 80 * 27;
		int breaklimitdis = screen.getHeight() / 80 * 12;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, shopString, shopStringY);
		int chkbreaklimitx = isBreakLimitMode ? screen.getWidth() / 80 * 69 - (coinString.length() - 6) * screen.getWidth() / 80 - 3 : screen.getWidth() / 80 * 36 - (coinString.length() - 6) * screen.getWidth() / 80 - 3;
		int chkbreaklimity = screen.getHeight() / 80 * 18 - 2;
		backBufferGraphics.drawImage(img_coin, chkbreaklimitx, chkbreaklimity, coinSize, coinSize, null);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.setFont(fontRegular);
		int coinx = isBreakLimitMode ? screen.getWidth() / 80 * 73 - (coinString.length() - 6) * screen.getWidth() / 80 - 3 : screen.getWidth() / 80 * 40 - (coinString.length() - 6) * screen.getWidth() / 100;
		int coiny = screen.getHeight() / 80 * 20;
		backBufferGraphics.drawString(coinString, coinx, coiny);


		for (int i = 0; i < 4; i++) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, itemString[i], screen.getHeight() / 80 * (28 + 12 * i));

			// Wallet에서 currentMaxLevel 가져오기
			int currentMaxLevel = wallet.getMaxLevel(i);

			String levelText = walletLevel[i] + " / " + currentMaxLevel;
			int textY = screen.getHeight() / 80 * (34 + 12 * i);
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredRegularString(screen, levelText, textY);
		}

		Font smallFont = fontRegular.deriveFont(Font.PLAIN, 12f);
		backBufferGraphics.setColor(Color.GRAY);
		backBufferGraphics.fillRect(screen.getWidth() - 120, screen.getHeight() / 80 * 5, 100, 40);
		FontMetrics metrics = backBufferGraphics.getFontMetrics();
		int textWidth = metrics.stringWidth("GAMBLING");
		int textHeight = metrics.getHeight();
		int textX = screen.getWidth() - 120 + (100 - textWidth) / 2; // X position
		int textY = screen.getHeight() / 80 * 5 + (40 - textHeight) / 2 + metrics.getAscent(); // Y position
		backBufferGraphics.setColor(Color.YELLOW);
		backBufferGraphics.drawString("GAMBLING", textX, textY);

		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawImage(itemImages[option - 1], imgstartx, imgstarty + (imgdis * (option - 1)), 50, 40, null);
		backBufferGraphics.setFont(smallFont);
		backBufferGraphics.drawImage(img_coin, coinstartx, coinstarty + (coindis * (option - 1)), coinSize, coinSize, null);
		backBufferGraphics.drawString("X " + (isBreakLimitMode ? breakLimitCosts[breakLimitLevel[option - 1] - 1] : costs[walletLevel[option - 1] - 1]), cointextstartx, cointextstarty + (cointextdis * (option - 1)));
		backBufferGraphics.drawString("Break Limit", breaklimitstartx, breaklimitstarty + (breaklimitdis * (option - 1)));

		String breakLimitText = breakLimitLevel[option - 1] - 1 + "/ 5";
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawString(breakLimitText, breaklimitstartx + 30, breaklimitstarty + 45 + (breaklimitdis * (option - 1)));

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen,exitString,screen.getHeight()/80*80);
		drawCenteredRegularString(screen,GamblingString,screen.getHeight()/90*90);
		drawCenteredRegularString(screen, exitString, screen.getHeight() / 80 * 80);


		if (!money_alertcooldown.checkFinished()) {
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth() - 300) / 2, (screen.getHeight() - 100) / 2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Insufficient coin", screen.getHeight() / 2);
		}
		if (!max_alertcooldown.checkFinished()) {
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth() - 300) / 2, (screen.getHeight() - 100) / 2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Max Level", screen.getHeight() / 2);
		}
		if (!max_bl_alertcooldown.checkFinished()) {
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth() - 300) / 2, (screen.getHeight() - 100) / 2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Break Limit Max", screen.getHeight() / 2);
		}
		if (!needBl_alertcooldown.checkFinished()) {
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth() - 300) / 2, (screen.getHeight() - 100) / 2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Needs Break Limit", screen.getHeight() / 2);
		}
	}

	public void drawGambling(Screen screen, int bettingAmount) {
		// 제목: BLACKJACK
		String BJString = "BLACKJACK";
		backBufferGraphics.setColor(Color.YELLOW);
		drawCenteredBigString(screen, BJString, screen.getHeight() / 6);

		// 안내 메시지: BLACKJACK 테이블로 이동 (Q)
		String enterString = "PRESS \"Q\" TO ENTER TO BLACKJACK TABLE";
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, enterString, screen.getHeight() / 4);

		// 안내 메시지: 메인 메뉴로 복귀 (ESC)
		String exitString = "PRESS \"ESC\" TO RETURN TO MAIN MENU";
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight() - 50);

		// Blackjack 규칙 설명 첫 줄
		String ruleExplanationLine1 = "Blackjack rule explanation:";
		FontMetrics metrics = backBufferGraphics.getFontMetrics();
		int ruleTextWidth1 = metrics.stringWidth(ruleExplanationLine1);
		int ruleTextX1 = (screen.getWidth() - ruleTextWidth1) / 2;
		int ruleTextY1 = (screen.getHeight() / 2) - 50;

		backBufferGraphics.setColor(Color.LIGHT_GRAY);
		backBufferGraphics.drawString(ruleExplanationLine1, ruleTextX1, ruleTextY1);

		// Blackjack 규칙 설명 두 번째 줄
		String ruleExplanationLine2 = "If you don't know the rules, you can learn by losing money.";
		int ruleTextWidth2 = metrics.stringWidth(ruleExplanationLine2);
		int ruleTextX2 = (screen.getWidth() - ruleTextWidth2) / 2;
		int ruleTextY2 = ruleTextY1 + 20;

		backBufferGraphics.setColor(Color.red);
		backBufferGraphics.drawString(ruleExplanationLine2, ruleTextX2, ruleTextY2);

		// 배팅 금액 표시
		String bettingString = "Betting Amount: " + bettingAmount;
		int bettingTextWidth = metrics.stringWidth(bettingString);
		int bettingTextX = (screen.getWidth() - bettingTextWidth) / 2;
		int bettingTextY = ruleTextY2 + 100;

		backBufferGraphics.setColor(Color.CYAN);
		backBufferGraphics.drawString(bettingString, bettingTextX, bettingTextY);
	}

	public void drawRotatedBullet(CurvedBullet bullet, int positionX, int positionY, double angle) {
		Graphics2D g2d = (Graphics2D) backBufferGraphics;
		AffineTransform oldTransform = g2d.getTransform();

		// 회전 중심 계산
		int centerX = positionX + bullet.getWidth() / 2;
		int centerY = positionY + bullet.getHeight() / 2;

		// 각도 설정 및 회전
		g2d.rotate(Math.sin(angle), centerX, centerY);
		drawEntity(bullet, positionX, positionY);

		g2d.setTransform(oldTransform); // 원래 상태 복구
	}

}