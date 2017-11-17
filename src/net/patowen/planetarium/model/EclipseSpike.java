package net.patowen.planetarium.model;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Vector2;
import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.Model;
import net.patowen.planetarium.rendering.SceneNodeType;
import net.patowen.planetarium.rendering.Vertex;

public class EclipseSpike extends SceneNodeType {
	public EclipseSpike() {
		Model model = new Model();
		model.setPrimitiveType(GL3.GL_LINES);
		model.addVertex(new Vertex(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector2(0, 0)));
		model.addVertex(new Vertex(new Vector3(4e5, 0, 0), new Vector3(0, 0, 0), new Vector2(0, 0)));
		model.addLine(0, 1);
		
		setModel(model);
		setColor(new float[] {0, 0, 0});
		setEmission(new float[] {1, 1, 1});
	}
}
