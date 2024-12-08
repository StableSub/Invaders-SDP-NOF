package entity;

import java.util.ArrayList;
import java.util.List;

public class ExplosionBullet extends Bullet {
    private boolean exploded, isChild;
    private List<ExplosionBullet> childBullets; // 폭발로 생성된 자식 총알을 관리하는 리스트

    private int speedX, speedY;

    public ExplosionBullet(final int positionX, final int positionY, final int speedY) {
        super(positionX, positionY, speedY);
        this.speedX = 0;
        this.speedY = speedY;
        this.exploded = false;
        this.isChild = false;
        this.childBullets = new ArrayList<>(); // 내부에서 관리하는 자식 총알 리스트
        setSprite();
    }

    public ExplosionBullet(final int positionX, final int positionY, final int speedX, final int speedY) {
        super(positionX, positionY, speedY);
        this.speedX = speedX;
        this.speedY = speedY;
        this.exploded = true;
        this.isChild = true;
        this.childBullets = new ArrayList<>();
        setSprite();
    }

    public final void update() {
        // 폭발한 경우에는 자식 총알만 업데이트
        if (exploded) {
            if (isChild) {
                this.positionY += this.speedY;
                this.positionX += this.speedX;
            }
            return;
        }

        this.positionY += this.speedY;
        this.positionX += this.speedX;
        if (shouldExplode()) {
            explode();
            this.exploded = true;
        }
    }

    private boolean shouldExplode() {
        // 폭발 조건: 특정 Y좌표 도달 시 폭발
        return this.positionY > 400; // 필요에 따라 조건을 변경 가능
    }

    private void explode() {
        int explosionSpeed = 4;
        // 폭발로 새로 생성된 총알
        ExplosionBullet leftBullet = new ExplosionBullet(this.positionX, this.positionY, -explosionSpeed, explosionSpeed); // 왼쪽
        ExplosionBullet downBullet = new ExplosionBullet(this.positionX, this.positionY, 0, explosionSpeed); // 아래
        ExplosionBullet rightBullet = new ExplosionBullet(this.positionX, this.positionY, explosionSpeed, explosionSpeed); // 오른쪽

        // 새로 생성된 총알을 내부 리스트에 추가
        childBullets.add(leftBullet);
        childBullets.add(downBullet);
        childBullets.add(rightBullet);
    }

    public List<ExplosionBullet> getChildBullets() {
        return childBullets;
    }
    public boolean getExploded() {
        return exploded;
    }
    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }
    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }
    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
    public void setChild(boolean isChild) {
        this.isChild = isChild;
    }
}