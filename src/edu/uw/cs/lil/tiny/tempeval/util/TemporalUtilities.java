package edu.uw.cs.lil.tiny.tempeval.util;

public class TemporalUtilities {


	public static double getRecall(int correct, int gold, int predicted) {
		return ((double) correct)/gold;
	}

	public static double getPrecision(int correct, int gold, int predicted) {
		return ((double) correct)/predicted;
	}

	public static double getF1(int correct, int gold, int predicted) {
		double r = getRecall(correct, gold, predicted);
		double p = getPrecision(correct, gold, predicted);
		return 2*r*p/(r+p);
	}

	public static boolean hasOverlap (int x1, int x2, int y1, int y2) {
		// Whether the first and second string at respective indexes have overlapping regions
		return x2 >= y1 && x1 <= y2;
	}

}
