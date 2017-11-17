package net.patowen.planetarium.math;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class LunarTheory {
	private Table[] tables;
	
	// Constants
	private double semimajorAxisCorrected = 384747.9806448954; // a0
	private double semimajorAxis = 384747.9806743165; //ath
	
	double secondsInRadian = 648000.0/Math.PI; //rad
	
	double radiansInDegree = Math.PI/180.0; //deg
	double minutesInDegree = 60.0; //c1
	double secondsInDegree = 3600.0; //c2
	
	double julian2000 = 2451545.0; //tj2000
	double daysInCentury = 36525.0; //sc
	
	double[] laskarP = {0.10180391e-4, 0.47020439e-6, -0.5417367e-9, -0.2507948e-11, 0.463486e-14};
	double[] laskarQ = {-0.113469002e-3, 0.12372674e-6, 0.1265417e-8, -0.1371808e-11, -0.320334e-14};
	
	double[][] eclipticAngles = new double[3][5];
	
	// Calculation results
	private ArrayList<LunarSeries> lunarSeries;
	
	public LunarTheory() {
		tables = new Table[36];
	}
	
	public class Table {
		int numInts, numDoubles;
		ArrayList<int[]> ints;
		ArrayList<double[]> doubles;
		
		int row;
		
		public Table(int numInts, int numDoubles) {
			this.numInts = numInts;
			this.numDoubles = numDoubles;
			ints = new ArrayList<>();
			doubles = new ArrayList<>();
		}
		
		public int numRows() {
			return ints.size();
		}
		
		public void setRow(int row) {
			this.row = row;
		}
		
		public int i(int column) {
			return ints.get(row)[column];
		}
		
		public double d(int column) {
			return doubles.get(row)[column];
		}
		
		public void read(int fileIndex) {
			ClassLoader cl = LunarTheory.class.getClassLoader();
			InputStream stream = cl.getResourceAsStream("net/patowen/planetarium/data/elp/ELP" + fileIndex);
			Scanner scan = new Scanner(stream);
			scan.nextLine();
			
			while (scan.hasNextLine()) {
				String str = scan.nextLine();
				str = str.replaceAll("-", " -");
				StringTokenizer toks = new StringTokenizer(str);
				int[] nextInts = new int[numInts];
				double[] nextDoubles = new double[numDoubles];
				
				for (int i=0; i<numInts; i++) {
					nextInts[i] = Integer.parseInt(toks.nextToken());
				}
				
				for (int i=0; i<numDoubles; i++) {
					nextDoubles[i] = Double.parseDouble(toks.nextToken());
				}
				
				ints.add(nextInts);
				doubles.add(nextDoubles);
			}
			
			scan.close();
		}
	}
	
	public class MainTerm {
		private double coefficient;
		private double[] sinArgument;
		
		public MainTerm(double coefficient, double[] sinArgument) {
			this.coefficient = coefficient;
			this.sinArgument = new double[5];
			for (int i=0; i<5; i++) {
				this.sinArgument[i] = sinArgument[i];
			}
		}
		
		public double compute(double[] t) {
			double y = 0;
			for (int k=0; k<=4; k++) {
				y += sinArgument[k]*t[k];
			}
			return coefficient * Math.sin(y);
		}
		
		public double derivative(double[] t) {
			double y = 0, yDeriv = 0;
			for (int k=0; k<=4; k++) {
				y += sinArgument[k]*t[k];
			}
			for (int k=1; k<=4; k++) {
				yDeriv += k*sinArgument[k]*t[k-1];
			}
			return coefficient * Math.cos(y) * yDeriv;
		}
	}
	
	public class PerturbationTerm {
		private double coefficient;
		private double[] sinArgument;
		
		public PerturbationTerm(double coefficient, double[] sinArgument) {
			this.coefficient = coefficient;
			this.sinArgument = new double[2];
			for (int i=0; i<2; i++) {
				this.sinArgument[i] = sinArgument[i];
			}
		}
		
		public double compute(double[] t) {
			double y = sinArgument[0] * t[0] + sinArgument[1] * t[1];
			return coefficient * Math.sin(y);
		}
		
		public double derivative(double[] t) {
			double y = sinArgument[0] + sinArgument[1] * t[1];
			double yDeriv = sinArgument[1];
			return coefficient * Math.cos(y) * yDeriv;
		}
	}
	
	public class MainSeries {
		private ArrayList<MainTerm> terms;
		
		public MainSeries() {
			terms = new ArrayList<>();
		}
		
		public void addTerm(double coefficient, double[] sinArgument) {
			terms.add(new MainTerm(coefficient, sinArgument));
		}
		
		public double compute(double[] t) {
			double result = 0;
			for (MainTerm term : terms) {
				result += term.compute(t);
			}
			return result;
		}
		
		public double derivative(double[] t) {
			double result = 0;
			for (MainTerm term : terms) {
				result += term.derivative(t);
			}
			return result;
		}
	}
	
	public class PerturbationSeries {
		private ArrayList<PerturbationTerm> terms;
		private int factorExponent;
		
		public PerturbationSeries(int factorExponent) {
			terms = new ArrayList<>();
			this.factorExponent = factorExponent;
		}
		
		public void addTerm(double coefficient, double[] sinArgument) {
			terms.add(new PerturbationTerm(coefficient, sinArgument));
		}
		
		public double compute(double[] t) {
			double result = 0;
			for (PerturbationTerm term : terms) {
				result += term.compute(t);
			}
			return result * t[factorExponent];
		}
		
		public double derivative(double[] t) {
			double result = 0;
			double sumDeriv = 0;
			for (PerturbationTerm term : terms) {
				sumDeriv += term.derivative(t);
			}
			
			result += sumDeriv * t[factorExponent];
			
			if (factorExponent > 0) {
				double sum = 0;
				for (PerturbationTerm term : terms) {
					sum += term.compute(t);
				}
				result += sum * t[factorExponent-1] * factorExponent;
			}
			return result;
		}
	}
	
	public class LunarSeries {
		private MainSeries mainSeries;
		private ArrayList<PerturbationSeries> perturbationSeries;
		
		public LunarSeries() {
			perturbationSeries = new ArrayList<>(11);
		}
		
		public void setMainSeries(MainSeries mainSeries) {
			this.mainSeries = mainSeries;
		}
		
		public void addPerturbationSeries(PerturbationSeries perturbationSeries) {
			this.perturbationSeries.add(perturbationSeries);
		}
		
		public double compute(double[] t) {
			double result = 0;
			result += mainSeries.compute(t);
			for (PerturbationSeries series : perturbationSeries) {
				result += series.compute(t);
			}
			return result;
		}
		
		public double derivative(double[] t) {
			double result = 0;
			result += mainSeries.derivative(t);
			for (PerturbationSeries series : perturbationSeries) {
				result += series.derivative(t);
			}
			return result;
		}
	}
	
	public void loadData() {
		// 1:3
		for (int i=0; i<3; i++) {
			tables[i] = new Table(4, 7);
		}
		
		// 4:9, 22:36
		for (int i=3; i<9; i++) {
			tables[i] = new Table(5, 3);
		}
		
		for (int i=21; i<36; i++) {
			tables[i] = new Table(5, 3);
		}
		
		// 10:21
		for (int i=9; i<21; i++) {
			tables[i] = new Table(11, 3);
		}
		
		//Actually reading
		for (int i=0; i<36; i++) {
			tables[i].read(i+1);
		}
	}
	
	public double degreesToRadians(double degrees, double minutes, double seconds) {
		return (degrees + minutes/minutesInDegree + seconds/secondsInDegree) * radiansInDegree;
	}
	
	public void generateLunarSeries(double prec) {
		// dimension commands
		double[] eart = new double[5];
		double[] peri = new double[5];
		double[][] p = new double[8][2];
		
		double[][] delaunay = new double[4][5];
		double[] zeta = new double[2];
		
		double[] pre = new double[3];
		double[] coef = new double[7];
		double[] ilu = new double[4];
		double[] ipla = new double[11];
		
		double[] mainSinArg = new double[5];
		double[] perturbationSinArg = new double[2];
		
		// Parameters
		
		double am = 0.074801329518;
		double alpha = 0.002571881335;
		double dtasm = 2.0*alpha/(3.0*am);
		
		eclipticAngles[0][0] = degreesToRadians(218, 18, 59.95571); // Moon longitude
		eclipticAngles[1][0] = (83+21/minutesInDegree+11.67475/secondsInDegree)*radiansInDegree; // Moon perigee
		eclipticAngles[2][0] = (125+2/minutesInDegree+40.39816/secondsInDegree)*radiansInDegree; // Moon ascending node
		eart[0] = (100+27/minutesInDegree+59.22059/secondsInDegree)*radiansInDegree; // Earth longitude
		peri[0] = (102+56/minutesInDegree+14.42753/secondsInDegree)*radiansInDegree; // Earth perigee
		eclipticAngles[0][1] = 1732559343.73604/secondsInRadian;
		eclipticAngles[1][1] = 14643420.2632/secondsInRadian;
		eclipticAngles[2][1] = -6967919.3622/secondsInRadian;
		eart[1] = 129597742.2758/secondsInRadian;
		peri[1] = 1161.2283/secondsInRadian;
		eclipticAngles[0][2] = -5.8883/secondsInRadian;
		eclipticAngles[1][2] = -38.2776/secondsInRadian;
		eclipticAngles[2][2] = 6.3622/secondsInRadian;
		eart[2] = -0.0202/secondsInRadian;
		peri[2] = 0.5327/secondsInRadian;
		eclipticAngles[0][3] = 0.6604e-2/secondsInRadian;
		eclipticAngles[1][3] = -0.45047e-1/secondsInRadian;
		eclipticAngles[2][3] = 0.7625e-2/secondsInRadian;
		eart[3] = 0.9e-5/secondsInRadian;
		peri[3] = -0.138e-3/secondsInRadian;
		eclipticAngles[0][4] = -0.3169e-4/secondsInRadian;
		eclipticAngles[1][4] = 0.21301e-3/secondsInRadian;
		eclipticAngles[2][4] = -0.3586e-4/secondsInRadian;
		eart[4] = 0.15e-6/secondsInRadian;
		peri[4] = 0.0;
		
		double precess = 5029.0966/secondsInRadian;
		
		p[0][0] = (252+15/minutesInDegree+3.25986/secondsInDegree)*radiansInDegree;
		p[1][0] = (181+58/minutesInDegree+47.28305/secondsInDegree)*radiansInDegree;
		p[2][0] = eart[0];
		p[3][0] = (355+25/minutesInDegree+59.78866/secondsInDegree)*radiansInDegree;
		p[4][0] = (34+21/minutesInDegree+5.34212/secondsInDegree)*radiansInDegree;
		p[5][0] = (50+4/minutesInDegree+38.89694/secondsInDegree)*radiansInDegree;
		p[6][0] = (314+3/minutesInDegree+18.01841/secondsInDegree)*radiansInDegree;
		p[7][0] = (304+20/minutesInDegree+55.19575/secondsInDegree)*radiansInDegree;
		p[0][1] = 538101628.68898/secondsInRadian;
		p[1][1] = 210664136.43355/secondsInRadian;
		p[2][1] = eart[1];
		p[3][1] = 68905077.59284/secondsInRadian;
		p[4][1] = 10925660.42861/secondsInRadian;
		p[5][1] = 4399609.65932/secondsInRadian;
		p[6][1] = 1542481.19393/secondsInRadian;
		p[7][1] = 786550.32074/secondsInRadian;
		
		double delnu = 0.55604 / secondsInRadian / eclipticAngles[0][1];
		double dele = 0.01789 / secondsInRadian;
		double delg = -0.08066 / secondsInRadian;
		double delnp = -0.06424 / secondsInRadian / eclipticAngles[0][1];
		double delep = -0.12879 / secondsInRadian;
		
		for (int i=0; i<=4; i++) {
			delaunay[0][i] = eclipticAngles[0][i] - eart[i];
			delaunay[3][i] = eclipticAngles[0][i] - eclipticAngles[2][i];
			delaunay[2][i] = eclipticAngles[0][i] - eclipticAngles[1][i];
			delaunay[1][i] = eart[i] - peri[i];
		}
		
		delaunay[0][0] = delaunay[0][0] + Math.PI;
		zeta[0] = eclipticAngles[0][0];
		zeta[1] = eclipticAngles[0][1] + precess;
		
		// Reading files
		
		lunarSeries = new ArrayList<LunarSeries>();
		for (int i=0; i<3; i++) {
			lunarSeries.add(new LunarSeries());
		}
		
		pre[0] = prec*secondsInRadian - 1e-12;
		pre[1] = prec*secondsInRadian - 1e-12;
		pre[2] = prec*semimajorAxis;
		
		for (int ific = 0; ific < 36; ific++) {
			int itab = ific / 3;
			int iv = ific % 3;
			
			Table tab = tables[ific];
			
			if (itab == 0) {
				MainSeries series = new MainSeries();
				for (int row=0; row<tab.numRows(); row++) {
					tab.setRow(row);
					for (int i=0; i<4; i++) {
						ilu[i] = tab.i(i);
					}
					for (int i=0; i<7; i++) {
						coef[i] = tab.d(i);
					}
					
					double coefficient = coef[0];
					if (Math.abs(coefficient) < pre[iv]) continue;
					
					// Additive corrections (See page 10)
					double tgv = coef[1] + dtasm * coef[5];
					coefficient = coef[0]+tgv*(delnp-am*delnu)+(coef[2]*delg+coef[3]*dele+coef[4]*delep);
					if (ific == 2) coefficient -= 2.0*coef[0]*delnu/3.0;
					
					for (int k=0; k<=4; k++) {
						double y = 0.0;
						for (int i=0; i<4; i++) {
							y += ilu[i] * delaunay[i][k];
						}
						mainSinArg[k] = y;
					}
					// Cosine for ELP3
					if (iv == 2) mainSinArg[0] += Math.PI / 2;
					series.addTerm(coefficient, mainSinArg);
				}
				lunarSeries.get(iv).setMainSeries(series);
			}
			else if ((itab >= 1 && itab < 3) || (itab >= 7 && itab < 12)) {
				int exponent = 0;
				if (itab == 2 || itab == 8) exponent = 1;
				if (itab == 11) exponent = 2;
				PerturbationSeries series = new PerturbationSeries(exponent);
				for (int row=0; row<tab.numRows(); row++) {
					tab.setRow(row);
					int iz = tab.i(0);
					for (int i=0; i<4; i++) {
						ilu[i] = tab.i(i+1);
					}
					double pha = tab.d(0);
					double coefficient = tab.d(1);
					
					if (coefficient < pre[iv]) continue;
					
					for (int k=0; k<=1; k++) {
						double y;
						if (k == 0) y = pha*radiansInDegree;
						else y = 0;
						y += iz*zeta[k];
						for (int i=0; i<4; i++) {
							y += ilu[i] * delaunay[i][k];
						}
						perturbationSinArg[k] = y;
					}
					series.addTerm(coefficient, perturbationSinArg);
				}
				lunarSeries.get(iv).addPerturbationSeries(series);
			}
			else if (itab >= 3 && itab < 7) {
				int exponent = 0;
				if (itab == 4 || itab == 6) exponent = 1;
				PerturbationSeries series = new PerturbationSeries(exponent);
				for (int row=0; row<tab.numRows(); row++) {
					tab.setRow(row);
					for (int i=0; i<11; i++) {
						ipla[i] = tab.i(i);
					}
					double pha = tab.d(0);
					double coefficient = tab.d(1);
					
					if (coefficient < pre[iv]) continue;
					
					if (ific < 15) {
						for (int k=0; k<=1; k++) {
							double y;
							if (k == 0) y = pha*radiansInDegree;
							else y = 0;
							y += ipla[8]*delaunay[0][k]+ipla[9]*delaunay[2][k]+ipla[10]*delaunay[3][k];
							for (int i=0; i<8; i++) {
								y += ipla[i]*p[i][k];
							}
							perturbationSinArg[k] = y;
						}
					} else {
						for (int k=0; k<=1; k++) {
							double y;
							if (k == 0) y = pha*radiansInDegree;
							else y = 0;
							for (int i=0; i<4; i++) {
								y += ipla[i+7]*delaunay[i][k];
							}
							for (int i=0; i<7; i++) {
								y += ipla[i]*p[i][k];
							}
							perturbationSinArg[k] = y;
						}
					}
					series.addTerm(coefficient, perturbationSinArg);
				}
				lunarSeries.get(iv).addPerturbationSeries(series);
			}
		}
	}
	
	public Vector3 getCoordinates(double[] seriesResult, double[] t) {
		double longitude = seriesResult[0] / secondsInRadian + eclipticAngles[0][0] + eclipticAngles[0][1]*t[1] + eclipticAngles[0][2]*t[2] + eclipticAngles[0][3]*t[3] + eclipticAngles[0][4]*t[4];
		double latitude = seriesResult[1] / secondsInRadian;
		double distance = seriesResult[2] * semimajorAxisCorrected / semimajorAxis;
		
		double xPos = distance * Math.cos(longitude) * Math.cos(latitude);
		double yPos = distance * Math.sin(longitude) * Math.cos(latitude);
		double zPos = distance * Math.sin(latitude);
		
		double pw = (laskarP[0] + laskarP[1]*t[1] + laskarP[2]*t[2] + laskarP[3]*t[3] + laskarP[4]*t[4])*t[1];
		double qw = (laskarQ[0] + laskarQ[1]*t[1] + laskarQ[2]*t[2] + laskarQ[3]*t[3] + laskarQ[4]*t[4])*t[1];
		double ra = 2.0*Math.sqrt(1.0-pw*pw-qw*qw);
		double pwqw = 2.0*pw*qw;
		double pw2 = 1.0 - 2*pw*pw;
		double qw2 = 1.0 - 2*qw*qw;
		pw = pw*ra;
		qw = qw*ra;
		
		return new Vector3(pw2*xPos + pwqw*yPos + pw*zPos,
				pwqw*xPos + qw2*yPos - qw*zPos,
				-pw*xPos + qw*yPos + (pw2+qw2-1.0)*zPos);
	}
	
	public Vector3 getCoordinatesDeriv(double[] seriesResult, double[] seriesResultDeriv, double[] t) {
		double longitude = seriesResult[0] / secondsInRadian + eclipticAngles[0][0] + eclipticAngles[0][1]*t[1] + eclipticAngles[0][2]*t[2] + eclipticAngles[0][3]*t[3] + eclipticAngles[0][4]*t[4];
		double latitude = seriesResult[1] / secondsInRadian;
		double distance = seriesResult[2] * semimajorAxisCorrected / semimajorAxis;
		
		double longitudeDeriv = seriesResultDeriv[0] / secondsInRadian + eclipticAngles[0][1] + eclipticAngles[0][2]*t[1]*2 + eclipticAngles[0][3]*t[2]*3 + eclipticAngles[0][4]*t[3]*4;
		double latitudeDeriv = seriesResultDeriv[1] / secondsInRadian;
		double distanceDeriv = seriesResultDeriv[2] * semimajorAxisCorrected / semimajorAxis;
		
		double xPos = distance * Math.cos(longitude) * Math.cos(latitude);
		double yPos = distance * Math.sin(longitude) * Math.cos(latitude);
		double zPos = distance * Math.sin(latitude);
		
		double xPosDeriv = distanceDeriv * Math.cos(longitude) * Math.cos(latitude)
				- distance * Math.sin(longitude) * longitudeDeriv * Math.cos(latitude)
				- distance * Math.cos(longitude) * Math.sin(latitude) * latitudeDeriv;
		
		double yPosDeriv = distanceDeriv * Math.sin(longitude) * Math.cos(latitude)
				+ distance * Math.cos(longitude) * longitudeDeriv * Math.cos(latitude)
				- distance * Math.sin(longitude) * Math.sin(latitude) * latitudeDeriv;
		
		double zPosDeriv = distanceDeriv * Math.sin(latitude)
				+ distance * Math.cos(latitude) * latitudeDeriv;
		
		double pw = (laskarP[0] + laskarP[1]*t[1] + laskarP[2]*t[2] + laskarP[3]*t[3] + laskarP[4]*t[4])*t[1];
		double qw = (laskarQ[0] + laskarQ[1]*t[1] + laskarQ[2]*t[2] + laskarQ[3]*t[3] + laskarQ[4]*t[4])*t[1];
		double pwDeriv = laskarP[0] + laskarP[1]*t[1]*2 + laskarP[2]*t[2]*3 + laskarP[3]*t[3]*4 + laskarP[4]*t[4]*5;
		double qwDeriv = laskarQ[0] + laskarQ[1]*t[1]*2 + laskarQ[2]*t[2]*3 + laskarQ[3]*t[3]*4 + laskarQ[4]*t[4]*5;
		double ra = 2.0*Math.sqrt(1.0-pw*pw-qw*qw);
		double raDeriv = -2.0 * (pw*pwDeriv + qw*qwDeriv) / Math.sqrt(1.0 - pw*pw - qw*qw);
		double pwqw = 2.0*pw*qw;
		double pwqwDeriv = 2.0 * (pw*qwDeriv + pwDeriv*qw);
		double pw2 = 1.0 - 2*pw*pw;
		double qw2 = 1.0 - 2*qw*qw;
		double pw2Deriv = -4.0 * pw * pwDeriv;
		double qw2Deriv = -4.0 * qw * qwDeriv;
		double pwra = pw*ra;
		double qwra = qw*ra;
		double pwraDeriv = pw*raDeriv + pwDeriv*ra;
		double qwraDeriv = qw*raDeriv + qwDeriv*ra;
		
		return new Vector3(pw2*xPosDeriv + pw2Deriv*xPos + pwqw*yPosDeriv + pwqwDeriv*yPos + pwra*zPosDeriv + pwraDeriv*zPos,
				pwqw*xPosDeriv + pwqwDeriv*xPos + qw2*yPosDeriv + qw2Deriv*yPos - qwra*zPosDeriv - qwraDeriv*zPos,
				-pwra*xPosDeriv - pwraDeriv*xPos + qwra*yPosDeriv + qwraDeriv*yPos + (pw2+qw2-1.0)*zPosDeriv + (pw2Deriv+qw2Deriv)*zPos);
	}
	
	public Vector3 computeSeriesAtTime(double tjj) {
		double[] result = new double[3];
		
		double[] t = new double[5];
		t[0] = 1;
		t[1] = (tjj - julian2000) / daysInCentury;
		t[2] = t[1] * t[1];
		t[3] = t[2] * t[1];
		t[4] = t[3] * t[1];
		
		for (int i=0; i<3; i++) {
			result[i] = lunarSeries.get(i).compute(t);
		}
		
		Vector3 coords = getCoordinates(result, t);
		return coords;
	}
	
	public Vector3 computeSeriesDerivativeAtTime(double tjj) {
		double[] result = new double[3];
		double[] resultDeriv = new double[3];
		
		double[] t = new double[5];
		t[0] = 1;
		t[1] = (tjj - julian2000) / daysInCentury;
		t[2] = t[1] * t[1];
		t[3] = t[2] * t[1];
		t[4] = t[3] * t[1];
		
		for (int i=0; i<3; i++) {
			result[i] = lunarSeries.get(i).compute(t);
			resultDeriv[i] = lunarSeries.get(i).derivative(t);
		}
		
		Vector3 coords = getCoordinatesDeriv(result, resultDeriv, t);
		return coords.times(1.0 / daysInCentury);
	}
	
	public void perturbOrbit(Orbit orbit, double t) {
		Vector3 pos = computeSeriesAtTime(julian2000 + t / 86400.0);
		Vector3 vel = computeSeriesDerivativeAtTime(julian2000 + t / 86400.0).times(1.0 / 86400.0);
		orbit.setParamsFromPositionAndVelocity(pos, vel);
	}
}
