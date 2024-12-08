package entity;

/**
 * Implements a bullet that moves vertically up or down.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class CurvedBullet extends Bullet {

    /**
     * Speed of the bullet, positive or negative depending on direction -
     * positive is down.
     */
    private int speed;
    private double angle;
    private double frequency;

    /** Amplitude for sinusoidal movement. */
    private int amplitude;
    /**
     * Constructor, establishes the bullet's properties.
     *
     * @param positionX
     *            Initial position of the bullet in the X axis.
     * @param positionY
     *            Initial position of the bullet in the Y axis.
     * @param speed
     *            Speed of the bullet, positive or negative depending on
     *            direction - positive is down.
     */
    public CurvedBullet(final int positionX, final int positionY, final int speed) {
        super(positionX, positionY, speed);
        this.angle = 0; // 초기 각도
        this.frequency = 0.1; // 주기 설정
        this.amplitude = 5; // 진폭 설정
        this.speed = speed;
        setSprite();
    }

    /**
     * Updates the bullet's position.
     */
    public final void update() {
        this.positionY += this.speed; // Y축 이동

        // X축 이동: 현재 각도에 따른 사인 값을 적용
        this.positionX += (int) (amplitude * Math.sin(angle));
        this.angle += frequency; // 각도 증가로 주기적 이동

        // X축이 화면 경계를 벗어날 경우 처리
        if (this.positionX < 0) {
            this.positionX = 0; // 경계 안으로 복구
            this.angle += Math.PI; // 방향 반전
        } else if (this.positionX > 600) {
            this.positionX = 600; // 경계 안으로 복구
            this.angle += Math.PI; // 방향 반전
        }
    }

    public double getAngle() {
        return angle;
    }

}
