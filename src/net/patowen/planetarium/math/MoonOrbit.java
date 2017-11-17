package net.patowen.planetarium.math;

public class MoonOrbit extends Orbit {
	private LunarTheory lunarTheory;
	
	public MoonOrbit(LunarTheory lunarTheory) {
		this.lunarTheory = lunarTheory;
	}
	
	public void setParametersFromTime(double time) {
		lunarTheory.perturbOrbit(this, time);
		setStartTime(time);
	}
}
