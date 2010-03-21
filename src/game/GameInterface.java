package game;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.Fighter;
import util.SerializableSpatial;
 
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Tube;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
 
/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class GameInterface extends SimpleGame {
	private static final Logger logger = Logger
			.getLogger(GameInterface.class.getName());
	
//	Node ties;
	
//	Node lasers;
 
	/** Material for my bullet */
	MaterialState bulletMaterial;
 
	/** Target you're trying to hit */
//	Node target;
	ByteArrayInputStream targetModel;
	
	Node laserNode;
	
	volatile HashMap<UUID, Spatial> targets;
	
	volatile HashMap<UUID, Tube> lasers;
 
	/** Location of laser sound */
	URL laserURL;
 
	/** Location of hit sound */
	URL hitURL;
 
	/** Used to move target location on a hit */
	Random r = new Random();
 
	/** A sky box for our scene. */
	Skybox sb;
 
	/**
	 * The sound tracks that will be in charge of maintaining our sound effects.
	 */
	AudioTrack laserSound;
	AudioTrack targetSound;
	long lastTimeFired;
 
	protected void simpleInitGame() {
		setupSound();
		lastTimeFired = System.currentTimeMillis();
 
		/** Create a + for the middle of the screen */
		Text cross = Text.createDefaultTextLabel("Crosshairs", "+");
 
		// 8 is half the width of a font char
		/** Move the + to the middle */
		cross.setLocalTranslation(new Vector3f(display.getWidth() / 2f - 8f,
				display.getHeight() / 2f - 8f, 0));
		statNode.attachChild(cross);
 
		/** Create a skybox to suround our world */
		setupSky();
 
		// Attach the skybox to our root node, and force the rootnode to show
		// so that the skybox will always show
		rootNode.attachChild(sb);
		rootNode.setCullHint(Spatial.CullHint.Never);
 
		/**
		 * Set the action called "firebullet", bound to DEVICE_MOUSE, to performAction
		 * FireBullet
		 */
		input.addAction(new FireBullet(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, true);
		
		input.addAction(new SpawnTieFighter(), "spawntiefighter", KeyInput.KEY_F, false);
 
		/** Make bullet material */
		bulletMaterial = display.getRenderer().createMaterialState();
		bulletMaterial.setEmissive(ColorRGBA.green.clone());
	
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		try{
			/** Make target material */
			// Point to a URL of my model
			URL model = GameInterface.class.getClassLoader().getResource(
				"jmetest/data/model/models/tie/TIEF.3DS");
	 
			// Create something to convert .3ds format to .jme
			FormatConverter converter = new MaxToJme();
			converter.convert(model.openStream(), BO);
			
			targetModel = new ByteArrayInputStream(BO.toByteArray());
			
		} catch (IOException e) { // Just in case anything happens
			logger.logp(Level.SEVERE, this.getClass().toString(),
				"simpleInitGame()", "Exception", e);
			System.exit(0);
		}
		
		laserNode = new Node("laserNode");
		rootNode.attachChild(laserNode);
		
		targets = new HashMap<UUID, Spatial>();
		lasers = new HashMap<UUID, Tube>();
	}
 
	private void setupSound() {
		/** Set the 'ears' for the sound API */
		AudioSystem audio = AudioSystem.getSystem();
		audio.getEar().trackOrientation(cam);
		audio.getEar().trackPosition(cam);
 
		/** Create program sound */
		targetSound = audio.createAudioTrack(getClass().getResource(
				"/jmetest/data/sound/explosion.ogg"), false);
		targetSound.setMaxAudibleDistance(1000);
		targetSound.setVolume(1.0f);
		laserSound = audio.createAudioTrack(getClass().getResource(
				"/jmetest/data/sound/sounds/gunstie.ogg"), false);
		laserSound.setMaxAudibleDistance(1000);
		laserSound.setVolume(1.0f);
	}
 
	private void setupSky() {
		sb = new Skybox("skybox", 400, 400, 400);
 
		try {
			ResourceLocatorTool.addResourceLocator(
					ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(getClass().getResource(
							"/jmetest/data/images/images/skybox/")));
		} catch (Exception e) {
			logger.warning("Unable to access texture directory.");
			e.printStackTrace();
		}
 
		sb.setTexture(Skybox.Face.North, TextureManager.loadTexture(
				"north.jpg", Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.setTexture(Skybox.Face.West, TextureManager.loadTexture("west.jpg",
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.setTexture(Skybox.Face.South, TextureManager.loadTexture(
				"south.jpg", Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.setTexture(Skybox.Face.East, TextureManager.loadTexture("east.jpg",
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.setTexture(Skybox.Face.Up, TextureManager.loadTexture("top.jpg",
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.setTexture(Skybox.Face.Down, TextureManager.loadTexture(
				"bottom.jpg", Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));
		sb.preloadTextures();
 
		CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.None);
		cullState.setEnabled(true);
		sb.setRenderState(cullState);
 
		sb.updateRenderState();
	}
	
	class SpawnTieFighter extends KeyInputAction{

		@Override
		public void performAction(InputActionEvent evt) {
//			try {
//				targetModel.reset();
//				Node target = (Node) BinaryImporter.getInstance().load(targetModel);
//				target.rotateUpTo(new Vector3f(0.0f, 0.0f, 1.0f));
				UUID targetUUID = UUID.randomUUID();
				Spatial target = new Sphere(targetUUID.toString(), 16, 16, 5);
				target.setModelBound(new BoundingSphere());
				target.updateModelBound();
				// Put her on the scene graph
				rootNode.attachChild(target);
				
				rootNode.updateRenderState();
				
				targets.put(UUID.randomUUID(), target);
//			} catch (IOException e) { // Just in case anything happens
//				logger.logp(Level.SEVERE, this.getClass().toString(),
//					"simpleInitGame()", "Exception", e);
//				System.exit(0);
//			}
		}
		
	}
 
	class FireBullet extends KeyInputAction {
		int numLasors;
 
		public void performAction(InputActionEvent evt) {
			long currTime = System.currentTimeMillis();
			if(currTime - lastTimeFired < 250){
				return;
			}
			lastTimeFired = currTime;
			logger.info("BANG");
			/** Create bullet */
			Tube lasor = new Tube("lasor" + numLasors++, 0.1f, 0.01f, 1.0f);
			lasor.rotateUpTo(cam.getDirection());
			lasor.setModelBound(new BoundingBox());
			lasor.updateModelBound();
			/** Move bullet to the camera location */
			Vector3f lasorDir = new Vector3f(1.0f, 1.0f, 1.0f);
			lasor.setLocalTranslation(lasorDir.add(new Vector3f(cam.getLocation())));
			lasor.setRenderState(bulletMaterial);
			/**
			 * Update the new world locaion for the bullet before I add a
			 * controller
			 */
			lasor.updateGeometricState(0, true);
			/**
			 * Add a movement controller to the bullet going in the camera's
			 * direction
			 */
			lasor.addController(new BulletMover(lasor, new Vector3f(cam
					.getDirection())));
			laserNode.attachChild(lasor);
			
			lasers.put(UUID.randomUUID(), lasor);
			
			lasor.updateRenderState();
			/** Signal our sound to play laser during rendering */
			laserSound.setWorldPosition(cam.getLocation());
			laserSound.play();
		}
	}
 
	class BulletMover extends Controller {
		private static final long serialVersionUID = 1L;
		/** Bullet that's moving */
		TriMesh bullet;
 
		/** Direciton of bullet */
		Vector3f direction;
 
		/** speed of bullet */
		float speed = 80;
 
		/** Seconds it will last before going away */
		float lifeTime = 5;
 
		BulletMover(TriMesh bullet, Vector3f direction) {
			this.bullet = bullet;
			this.direction = direction;
			this.direction.normalizeLocal();
		}
 
		public void update(float time) {
			lifeTime -= time;
			/** If life is gone, remove it */
			if (lifeTime < 0) {
				laserNode.detachChild(bullet);
				bullet.removeController(this);
				lasers.remove(bullet);
				return;
			}
			/** Move bullet */
			Vector3f bulletPos = bullet.getLocalTranslation();
			bulletPos.addLocal(direction.mult(time * speed));
			bullet.setLocalTranslation(bulletPos);
			/** Does the bullet intersect with a target? */
			for(Entry<UUID, Spatial> targetEntry: targets.entrySet()){
				Spatial target = targetEntry.getValue();
				UUID targetuuid = targetEntry.getKey();
				
				if (bullet.getWorldBound().intersects(target.getWorldBound())) {
					logger.info("OWCH!!!");
					targetSound.setWorldPosition(target.getWorldTranslation());
	 
					target.setLocalTranslation(new Vector3f(r.nextFloat() * 10, r
							.nextFloat() * 10, r.nextFloat() * 10));
//					target.removeFromParent();
//					targets.remove(targetuuid);
	 
					lifeTime = 0;
	 
					targetSound.play();
				}
			}
		}
	}
 
	/**
	 * Called every frame for updating
	 */
	protected void simpleUpdate() {
		// Let the programmable sound update itself.
		AudioSystem.getSystem().update();
		// Move the skybox into position
		sb.getLocalTranslation().set(cam.getLocation().x, cam.getLocation().y,
				cam.getLocation().z);
		
//		target.setLocalTranslation(new Vector3f(0, 0, target.getLocalTranslation().z + 0.5f)); //moving tie target
	}
 
	@Override
	protected void cleanup() {
		super.cleanup();
		if (AudioSystem.isCreated()) {
			AudioSystem.getSystem().cleanup();
		}
	}
	
	Node getRootNode(){
		return(rootNode);
	}
	
	void setRootNode(Node rootNode){
		this.rootNode = rootNode;
		this.rootNode.updateRenderState();
	}
	
	public LinkedList<Fighter> getFighters(){
		LinkedList<Fighter> fighters = new LinkedList<Fighter>();
		
		if(targets == null)
			return(fighters);
		
		for(Entry<UUID, Spatial> targetEntry : targets.entrySet()){
			UUID targetUUID = targetEntry.getKey();
			Spatial target = targetEntry.getValue();
			
			fighters.add(new Fighter(targetUUID, target));
		}
		
		return(fighters);
	}
	
	public void setFighters(LinkedList<Fighter> fighters){
		if(targetModel == null || targets == null)
			return;
		
		HashMap<UUID,Spatial> targetsToBeRemoved = new HashMap<UUID, Spatial>(targets);
		
		for(Fighter fighter : fighters){
			Spatial target = null;
			if(targets.containsKey(fighter.getUUID())){
				target = targets.get(fighter.getUUID());
				targetsToBeRemoved.remove(fighter.getUUID());
			}
			else{
//				try {
//					targetModel.reset();
//					// Use the format converter to convert .3ds to .jme
//					target = (Node) BinaryImporter.getInstance().load(targetModel);
					UUID targetUUID = fighter.getUUID();
					target = new Sphere(targetUUID.toString(), 16, 16, 5);
					target.setModelBound(new BoundingSphere());
					target.updateModelBound();
					// Put her on the scene graph
					rootNode.attachChild(target);
					
					rootNode.updateRenderState();
					
					targets.put(fighter.getUUID(), target);
				
//				} catch (IOException e) { // Just in case anything happens
//					logger.logp(Level.SEVERE, this.getClass().toString(),
//						"simpleInitGame()", "Exception", e);
//					System.exit(0);
//				}
			}
			
			target.setLocalRotation(fighter.getLocalRotation());
			target.setLocalTranslation(fighter.getLocalTranslation());
		}
		for(Entry<UUID, Spatial> targetEntry : targetsToBeRemoved.entrySet()){
			UUID targetUUID = targetEntry.getKey();
			Spatial target = targetEntry.getValue();
			target.removeFromParent();
			targets.remove(targetUUID);
		}
	}
	
	public LinkedList<SerializableSpatial> getLasers(){
		LinkedList<SerializableSpatial> serLasers = new LinkedList<SerializableSpatial>();
		
		if(lasers == null)
			return(serLasers);
		
		for(Entry<UUID, Tube> laser : lasers.entrySet()){
			serLasers.add(new SerializableSpatial(laser.getKey(), laser.getValue()));
		}
		
		return(serLasers);
	}
	
	public void setLasers(LinkedList<SerializableSpatial> serLasers){
		if(laserNode == null || lasers == null)
			return;
	
//		for(Tube laser : lasers.values())
//			rootNode.detachChild(laser);
		
		laserNode.detachAllChildren();
		
		lasers = new HashMap<UUID, Tube>();
		
		for(SerializableSpatial serLaser : serLasers){
			Tube laser = null;
			if(lasers.containsKey(serLaser.getUUID())){
				laser = lasers.get(serLaser.getUUID());
			}
			else{
				laser = new Tube(serLaser.getUUID().toString(), 0.1f, 0.01f, 1.0f);
				laser.setModelBound(new BoundingBox());
				laser.updateModelBound();
				laser.setRenderState(bulletMaterial);
				
				laser.updateGeometricState(0, true);

				laserNode.attachChild(laser);
				
				lasers.put(serLaser.getUUID(), laser);
				
				laser.updateRenderState();
			}
			
			laser.setLocalRotation(serLaser.getLocalRotation());
			laser.setLocalTranslation(serLaser.getLocalTranslation());
		}
	}
}
