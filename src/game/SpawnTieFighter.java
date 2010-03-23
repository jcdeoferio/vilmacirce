package game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import util.Fighter;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class SpawnTieFighter extends KeyInputAction {
	private GameInterface game;

	public SpawnTieFighter(GameInterface game, ByteArrayInputStream targetModel) {
		super();
		this.game = game;
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
		
		return(((r.nextFloat() * 1000) % 300 + 75) * d);
	}

}
