package game;

import java.util.UUID;

import com.jme.bounding.BoundingBox;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Tube;

class FireBullet extends KeyInputAction {
	int numLasors;
	long lastTimeFired;
	GameInterface game;

	public FireBullet(GameInterface game) {
		lastTimeFired = 0;
		this.game = game;
	}

	public void performAction(InputActionEvent evt) {
		long currTime = System.currentTimeMillis();
		if (currTime - lastTimeFired < 250) {
			return;
		}
		lastTimeFired = currTime;
		GameInterface.getLogger().info("BANG");
		/** Create bullet */
		Tube lasor = new Tube("lasor" + numLasors++, 0.1f, 0.01f, 1.0f);
		lasor.rotateUpTo(game.getCam().getDirection());
		lasor.setModelBound(new BoundingBox());
		lasor.updateModelBound();
		/** Move bullet to the camera location */
		lasor.setLocalTranslation(game.getCam().getLocation().add(0.0f, 1.0f, 0.0f));
		lasor.setRenderState(game.bulletMaterial);
		/**
		 * Update the new world locaion for the bullet before I add a controller
		 */
		lasor.updateGeometricState(0, true);
		/**
		 * Add a movement controller to the bullet going in the camera's
		 * direction
		 */
		lasor.addController(new BulletMover(lasor, new Vector3f(game.getCam()
				.getDirection()), game));
		game.getRootNode().attachChild(lasor);

		game.lasers.put(UUID.randomUUID(), lasor);

		lasor.updateRenderState();
		/** Signal our sound to play laser during rendering */
		game.laserSound.setWorldPosition(game.getCam().getLocation());
		game.laserSound.play();
	}
}
