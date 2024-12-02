package ui;

import engine.core.GameSettings;
import engine.core.GameState;
import engine.manager.DrawManager;
import engine.utility.Cooldown;
import entity.EnemyShip;
import entity.Ship;
import entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScreenTest {

    private GameScreen gameScreen;

    @BeforeEach
    void setUp() {
        GameState testGameState = new GameState(
                1, 0,Ship.ShipType.StarDefender, 3,
                0,0,0,"",0, 0,0,0
        );

        GameSettings testGameSettings = new GameSettings(
                10, 5, 2,3
        );

        Wallet testWallet = new Wallet(); // Wallet 생성

        int width = 800;
        int height = 600;
        int fps = 60;
        // DrawManager의 frame 초기화
        DrawManager drawManager = DrawManager.getInstance();
        drawManager.setFrame(new Frame(width, height)); // 실제 Frame 객체 생성

        // 게임 화면 초기화
        gameScreen = new GameScreen(testGameState, testGameSettings, false, width, height, fps, testWallet);
        gameScreen.initialize();
        gameScreen.enemyShipSpecialCooldown = new Cooldown(0);

    }

    @Test
    void playerLivesDecreaseOnHitTest() {

        //플레이어가 적 총알에 맞는 상황 시뮬레이션
        gameScreen.lvdamage();

        //생명이 줄어들었는지 확인
        assertEquals(2, gameScreen.getGameState().getLivesRemaining(), "플레이어의 생명이 제대로 감소하지 않았습니다.");
    }

    @Test
    void bossDestroyedDirectTest() {
        EnemyShip boss = new EnemyShip();
        gameScreen.enemyShipSpecial = boss;
        gameScreen.enemyShipSpecialExplosionCooldown = new Cooldown(0);
        gameScreen.enemyShipSpecialExplosionCooldown.reset();

        boss.specialDestroy(); // 보스 파괴
        assertTrue(boss.isDestroyed(), "보스가 파괴 상태가 아닙니다.");

        if (gameScreen.enemyShipSpecialExplosionCooldown.checkFinished()) {
            gameScreen.enemyShipSpecial = null; // 보스 제거
        }
        assertNull(gameScreen.enemyShipSpecial, "보스가 제거되지 않았습니다.");
    }



}
