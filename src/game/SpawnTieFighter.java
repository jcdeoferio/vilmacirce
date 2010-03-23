package game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import util.Fighter;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.audio.AudioTrack;

public class SpawnTieFighter extends KeyInputAction {
	private GameInterface game;
	private ByteArrayInputStream targetModel;
	private AudioTrack tieSound;

	public SpawnTieFighter(GameInterface game, ByteArrayInputStream targetModel) {
		super();
		this.game = game;
		this.targetModel = targetModel;
		
		this.tieSound = game.getTieSound();
	}

	@Override
	public void performAction(InputActionEvent evt) {
		spawnRandom(5, game);
	}
	
	public static void spawnRandom(int n, GameInterface game){
		for(int i = 0; i < n; i++)
			try {
				spawnRandom(game);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void spawnRandom(GameInterface game) throws IOException{
		Fighter fighter = Fighter.newFighter();
		Camera cam = game.getCam();
		
		float randX = randDisp();
		float randY = randDisp();
		float randZ = randDisp();
		Vector3f randomPos = new Vector3f(cam.getLocation()).add(randX, randY, randZ);
		fighter.getNode().setLocalTranslation(randomPos);
		
		game.addFighter(fighter);
	}
	
	private static float randDisp(){
		Random r = new Random();
		
		float d = 0;
		if(r.nextBoolean())
			d = -1;
		else
			d = 1;
		
		return(((r.nextFloat() * 100) % 50 + 20) * d);
	}

}
