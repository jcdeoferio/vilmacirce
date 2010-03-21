import java.io.Serializable;
import java.util.UUID;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;


public class SerializableSpatial implements Serializable{
	private static final long serialVersionUID = -2082269825122823380L;
	
	protected transient Spatial obj;
	protected UUID uuid;
	protected Quaternion localRotation;
	protected Vector3f localTranslation;
	
	public SerializableSpatial(UUID uuid, Spatial obj){
		this.obj = obj;
		this.uuid = uuid;
		this.localRotation = obj.getLocalRotation();
		this.localTranslation = obj.getLocalTranslation();
//		System.out.println("Local translation!!!:"+localTranslation);
	}
	
	public void setSpatial(Spatial obj){
		this.obj= obj;
		this.localRotation = obj.getLocalRotation();
		this.localTranslation = obj.getLocalTranslation();
	}
	
	public Spatial getSpatial(){
		return(obj);
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
