package net.patowen.planetarium.math;

import java.nio.FloatBuffer;

/**
 * The {@code Vector3} class represents a given vector in 3-dimensional Euclidean space.
 * @author Patrick Owen
 */
public class Vector2 extends VectorN {
	/**
	 * The x-coordinate of the vector.
	 */
	public double x;
	
	/**
	 * The y-coordinate of the vector.
	 */
	public double y;
	
	/**
	 * Constructs a {@code Vector2} object representing the zero vector.
	 */
	public Vector2() {
		x = 0; y = 0;
	}
	
	/**
	 * Constructs a {@code Vector2} object that represents the same vector as the argument.
	 * @param v a {@code Vector2}
	 */
	public Vector2(Vector2 v) {
		this(v.x, v.y);
	}
	
	/**
	 * Changes the vector to the zero vector.
	 */
	public void reset() {
		x = 0; y = 0;
	}
	
	/**
	 * Constructs a {@code Vector2} object with the specified coordinates.
	 * @param x the x-coordinate of the vector.
	 * @param y the y-coordinate of the vector.
	 */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2(double[] coords) {
		x = coords[0];
		y = coords[1];
	}
	
	/**
	 * Changes the vector to the unit vector pointing in the same direction. This method
	 * does nothing to the zero vector.
	 */
	public void normalize() {
		double dist = x*x+y*y;
		if (dist > 0) {
			double size = Math.sqrt(dist);
			x /= size;
			y /= size;
		}
	}
	
	/**
	 * Returns the magnitude of the vector.
	 * @return the magnitude, or Euclidean norm, of the vector
	 */
	public double magnitude() {
		return Math.sqrt(x*x+y*y);
	}
	
	/**
	 * Returns the squared magnitude of the vector.
	 * @return the magnitude squared, or the dot product of the vector and itself
	 */
	public double squared() {
		return x*x+y*y;
	}
	
	/**
	 * Returns the result of adding the vector to the argument.
	 * @param v a vector
	 * @return the vector plus the argument
	 */
	public Vector2 plus(Vector2 v) {
		return new Vector2(x+v.x, y+v.y);
	}
	
	/**
	 * Returns the result of adding the vector to the argument times the given scalar.
	 * @param v a vector
	 * @param c a scalar factor
	 * @return the vector plus the argument times the given scalar
	 */
	public Vector2 plusMultiple(Vector2 v, double c) {
		return new Vector2(x+v.x*c, y+v.y*c);
	}
	
	/**
	 * Returns the result of subtracting the argument from the vector.
	 * @param v a vector
	 * @return the vector minus the argument
	 */
	public Vector2 minus(Vector2 v) {
		return new Vector2(x-v.x, y-v.y);
	}
	
	/**
	 * Returns the vector multiplied by the given scalar.
	 * @param c a scalar factor
	 * @return the vector times the scalar factor
	 */
	public Vector2 times(double c) {
		return new Vector2(c*x, c*y);
	}
	
	/**
	 * Returns the dot product of the vector and the argument.
	 * @param v a vector
	 * @return the dot product of the vector and the argument
	 */
	public double dot(Vector2 v) {
		return (x*v.x + y*v.y);
	}
	
	/**
	 * Returns a perpendicular vector to the current vector.
	 * @return the cross product of the vector and <0,0,1>
	 */
	public Vector2 cross() {
		return new Vector2(y, -x);
	}
	
	/**
	 * Rotates the vector about the origin by {@code theta}.
	 * @param theta the angle in radians
	 */
	public void rotate(double theta) {
		double c = Math.cos(theta), s = Math.sin(theta);
		
		double xNew = x*c - y*s;
		double yNew = x*s + y*c;
		
		x = xNew; y = yNew;
	}
	
	/**
	 * Returns a string representation of the vector
	 */
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public void addToFloatBuffer(FloatBuffer floatBuffer) {
		floatBuffer.put((float)x);
		floatBuffer.put((float)y);
	}
}
