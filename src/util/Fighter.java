package util;
import game.GameInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;


import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
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
	
	public Fighter(Node node){
		this.node = node;
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
	
	public static void spawnRandom(int n){
		
	}
	
	public static void spawnRandom(){
		
	}
	
	public Node getNode(){
		return(node);
	}
	
}
