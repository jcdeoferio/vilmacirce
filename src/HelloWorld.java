import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import com.jme.app.SimpleGame;
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
import com.jmex.model.converters.ObjToJme;
 
/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class HelloWorld extends SimpleGame {
	private static final Logger logger = Logger
			.getLogger(HelloWorld.class.getName());
 
	/** Material for my bullet */
	MaterialState bulletMaterial;
 
	/** Target you're trying to hit */
	Node target;
 
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
 
	public static void main(String[] args) {
		HelloWorld app = new HelloWorld();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}
 
	protected void simpleInitGame() {
		setupSound();
 
		/** Create a + for the middle of the screen */
		Text cross = Text.createDefaultTextLabel("Crosshairs", "crosshair");
 
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
		 * Set the action called "firebullet", bound to KEY_F, to performAction
		 * FireBullet
		 */
		input.addAction(new FireBullet(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, true);
 
		/** Make bullet material */
		bulletMaterial = display.getRenderer().createMaterialState();
		bulletMaterial.setEmissive(ColorRGBA.green.clone());
 
		/** Make target material */
		// Point to a URL of my model
		URL model = HelloWorld.class.getClassLoader().getResource(
			"jmetest/data/model/models/tie/TIEF.3DS");
 
		// Create something to convert .3ds format to .jme
		FormatConverter converter = new MaxToJme();
 
		// This byte array will hold my .jme file
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		try {
			// Use the format converter to convert .3ds to .jme
			converter.convert(model.openStream(), BO);
			target = (Node) BinaryImporter.getInstance().load(
				new ByteArrayInputStream(BO.toByteArray()));
			target.rotateUpTo(new Vector3f(0.0f, 0.0f, 1.0f));
			target.setModelBound(new BoundingSphere());
			target.updateModelBound();
			// Put her on the scene graph
			rootNode.attachChild(target);
		} catch (IOException e) { // Just in case anything happens
			logger.logp(Level.SEVERE, this.getClass().toString(),
				"simpleInitGame()", "Exception", e);
			System.exit(0);
		}

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
				"/jmetest/data/sound/laser.ogg"), false);
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
 
	class FireBullet extends KeyInputAction {
		int numBullets;
 
		public void performAction(InputActionEvent evt) {
			logger.info("BANG");
			/** Create bullet */
			Sphere bullet = new Sphere("bullet" + numBullets++, 32, 32, .25f);
			bullet.setModelBound(new BoundingSphere());
			bullet.updateModelBound();
			/** Move bullet to the camera location */
			bullet.setLocalTranslation(new Vector3f(cam.getLocation()));
			bullet.setRenderState(bulletMaterial);
			/**
			 * Update the new world locaion for the bullet before I add a
			 * controller
			 */
			bullet.updateGeometricState(0, true);
			/**
			 * Add a movement controller to the bullet going in the camera's
			 * direction
			 */
			bullet.addController(new BulletMover(bullet, new Vector3f(cam
					.getDirection())));
			rootNode.attachChild(bullet);
			bullet.updateRenderState();
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
				rootNode.detachChild(bullet);
				bullet.removeController(this);
				return;
			}
			/** Move bullet */
			Vector3f bulletPos = bullet.getLocalTranslation();
			bulletPos.addLocal(direction.mult(time * speed));
			bullet.setLocalTranslation(bulletPos);
			/** Does the bullet intersect with target? */
			if (bullet.getWorldBound().intersects(target.getWorldBound())) {
				logger.info("OWCH!!!");
				targetSound.setWorldPosition(target.getWorldTranslation());
 
				target.setLocalTranslation(new Vector3f(r.nextFloat() * 10, r
						.nextFloat() * 10, r.nextFloat() * 10));
 
				lifeTime = 0;
 
				targetSound.play();
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
	}
 
	@Override
	protected void cleanup() {
		super.cleanup();
		if (AudioSystem.isCreated()) {
			AudioSystem.getSystem().cleanup();
		}
	}
}