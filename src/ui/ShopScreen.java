package ui;

import java.awt.event.KeyEvent;

import engine.manager.DrawManager;
import engine.utility.Cooldown;
import engine.core.Core;
import engine.utility.Sound;
import engine.manager.SoundManager;
import entity.Wallet;
import blackjack.*;




public class ShopScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    private static final int ALERT_TIME = 1500;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /** Time until not enough coin alert disappear */
    private Cooldown money_alertCooldown;

    /** Time until max_lv alert disappear */
    private Cooldown max_alertCooldown;

    /** Time until max_bl alert disappear */
    private Cooldown max_bl_alertCooldown;
    /** Time until needBl alert disappear */
    private Cooldown needBl_alertCooldown;

    /** Player's wallet */
    private Wallet wallet;
    /** 1-bullet speed 2-shot frequency 3-additional lives 4-gain coin upgrade */
    private int selected_item;

    private final int[] lvCost = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 256000};

    private final int[] blCost = {5000, 10000, 30000, 120000, 600000};

    public static boolean isBreakLimitMode;
    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     * @param wallet
     *            Player's wallet
     */
    public ShopScreen(final int width, final int height, final int fps, final Wallet wallet) {
        super(width, height, fps);

        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.money_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.max_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.max_bl_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.needBl_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.wallet = wallet;
        selected_item = 1;
        isBreakLimitMode = false;

        soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_SHOP);

        this.drawManager = DrawManager.getInstance();  // DrawManager 초기화 확인
    }

    /**
     * Starts the action.
     *
     * @return Next screen code(MainMenu) 1.
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

        // ENTER 키를 눌렀을 때 갬블링 화면으로 이동
        if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
            this.isRunning = false; // ShopScreen 종료
            GamblingScreen gamblingScreen = new GamblingScreen(this.width, this.height, this.fps, this.wallet);
            gamblingScreen.run(); // 다음 화면 실행
            return;
        }

        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()
                && this.money_alertCooldown.checkFinished()
                && this.max_alertCooldown.checkFinished()
                && this.max_bl_alertCooldown.checkFinished()
                && this.needBl_alertCooldown.checkFinished()) {

            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
                    || inputManager.isKeyDown(KeyEvent.VK_A)
                    || inputManager.isKeyDown(KeyEvent.VK_RIGHT)
                    || inputManager.isKeyDown(KeyEvent.VK_D)) {
                toggleUpgradeMode();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if(isBreakLimitMode){
                    switch (selected_item){
                        case 1:
                            if (blUpgrade(wallet.getBullet_bl())) {
                                wallet.setBullet_bl(wallet.getBullet_bl() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 2:
                            if (blUpgrade(wallet.getShot_bl())) {
                                wallet.setShot_bl(wallet.getShot_bl() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 3:
                            if (blUpgrade(wallet.getLives_bl())) {
                                wallet.setLives_bl(wallet.getLives_bl() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 4:
                            if (blUpgrade(wallet.getCoin_bl())) {
                                wallet.setCoin_bl(wallet.getCoin_bl() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        default:
                            break;
                    }
                }
                else {
                    this.selectionCooldown.reset();
                    switch (selected_item) {
                        case 1:
                            if (upgrade(wallet.getBullet_lv())) {
                                wallet.setBullet_lv(wallet.getBullet_lv() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 2:
                            if (upgrade(wallet.getShot_lv())) {
                                wallet.setShot_lv(wallet.getShot_lv() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 3:
                            if (upgrade(wallet.getLives_lv())) {
                                wallet.setLives_lv(wallet.getLives_lv() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        case 4:
                            if (upgrade(wallet.getCoin_lv())) {
                                wallet.setCoin_lv(wallet.getCoin_lv() + 1);
                                this.selectionCooldown.reset();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
                soundManager.stopSound(Sound.BGM_SHOP);
            }
        }
    }

    private void toggleUpgradeMode() {
        isBreakLimitMode = !isBreakLimitMode;
    }

    /**
     * Shifts the focus to the next shop item.
     */
    private void nextMenuItem() {
        if (this.selected_item == 4)
            this.selected_item = 1;
        else
            this.selected_item++;
    }

    /**
     * Shifts the focus to the previous shop item.
     */
    private void previousMenuItem() {
        if (this.selected_item == 1)
            this.selected_item = 4;
        else
            this.selected_item--;
    }

    private void draw() {
        drawManager.initDrawing(this);


        drawManager.drawShop(this,selected_item,wallet,money_alertCooldown,max_alertCooldown, max_bl_alertCooldown, needBl_alertCooldown);

        drawManager.completeDrawing(this);
    }

    public boolean upgrade(int level)
    {
        if (level == 10) {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            max_alertCooldown.reset();
            return false;
        }
        if (level >= 5) {
            if (!wallet.blockWithdraw(selected_item)) {
                needBl_alertCooldown.reset();
                return false;
            }
            else if(wallet.withdraw(lvCost[level-1]))
            {
                soundManager.playSound(Sound.COIN_USE);
                return true;
            }
            else
            {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                money_alertCooldown.reset();
                return false;
            }
        } else {
            if (wallet.withdraw(lvCost[level - 1])) {
                soundManager.playSound(Sound.COIN_USE);
                return true;
            } else {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                money_alertCooldown.reset();
                return false;
            }
        }
    }

    public boolean blUpgrade(int level){
        if (level >= 6) {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            max_bl_alertCooldown.reset();
            return false;
        }
        if (wallet.withdraw(blCost[level-1])) {
            soundManager.playSound(Sound.COIN_USE);
            return true;
        } else
        {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            money_alertCooldown.reset();
            return false;
        }
    }
}