package game;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import util.Fighter;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;

/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class GameInterface extends SimpleGame {
	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}
	
	public Camera getCam(){
		return cam;
	}

	private static final Logger logger = Logger.getLogger(GameInterface.class
			.getName());

	/** Material for my bullet */
	MaterialState bulletMaterial;

	/** Target you're trying to hit */
	// Node target;
	ByteArrayInputStream targetModel;

	private HashSet<Fighter> targets;

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
	private AudioTrack laserSound;
	private AudioTrack targetSound;
	private AudioTrack tieSound;
	public boolean initialized;
	long lastTimeFired;
	
	int score;
	long lastTimeHit;

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
		 * Set the action called "firebullet", bound to DEVICE_MOUSE, to
		 * performAction FireBullet
		 */
		input.addAction(new FireBullet(this), InputHandler.DEVICE_MOUSE,
				InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, true);

		input.addAction(new SpawnTieFighter(this, Fighter.fighterModel()), "spawntiefighter",
				KeyInput.KEY_F, false);

		/** Make bullet material */
		bulletMaterial = display.getRenderer().createMaterialState();
		bulletMaterial.setEmissive(ColorRGBA.green.clone());

		targets = new HashSet<Fighter>();
		initialized = true;
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
		
		tieSound = audio.createAudioTrack(getClass().getResource(
		"/jmetest/data/sound/sounds/tie.ogg"), false);
		tieSound.setMaxAudibleDistance(1000);
		tieSound.setVolume(1.0f);
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
	
	/**
	 * Called every frame for updating
	 */
	protected void simpleUpdate() {
		// Let the programmable sound update itself.
		AudioSystem.getSystem().update();
		// Move the skybox into position
		sb.getLocalTranslation().set(cam.getLocation().x, cam.getLocation().y,
				cam.getLocation().z);

		// target.setLocalTranslation(new Vector3f(0, 0,
		// target.getLocalTranslation().z + 0.5f)); //moving tie target
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
	
	synchronized void addFighter(Fighter fighter){
		fighter.getNode().addController(new FighterMover(fighter, this));
		
		rootNode.attachChild(fighter.getNode());
		rootNode.updateRenderState();
		
		targets.add(fighter);
		
		tieSound.setWorldPosition(fighter.getNode().getLocalTranslation());
		tieSound.play();
	}
	
	void removeFighter(Fighter fighter){
		targetSound.setWorldPosition(fighter.getNode().getLocalTranslation());
		targetSound.play();
		
		fighter.getNode().removeFromParent();
		
		targets.remove(fighter);
		
		fighter.clearNode();
		
		score += 10;
	}
	
	AudioTrack getLaserSound(){
		return(laserSound);
	}
	
	AudioTrack getTargetSound(){
		return(targetSound);
	}
	
	AudioTrack getTieSound(){
		return(tieSound);
	}
	
	HashSet<Fighter> getTargets(){
		return(targets);
	}
	
	int getScore(){
		return(score);
	}
	
	void hit(){
		long currTime = System.currentTimeMillis();
		if (currTime - lastTimeHit < 250) {
			return;
		}
		
		score -= 10;
	}

}
