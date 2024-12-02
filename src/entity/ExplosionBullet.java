package entity;

import java.util.ArrayList;
import java.util.List;

public class ExplosionBullet extends Bullet {
    private boolean exploed, isChild;
    private List<ExplosionBullet> childBullets; // 폭발로 생성된 자식 총알을 관리하는 리스트

    private int speedX, speedY;

    public ExplosionBullet(final int positionX, final int positionY, final int speed) {
        super(positionX, positionY, speed);
        this.speedY = speed;
        this.exploed = false;
        this.isChild = false;
        this.childBullets = new ArrayList<>(); // 내부에서 관리하는 자식 총알 리스트
        setSprite();
    }

    public ExplosionBullet(final int positionX, final int positionY, final int speedX, final int speedY) {
        super(positionX, positionY, speedY);
        this.speedX = speedX;
        this.speedY = speedY;
        this.exploed = true;
        this.childBullets = new ArrayList<>();
        this.isChild = true;
        setSprite();
    }

    public final void update() {
        // 폭발한 경우에는 자식 총알만 업데이트
        if (exploed) {
            if (isChild) {
                this.positionY += this.speedY;
                this.positionX += this.speedX;
            }
            for (Bullet bullet : childBullets) {
                bullet.update(); // 자식 총알만 업데이트
            }
            return;
        }

        if (shouldExplode()) {
            explode();
            this.exploed = true;
        } else {
            this.positionY += this.speedY;
            this.positionX += this.speedX;
        }
    }

    private boolean shouldExplode() {
        // 폭발 조건: 특정 Y좌표 도달 시 폭발
        return this.positionY > 400; // 필요에 따라 조건을 변경 가능
    }

    private void explode() {

        // 폭발로 새로 생성된 총알
        ExplosionBullet leftBullet = new ExplosionBullet(this.positionX, this.positionY, -speedX, speedY); // 왼쪽
        ExplosionBullet downBullet = new ExplosionBullet(this.positionX, this.positionY, 0, speedY); // 아래
        ExplosionBullet rightBullet = new ExplosionBullet(this.positionX, this.positionY, speedX, speedY); // 오른쪽

        // 새로 생성된 총알을 내부 리스트에 추가
        childBullets.add(leftBullet);
        childBullets.add(downBullet);
        childBullets.add(rightBullet);
    }

    public List<ExplosionBullet> getChildBullets() {
        return childBullets;
    }

    public int getExplode() {
        return 400;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }
    public int getSpeedX() {
        return speedX;
    }
    public int getSpeedY() {
        return speedY;
    }

    public void setExploed(boolean exploed) {
        this.exploed = exploed;
    }

}