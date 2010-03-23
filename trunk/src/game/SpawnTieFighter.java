package game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import util.Fighter;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.binary.BinaryImporter;

public class SpawnTieFighter extends KeyInputAction {
	private GameInterface game;
	private ByteArrayInputStream targetModel;

	public SpawnTieFighter(GameInterface game, ByteArrayInputStream targetModel) {
		super();
		this.game = game;
		this.targetModel = targetModel;
	}

	@Override
	public void performAction(InputActionEvent evt) {
		try {
			targetModel.reset();
			Node target = (Node) BinaryImporter.getInstance().load(targetModel);
			target.rotateUpTo(new Vector3f(0.0f, 0.0f, 1.0f));
			// Spatial target = new Sphere(targetUUID.toString(), 16, 16, 5);
			target.setModelBound(new BoundingBox());
			target.updateModelBound();
			
			target.addController(new FighterMover(new Fighter(target), game));
			
			// Put her on the scene graph
			game.getRootNode().attachChild(target);

			game.getRootNode().updateRenderState();

			game.targets.put(UUID.randomUUID(), target);
		} catch (IOException e) { // Just in case anything happens
			GameInterface.getLogger().logp(Level.SEVERE,
					this.getClass().toString(), "simpleInitGame()",
					"Exception", e);
			System.exit(0);
		}
	}

}
