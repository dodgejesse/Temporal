package edu.uw.cs.lil.tiny.tempeval.util;

public class TemporalStatistics {
	private int correctMentions, goldMentions, predictedMentions;
	private int totalObservations, correctObservations, correctTypesOnly, correctValuesOnly, incorrectObservations, incorrectParseSelection, noParses;
	public TemporalStatistics() {
		correctMentions = goldMentions = predictedMentions = 0;
		totalObservations = correctObservations = correctValuesOnly = incorrectObservations = incorrectParseSelection = noParses = 0;
	}

	public synchronized void addCorrect(int newCorrect) {
		correctMentions += newCorrect;
	}

	public synchronized void addGold(int newGold) {
		goldMentions += newGold;
	}

	public synchronized void addPredicted(int newPredicted) {
		predictedMentions += newPredicted;
	}

	public synchronized void incrementTotalObservations() {
		totalObservations ++;
	}
	
	public synchronized void incrementCorrectObservations() {
		correctObservations ++;
	}

	public synchronized void incrementCorrectTypes() {
		correctTypesOnly ++;
	}
	
	public synchronized void incrementCorrectValues() {
		correctValuesOnly ++;
	}
	
	public synchronized void incrementIncorrectObservations() {
		incorrectObservations ++;
	}
	
	public synchronized void incrementNoParses() {
		noParses ++;
	}

	public synchronized void incrementIncorrectParseSelection() {
		incorrectParseSelection ++;
	}
	
	public double getRecall() {
		return ((double) correctMentions)/goldMentions;
	}

	public double getPrecision() {
		return ((double) correctMentions)/predictedMentions;
	}

	public double getF1() {
		double r = getRecall();
		double p = getPrecision();
		return 2*r*p/(r+p);
	}

	private double percentage(int numerator, int denominator) {
		return numerator*100.0/denominator;
	}
	
	public String toString() {
		return detectionToString() + "\n" + attributetoString();
	}

	public String detectionToString() {
		String s = "";
		s += String.format("Recall:    %.2f%% (%d/%d)\n", 100*getRecall(), correctMentions, goldMentions);
		s += String.format("Precision: %.2f%% (%d/%d)\n", 100*getPrecision(), correctMentions, predictedMentions);
		s += String.format("F1:        %.2f%%\n", 100*getF1());
		return s;
	}
	
	public String attributetoString(){
		int correctValues = correctObservations + correctValuesOnly;
		int incorrectValues = totalObservations - correctValues - noParses;
		String s = "";
		s += String.format("Correct value:              %.2f%% (%d/%d)\n", percentage(correctValues, totalObservations), correctValues, totalObservations);
		s += String.format("  Correct value and type:   %.2f%% (%d/%d)\n", percentage(correctObservations, totalObservations), correctObservations, totalObservations);
		s += String.format("Incorrect value:            %.2f%% (%d/%d)\n", percentage(incorrectValues, totalObservations), incorrectValues, totalObservations);
		s += String.format("  Incorrect value and type: %.2f%% (%d/%d)\n", percentage(incorrectObservations, totalObservations), incorrectObservations, totalObservations);
		s += String.format("  Incorrect parse selection:%.2f%% (%d/%d)\n", percentage(incorrectParseSelection, totalObservations), incorrectParseSelection, totalObservations);
		s += String.format("No parses:                  %.2f%% (%d/%d)\n", percentage(noParses, totalObservations), noParses, totalObservations);
		return s;
	}
}
