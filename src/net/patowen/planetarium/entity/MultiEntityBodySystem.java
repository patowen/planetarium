package net.patowen.planetarium.entity;

import java.util.ArrayList;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Orbit;
import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.RenderContext;

public class MultiEntityBodySystem extends BodySystem {
	private BodySystem mainBody;
	private ArrayList<Satellite> satellites;
	private Vector3 mainBodyDisplacement;
	
	public MultiEntityBodySystem(BodySystem mainBody) {
		super(mainBody.garam);
		this.mainBody = mainBody;
		addChild(mainBody);
		satellites = new ArrayList<>();
	}
	
	public void addSatellite(BodySystem satellite, Orbit orbit) {
		satellites.add(new Satellite(satellite, mainBody, orbit));
		garam += satellite.getGaram();
		
		addChild(satellite);
	}
	
	public void updateFromTime(double time) {
		mainBodyDisplacement = new Vector3();
		mainBody.updateFromTime(time);
		
		for (Satellite satellite : satellites) {
			satellite.updateFromTime(time);
			mainBodyDisplacement = mainBodyDisplacement.plus(satellite.getMainBodyDisplacement());
		}
		
		for (Satellite satellite : satellites) {
			satellite.updateRelativePosition(mainBodyDisplacement);
		}
	}
	
	public void render(GL3 gl, RenderContext context) {
		mainBody.render(gl, context);
		for (Satellite satellite : satellites) {
			satellite.render(gl, context);
		}
	}
}
