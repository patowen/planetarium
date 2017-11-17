package net.patowen.planetarium.entity;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.Controller;
import net.patowen.planetarium.World;
import net.patowen.planetarium.math.LunarTheory;
import net.patowen.planetarium.math.MathHelper;
import net.patowen.planetarium.math.MoonOrbit;
import net.patowen.planetarium.math.PlanetOrbit;
import net.patowen.planetarium.math.PlanetRotation;
import net.patowen.planetarium.math.Transform;
import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.math.Vsop;
import net.patowen.planetarium.rendering.SceneNode;
import net.patowen.planetarium.rendering.RenderContext;

public class SolarSystem {
	private Controller c;
	private World w;
	
	private double sunParam = 1.32712440019e11;
	private double earthParam = 3.986004419e5;
	private double moonParam = 4.9048696e3;
	private Body sun, earth, moon;
	
	private SceneNode eclipseSpike;
	
	private MultiEntityBodySystem solarSystem;
	
	private double time;
	
	private Vsop vsop;
	private LunarTheory lunarTheory;
	
	public SolarSystem(Controller controller, World world) {
		c = controller;
		w = world;
		
		eclipseSpike = new SceneNode(c.eclipseSpike);
		w.addNode(eclipseSpike);
		
		lunarTheory = new LunarTheory();
		lunarTheory.loadData();
		lunarTheory.generateLunarSeries(1e-5);
		
		vsop = new Vsop();
		
		sun = new Body(c.sun, sunParam, PlanetRotation.getEarthRotation());
		earth = new Body(c.earth, earthParam, PlanetRotation.getEarthRotation());
		moon = new Body(c.moon, moonParam, PlanetRotation.getMoonRotation());
		
		MultiEntityBodySystem earthMoonSystem = new MultiEntityBodySystem(earth);
		earthMoonSystem.addSatellite(moon, new MoonOrbit(lunarTheory));
		
		solarSystem = new MultiEntityBodySystem(sun);
		solarSystem.addSatellite(earthMoonSystem, new PlanetOrbit(vsop));
		
		//time = 0;
		//time = -12*3600 + (31+30+20)*86400;
		/*time = -12*3600+(366+365+365+365+366+365+365+365+366+365+365+365+366+365+365+365+366+365+365+365+366+365+365+365
				+31+29+31+7)*86400 + 16*3600 + 15*60;*/
		/*time = -12*3600+(366+365+365+365+366+365+365+365+366+365+365+365+366+365+365+365+366
				+31+28+31+30+31+30+31+20)*86400 + 15*3600 + 15*60;*/
		time = MathHelper.getNumSeconds(2017, 11, 4, 12, 0, 0);
	}
	
	public Transform getAnchor() {
		return Transform.translation(earth.getAbsolutePosition().times(-1));
	}
	
	public void step(double dt) {
		time += dt*60*5;// + dt;
		
		solarSystem.updateFromTime(time);
		
		Vector3 moonRelLoc = moon.getAbsolutePosition().minus(sun.getAbsolutePosition());
		Vector3 xx = new Vector3(moonRelLoc);
		xx.normalize();
		Vector3 yy = xx.cross(new Vector3(0, 0, 1));
		yy.normalize();
		Vector3 zz = xx.cross(yy);
		eclipseSpike.setTransformation(Transform.translation(moon.getAbsolutePosition()).transform(new Transform(xx, yy, zz, new Vector3())));
	}
	
	public void render(GL3 gl, RenderContext context) {
		solarSystem.render(gl, context);
	}
}
