package net.patowen.planetarium.entity;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Orbit;
import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.RenderContext;

public class Satellite {
	private BodySystem bodySystem;
	private BodySystem parentBodySystem;
	private Orbit orbit;
	private Vector3 position; // Relative to parent body
	private Vector3 parentDisplacement;
	
	public Satellite(BodySystem bodySystem, BodySystem parentBodySystem, Orbit orbit) {
		this.bodySystem = bodySystem;
		this.parentBodySystem = parentBodySystem;
		this.orbit = orbit;
		orbit.setGaram(bodySystem.getGaram() + parentBodySystem.getGaram());
	}
	
	public void updateFromTime(double time) {
		orbit.setParametersFromTime(time);
		Vector3 orbitPosition = orbit.getPosition(time);
		position = orbitPosition;
		parentDisplacement = orbitPosition.times(-bodySystem.getGaram() / (bodySystem.getGaram() + parentBodySystem.getGaram()));
		bodySystem.updateFromTime(time);
	}
	
	public void updateRelativePosition(Vector3 totalParentDisplacment) {
		bodySystem.setRelativePosition(position.plus(totalParentDisplacment));
	}
	
	public void render(GL3 gl, RenderContext context) {
		bodySystem.render(gl, context);
	}
	
	public Vector3 getMainBodyDisplacement() {
		return parentDisplacement;
	}
}
