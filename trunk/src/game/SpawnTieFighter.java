package game;

import java.util.UUID;

import com.jme.bounding.BoundingSphere;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

public class SpawnTieFighter extends KeyInputAction {
	private GameInterface game;
	public SpawnTieFighter(GameInterface game){
		super();
		this.game = game;
	}

	@Override
	public void performAction(InputActionEvent evt) {
		// try {
		// targetModel.reset();
		// Node target = (Node)
		// BinaryImporter.getInstance().load(targetModel);
		// target.rotateUpTo(new Vector3f(0.0f, 0.0f, 1.0f));
		UUID targetUUID = UUID.randomUUID();
		Spatial target = new Sphere(targetUUID.toString(), 16, 16, 5);
		target.setModelBound(new BoundingSphere());
		target.updateModelBound();
		// Put her on the scene graph
		game.getRootNode().attachChild(target);

		game.getRootNode().updateRenderState();

		game.targets.put(UUID.randomUUID(), target);
		// } catch (IOException e) { // Just in case anything happens
		// logger.logp(Level.SEVERE, this.getClass().toString(),
		// "simpleInitGame()", "Exception", e);
		// System.exit(0);
		// }
	}

}
