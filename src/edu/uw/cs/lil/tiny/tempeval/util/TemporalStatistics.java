package edu.uw.cs.lil.tiny.tempeval.util;

public class TemporalStatistics {
	private int correctMentions, goldMentions, predictedMentions;
	private int totalObservations, correctObservations, correctTypes, correctValues, incorrectObservations, noParses;
	public TemporalStatistics() {
		correctMentions = goldMentions = predictedMentions = 0;
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
		correctTypes ++;
	}
	
	public synchronized void incrementCorrectValues() {
		correctValues ++;
	}
	
	public synchronized void incrementIncorrectObservations() {
		incorrectObservations ++;
	}
	
	public synchronized void incrementNoParses() {
		noParses ++;
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
		return detectionToString() + "\n\n" + attributetoString();
	}

	public String detectionToString() {
		String s = "";
		s += String.format("Recall: %.2f (%d/%d)\n", 100*getRecall(), correctMentions, goldMentions);
		s += String.format("Precision: %.2f (%d/%d)\n", 100*getPrecision(), correctMentions, predictedMentions);
		s += String.format("F1: %.2f\n", 100*getF1());
		return s;
	}
	
	public String attributetoString(){
		String s = "";
		s += String.format("Correct type and value: %d/%d (%.2f%%)\n", correctObservations, totalObservations, percentage(correctObservations, totalObservations));
		s += String.format("Correct value only: %d/%d (%.2f%%)\n", correctValues, totalObservations, percentage(correctValues, totalObservations));
		s += String.format("Correct type only: %d/%d (%.2f%%)\n", correctTypes, totalObservations, percentage(correctTypes, totalObservations));
		s += String.format("Correct type and value: %d/%d (%.2f%%)\n", incorrectObservations, totalObservations, percentage(incorrectObservations, totalObservations));
		s += String.format("Correct type and value: %d/%d (%.2f%%)\n", noParses, totalObservations, percentage(noParses, totalObservations));
		return s;
	}
}
