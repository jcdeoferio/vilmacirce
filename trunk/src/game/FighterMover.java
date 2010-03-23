package game;

import util.Fighter;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.Node;

public class FighterMover extends Controller {
	private static final long serialVersionUID = 6870816191499907127L;

	private static final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	
	private Fighter fighter;
	private GameInterface game;
	private float speed = 10;
	
	public FighterMover(Fighter fighter, GameInterface game){
		this.fighter = fighter;
		this.game = game;
	}
	
	@Override
	public void update(float time) {
		Camera cam = game.getCam();
		
		Node fighterNode = fighter.getNode();
		
		Vector3f fighterPos = fighterNode.getLocalTranslation();
		Vector3f fighterDir = fighterPos.subtract(cam.getLocation()).normalize().negateLocal();
		fighterNode.setLocalTranslation(fighterPos.add(fighterDir.mult(time * speed)));
		
		fighterNode.lookAt(cam.getLocation(), up);
		fighterNode.rotateUpTo(cam.getUp());
	}

}
