package game;

import java.util.HashSet;

import util.Fighter;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jmex.audio.AudioTrack;

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
	
	int bulletDmg = 1;
	
	GameInterface game;
	
	AudioTrack targetSound;

	BulletMover(TriMesh bullet, Vector3f direction, GameInterface game) {
		this.bullet = bullet;
		this.direction = direction;
		this.direction.normalizeLocal();
		this.game = game;
		
		this.targetSound = game.getTargetSound();
	}

	public void update(float time) {
		lifeTime -= time;
		
		/** If life is gone, remove it */
		if (lifeTime < 0) {
			game.getRootNode().detachChild(bullet);
			bullet.removeController(this);
			return;
		}
		
		/** Move bullet */
		Vector3f bulletPos = bullet.getLocalTranslation();
		bulletPos.addLocal(direction.mult(time * speed));
		bullet.setLocalTranslation(bulletPos);
		
		/** Does the bullet intersect with a target? */
		for (Fighter targetFighter : new HashSet<Fighter>(game.getTargets())) {
			Node target = targetFighter.getNode();

			if (bullet.getWorldBound().intersects(target.getWorldBound())) {
				GameInterface.getLogger().info("OWCH!!!");
				
				targetSound.setWorldPosition(target.getWorldTranslation());
				targetSound.play();
				
				if(targetFighter.hit(bulletDmg))
					game.removeFighter(targetFighter);
				

				lifeTime = 0;
			}
		}
	}
}
