package ui;

import java.awt.event.KeyEvent;

import engine.utility.Cooldown;
import engine.core.Core;
import engine.utility.Sound;
import engine.manager.SoundManager;
import entity.Wallet;

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

    /** Player's wallet */
    private Wallet wallet;

    /** 1-bullet speed 2-shot frequency 3-additional lives 4-gain coin upgrade */
    private int selected_item;

    /** price per upgrade level */
    private int lv1cost = 1000;
    private int lv2cost = 2000;
    private int lv3cost = 4000;
    private int lv4cost = 8000;
    private int lv5cost = 16000;
    private int lv6cost = 32000;
    private int lv7cost = 64000;
    private int lv8cost = 128000;
    private int lv9cost = 256000;
    private int bl1cost = 5000;
    private int bl2cost = 10000;
    private int bl3cost = 30000;
    private int bl4cost = 120000;
    private int bl5cost = 600000;

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
        this.wallet = wallet;
        selected_item = 1;
        isBreakLimitMode = false;

        soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_SHOP);
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
        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()
                && this.money_alertCooldown.checkFinished()
                && this.max_alertCooldown.checkFinished()) {

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
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
            {
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


        drawManager.drawShop(this,selected_item,wallet,money_alertCooldown,max_alertCooldown);

        drawManager.completeDrawing(this);
    }

    public boolean upgrade(int level)
    {
        if(level == 1)
        {
            if(wallet.withdraw(lv1cost))
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
        }
        else if(level == 2)
        {
            if(wallet.withdraw(lv2cost))
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
        }
        else if(level == 3)
        {
            if(wallet.withdraw(lv3cost))
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
        }
        else if(level == 4)
        {
            if(wallet.withdraw(lv4cost))
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
        }
        else if(level == 5)
        {
            if(wallet.withdraw(lv5cost))
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
        }
        else if(level == 6)
        {
            if(wallet.withdraw(lv6cost))
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
        }
        else if(level == 7)
        {
            if(wallet.withdraw(lv7cost))
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
        }
        else if(level == 8)
        {
            if(wallet.withdraw(lv8cost))
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
        }
        else if(level == 9)
        {
            if(wallet.withdraw(lv9cost))
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
        }
        else if(level == 10)
        {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            max_alertCooldown.reset();
            return false;
        }
        return false;
    }

    public boolean blUpgrade(int level){
        if(level == 0)
        {
            if(wallet.withdraw(bl1cost))
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
        }
        else if(level == 1)
        {
            if(wallet.withdraw(bl2cost))
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
        }
        else if(level == 2)
        {
            if(wallet.withdraw(bl3cost))
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
        }
        else if(level == 3)
        {
            if(wallet.withdraw(bl4cost))
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
        }
        else if(level == 4)
        {
            if(wallet.withdraw(bl5cost))
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
        }
        else if(level == 5)
        {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            max_alertCooldown.reset();
            return false;
        }
        return false;
    }

}