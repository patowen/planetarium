package net.patowen.planetarium.math;

public abstract class Orbit {
	protected double eccentricity;
	protected double semimajorAxis;
	protected double inclination;
	protected double ascendingNodeLongitude;
	protected double argumentOfPeriapsis;
	private double garam;
	protected double startMeanAnomaly;
	protected double startTime;
	
	public void setSemimajorAxis(double semimajorAxis) {
		this.semimajorAxis = semimajorAxis;
	}
	
	public void setEccentricity(double eccentricity) {
		this.eccentricity = eccentricity;
	}
	
	public void setInclination(double inclination) {
		this.inclination = inclination;
	}
	
	public void setAscendingNodeLongitude(double ascendingNodeLongitude) {
		this.ascendingNodeLongitude = ascendingNodeLongitude;
	}
	
	public void setArgumentOfPeriapsis(double argumentOfPeriapsis) {
		this.argumentOfPeriapsis = argumentOfPeriapsis;
	}
	
	public void setGaram(double garam) {
		this.garam = garam;
	}
	
	public void setStartMeanAnomaly(double startMeanAnomaly) {
		this.startMeanAnomaly = startMeanAnomaly;
	}
	
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	
	public abstract void setParametersFromTime(double time);
	
	private double sqr(double x) {
		return x*x;
	}
	
	private double cube(double x) {
		return x*x*x;
	}
	
	private double getEccentricAnomaly(double time) {
		double orbitalFrequency = Math.sqrt(garam / cube(semimajorAxis));
		double meanAnomaly = startMeanAnomaly + (time - startTime)*orbitalFrequency;
		return MathHelper.getEccentricAnomaly(meanAnomaly, eccentricity);
	}
	
	public Vector3 getPosition(double time) {
		double eccentricAnomaly = getEccentricAnomaly(time);
		double semiminor = semimajorAxis*Math.sqrt(1-sqr(eccentricity));
		double ellipseCenter = -semimajorAxis*eccentricity;
		
		Vector3 pos = new Vector3(ellipseCenter + semimajorAxis*Math.cos(eccentricAnomaly), semiminor*Math.sin(eccentricAnomaly), 0);
		pos = Transform.rotation(new Vector3(0, 0, 1), argumentOfPeriapsis).transformVector(pos);
		pos = Transform.rotation(new Vector3(1, 0, 0), inclination).transformVector(pos);
		pos = Transform.rotation(new Vector3(0, 0, 1), ascendingNodeLongitude).transformVector(pos);
		return pos;
	}
	
	public Vector3 getVelocity(double time) {
		double eccentricAnomaly = getEccentricAnomaly(time);
		double semiminor = semimajorAxis*Math.sqrt(1-sqr(eccentricity));
		double ellipseCenter = -semimajorAxis*eccentricity;
		
		Vector3 pos = new Vector3(ellipseCenter + semimajorAxis*Math.cos(eccentricAnomaly), semiminor*Math.sin(eccentricAnomaly), 0);
		double speed = Math.sqrt(garam * (2 / pos.magnitude() - 1/semimajorAxis));
		
		Vector3 vel = new Vector3(-semimajorAxis*Math.sin(eccentricAnomaly), semiminor*Math.cos(eccentricAnomaly), 0);
		vel.normalize();
		vel = vel.times(speed);
		vel = Transform.rotation(new Vector3(0, 0, 1), argumentOfPeriapsis).transformVector(vel);
		vel = Transform.rotation(new Vector3(1, 0, 0), inclination).transformVector(vel);
		vel = Transform.rotation(new Vector3(0, 0, 1), ascendingNodeLongitude).transformVector(vel);
		
		return vel;
	}
	
	protected void setParamsFromPositionAndVelocity(Vector3 position, Vector3 velocity) {
		double mu = garam;
		Vector3 angularMomentum = position.cross(velocity);
		Vector3 nodeVector = new Vector3(0, 0, 1).cross(angularMomentum);
		
		Vector3 eccentricityVector = position.times(velocity.squared() - mu / position.magnitude()).minus(velocity.times(position.dot(velocity))).times(1.0/mu);
		eccentricity = eccentricityVector.magnitude();
		
		double energy = velocity.squared() * 0.5 - mu / position.magnitude();
		semimajorAxis = -mu / (energy * 2);
		
		inclination = Math.acos(angularMomentum.z / angularMomentum.magnitude());
		ascendingNodeLongitude = Math.atan2(nodeVector.y, nodeVector.x);
		argumentOfPeriapsis = Math.acos(nodeVector.dot(eccentricityVector) / (nodeVector.magnitude() * eccentricity));
		if (eccentricityVector.z < 0) argumentOfPeriapsis = Math.PI*2 - argumentOfPeriapsis;
		double cosTrueAnomaly = eccentricityVector.dot(position) / (eccentricity * position.magnitude());
		
		double cosEccentricAnomaly = (eccentricity + cosTrueAnomaly) / (1 + eccentricity * cosTrueAnomaly);
		double eccentricAnomaly = Math.acos(cosEccentricAnomaly);
		if (position.dot(velocity) < 0) eccentricAnomaly = Math.PI*2 - eccentricAnomaly;
		startMeanAnomaly = eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly);
	}
}
