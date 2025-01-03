package blackjack;

import engine.manager.SoundManager;
import engine.utility.Sound;
import entity.Wallet;
import ui.Screen;

import java.awt.event.KeyEvent;

public class GamblingScreen extends Screen {
    private final Wallet wallet;

    private final SoundManager soundManager = SoundManager.getInstance();

    private int bettingAmount = 0; // 베팅 금액

    public GamblingScreen(int width, int height, int fps, Wallet wallet) {
        super(width, height, fps);
        this.wallet = wallet;

        soundManager.stopSound(Sound.BGM_SHOP);
        soundManager.loopSound(Sound.BGM_GAMBLING);
    }

    @Override
    public final int run() {
        this.isRunning = true;
        while (this.isRunning) {
            update(); // 로직 처리
            draw();   // 화면 그리기
            try {
                Thread.sleep(16); // FPS 조정 (대략 60 FPS)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.returnCode;
    }

    @Override
    protected final void update() {
        super.update();

        // ESC 키로 메인메뉴으로 돌아가기
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            soundManager.stopSound(Sound.BGM_GAMBLING);
            this.isRunning = false;
        }

        // 베팅 금액 증가 (UP 키)
        if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
            soundManager.playSound(Sound.COIN_USE);
            if (bettingAmount + 10 <= wallet.getCoin()) {
                bettingAmount += 10;
            }
        }

        // 베팅 금액 감소 (DOWN 키)
        if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
            soundManager.playSound(Sound.COIN_USE);
            if (bettingAmount - 10 >= 0) {
                bettingAmount -= 10;
            }
        }
        // Q 키를 누르면 블랙잭 게임 시작 - 새로운 창으로 시작합니다.
        if (inputManager.isKeyDown(KeyEvent.VK_Q)) {
            System.out.println("Starting BlackJackScreen...");
            this.isRunning = false; // 현재 화면 종료
            new BlackJackScreen(wallet,bettingAmount);
            // 블랙잭 게임 창 열기
        }
    }

    protected final void draw() {
        drawManager.initDrawing(this); // 드로잉 초기화

        // UI 요소: 배팅 금액과 기타 메시지 모두 표시
        drawManager.drawGambling(this, bettingAmount);

        drawManager.completeDrawing(); // 드로잉 완료
    }


}
