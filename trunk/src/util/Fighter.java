package util;
import java.util.UUID;


import com.jme.scene.Spatial;


public class Fighter extends SerializableSpatial{
	private static final long serialVersionUID = 6450300429259485682L;
	
	public Fighter(UUID uuid, Spatial obj) {
		super(uuid, obj);
	}
}
