import java.io.Serializable;
import java.util.UUID;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;


public class Fighter implements Serializable {
	private static final long serialVersionUID = 6450300429259485682L;
	
	private transient Node node;
	private UUID uuid;
	private Quaternion localRotation;
	private Vector3f localTranslation;
	
	public Fighter(UUID uuid, Node model){
		this.node = model;
		this.uuid = uuid;
		this.localRotation = model.getLocalRotation();
		this.localTranslation = model.getLocalTranslation();
		System.out.println("Local translation!!!:"+localTranslation);
	}
	
	public void setModel(Node model){
		this.node = model;
		this.localRotation = model.getLocalRotation();
		this.localTranslation = model.getLocalTranslation();
	}
	
	public Node getModel(){
		return(node);
	}
	
	public Quaternion getLocalRotation(){
		return(localRotation);
	}
	
	public Vector3f getLocalTranslation(){
		return(localTranslation);
	}
	
	public UUID getUUID(){
		return(uuid);
	}
}
