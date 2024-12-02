package blackjack;

import engine.manager.SoundManager;
import engine.utility.Sound;
import entity.Wallet;
import ui.Screen;

import java.awt.event.KeyEvent;

public class GamblingScreen extends Screen {
    private final Gamer gamer; // Gamer 객체 추가
    private final Wallet wallet;

    private final SoundManager soundManager = SoundManager.getInstance();

    public GamblingScreen(int width, int height, int fps, Wallet wallet, Gamer gamer) {
        super(width, height, fps);
        this.wallet = wallet;
        this.gamer = gamer;

        setupKeyBindings();

        soundManager.stopSound(Sound.BGM_SHOP);
        soundManager.loopSound(Sound.BGM_GAMBLING);
    }


    private void setupKeyBindings() {
        // Q키에 대한 바인딩은 update 메서드에서 체크하도록 설정
        // 더 이상 여기에 직접 키 바인딩 코드는 없습니다.
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

        // Q 키를 누르면 블랙잭 게임 시작 - 새로운 창으로 시작합니다.
        if (inputManager.isKeyDown(KeyEvent.VK_Q)) {
            System.out.println("Starting BlackJackScreen...");
            this.isRunning = false; // 현재 화면 종료
            new BlackJackScreen(); // 블랙잭 게임 창 열기
        }
    }

    protected final void draw() {
        drawManager.initDrawing(this); // 드로잉 초기화
        drawManager.drawGambling(this, 150, 150, 150, 200, 150, 50, 150, 300, 150, 400);
        drawManager.completeDrawing(this); // 드로잉 완료
    }
}
