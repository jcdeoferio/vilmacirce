package util;
import game.GameInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;


import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;


public class Fighter{
	private static final long serialVersionUID = 6450300429259485682L;
	
	private static ByteArrayInputStream fighterModel;
	
	static{
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		try {
			/** Make target material */
			// Point to a URL of my model
			URL model = GameInterface.class.getClassLoader().getResource(
					"jmetest/data/model/models/tie/TIEF.3DS");

			// Create something to convert .3ds format to .jme
			FormatConverter converter = new MaxToJme();
			converter.convert(model.openStream(), BO);

			fighterModel = new ByteArrayInputStream(BO.toByteArray());
		} catch (IOException e) { // Just in case anything happens
			System.out.println("exception in loading model");
			System.exit(0);
		}
	}
	
	private Node node;
	private int HP;
	
	public Fighter(Node node){
		this.node = node;
		this.HP = 3;
	}
	
	//true if shot was killing shot
	public synchronized boolean hit(int dmg){
		if(node != null && HP > 0){
			HP -= dmg;
			
			if(HP <= 0)
				return(true);
		}
		
		return(false);
	}
	
	public static ByteArrayInputStream fighterModel(){
		return(fighterModel);
	}
	
	public static Node newFighterNode() throws IOException{
		fighterModel.reset();
		
		Node fighter = (Node)BinaryImporter.getInstance().load(fighterModel);
		fighter.rotateUpTo(new Vector3f(0.0f, 0.0f, 1.0f));
		fighter.setModelBound(new BoundingBox());
		fighter.updateModelBound();
		
		return(fighter);
	}
	
	public static Fighter newFighter() throws IOException{
		return(new Fighter(newFighterNode()));
	}
	
	public Node getNode(){
		return(node);
	}

	public void clearNode() {
		node = null;
	}
	
}
