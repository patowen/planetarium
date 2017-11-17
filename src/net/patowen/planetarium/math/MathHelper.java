package net.patowen.planetarium.math;

/**
 * {@code MathHelper} includes some trigonometric functions that were missing from
 * the {@code Math} class for certain operations.
 * @author Patrick Owen
 */
public class MathHelper {
	/**
	 * Returns the inverse hyperbolic sine of the argument
	 * @param x
	 * @return arsinh x
	 */
	public static double asinh(double x) {
		return Math.log(x + Math.sqrt(1+x*x));
	}
	
	/**
	 * Returns the inverse hyperbolic cosine of the argument
	 * @param x
	 * @return arcosh x
	 */
	public static double acosh(double x) {
		return Math.log(x + Math.sqrt(x*x-1));
	}
	
	/**
	 * Returns the inverse hyperbolic tangent of the argument
	 * @param x
	 * @return artanh x
	 */
	public static double atanh(double x) {
		return 0.5 * (Math.log1p(x) - Math.log1p(-x));
	}
	
	/**
	 * Returns the square of the argument
	 * @param x
	 * @return x^2
	 */
	public static double sqr(double x) {
		return x*x;
	}
	
	/**
	 * Returns a number congruent to x (mod n) in the range [lower, lower+n)
	 * @param x
	 * @param n
	 * @param lower
	 * @return x + a*n in the specified range for some a
	 */
	public static int modBound(int x, int n, int lower) {
		return Math.floorMod(x-lower, n)+lower;
	}
	
	public static double getEccentricAnomaly(double meanAnomaly, double eccentricity) {
		meanAnomaly -= Math.floor(meanAnomaly/(Math.PI*2))*Math.PI*2;
		double guess = meanAnomaly;
		if (eccentricity > 0.8) guess = Math.PI;
		while (true) {
			double correction = (guess - (meanAnomaly + eccentricity * Math.sin(guess))) /
					(1 - eccentricity*Math.cos(guess));
			guess -= correction;
			if (Math.abs(correction) < 1e-10) break;
		}
		return guess;
	}
	
	private static int[] monthTable = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private static int[] monthTableCumulative = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
	
	public static double getNumSeconds(double year, double month, double day, double hours, double minutes, double seconds) {
		int yearF = (int)Math.floor(year);
		int yearsPassed = yearF - 2000;
		int leapYearsPassed = Math.floorDiv(yearsPassed - 1, 4) - Math.floorDiv(yearsPassed - 1, 100) + Math.floorDiv(yearsPassed - 1, 400) + 1;
		
		double totalDays = yearsPassed * 365 + leapYearsPassed;
		boolean isLeapYear = (yearF % 4 == 0 && yearF % 100 != 0) || (yearF % 400 == 0);
		double currentYearDays = isLeapYear ? 366.0 : 365.0;
		
		int monthF = (int)Math.floor(month);
		totalDays += monthTableCumulative[monthF - 1];
		if (isLeapYear && monthF > 2) totalDays += 1;
		double currentMonthDays = monthTable[monthF - 1];
		if (isLeapYear && monthF == 2) currentMonthDays += 1;
		
		totalDays += day - 1;
		
		// Allow fractional months and years
		totalDays += currentMonthDays * (month - Math.floor(month)) + currentYearDays * (year - Math.floor(year));
		
		double totalSeconds = totalDays * 86400.0 + hours * 3600.0 + minutes * 60.0 + seconds - 43200.0;
		
		return totalSeconds;
	}
	
	public double getNumSeconds(double year, double month, double day) {
		return getNumSeconds(year, month, day, 0, 0, 0);
	}
}
