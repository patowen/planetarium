package net.patowen.planetarium;

import java.util.ArrayList;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.entity.Player;
import net.patowen.planetarium.entity.SolarSystem;
import net.patowen.planetarium.math.Transform;
import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.SceneNode;
import net.patowen.planetarium.rendering.RenderContext;
import net.patowen.planetarium.rendering.ShaderUniformHandler;

/**
 * Handles the logic and rendering of all elements in the scene and
 * represents the scene itself
 * @author Patrick Owen
 */
public class World {
	private Controller c;
	
	private ArrayList<SceneNode> nodes; //List of rendered nodes
	private ArrayList<SceneNode> cameraAttachedNodes; //List of rendered nodes attached to the camera
	private Player player; //Controllable camera
	private SolarSystem solarSystem;
	
	private int viewportWidth, viewportHeight;
	
	/**
	 * Initializes the world and its {@code Player} object
	 * @param c
	 */
	public World(Controller c) {
		this.c = c;
		
		nodes = new ArrayList<>();
		cameraAttachedNodes = new ArrayList<>();
		reset();
	}
	
	/**
	 * Sets the viewport used for perspective in the world
	 * @param width The width in pixels of the viewport
	 * @param height The height in pixels of the viewport
	 */
	public void setViewport(int width, int height) {
		viewportWidth = width;
		viewportHeight = height;
	}
	
	/**
	 * Resets the world to how it was before it was initialized
	 */
	public void reset() {
		nodes.clear();
		player = new Player(c, this);
		solarSystem = new SolarSystem(c, this);
		SceneNode starfield = new SceneNode(c.starfield);
		starfield.setTransformation(Transform.rotation(new Vector3(1, 0, 0), -23.4392811 * Math.PI / 180)
				.transform(Transform.rotation(new Vector3(0, 0, 1), Math.PI)));
		cameraAttachedNodes.add(starfield);
	}
	
	/**
	 * Spawns the specified node into the scene
	 * @param sceneNode the node to add
	 */
	public void addNode(SceneNode sceneNode) {
		nodes.add(sceneNode);
	}
	
	public void addCameraAttachdNode(SceneNode sceneNode) {
		cameraAttachedNodes.add(sceneNode);
	}
	
	/**
	 * Handles a step of the world's logic
	 * @param dt the time step in seconds
	 */
	public void step(double dt) {
		InputHandler inputHandler = c.getInputHandler();
		player.step(dt);
		solarSystem.step(dt);
		
		if (inputHandler.getKeyPressed(InputHandler.RESET))
			reset();
		if (inputHandler.getKeyPressed(InputHandler.CLEAR))
			nodes.clear();
		
		inputHandler.updatePressed();
	}
	
	/**
	 * Renders a single frame of the world
	 * @param gl
	 */
	public void render(GL3 gl, RenderContext sh) {
		player.setPerspective((float)viewportWidth/viewportHeight);
		
		ShaderUniformHandler suh = sh.getShaderUniformHandler();
		suh.setLightAmbient(new float[] {0.01f, 0.01f, 0.01f});
		suh.setLightDiffuse(0, new float[] {0.9f, 0.9f, 0.9f});
		
		player.setView();
		suh.addTransformation(solarSystem.getAnchor());
		
		suh.setLightPosition(0, new Vector3(0, 0, 0));
		
		gl.glDisable(GL3.GL_DEPTH_TEST);
		
		for (SceneNode node : cameraAttachedNodes) {
			suh.pushTransformation();
			suh.addTransformation(Transform.translation(solarSystem.getAnchor().w.times(-1).plus(player.getPosition())));
			node.render(gl, sh);
			suh.popTransformation();
		}
		
		gl.glEnable(GL3.GL_DEPTH_TEST);
		
		for (SceneNode node : nodes)
			node.render(gl, sh);
		
		solarSystem.render(gl, sh);
	}
}
