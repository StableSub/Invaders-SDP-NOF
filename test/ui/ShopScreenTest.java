package ui;

import entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopScreenTest {

    private ShopScreen shopScreen;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.deposit(1000000);
        shopScreen = new ShopScreen(800, 600, 60, wallet);
    }

    @Test
    void testUpgrade_success() {
        // 레벨 1 업그레이드
        boolean result = shopScreen.upgrade(1);
        assertTrue(result, "Upgrade succeed");

    }

    @Test
    void testUpgrade_insufficientFunds() {
        wallet.withdraw(1000000); // 잔액 0원
        boolean result = shopScreen.upgrade(1);
        assertFalse(result, "Upgrade should fail because of no money");
    }

    @Test
    void testUpgrade_maxLevel() {
        // 최대 레벨 이상에서 업그레이드
        boolean result = shopScreen.upgrade(10); // 10이 최대 레벨이라고 가정
        assertFalse(result, "Upgrade should fail because of max level");
    }

    @Test
    void testBlUpgrade_success() {
        // Break Limit 업그레이드
        boolean result = shopScreen.blUpgrade(1);
        assertTrue(result, "Break Limit upgrade succeed");
    }

    @Test
    void testBlUpgrade_insufficientFunds() {
        wallet.withdraw(1000000); // 잔액 0원
        boolean result = shopScreen.blUpgrade(1);
        assertFalse(result, "Break Limit upgrade should fail because of no money.");
    }

    @Test
    void testBlUpgrade_maxLevel() {
        // 최대 Break Limit 레벨 이상 업그레이드
        boolean result = shopScreen.blUpgrade(6); // 6이 최대 Break Limit 레벨이라고 가정
        assertFalse(result, "Break Limit upgrade should fail because of break limit max.");
    }
}
