package net.patowen.planetarium.math;

public class Transform {
	public Vector3 x;
	public Vector3 y;
	public Vector3 z;
	public Vector3 w;
	
	public Transform(Vector3 x, Vector3 y, Vector3 z, Vector3 w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Transform(Transform t) {
		this(new Vector3(t.x), new Vector3(t.y), new Vector3(t.z), new Vector3(t.w));
	}
	
	public static Transform identity() {
		return new Transform(
			new Vector3(1, 0, 0),
			new Vector3(0, 1, 0),
			new Vector3(0, 0, 1),
			new Vector3(0, 0, 0));
	}
	
	public static Transform translation(Vector3 v) {
		return new Transform(
			new Vector3(1, 0, 0),
			new Vector3(0, 1, 0),
			new Vector3(0, 0, 1),
			new Vector3(v.x, v.y, v.z));
	}
	
	public static Transform rotation(Vector3 v, double theta) {
		double xx = v.x, yy = v.y, zz = v.z;
		double c = Math.cos(theta), s = Math.sin(theta);
		
		return new Transform(
			new Vector3(xx*xx+(1-xx*xx)*c, xx*yy-xx*yy*c+zz*s, xx*zz-xx*zz*c-yy*s),
			new Vector3(xx*yy-xx*yy*c-zz*s, yy*yy+(1-yy*yy)*c, yy*zz-yy*zz*c+xx*s),
			new Vector3(xx*zz-xx*zz*c+yy*s, yy*zz-yy*zz*c-xx*s, zz*zz+(1-zz*zz)*c),
			new Vector3(0, 0, 0));
	}
	
	public Transform inverse() {
		return new Transform(
			new Vector3(x.x, y.x, z.x),
			new Vector3(x.y, y.y, z.y),
			new Vector3(x.z, y.z, z.z),
			new Vector3(-x.x*w.x - x.y*w.y - x.z*w.z,
					-y.x*w.x - y.y*w.y - y.z*w.z,
					-z.x*w.x - z.y*w.y - z.z*w.z));
	}
	
	public boolean normalize() {
		boolean success = true;
		
		// Valid vectors are assumed. If this is not the case, NaN's will propagate, and
		// very bad stuff will happen, such as hidden, hard-to-debug errors.
		success = x.normalize() && success; // Normalize x
		y = y.plusMultiple(x, -y.dot(x)); // Separate y from x
		z = z.plusMultiple(x, -z.dot(x)); // Separate z from x
		
		success = y.normalize() && success; // Normalize y
		z = z.plusMultiple(y, -z.dot(y)); // Separate z from y
		
		success = z.normalize() && success; // Normalize z
		
		return success;
	}
	
	public Vector3 transform(Vector3 v) {
		return new Vector3(
			x.x*v.x + y.x*v.y + z.x*v.z + w.x,
			x.y*v.x + y.y*v.y + z.y*v.z + w.y,
			x.z*v.x + y.z*v.y + z.z*v.z + w.z);
	}
	
	public Vector3 transformVector(Vector3 v) {
		return new Vector3(
			x.x*v.x + y.x*v.y + z.x*v.z,
			x.y*v.x + y.y*v.y + z.y*v.z,
			x.z*v.x + y.z*v.y + z.z*v.z);
	}
	
	public Transform transform(Transform t) {
		return new Transform(
			transformVector(t.x),
			transformVector(t.y),
			transformVector(t.z),
			transform(t.w));
	}
	
	public Transform transformedBy(Transform t) {
		return new Transform(
			t.transformVector(x),
			t.transformVector(y),
			t.transformVector(z),
			t.transform(w));
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("x: ").append(x.toString()).append("\n");
		str.append("y: ").append(y.toString()).append("\n");
		str.append("z: ").append(z.toString()).append("\n");
		str.append("w: ").append(w.toString()).append("\n");
		return str.toString();
	}
}
