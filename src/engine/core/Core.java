package engine.core;
import javax.swing.*; // GUI 요소 가져오기

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.*;
import engine.utility.*;
import engine.manager.*;
import entity.Achievement;
import entity.Ship;
import entity.Wallet;
import ui.*;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 600;
	/** Height of current screen. */
	private static final int HEIGHT = 650;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Base ship type. */
	public static Ship.ShipType BASE_SHIP = Ship.ShipType.StarDefender;
	/** Max lives. */
	public static int MAX_LIVES;
	/** Levels between extra life. */
	public static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;
	/** Initialize singleton instance of SoundManager and return that */
	private static final SoundManager soundManager = SoundManager.getInstance();

	private static long startTime, endTime;

	private static int DifficultySetting;// <- setting EASY(0), NORMAL(1), HARD(2);

	private static DatabaseManager db;  // UserDatabase 객체 추가

	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main (final String[] args) throws IOException {
		try {
			// Cross-platform Look and Feel 설정
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		db = new DatabaseManager(); // UserDatabase 객체 생성
		db.createTable(); // 데이터베이스 users 테이블 생성 (최초 1회 실행 필요)
		LoginFrame login = new LoginFrame(db);// 로그인 창 생성 및 표시

		while (WelcomeFrame.getStart()) {
			try {
				Thread.sleep(100); // CPU 부담을 줄이기 위해 대기
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();
		Wallet wallet = db.loadWalletData(login.getID());
		int returnCode = 1;
		do {
			MAX_LIVES = 3+ wallet.getLives_lv()-1;
			GameState gameState = new GameState(1, 0, BASE_SHIP, MAX_LIVES, 0, 0, 0, "", 0, 0, 0 ,0);
			Achievement achievement = db.loadAchData(login.getID());
			AchievementManager achievementManager = new AchievementManager(achievement);

			GameSettings gameSetting = new GameSettings(4, 4, 60, 2500);

			switch (returnCode) {
			case 1:
				// Main menu.
				currentScreen = new TitleScreen(width, height, FPS, wallet);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " title screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing title screen.");
				break;
			case 2:
				// Game & score.
				do {
					// One extra live every few levels.
					startTime = System.currentTimeMillis();
					boolean bonusLife = gameState.getLevel()
							% EXTRA_LIFE_FRECUENCY == 0
							&& gameState.getLivesRemaining() < MAX_LIVES;
					LOGGER.info("difficulty is " + DifficultySetting);
					//add variation
					gameSetting = gameSetting.LevelSettings(gameSetting.getFormationWidth(),
							gameSetting.getFormationHeight(),
							gameSetting.getBaseSpeed(),
							gameSetting.getShootingFrecuency(),
							gameState.getLevel(), DifficultySetting);

					currentScreen = new GameScreen(gameState,
							gameSetting,
							bonusLife, width, height, FPS, wallet);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT

							+ " game screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing game screen.");

					gameState = ((GameScreen) currentScreen).getGameState();

					gameState = new GameState(gameState, gameState.getLevel() + 1);
					endTime = System.currentTimeMillis();
					achievementManager.updatePlaying(gameState.getMaxCombo(),(int) (endTime - startTime) / 1000, MAX_LIVES, gameState.getLivesRemaining(), gameState.getLevel()-1);
				} while (gameState.getLivesRemaining() > 0);
				achievementManager.updatePlayed(gameState.getAccuracy(), gameState.getScore());
                achievementManager.updateAllAchievements();
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " score screen at " + FPS + " fps, with a score of "
						+ gameState.getScore() + ", "
						+ gameState.getShipType().toString() + " ship, "
						+ gameState.getLivesRemaining() + " lives remaining, "
						+ gameState.getBulletsShot() + " bullets shot and "
						+ gameState.getShipsDestroyed() + " ships destroyed.");
				currentScreen = new ScoreScreen(width, height, FPS, gameState, wallet, achievementManager);

				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing score screen.");
				break;

			case 3:
				//Shop
				currentScreen = new ShopScreen(width, height, FPS, wallet);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " Shop screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing Shop screen.");
				break;

			case 4:
				// Achievement
				currentScreen = new AchievementScreen(width, height, FPS, achievement);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " achievement screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing Achievement screen.");
				break;

			case 5:
				//Setting
				currentScreen = new SettingScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " setting screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing Setting screen.");
				break;

			case 6:
				//Game Setting
				currentScreen = new GameSettingScreen(login.getID(), width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " game setting screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing game setting screen.");
        		break;

			case 7:
				//Credit Screen
				currentScreen = new CreditScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " credit screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing credit screen.");
				break;
			default:
				break;
			}

		} while (returnCode != 0);
		fileHandler.flush();
		fileHandler.close();
		soundManager.closeAllSounds();
		shutdown();

		System.exit(0);
	}
	// 프로그램 종료 시 DB 연결 닫기
	public static void shutdown() {
		db.closeConnection();  // DB 연결을 닫는 메서드 호출
	}
	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}

	/**
	 * @param level set LevelSetting from GameSettingScreen
	 */
	public static void setLevelSetting(final int level) {
		DifficultySetting = level;
	}

	public static int getLevelSetting(){
		return DifficultySetting;
	}
}