package net.patowen.planetarium;

import javax.swing.JOptionPane;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import net.patowen.planetarium.model.EclipseSpike;
import net.patowen.planetarium.model.Planet;
import net.patowen.planetarium.model.PlanetCubic;
import net.patowen.planetarium.model.StarfieldCubic;
import net.patowen.planetarium.rendering.ETexture;
import net.patowen.planetarium.rendering.RenderContext;
import net.patowen.planetarium.rendering.SceneNodeType;

/**
 * {@code Controller} acts as a context in which all other classes can access shared
 * data without having to pass things around arbitrarily.
 * @author Patrick Owen
 */
public class Controller {
	private FPSAnimator anim;
	private RenderContext renderContext;
	private InputHandler inputHandler;
	private GLWindow win;
	
	/** A renderable scene node */
	public SceneNodeType starfield;
	public SceneNodeType earth, moon, sun;
	public SceneNodeType eclipseSpike;
	
	/**
	 * Constructs all meshes
	 */
	public void init() {
		starfield = new StarfieldCubic();
		earth = new PlanetCubic(ETexture.EARTH, 6378.1, 6356.8, false);
		moon = new Planet(ETexture.MOON, 1738.1, 1736.0, false);
		sun = new Planet(ETexture.BLANK, 6.957e5, 6.957e5, true);
		eclipseSpike = new EclipseSpike();
	}
	
	/**
	 * Initializes all textures and prepares all meshes for rendering
	 * @param gl
	 */
	public void renderInit(GL3 gl) {
		starfield.renderInit(gl);
		earth.renderInit(gl);
		moon.renderInit(gl);
		sun.renderInit(gl);
		eclipseSpike.renderInit(gl);
	}
	
	/**
	 * Gracefully quits the application
	 */
	public void exit() {
		anim.stop();
	}
	
	/**
	 * Begins the render loop
	 */
	public void startAnimation() {
		anim = new FPSAnimator(win, 60, true);
		anim.start();
	}
	
	/**
	 * Toggles whether the window is fullscreen
	 */
	public void toggleFullscreen() {
		win.setFullscreen(!win.isFullscreen());
	}
	
	/**
	 * Initializes OpenGL and creates a window with the OpenGL3 context
	 */
	public boolean createWindow() {
		GLCapabilities caps;
		try {
			caps = new GLCapabilities(GLProfile.get("GL3"));
		} catch (GLException e) {
			JOptionPane.showMessageDialog(null, "Your video card does not support OpenGL3, which is required to run this application.",
					"OpenGL3 not supported", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Failed to load native libraries for OpenGL. Your operating system might not be supported.",
					"Library loading failed", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		
		caps.setDepthBits(24);
		caps.setNumSamples(16);
		caps.setSampleBuffers(true);
		
		win = GLWindow.create(caps);
		
		return true;
	}
	
	/**
	 * Initializes the listener for input
	 */
	public void createInputHandler() {
		inputHandler = new InputHandler(this);
	}
	
	/**
	 * Loads the shader resources and compiles all the shaders
	 * @param gl
	 */
	public void initShaders(GL3 gl) {
		renderContext = new RenderContext();
		renderContext.initShaders(gl);
	}
	
	public RenderContext getRenderContext() {
		return renderContext;
	}
	
	/**
	 * Returns the main {@code InputHandler} object
	 * @return a reference to the main {@code InputHandler} object
	 */
	public InputHandler getInputHandler() {
		return inputHandler;
	}
	
	/**
	 * Returns the main window
	 * @return a reference to the main window
	 */
	public GLWindow getWindow() {
		return win;
	}
}
