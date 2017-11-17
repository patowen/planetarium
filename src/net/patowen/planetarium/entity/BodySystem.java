package net.patowen.planetarium.entity;

import java.util.ArrayList;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.RenderContext;

public abstract class BodySystem {
	protected double garam;
	
	private BodySystem parentBodySystem;
	private ArrayList<BodySystem> childBodySystems;
	private Vector3 relativePosition;
	
	public BodySystem(double garam) {
		this.garam = garam;
		parentBodySystem = null;
		childBodySystems = new ArrayList<>();
		relativePosition = new Vector3();
	}
	
	protected void addChild(BodySystem childBodySystem) {
		childBodySystems.add(childBodySystem);
		childBodySystem.parentBodySystem = this;
	}
	
	public abstract void updateFromTime(double time);
	public abstract void render(GL3 gl, RenderContext context);
	
	public double getGaram() {
		return garam;
	}
	
	public void setRelativePosition(Vector3 position) {
		relativePosition = position;
	}
	
	public Vector3 getAbsolutePosition() {
		if (parentBodySystem == null) {
			return relativePosition;
		} else {
			return relativePosition.plus(parentBodySystem.getAbsolutePosition());
		}
	}
}
