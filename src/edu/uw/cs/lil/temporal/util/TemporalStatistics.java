package edu.uw.cs.lil.temporal.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TemporalStatistics {
	private int correctMentions, goldMentions, predictedMentions;
	private int totalAttributes, correctAttributes;
	private Map<String, Integer> correctClasses;
	private Map<String, Integer> incorrectClasses;

	public TemporalStatistics() {
		correctMentions = goldMentions = predictedMentions = 0;
		totalAttributes = correctAttributes = 0;
		correctClasses = new TreeMap<String, Integer>();
		incorrectClasses = new TreeMap<String, Integer>();
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

	public synchronized void incrementTotalAttributes() {
		totalAttributes ++;
	}

	public synchronized void incrementCorrectAttributes() {
		correctAttributes ++;
	}

	public synchronized void incrementIncorrectClass(String exampleClass) {
		if(!incorrectClasses.containsKey(exampleClass))
			incorrectClasses.put(exampleClass, 0);
		incorrectClasses.put(exampleClass, incorrectClasses.get(exampleClass) + 1);
	}

	public synchronized void incrementCorrectClass(String exampleClass) {
		if(!correctClasses.containsKey(exampleClass))
			correctClasses.put(exampleClass, 0);
		correctClasses.put(exampleClass, correctClasses.get(exampleClass) + 1);
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
		String format = "%-40s%.2f%% (%d/%d)\n";
		String s = "";

		s += String.format(format, "Correct values", percentage(correctAttributes, totalAttributes), correctAttributes, totalAttributes);
		for (Entry<String, Integer> entry : getSortedClasses(correctClasses.entrySet()))
			s += String.format(format, "  " + entry.getKey(), percentage(entry.getValue(), totalAttributes), entry.getValue(), totalAttributes);

		s += String.format(format, "Incorrect values", percentage(totalAttributes - correctAttributes, totalAttributes), totalAttributes - correctAttributes, totalAttributes);
		for (Entry<String, Integer> entry : getSortedClasses(incorrectClasses.entrySet()))
			s += String.format(format, "  " + entry.getKey(), percentage(entry.getValue(), totalAttributes), entry.getValue(), totalAttributes);

		return s;
	}

	private List<Entry<String, Integer>> getSortedClasses(Set<Entry<String, Integer>> entrySet) {
		List<Entry<String, Integer>> sortedEntries = new LinkedList<Entry<String, Integer>> (entrySet);
		Collections.sort(sortedEntries, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				// Flip e1 and e2 to get most occurrences first
				return e2.getValue().compareTo(e1.getValue());
			}        
		});
		return sortedEntries;
	}
}
