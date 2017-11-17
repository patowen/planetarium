package net.patowen.planetarium.model;

import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.ETexture;
import net.patowen.planetarium.rendering.Model;
import net.patowen.planetarium.rendering.SceneNodeType;

public class Starfield extends SceneNodeType {
	private int textureStepsPerWrap = 256;
	private int numSteps = 256;
	private double radius = 1000;

	public Starfield() {
		Model model = new Model();
		int slices = numSteps*2, stacks = numSteps;
		VertexGrid grid = new VertexGrid(slices, stacks); //slices, stacks
		
		for (int slice=0; slice<=slices; slice++) {
			for (int stack=0; stack<=stacks; stack++) {
				double phi = stack*Math.PI/stacks;
				double theta = slice*Math.PI*2/slices;
				Vector3 unitVector = new Vector3(
						Math.cos(theta)*Math.sin(phi),
						Math.sin(theta)*Math.sin(phi),
						Math.cos(phi));
				Vector3 position = unitVector.times(radius);
				grid.setPosition(slice, stack, position);
				grid.setNormal(slice, stack, new Vector3(unitVector.x, unitVector.y, unitVector.z));
			}
		}
		grid.setTexCoords(1, 0, 0, 1, textureStepsPerWrap*2, textureStepsPerWrap, 0, 0);
		grid.addToModel(model);
		setModel(model);
		setTexture(ETexture.STARFIELD);
		
		setColor(new float[] {0, 0, 0});
		setEmission(new float[] {1, 1, 1});
	}
}
