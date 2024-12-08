package entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testSetBulletLv() {
        Wallet wallet = new Wallet();
        wallet.setBullet_lv(3);
        assertEquals(3, wallet.getBullet_lv(), "Bullet level should be set to 3");
    }

    @Test
    void testSetShotLv() {
        Wallet wallet = new Wallet();
        wallet.setShot_lv(8);
        assertEquals(8, wallet.getShot_lv(), "Shot level should be set to 8");
    }

    @Test
    void testSetLivesLv() {
        Wallet wallet = new Wallet();
        wallet.setLives_lv(1);
        assertEquals(1, wallet.getLives_lv(), "Lives level should be set to 1");
    }

    @Test
    void testSetCoinLv() {
        Wallet wallet = new Wallet();
        wallet.setCoin_lv(10);
        assertEquals(10, wallet.getCoin_lv(), "Coin level should be set to 10");
    }

    @Test
    void testBlockWithdraw() {
        Wallet wallet = new Wallet();

        wallet.setBullet_lv(4);
        assertFalse(wallet.blockWithdraw(1), "Bullet withdrawal should not be blocked");

        wallet.setShot_lv(3);
        assertTrue(wallet.blockWithdraw(2), "Shot withdrawal should be blocked");

        wallet.setLives_lv(5);
        assertFalse(wallet.blockWithdraw(3), "Lives withdrawal should not be blocked");

        wallet.setCoin_lv(1);
        assertTrue(wallet.blockWithdraw(4), "Coin withdrawal should be blocked");
    }

    @Test
    void testSetBulletBl() {
        Wallet wallet = new Wallet();
        wallet.setBullet_bl(1);
        assertEquals(1, wallet.getBullet_bl(), "Bullet block level should be set to 1");
    }

    @Test
    void testSetShotBl() {
        Wallet wallet = new Wallet();
        wallet.setShot_bl(2);
        assertEquals(2, wallet.getShot_bl(), "Shot block level should be set to 2");
    }

    @Test
    void testSetLivesBl() {
        Wallet wallet = new Wallet();
        wallet.setLives_bl(3);
        assertEquals(3, wallet.getLives_bl(), "Lives block level should be set to 3");
    }

    @Test
    void testSetCoinBl() {
        Wallet wallet = new Wallet();
        wallet.setCoin_bl(5);
        assertEquals(5, wallet.getCoin_bl(), "Coin block level should be set to 5");
    }

    @Test
    void testGetMaxLevel() {
        Wallet wallet = new Wallet();

        wallet.setBullet_bl(5);
        assertEquals(9, wallet.getMaxLevel(0), "Max level for bullet should be 9");

        wallet.setShot_bl(1);
        assertEquals(5, wallet.getMaxLevel(1), "Max level for shot should be 5");

        wallet.setLives_bl(3);
        assertEquals(7, wallet.getMaxLevel(2), "Max level for lives should be 7");

        wallet.setCoin_bl(6);
        assertEquals(10, wallet.getMaxLevel(3), "Max level for coin should be 10");
    }
}
