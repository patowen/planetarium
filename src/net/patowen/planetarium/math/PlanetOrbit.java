package net.patowen.planetarium.math;

public class PlanetOrbit extends Orbit {
	private Vsop vsop;
	
	public PlanetOrbit(Vsop vsop) {
		this.vsop = vsop;
	}
	
	public void setParametersFromTime(double time) {
		vsop.perturbOrbit(this, time);
		setStartTime(time);
	}
}
