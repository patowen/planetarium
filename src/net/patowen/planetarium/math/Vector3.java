package net.patowen.planetarium.math;

import java.nio.FloatBuffer;

/**
 * The {@code Vector3} class represents a given vector in 3-dimensional Euclidean space.
 * @author Patrick Owen
 */
public class Vector3 extends VectorN {
	/**
	 * The x-coordinate of the vector.
	 */
	public double x;
	
	/**
	 * The y-coordinate of the vector.
	 */
	public double y;
	
	/**
	 * The z-coordinate of the vector.
	 */
	public double z;
	
	/**
	 * Constructs a {@code Vector3} object representing the zero vector.
	 */
	public Vector3() {
		x = 0; y = 0; z = 0;
	}
	
	/**
	 * Constructs a {@code Vector3} object that represents the same vector as the argument.
	 * @param v a {@code Vector3}
	 */
	public Vector3(Vector3 v) {
		this(v.x, v.y, v.z);
	}
	
	/**
	 * Changes the vector to the zero vector.
	 */
	public void reset() {
		x = 0; y = 0; z = 0;
	}
	
	/**
	 * Constructs a {@code Vector3} object with the specified coordinates.
	 * @param x the x-coordinate of the vector.
	 * @param y the y-coordinate of the vector.
	 * @param z the z-coordinate of the vector.
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3(double[] coords) {
		x = coords[0];
		y = coords[1];
		z = coords[2];
	}
	
	/**
	 * Changes the vector to the unit vector pointing in the same direction. This method
	 * does nothing to the zero vector.
	 */
	public boolean normalize() {
		double dist = x*x+y*y+z*z;
		if (dist > 1e-6) {
			double factor = 1.0/Math.sqrt(dist);
			x *= factor;
			y *= factor;
			z *= factor;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the magnitude of the vector.
	 * @return the magnitude, or Euclidean norm, of the vector
	 */
	public double magnitude() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	/**
	 * Returns the squared magnitude of the vector.
	 * @return the magnitude squared, or the dot product of the vector and itself
	 */
	public double squared() {
		return x*x+y*y+z*z;
	}
	
	/**
	 * Returns the result of adding the vector to the argument.
	 * @param v a vector
	 * @return the vector plus the argument
	 */
	public Vector3 plus(Vector3 v) {
		return new Vector3(x+v.x, y+v.y, z+v.z);
	}
	
	/**
	 * Returns the result of adding the vector to the argument times the given scalar.
	 * @param v a vector
	 * @param c a scalar factor
	 * @return the vector plus the argument times the given scalar
	 */
	public Vector3 plusMultiple(Vector3 v, double c) {
		return new Vector3(x+v.x*c, y+v.y*c, z+v.z*c);
	}
	
	/**
	 * Returns the result of subtracting the argument from the vector.
	 * @param v a vector
	 * @return the vector minus the argument
	 */
	public Vector3 minus(Vector3 v) {
		return new Vector3(x-v.x, y-v.y, z-v.z);
	}
	
	/**
	 * Returns the vector multiplied by the given scalar.
	 * @param c a scalar factor
	 * @return the vector times the scalar factor
	 */
	public Vector3 times(double c) {
		return new Vector3(c*x, c*y, c*z);
	}
	
	/**
	 * Returns the dot product of the vector and the argument.
	 * @param v a vector
	 * @return the dot product of the vector and the argument
	 */
	public double dot(Vector3 v) {
		return (x*v.x + y*v.y + z*v.z);
	}
	
	/**
	 * Returns the cross product of the vector and the argument.
	 * @param v a vector
	 * @return the cross product of the vector and the argument
	 */
	public Vector3 cross(Vector3 v) {
		return new Vector3(y*v.z-z*v.y, z*v.x-x*v.z, x*v.y-y*v.x);
	}
	
	/**
	 * Rotates the vector about the given unit vector by {@code theta}.
	 * @param v a unit vector
	 * @param theta the angle in radians
	 */
	public void rotate(Vector3 v, double theta) {
		double xx = v.x, yy = v.y, zz = v.z;
		double c = Math.cos(theta), s = Math.sin(theta);
		
		double xNew = x*(xx*xx+(1-xx*xx)*c) + y*(xx*yy-xx*yy*c-zz*s) + z*(xx*zz-xx*zz*c+yy*s);
		double yNew = x*(xx*yy-xx*yy*c+zz*s) + y*(yy*yy+(1-yy*yy)*c) + z*(yy*zz-yy*zz*c-xx*s);
		double zNew = x*(xx*zz-xx*zz*c-yy*s) + y*(yy*zz-yy*zz*c+xx*s) + z*(zz*zz+(1-zz*zz)*c);
		
		x = xNew; y = yNew; z = zNew;
	}
	
	/**
	 * Returns a string representation of the vector
	 */
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public void addToFloatBuffer(FloatBuffer floatBuffer) {
		floatBuffer.put((float)x);
		floatBuffer.put((float)y);
		floatBuffer.put((float)z);
	}
}
