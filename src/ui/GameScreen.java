package ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;


import engine.core.Core;
import engine.core.GameSettings;
import engine.core.GameState;
import engine.manager.DrawManager;
import engine.manager.ItemManager;
import engine.manager.SoundManager;
import engine.utility.Cooldown;
import engine.utility.Score;
import engine.utility.Sound;
import entity.*;


/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private final GameSettings gameSettings;
	/** Current difficulty level number. */
	private final int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	private EnemyShipFormation specialEnemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
    public EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
    public Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
    public Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;

	private Set<CurvedBullet> curvedBullets;
	private Set<ExplosionBullet> explosionBullets;

	private int score;
	/** tempScore records the score up to the previous level. */
	private int tempScore;
	/** Current ship type. */
	private final Ship.ShipType shipType;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Number of consecutive hits.
	 * maxCombo records the maximum value of combos in that level. */
	private int combo;
	private int maxCombo;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private final boolean bonusLife;
	/** Elapsed time while playing this game.
	 * lapTime records the time to the previous level. */
	private int elapsedTime;
	private int lapTime;
	/** Keep previous timestamp. */
	private Integer prevTime;
	/** Alert Message when a special enemy appears. */
    public String alertMessage;
	/** checks if it's executed. */
  	private boolean isExecuted = false;
	/** timer.. */
	private Timer timer;
    /** Spider webs restricting player movement */
	private List<Web> web;
	/**
	 * Obstacles preventing a player's bullet
	 */
	private List<Block> block;

	private Wallet wallet;
	/* Blocker appearance cooldown */
	private final Cooldown blockerCooldown;
	private final Random random;
	private final List<Blocker> blockers = new ArrayList<>();
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();
	/** Singleton instance of ItemManager. */
	private ItemManager itemManager;
	/** Item boxes that dropped when kill enemy ships. */
	private Set<ItemBox> itemBoxes;
	/** Barriers appear in game screen. */
	private Set<Barrier> barriers;
	/** Sound balance for each player*/

	private int MAX_BLOCKERS = 0;

	private final GameState gameState;

	private int hitBullets;
	public boolean isSpecialEnemySpawned = false; // 특별 함선 생성 여부 추적
	private boolean movingRight = true; // 초기 방향은 오른쪽
	public boolean bossDestroyed = false;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps, final Wallet wallet) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.gameState = gameState;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.elapsedTime = gameState.getElapsedTime();
		this.alertMessage = gameState.getAlertMessage();
		this.shipType = gameState.getShipType();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.maxCombo = gameState.getMaxCombo();
		this.lapTime = gameState.getPrevTime();
		this.tempScore = gameState.getPrevScore();

		this.hitBullets = gameState.getHitBullets();

		this.wallet = wallet;

		this.random = new Random();
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();

		this.alertMessage = "";

		this.wallet = wallet;
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
		enemyShipFormation.attach(this);
		specialEnemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
		specialEnemyShipFormation.attach(this);
        // Appears each 10-30 seconds.
        this.ship = ShipFactory.create(this.shipType, this.width / 2, this.height - 30);
		logger.info("Player ship created " + this.shipType + " at " + this.ship.getPositionX() + ", " + this.ship.getPositionY());
        ship.applyItem(wallet);
		//Create random Spider Web.
		int web_count = 1 + level / 3;
		web = new ArrayList<>();
		for(int i = 0; i < web_count; i++) {
			double randomValue = Math.random();
			this.web.add(new Web((int) Math.max(0, randomValue * width - 12 * 2), this.height - 30));
			this.logger.info("Spider web creation location : " + web.get(i).getPositionX());
		}
		//Create random Block.
		int blockCount = level / 2;
		int playerTopY_contain_barrier = this.height - 40 - 150;
		int enemyBottomY = 100 + (gameSettings.getFormationHeight() - 1) * 48;
		this.block = new ArrayList<Block>();
		for (int i = 0; i < blockCount; i++) {
			Block newBlock;
			boolean overlapping;
			do {
				newBlock = new Block(0,0);
				int positionX = (int) (Math.random() * (this.width - newBlock.getWidth()));
				int positionY = (int) (Math.random() * (playerTopY_contain_barrier - enemyBottomY - newBlock.getHeight())) + enemyBottomY;
				newBlock = new Block(positionX, positionY);
				overlapping = false;
				for (Block block : block) {
					if (checkCollision(newBlock, block)) {
						overlapping = true;
						break;
					}
				}
			} while (overlapping);
			block.add(newBlock);
		}


		int bossInterval = 5000 + (5000 * gameState.getLevel());
		// Appears each 5 + 5 * (gameLevel) seconds.
		this.enemyShipSpecialCooldown = Core.getCooldown(
				bossInterval);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<>();
		this.curvedBullets = new HashSet<>();
		this.explosionBullets = new HashSet<>();
		this.barriers = new HashSet<>();
        this.itemBoxes = new HashSet<>();
		this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, this.height, this.width);

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.stopSound(Sound.BGM_MAIN);
		soundManager.playSound(Sound.COUNTDOWN);

		switch (this.level) {
			case 1: soundManager.loopSound(Sound.BGM_LV1); break;
			case 2: soundManager.loopSound(Sound.BGM_LV2); break;
			case 3: soundManager.loopSound(Sound.BGM_LV3); break;
			case 4: soundManager.loopSound(Sound.BGM_LV4); break;
			case 5: soundManager.loopSound(Sound.BGM_LV5); break;
			case 6: soundManager.loopSound(Sound.BGM_LV6); break;
            case 7:
				// From level 7 and above, it continues to play at BGM_LV7.
            default: soundManager.loopSound(Sound.BGM_LV7); break;
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		if(this.lives == 0) this.score += 100;
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();
		Random random = new Random();
		int choice = random.nextInt(2); // 0이면 shootExplosion, 1이면 shootCurved
		if (this.inputDelay.checkFinished() && !this.levelFinished) {
			boolean playerAttacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);

			if (playerAttacking) {
				if (this.ship.shoot(this.bullets, this.itemManager.getShotNum())) {
					this.bulletsShot += this.itemManager.getShotNum();
				}
			}

			/*Elapsed Time Update*/
			long currentTime = System.currentTimeMillis();

			if (this.prevTime != null)
				this.elapsedTime += (int) (currentTime - this.prevTime);

			this.prevTime = (int) currentTime;

			if(!itemManager.isGhostActive())
				this.ship.setColor(Color.GREEN);

			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
				for(int i = 0; i < web.size(); i++) {
					//escape Spider Web
					if (ship.getPositionX() + 6 <= web.get(i).getPositionX() - 6
							|| web.get(i).getPositionX() + 6 <= ship.getPositionX() - 6) {
						this.ship.setThreadWeb(false);
					}
					//get caught in a spider's web
					else {
						this.ship.setThreadWeb(true);
						break;
					}
				}
			}

			if (this.enemyShipSpecial != null) {
				specialEnemyShipFormation.update();
				if (!this.enemyShipSpecial.isDestroyed()) {
					this.alertMessage = "Boss's Health: " + (this.enemyShipSpecial.getHealth()+1);
					// 양옆으로 움직이는 로직 추가
					if (movingRight) {
						this.enemyShipSpecial.move(2, 0); // 오른쪽으로 이동
						if (this.enemyShipSpecial.getPositionX() + this.enemyShipSpecial.getWidth() >= this.width) {
							movingRight = false; // 오른쪽 끝에 도달하면 방향 변경
						}
					} else {
						this.enemyShipSpecial.move(-2, 0); // 왼쪽으로 이동
						if (this.enemyShipSpecial.getPositionX() <= 0) {
							movingRight = true; // 왼쪽 끝에 도달하면 방향 변경
						}
					}
					if (choice == 0) {
						this.specialEnemyShipFormation.shootExplosion(this.enemyShipSpecial, this.explosionBullets);
					} else {
						this.specialEnemyShipFormation.shootCurved(this.enemyShipSpecial, this.curvedBullets);
					}
				} else if (this.enemyShipSpecialExplosionCooldown.checkFinished()) {
					this.enemyShipSpecial = null;
					this.bossDestroyed = true;
					this.alertMessage = "!!  You destroyed the boss  !!";
				}
			}

			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()&& !this.isSpecialEnemySpawned) {
				this.enemyShipSpecial = new EnemyShip(gameState);
				this.isSpecialEnemySpawned = true; // 생성된 이후에는 다시 생성되지 않도록 설정
				this.alertMessage = "Boss's Health : " + enemyShipSpecial.getHealth();


				this.enemyShipSpecialCooldown.reset();
				soundManager.playSound(Sound.UFO_APPEAR);
				this.logger.info("A special ship appears");
			}
			if(this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkAlert()&& !bossDestroyed) {
				switch (this.enemyShipSpecialCooldown.checkAlertAnimation()){
					case 1: this.alertMessage = "Boss spawn 3 seconds ago";
						break;

					case 2: this.alertMessage = "Boss spawn 2 seconds ago";
						break;

					case 3: this.alertMessage = "Boss spawn 1 seconds ago";
						break;

					default: this.alertMessage = "";
						break;
				}

			}


			this.ship.update();

			// If Time-stop is active, Stop updating enemy ships' move and their shoots.
			if (!itemManager.isTimeStopActive()) {
				this.enemyShipFormation.update();
				this.enemyShipFormation.shoot(this.bullets, this.level);
			}

			if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
				handleBlockerAppearance();
			}
		}

		manageCollisions();
		cleanBullets();
		draw();

		if (((this.enemyShipFormation.isEmpty() && bossDestroyed
				|| this.lives <= 0))
				&& !this.levelFinished) {
			this.levelFinished = true;
			bossDestroyed = false;

			soundManager.stopSound(soundManager.getCurrentBGM());
			if (this.lives == 0)
				soundManager.playSound(Sound.GAME_END);
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
			//Reset alert message when level is finished
			this.alertMessage = "";
			this.isRunning = false;
		}
	}


	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawGameTitle(this);

		drawManager.drawLaunchTrajectory( this,this.ship.getPositionX());

		drawManager.drawEntity(this.ship, this.ship.getPositionX(), this.ship.getPositionY());

		//draw Spider Web
        for (Web value : web) {
            drawManager.drawEntity(value, value.getPositionX(),
                    value.getPositionY());
        }
		//draw Blocks
		for (Block block : block)
			drawManager.drawEntity(block, block.getPositionX(),
					block.getPositionY());


		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (ItemBox itemBox : this.itemBoxes)
			drawManager.drawEntity(itemBox, itemBox.getPositionX(), itemBox.getPositionY());

		for (Barrier barrier : this.barriers)
			drawManager.drawEntity(barrier, barrier.getPositionX(), barrier.getPositionY());

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());
		for (CurvedBullet bullet : this.curvedBullets)
			drawManager.drawRotatedBullet(bullet, bullet.getPositionX(), bullet.getPositionY(), bullet.getAngle());
		for (ExplosionBullet bullet : this.explosionBullets) {
			if (!bullet.getExploded()) {
				drawManager.drawEntity(bullet, bullet.getPositionX(), bullet.getPositionY());
			}
			for (ExplosionBullet child : bullet.getChildBullets()) {
				drawManager.drawEntity(child, child.getPositionX(), child.getPositionY());
			}
		}


		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawElapsedTime(this, this.elapsedTime);
		drawManager.drawAlertMessage(this, this.alertMessage);
		drawManager.drawLives(this.lives, this.shipType);
		drawManager.drawLevel(this, this.level);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
		drawManager.drawReloadTimer(this.ship,ship.getRemainingReloadTime());
		drawManager.drawCombo(this,this.combo);


		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown, this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);

			//Intermediate aggregation
			if (this.level > 1){
                if (countdown == 0) {
					//Reset mac combo and edit temporary values
                    this.lapTime = this.elapsedTime;
                    this.tempScore = this.score;
                    this.maxCombo = 0;
                } else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
                    drawManager.interAggre(this, this.level - 1, this.maxCombo, this.elapsedTime, this.lapTime, this.score, this.tempScore);
                }
			}
		}

		// Blocker drawing part
		if (!blockers.isEmpty()) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, blocker.getPositionX(), blocker.getPositionY(), blocker.getAngle());
			}
		}

		drawManager.completeDrawing();
	}

	// Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) MAX_BLOCKERS = 1;
		else if (level >= 6 && level < 11) MAX_BLOCKERS = 2;
		else if (level >= 11) MAX_BLOCKERS = 3;

		int kind = random.nextInt(2-1 + 1) +1; // 1~2
		DrawManager.SpriteType newSprite = switch (kind) {
            case 1 -> DrawManager.SpriteType.Blocker1; // artificial satellite
            case 2 -> DrawManager.SpriteType.Blocker2; // astronaut
            default -> DrawManager.SpriteType.Blocker1;
        };

        // Check number of blockers, check timing of exit
		if (blockers.size() < MAX_BLOCKERS && blockerCooldown.checkFinished()) {
			boolean moveLeft = random.nextBoolean(); // Randomly sets the movement direction of the current blocker
			int startY = random.nextInt(this.height - 90) + 25; // Random Y position with margins at the top and bottom of the screen
			int startX = moveLeft ? this.width + 300 : -300; // If you want to move left, outside the right side of the screen, if you want to move right, outside the left side of the screen.
			// Add new Blocker
			if (moveLeft) {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from right to left
			} else {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from left to right
			}
			blockerCooldown.reset();
		}

		// Items in the blocker list that will disappear after leaving the screen
		List<Blocker> toRemove = new ArrayList<>();
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// If the blocker leaves the screen, remove it directly from the list.
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300 || !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				blockers.remove(i);
				i--; // When an element is removed from the list, the index must be decreased by one place.
				continue;
			}

			// Blocker movement and rotation (positionX, Y value change)
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // move left
			} else {
				blocker.move(1.5, 0); // move right
			}
			blocker.rotate(0.2); // Blocker rotation
		}

		// Remove from the blocker list that goes off screen
		blockers.removeAll(toRemove);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclableNormal = new HashSet<>();
		Set<CurvedBullet> recyclableCurved = new HashSet<>();
		Set<ExplosionBullet> recyclableExplosion = new HashSet<>();

		// 일반 총알 정리
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT || bullet.getPositionY() > this.height) {
				recyclableNormal.add(bullet);
			}
		}
		this.bullets.removeAll(recyclableNormal);
		BulletPool.recycleNormal(recyclableNormal);

		// 곡선 총알 정리
		for (CurvedBullet bullet : this.curvedBullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT || bullet.getPositionY() > this.height) {
				recyclableCurved.add(bullet);
			}
		}
		this.curvedBullets.removeAll(recyclableCurved);
		BulletPool.recycleCurved(recyclableCurved);

		// 폭파 총알 정리
		for (ExplosionBullet bullet : this.explosionBullets) {
			bullet.update();

			// 자식 총알 정리
			Set<ExplosionBullet> childToRecycle = new HashSet<>();
			for (ExplosionBullet child : bullet.getChildBullets()) {
				child.update();
				if (child.getPositionY() < SEPARATION_LINE_HEIGHT || child.getPositionY() > this.height) {
					childToRecycle.add(child);
				}
			}
			bullet.getChildBullets().removeAll(childToRecycle);
			recyclableExplosion.addAll(childToRecycle);

			// 부모 총알 정리 (폭발 상태이며 자식이 모두 제거된 경우)
			if (bullet.getExploded() && bullet.getChildBullets().isEmpty()) {
				recyclableExplosion.add(bullet);
			}
		}
		this.explosionBullets.removeAll(recyclableExplosion);
		BulletPool.recycleExplosion(recyclableExplosion);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclableNomal = new HashSet<Bullet>();
		Set<CurvedBullet> recyclableCurved = new HashSet<CurvedBullet>();
		Set<ExplosionBullet> recyclableExplosion = new HashSet<ExplosionBullet>();
		if (!isExecuted){
			isExecuted = true;
			timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    combo = 0;
                }
            };
			timer.schedule(timerTask, 3000);
		}

		int topEnemyY = Integer.MAX_VALUE;
		for (EnemyShip enemyShip : this.enemyShipFormation) {
			if (enemyShip != null && !enemyShip.isDestroyed() && enemyShip.getPositionY() < topEnemyY) {
				topEnemyY = enemyShip.getPositionY();
			}
		}
		if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed() && this.enemyShipSpecial.getPositionY() < topEnemyY) {
			topEnemyY = this.enemyShipSpecial.getPositionY();
		}

		for (Bullet bullet : this.bullets) {

			// Enemy ship's bullets
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
					recyclableNomal.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						damage();
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
					}
				}

				if (this.barriers != null) {
					Iterator<Barrier> barrierIterator = this.barriers.iterator();
					while (barrierIterator.hasNext()) {
						Barrier barrier = barrierIterator.next();
						if (checkCollision(bullet, barrier)) {
							recyclableNomal.add(bullet);
							barrier.reduceHealth();
							if (barrier.isDestroyed()) {
								barrierIterator.remove();
							}
						}
					}
				}

			} else {	// Player ship's bullets
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (enemyShip != null && !enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						// Decide whether to destroy according to physical strength
						this.enemyShipFormation.HealthManageDestroy(enemyShip);
						// If the enemy doesn't die, the combo increases;
						// if the enemy dies, both the combo and score increase.
						this.score += Score.comboScore(this.enemyShipFormation.getPoint(), this.combo);
						this.shipsDestroyed += this.enemyShipFormation.getDistroyedship();
						this.combo++;
						this.hitBullets++;
						if (this.combo > this.maxCombo) this.maxCombo = this.combo;
						timer.cancel();
						isExecuted = false;
						recyclableNomal.add(bullet);
						if (enemyShip.getHealth() < 0 && itemManager.dropItem()) {
							this.itemBoxes.add(new ItemBox(enemyShip.getPositionX() + 6, enemyShip.getPositionY() + 1));
							logger.info("Item box dropped");
						}
					}

				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.specialEnemyShipFormation.HealthManageDestroy(enemyShipSpecial);
					this.score += Score.comboScore(this.enemyShipSpecial.getPointValue(), this.combo);
					this.shipsDestroyed++;
					this.combo++;
					this.hitBullets++;
					if (this.combo > this.maxCombo) this.maxCombo = this.combo;
					this.enemyShipSpecialExplosionCooldown.reset();
					timer.cancel();
					isExecuted = false;

					recyclableNomal.add(bullet);
				}

				if (this.itemManager.getShotNum() == 1 && bullet.getPositionY() < topEnemyY) {
					this.combo = 0;
					isExecuted = true;
				}

				Iterator<ItemBox> itemBoxIterator = this.itemBoxes.iterator();
				while (itemBoxIterator.hasNext()) {
					ItemBox itemBox = itemBoxIterator.next();
					if (checkCollision(bullet, itemBox) && !itemBox.isDroppedRightNow()) {
						this.hitBullets++;
						itemBoxIterator.remove();
						recyclableNomal.add(bullet);
						Entry<Integer, Integer> itemResult = this.itemManager.useItem();

						if (itemResult != null) {
							this.score += itemResult.getKey();
							this.shipsDestroyed += itemResult.getValue();
						}
					}
				}

				//check the collision between the obstacle and the bullet
				for (Block block : this.block) {
					if (checkCollision(bullet, block)) {
						recyclableNomal.add(bullet);
                        soundManager.playSound(Sound.BULLET_BLOCKING);
						break;
					}
				}
			}
		}

		//check the collision between the obstacle and the enemyship
		Set<Block> removableBlocks = new HashSet<>();
		for (EnemyShip enemyShip : this.enemyShipFormation) {
			if (enemyShip != null && !enemyShip.isDestroyed()) {
				for (Block block : block) {
					if (checkCollision(enemyShip, block)) {
						removableBlocks.add(block);
					}
				}
			}
		}

		for (CurvedBullet bullet : this.curvedBullets) {
			if (checkCollision(bullet, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
				recyclableCurved.add(bullet);
				if (!this.ship.isDestroyed()) {
					this.ship.destroy();
					damage();
					this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
				}
			}
			if (this.barriers != null) {
				Iterator<Barrier> barrierIterator = this.barriers.iterator();
				while (barrierIterator.hasNext()) {
					Barrier barrier = barrierIterator.next();
					if (checkCollision(bullet, barrier)) {
						recyclableCurved.add(bullet);
						barrier.reduceHealth();
						if (barrier.isDestroyed()) {
							barrierIterator.remove();
						}
					}
				}
			}
		}

		for (ExplosionBullet bullet : this.explosionBullets) {
			for (ExplosionBullet child : bullet.getChildBullets()) {
				if (checkCollision(child, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
					recyclableExplosion.add(child);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						damage();
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
					}
				}
				if (this.barriers != null) {
					Iterator<Barrier> barrierIterator = this.barriers.iterator();
					while (barrierIterator.hasNext()) {
						Barrier barrier = barrierIterator.next();
						if (checkCollision(child, barrier)) {
							recyclableExplosion.add(child);
							barrier.reduceHealth();
							if (barrier.isDestroyed()) {
								barrierIterator.remove();
							}
						}
					}
				}
			}
		}

		// remove crashed obstacle
		block.removeAll(removableBlocks);
		this.bullets.removeAll(recyclableNomal);
		this.curvedBullets.removeAll(recyclableCurved);
		this.explosionBullets.removeAll(recyclableExplosion);
		BulletPool.recycleNormal(recyclableNomal);
		BulletPool.recycleCurved(recyclableCurved);
		BulletPool.recycleExplosion(recyclableExplosion);
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.shipType, this.lives,
				this.bulletsShot, this.shipsDestroyed, this.elapsedTime, this.alertMessage, this.maxCombo, this.lapTime, this.tempScore, this.hitBullets);
	}
	public void damage(){
		for(int i=0; i<=level/3;i++){
			this.lives--;
		}
		if(this.lives < 0){
			this.lives = 0;
		}
	}
}
