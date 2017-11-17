package net.patowen.planetarium.math;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Vsop {
	private static final double[] lambdaCoeffConstant = {
		0.4402608631669000e1,
		0.3176134461576000e1,
		0.1753470369433000e1,
		0.6203500014141000e1,
		0.4091360003050000e1,
		0.1713740719173000e1,
		0.5598641292287000e1,
		0.2805136360408000e1,
		0.2326989734620000e1,
		0.5995461070350000e0,
		0.8740185101070000e0,
		0.5481225395663000e1,
		0.5311897933164000e1,
		0.e0,
		5.19846640063e0,
		1.62790513602e0,
		2.35555563875e0
	};
	
	private static final double[] lambdaCoeffLinear = {
		0.2608790314068555e5,
		0.1021328554743445e5,
		0.6283075850353215e4,
		0.3340612434145457e4,
		0.1731170452721855e4,
		0.1704450855027201e4,
		0.1428948917844273e4,
		0.1364756513629990e4,
		0.1361923207632842e4,
		0.5296909615623250e3,
		0.2132990861084880e3,
		0.7478165903077800e2,
		0.3813297222612500e2,
		0.3595362285049309e0,
		77713.7714481804e0,
		84334.6615717837e0,
		83286.9142477147e0
	};
	
	private PlanetarySolution solution;
	
	public static class PlanetarySolution {
		private PlanetarySeries[] variables;
		
		public PlanetarySolution(Scanner scan, double precision) {
			variables = new PlanetarySeries[6];
			for (int i=0; i<6; i++) {
				variables[i] = new PlanetarySeries();
			}
			
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				int k = 12;
				int varIndex = Integer.parseInt(line.substring(k, k+3).trim()) - 1;
				k += 3;
				int exponent = Integer.parseInt(line.substring(k, k+3).trim());
				k += 3;
				int numTerms = Integer.parseInt(line.substring(k, k+7).trim());
				k += 7;
				
				variables[varIndex].addCoefficient(scan, exponent, numTerms, precision);
			}
		}
		
		public double[] calculate(double t) {
			double[] lambda1 = new double[17];
			for (int i=0; i<17; i++) {
				lambda1[i] = lambdaCoeffConstant[i] + lambdaCoeffLinear[i] * t;
			}
			
			double[] result = new double[6];
			for (int i=0; i<6; i++) {
				result[i] = variables[i].calculate(t, lambda1);
			}
			
			return result;
		}
	}
	
	public static class PlanetarySeries {
		private ArrayList<Series> coefficients;
		
		public PlanetarySeries() {
			coefficients = new ArrayList<>();
		}
		
		public void addCoefficient(Scanner scan, int exponent, int numTerms, double precision) {
			coefficients.add(new Series(scan, exponent, numTerms, precision));
		}
		
		public double calculate(double t, double[] lambda1) {
			double result = 0;
			for (Series coefficient : coefficients) {
				result += coefficient.calculate(t, lambda1);
			}
			return result;
		}
	}
	
	public static class Series {
		private ArrayList<Term> terms;
		private int exponent;
		
		public Series(Scanner scan, int exponent, int numTerms, double precision) {
			this.exponent = exponent;
			terms = new ArrayList<Term>(numTerms);
			for (int i=0; i<numTerms; i++) {
				Term nextTerm = new Term(scan.nextLine());
				if (nextTerm.isSignificant(precision)) {
					terms.add(nextTerm);
				}
			}
		}
		
		public double calculate(double t, double[] lambda1) {
			double result = 0;
			for (Term term : terms) {
				result += term.calculate(lambda1);
			}
			result *= Math.pow(t, exponent);
			return result;
		}
	}
	
	public static class Term {
		private int[] a;
		private double s, c;
		
		public Term(String line) {
			a = new int[17];
			
			int k = 6;
			for (int i=0; i<4; i++) {
				a[i] = Integer.parseInt(line.substring(k, k+3).trim());
				k += 3;
			}
			k += 1;
			for (int i=4; i<9; i++) {
				a[i] = Integer.parseInt(line.substring(k, k+3).trim());
				k += 3;
			}
			k += 1;
			for (int i=9; i<13; i++) {
				a[i] = Integer.parseInt(line.substring(k, k+4).trim());
				k += 4;
			}
			k += 1;
			a[13] = Integer.parseInt(line.substring(k, k+6).trim());
			k += 6;
			k += 1;
			for (int i=14; i<17; i++) {
				a[i] = Integer.parseInt(line.substring(k, k+3).trim());
				k += 3;
			}
			
			s = Double.parseDouble(line.substring(k, k+20).trim());
			k += 20;
			k += 1;
			s *= Math.pow(10, Integer.parseInt(line.substring(k, k+3).trim()));
			k += 3;
			
			c = Double.parseDouble(line.substring(k, k+20).trim());
			k += 20;
			k += 1;
			c *= Math.pow(10, Integer.parseInt(line.substring(k, k+3).trim()));
			k += 3;
		}
		
		public double getPhi(double[] lambda1) {
			double result = 0;
			for (int i=0; i<17; i++) {
				result += a[i] * lambda1[i];
			}
			return result;
		}
		
		public double calculate(double[] lambda1) {
			double phi = getPhi(lambda1);
			return s * Math.sin(phi) + c * Math.cos(phi);
		}
		
		public boolean isSignificant(double precision) {
			return s*s + c*c > precision;
		}
	}
	
	public Vsop() {
		ClassLoader cl = Vsop.class.getClassLoader();
		InputStream stream = cl.getResourceAsStream("net/patowen/planetarium/data/vsop/VSOP2013p" + 3 + ".dat");
		Scanner scan = new Scanner(stream);
		
		solution = new PlanetarySolution(scan, 1e-16);
	}
	
	public void perturbOrbit(Orbit orbit, double t) {
		double[] params = solution.calculate(t / 365250.0 / 86400.0);
		orbit.setSemimajorAxis(params[0] * 0.149597870691e9);
		double ascendLong = Math.atan2(params[5], params[4]);
		orbit.setAscendingNodeLongitude(ascendLong);
		
		double sqrMag = params[4]*params[4] + params[5]*params[5];
		orbit.setInclination(Math.acos(1 - sqrMag*2));
		
		orbit.setEccentricity(Math.sqrt(params[2]*params[2] + params[3]*params[3]));
		double periLong = Math.atan2(params[3], params[2]);
		orbit.setArgumentOfPeriapsis(periLong - ascendLong);
		
		orbit.setStartMeanAnomaly(params[1] - periLong);
		orbit.setStartTime(t);
	}
}
