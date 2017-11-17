package net.patowen.planetarium.entity;

import com.jogamp.opengl.math.FloatUtil;

import net.patowen.planetarium.Controller;
import net.patowen.planetarium.InputHandler;
import net.patowen.planetarium.World;
import net.patowen.planetarium.math.Transform;
import net.patowen.planetarium.math.Vector3;

/**
 * A controllable camera used to navigate hyperbolic space with some control over the world
 * @author Patrick Owen
 */
public class Player {
	private Controller c;
	@SuppressWarnings("unused")
	private World w;
	
	private Transform pos;
	private Vector3 vel;
	
	private boolean noclip;
	
	//Perspective
	private double zoom = 0.6;
	
	/**
	 * Initializes the {@code Player} and puts it in the specified {@code World}
	 * @param c
	 * @param w the world the {@code Player} can explore
	 */
	public Player(Controller c, World w) {
		this.c = c;
		this.w = w;
		
		pos = Transform.translation(new Vector3(15000, 0, 0));
		pos = pos.transform(Transform.rotation(new Vector3(0, 1, 0), Math.PI/2));
		pos = pos.transform(Transform.rotation(new Vector3(0, 0, 1), Math.PI/2));
		vel = new Vector3();
		
		noclip = true;
		
		handleOrientation();
	}
	
	public Vector3 getPosition() {
		return new Vector3(pos.w);
	}
	
	/**
	 * Moves the camera based on player input and exercises some control, such
	 * as spawning structures
	 * @param dt the time step in seconds
	 */
	public void step(double dt) {
		handleTurning(dt);
		handleAcceleration(dt);
		
		handleMovement(dt);
		
		handleOrientation();
		
		handleSpawning();
		
		handleZooming(dt);
	}
	
	/**
	 * Places meshes in front of the player depending on what controls are pressed
	 */
	private void handleSpawning() {
	}
	
	/**
	 * Accelerates to the desired velocity depending on the controls the user is pressing
	 * @param dt the time step in seconds
	 */
	private void handleAcceleration(double dt) {
		InputHandler inputHandler = c.getInputHandler();
		double maxChange, maxVel;
		
		maxVel = 40000;
		if (inputHandler.getKey(InputHandler.SLOW)) {
			maxVel /= 10;
		}
		maxChange = maxVel*2*dt;
		double dx=0, dy=0, dz=0;
		if (inputHandler.getMouseButton(InputHandler.FORWARDS))
			dz -= 1;
		if (inputHandler.getMouseButton(InputHandler.BACKWARDS))
			dz += 1;
		if (inputHandler.getKey(InputHandler.UP))
			dy += 1;
		if (inputHandler.getKey(InputHandler.DOWN))
			dy -= 1;
		if (inputHandler.getKey(InputHandler.RIGHT))
			dx += 1;
		if (inputHandler.getKey(InputHandler.LEFT))
			dx -= 1;
		
		if (noclip) {
			Vector3 goalVel = new Vector3(maxVel*dx, maxVel*dy, maxVel*dz);
			approachVelocity(goalVel, maxChange);
		} else {
			vel = vel.times(Math.exp(-0.2*dt)); //Air friction
		}
	}
	
	/**
	 * Accelerates to the given velocity linearly
	 * @param goalVel the ideal velocity to achieve
	 * @param maxChange the maximum change allowed to be added to the current velocity
	 */
	private void approachVelocity(Vector3 goalVel, double maxChange) {
		double dist = goalVel.minus(vel).magnitude();
		if (dist <= maxChange) {
			vel = goalVel;
		} else {
			double progress = maxChange/dist;
			vel = vel.times(1-progress).plus(goalVel.times(progress));
		}
	}
	
	/**
	 * Moves the camera properly given the current velocity
	 * @param dt the time step
	 */
	private void handleMovement(double dt) {
		Transform translation = Transform.translation(convertToPosition(vel, dt));
		pos = pos.transform(translation);
		pos.normalize();
	}
	
	private void handleOrientation() {
		// This method should slowly orient the player toward the gravity field.
	}
	
	/**
	 * Rotates the camera based on the controls pressed
	 * @param dt the time step
	 */
	private void handleTurning(double dt) {
		InputHandler inputHandler = c.getInputHandler();
		
		inputHandler.readMouse();
		
		Transform o = Transform.identity();
		o = o.transformedBy(Transform.rotation(o.y, -inputHandler.getMouseX()*45*Math.atan(zoom)));
		o = o.transformedBy(Transform.rotation(o.x, -inputHandler.getMouseY()*45*Math.atan(zoom)));
		double tilt = 0;
		if (inputHandler.getKey(InputHandler.TILT_LEFT))
			tilt -= 1;
		if (inputHandler.getKey(InputHandler.TILT_RIGHT))
			tilt += 1;
		o = o.transformedBy(Transform.rotation(o.z, -tilt*dt));
		vel = o.inverse().transform(vel);
		pos = pos.transform(o);
	}
	
	/**
	 * Allows the user to change perspective
	 * @param dt the time step
	 */
	private void handleZooming(double dt) {
		InputHandler inputHandler = c.getInputHandler();
		if (inputHandler.getKey(InputHandler.ZOOM_IN))
			zoom *= Math.exp(-dt);
		if (inputHandler.getKey(InputHandler.ZOOM_OUT))
			zoom *= Math.exp(dt);
	}
	
	/**
	 * Sets the perspective matrix to the correct type based on player input
	 * @param aspect Aspect ratio of the screen
	 */
	public void setPerspective(float aspect) {
		float[] mat = new float[16];
		c.getRenderContext().getShaderUniformHandler().setPerspective(FloatUtil.makePerspective(mat, 0, true, (float)(Math.atan(zoom)*2), aspect, 100f, 1e9f));
	}
	
	/**
	 * Transforms the view such that the player is transformed to the origin facing the default direction
	 */
	public void setView() {
		c.getRenderContext().getShaderUniformHandler().addTransformation(pos.inverse());
	}
	
	/**
	 * Converts the velocity to the position an object would reach after traveling at that
	 * velocity for the given amount of time.
	 * @param vel the velocity to convert
	 * @param dt the time in seconds
	 * @return the resulting position
	 */
	private Vector3 convertToPosition(Vector3 vel, double dt) {
		return vel.times(dt);
	}
}
