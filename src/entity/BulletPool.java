package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a normalPool of recyclable bullets.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class BulletPool {

	/** Set of already created bullets. */
	private static Set<Bullet> normalPool = new HashSet<Bullet>();
	private static Set<CurvedBullet> curvedPool = new HashSet<CurvedBullet>();

	private static Set<ExplosionBullet> explosionPool = new HashSet<ExplosionBullet>();

	/**
	 * Returns a bullet from the normalPool if one is available, a new one if there
	 * isn't.
	 *
	 * @param positionX
	 *            Requested position of the bullet in the X axis.
	 * @param positionY
	 *            Requested position of the bullet in the Y axis.
	 * @param speed
	 *            Requested speed of the bullet, positive or negative depending
	 *            on direction - positive is down.
	 * @return Requested bullet.
	 */
	public static Bullet getNomalBullet(final int positionX, final int positionY, final int speed) {
		Bullet bullet;

		if (!normalPool.isEmpty()) {
			bullet = normalPool.iterator().next();
			normalPool.remove(bullet);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeed(speed);
			bullet.setSprite();
		} else {
			bullet = new Bullet(positionX, positionY, speed);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
		}
		return bullet;
	}

	public static CurvedBullet getCurvedBullet(final int positionX, final int positionY, final int speed) {
		CurvedBullet bullet;

		if (!curvedPool.isEmpty()) {
			bullet = curvedPool.iterator().next();
			curvedPool.remove(bullet);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeed(speed);
			bullet.setSprite();
		} else {
			bullet = new CurvedBullet(positionX, positionY, speed);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
		}
		return bullet;
	}

	public static ExplosionBullet getExplosionBullet(final int positionX, final int positionY, final int speed) {
		ExplosionBullet bullet;

		if (!explosionPool.isEmpty()) {
			bullet = explosionPool.iterator().next();
			explosionPool.remove(bullet);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeedX(0); // X 속도 초기화
			bullet.setSpeedY(speed); // Y 속도 초기화
			bullet.setExploded(false); // 폭발 상태 초기화
			bullet.setChild(false); // 자식 상태 초기화
			bullet.getChildBullets().clear(); // 자식 총알 초기화
			bullet.setSprite();
		} else {
			bullet = new ExplosionBullet(positionX, positionY, speed);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
		}
		return bullet;
	}
	/**
	 * Adds one or more bullets to the list of available ones.
	 *
	 * @param bullet
	 *            Bullets to recycle.
	 */
	public static void recycleNormal(final Set<Bullet> bullet) {
		normalPool.addAll(bullet);
	}

	public static void recycleCurved(final Set<CurvedBullet> bullet) {
		curvedPool.addAll(bullet);
	}

	public static void recycleExplosion(final Set<ExplosionBullet> bullet) {
		explosionPool.addAll(bullet);
	}
}
