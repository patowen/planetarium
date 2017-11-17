package net.patowen.planetarium.math;

public class PlanetRotation extends Rotation {
	private double[] raPoly;
	private double[] decPoly;
	private double[] pmPoly;
	
	public PlanetRotation() {
		super(Transform.rotation(new Vector3(1, 0, 0), -23.4392811 * Math.PI / 180));
	}
	
	public void setParametersFromTime(double time) {
		double days = time / 86400;
		double centuries = days / 36525;
		
		modifiedRightAscension = Math.PI / 2. + (raPoly[0] + (raPoly[1] + raPoly[2] * centuries) * centuries) * Math.PI / 180.;
		codeclination = Math.PI / 2. - (decPoly[0] + (decPoly[1] + decPoly[2] * centuries) * centuries) * Math.PI / 180.;
		primeMeridian = (pmPoly[0] + (pmPoly[1] + pmPoly[2] * days) * days) * Math.PI / 180.;
	}
	
	public static PlanetRotation getEarthRotation() {
		PlanetRotation earthRotation = new PlanetRotation();
		earthRotation.raPoly = new double[] {0., -0.641, 0.};
		earthRotation.decPoly = new double[] {90., -0.557, 0.};
		earthRotation.pmPoly = new double[] {190.147, 360.9856235, 0.};
		return earthRotation;
	}
	
	public static PlanetRotation getMoonRotation() {
		PlanetRotation earthRotation = new PlanetRotation();
		earthRotation.raPoly = new double[] {270., 0., 0.};
		earthRotation.decPoly = new double[] {66.534, 0., 0.};
		earthRotation.pmPoly = new double[] {38.314, 13.1763581, 0.};
		return earthRotation;
	}
}
