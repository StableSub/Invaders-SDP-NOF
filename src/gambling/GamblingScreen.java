package gambling;

import entity.Wallet;
import ui.Screen;
import engine.utility.Sound;
import engine.manager.SoundManager;

import java.awt.event.KeyEvent;


public class GamblingScreen extends Screen {

    private final int buttonWidth = 150; // 버튼 너비
    private final int buttonHeight = 50; // 버튼 높이

    // 버튼 위치
    private final int drawButtonX = this.width / 3;
    private final int drawButtonY = this.height / 3;
    private final int releaseButtonX = this.width / 3;
    private final int releaseButtonY = this.height / 3 + 100;
    private final Wallet wallet;

    private final SoundManager soundManager = SoundManager.getInstance();

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
        }
        return this.returnCode;
    }

    @Override
    protected final void update() {
        super.update();

        // ESC 키로 상점 화면으로 돌아가기
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            soundManager.stopSound(Sound.BGM_GAMBLING); // 갬블링 음악 중지
            soundManager.loopSound(Sound.BGM_SHOP);    // 상점 음악 재생
            this.isRunning = false;
        }

        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {

        }

        if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {

        }
    }

    protected final void draw() {
        drawManager.initDrawing(this); // 드로잉 초기화
        drawManager.drawGambling(this, drawButtonX, drawButtonY, releaseButtonX, releaseButtonY, buttonWidth, buttonHeight);
        drawManager.completeDrawing(this); // 드로잉 완료
    }
}