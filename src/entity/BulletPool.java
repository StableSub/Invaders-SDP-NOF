package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a nomalPool of recyclable bullets.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class BulletPool {

	/** Set of already created bullets. */
	private static Set<Bullet> nomalPool = new HashSet<Bullet>();
	private static Set<curvedBullet> curvedPool = new HashSet<curvedBullet>();

	/**
	 * Returns a bullet from the nomalPool if one is available, a new one if there
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

		if (!nomalPool.isEmpty()) {
			bullet = nomalPool.iterator().next();
			nomalPool.remove(bullet);
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

	public static curvedBullet getCurvedBullet(final int positionX, final int positionY, final int speed) {
		curvedBullet bullet;

		if (!curvedPool.isEmpty()) {
			bullet = curvedPool.iterator().next();
			curvedPool.remove(bullet);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeed(speed);
			bullet.setSprite();
		} else {
			bullet = new curvedBullet(positionX, positionY, speed);
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
	public static void recycleNomal(final Set<Bullet> bullet) {
		nomalPool.addAll(bullet);
	}

	public static void recycleCurved(final Set<curvedBullet> bullet) {
		curvedPool.addAll(bullet);
	}
}
