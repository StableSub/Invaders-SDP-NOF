package blackjack;

import engine.manager.SoundManager;
import entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackJackScreenTest {
    private Wallet wallet;
    private BlackJackScreen blackJackScreen;
    public SoundManager soundManager;

    @BeforeEach
    void setUp() {
        wallet = new Wallet(); // 초기 코인 1000 설정
        wallet.deposit(1000);
        soundManager = new SoundManager();
        blackJackScreen = new BlackJackScreen(wallet, 100); // 초기 베팅 금액 100 설정
    }

    @Test
    void testDrawCondition() {
        // 상황: 플레이어와 딜러 모두 버스트 (DRAW)
        blackJackScreen.gamerScore = 22;
        blackJackScreen.dealerScore = 22;
        // 메서드 실행
        blackJackScreen.endGame();
        // Wallet에 변화가 없어야 함
        assertEquals(1000, wallet.getCoin()); // 코인 변동 없음 확인
    }

    @Test
    void testPlayerBust() {
        // 상황: 플레이어가 버스트
        blackJackScreen.gamerScore = 22;
        blackJackScreen.dealerScore = 20;
        // 메서드 실행
        blackJackScreen.endGame();

        // Wallet에서 배팅 금액만큼 차감
        assertEquals(900, wallet.getCoin()); // 100 차감 확인
    }

    @Test
    void testDealerBust() {
        // 상황: 딜러가 버스트
        blackJackScreen.gamerScore = 20;
        blackJackScreen.dealerScore = 22;
        // 메서드 실행
        blackJackScreen.endGame();

        // Wallet에 배팅 금액만큼 추가
        assertEquals(1100, wallet.getCoin()); // 100 추가 확인
    }

    @Test
    void testPlayerWin() {
        // 상황: 플레이어가 승리
        blackJackScreen.gamerScore = 20;
        blackJackScreen.dealerScore = 19;

        // 메서드 실행
        blackJackScreen.endGame();

        // Wallet에 배팅 금액만큼 추가
        assertEquals(1100, wallet.getCoin()); // 100 추가 확인
    }

    @Test
    void testDealerWin() {
        // 상황: 딜러가 승리
        blackJackScreen.gamerScore = 18;
        blackJackScreen.dealerScore = 20;

        // 메서드 실행
        blackJackScreen.endGame();

        // Wallet에서 배팅 금액만큼 차감
        assertEquals(900, wallet.getCoin()); // 100 차감 확인
    }
}