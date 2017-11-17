package net.patowen.planetarium.model;

import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.ETexture;
import net.patowen.planetarium.rendering.Model;
import net.patowen.planetarium.rendering.SceneNodeType;

public class Planet extends SceneNodeType {
	private int textureStepsPerWrap = 64;
	private int numSteps = 64;
	private double eqRadius = 1;
	private double poleRadius = 0.5;
	
	public Planet(ETexture texture, double equatorialRadius, double polarRadius, boolean shining) {
		eqRadius = equatorialRadius;
		poleRadius = polarRadius;
		Model model = new Model();
		int slices = numSteps*2, stacks = numSteps;
		VertexGrid grid = new VertexGrid(slices, stacks); //slices, stacks
		
		for (int slice=0; slice<=slices; slice++) {
			for (int stack=0; stack<=stacks; stack++) {
				double phi = stack*Math.PI/stacks;
				double theta = slice*Math.PI*2/slices;
				Vector3 position = new Vector3(
						-eqRadius*Math.cos(theta)*Math.sin(phi),
						-eqRadius*Math.sin(theta)*Math.sin(phi),
						-poleRadius*Math.cos(phi));
				grid.setPosition(slice, stack, position);
				
				Vector3 normal = new Vector3(
						-poleRadius*Math.cos(theta)*Math.sin(phi),
						-poleRadius*Math.sin(theta)*Math.sin(phi),
						-eqRadius*Math.cos(phi));
				normal.normalize();
				grid.setNormal(slice, stack, normal);
			}
		}
		grid.setTexCoords(0, 1, 1, 0, textureStepsPerWrap*2, textureStepsPerWrap, 0, 0);
		grid.addToModel(model);
		setModel(model);
		setTexture(texture);
		
		if (shining) {
			setColor(new float[] {0, 0, 0});
			setEmission(new float[] {1, 1, 1});
		}
	}
}
