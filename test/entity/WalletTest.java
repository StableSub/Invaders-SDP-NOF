package entity;

import junit.framework.TestCase;

public class WalletTest extends TestCase {

    public void testSetBullet_lv() {
        Wallet wallet = new Wallet();
        wallet.setBullet_lv(3);
        assertEquals(3, wallet.getBullet_lv());
    }

    public void testSetShot_lv() {
        Wallet wallet = new Wallet();
        wallet.setShot_lv(8);
        assertEquals(8, wallet.getShot_lv());
    }

    public void testSetLives_lv() {
        Wallet wallet = new Wallet();
        wallet.setLives_lv(1);
        assertEquals(1, wallet.getLives_lv());
    }

    public void testSetCoin_lv() {
        Wallet wallet = new Wallet();
        wallet.setCoin_lv(10);
        assertEquals(10, wallet.getCoin_lv());
    }

    public void testBlockWithdraw() {
        Wallet wallet = new Wallet();
        wallet.setBullet_lv(4);
        assertFalse(wallet.blockWithdraw(1));

        wallet.setShot_lv(3);
        assertTrue(wallet.blockWithdraw(2));

        wallet.setLives_lv(5);
        assertFalse(wallet.blockWithdraw(3));

        wallet.setCoin_lv(1);
        assertTrue(wallet.blockWithdraw(4));
    }

    public void testSetBullet_bl() {
        Wallet wallet = new Wallet();
        wallet.setBullet_bl(1);
        assertEquals(1, wallet.getBullet_bl());
    }

    public void testSetShot_bl() {
        Wallet wallet = new Wallet();
        wallet.setShot_bl(2);
        assertEquals(2, wallet.getShot_bl());
    }

    public void testSetLives_bl() {
        Wallet wallet = new Wallet();
        wallet.setLives_bl(3);
        assertEquals(3, wallet.getLives_bl());
    }

    public void testSetCoin_bl() {
        Wallet wallet = new Wallet();
        wallet.setCoin_bl(5);
        assertEquals(5, wallet.getCoin_bl());
    }

    public void testGetMaxLevel() {
        Wallet wallet = new Wallet();
        wallet.setBullet_bl(5);
        assertEquals(9, wallet.getMaxLevel(0));

        wallet.setShot_bl(1);
        assertEquals(5, wallet.getMaxLevel(1));

        wallet.setLives_bl(3);
        assertEquals(7, wallet.getMaxLevel(2));

        wallet.setCoin_bl(6);
        assertEquals(10, wallet.getMaxLevel(3));
    }
}
