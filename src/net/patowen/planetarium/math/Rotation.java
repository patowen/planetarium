package net.patowen.planetarium.math;

public abstract class Rotation {
	private Transform baseTransform;
	protected double modifiedRightAscension;
	protected double codeclination;
	protected double primeMeridian;
	
	public Rotation(Transform baseTransform) {
		this.baseTransform = baseTransform;
	}
	
	public Transform getTransform() {
		return baseTransform
				.transform(Transform.rotation(new Vector3(0, 0, 1), modifiedRightAscension))
				.transform(Transform.rotation(new Vector3(1, 0, 0), codeclination))
				.transform(Transform.rotation(new Vector3(0, 0, 1), primeMeridian));
	}
	
	public abstract void setParametersFromTime(double time);
}
