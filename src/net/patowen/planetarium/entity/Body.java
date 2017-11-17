package net.patowen.planetarium.entity;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Rotation;
import net.patowen.planetarium.math.Transform;
import net.patowen.planetarium.rendering.SceneNodeType;
import net.patowen.planetarium.rendering.RenderContext;

public class Body extends BodySystem {
	private SceneNodeType renderInfo;
	private Rotation rotation;
	
	public Body(SceneNodeType renderInfo, double garam, Rotation rotation) {
		super(garam);
		this.renderInfo = renderInfo;
		this.rotation = rotation;
	}
	
	public void render(GL3 gl, RenderContext context) {
		renderInfo.render(gl, context, Transform.translation(getAbsolutePosition()).transform(rotation.getTransform()));
	}
	
	public void updateFromTime(double time) {
		rotation.setParametersFromTime(time);
	}
}
