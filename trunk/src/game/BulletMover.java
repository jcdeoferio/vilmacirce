package game;

import java.util.UUID;
import java.util.Map.Entry;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

class BulletMover extends Controller {
	private static final long serialVersionUID = 1L;
	/** Bullet that's moving */
	TriMesh bullet;

	/** Direciton of bullet */
	Vector3f direction;

	/** speed of bullet */
	float speed = 80;

	/** Seconds it will last before going away */
	float lifeTime = 5;
	
	GameInterface game;

	BulletMover(TriMesh bullet, Vector3f direction, GameInterface game) {
		this.bullet = bullet;
		this.direction = direction;
		this.direction.normalizeLocal();
		this.game = game;
	}

	public void update(float time) {
		lifeTime -= time;
		/** If life is gone, remove it */
		if (lifeTime < 0) {
			game.getRootNode().detachChild(bullet);
			bullet.removeController(this);
			game.lasers.remove(bullet);// should be the key not the value
			return;
		}
		/** Move bullet */
		Vector3f bulletPos = bullet.getLocalTranslation();
		bulletPos.addLocal(direction.mult(time * speed));
		bullet.setLocalTranslation(bulletPos);
		/** Does the bullet intersect with a target? */
		for (Entry<UUID, Spatial> targetEntry : game.targets.entrySet()) {
			Spatial target = targetEntry.getValue();

			if (bullet.getWorldBound().intersects(target.getWorldBound())) {
				GameInterface.getLogger().info("OWCH!!!");
				game.targetSound.setWorldPosition(target.getWorldTranslation());

				target.setLocalTranslation(new Vector3f(game.r.nextFloat() * 10, game.r
						.nextFloat() * 10, game.r.nextFloat() * 10));
//				 target.removeFromParent();
				// targets.remove(targetuuid);

				lifeTime = 0;

				game.targetSound.play();
			}
		}
	}
}
