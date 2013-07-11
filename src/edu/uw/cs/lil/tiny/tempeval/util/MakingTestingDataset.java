package edu.uw.cs.lil.tiny.tempeval.util;

public class MakingTestingDataset {

	public static void main(String[] args) {
		String[] numbers = {"one", "two", "three", "four", "five", "six",
				"seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen"};
		for (int i = 0; i < numbers.length; i++){
			System.out.println(numbers[i] + " :- NP/NP : (lambda $0:d (*:<d,<n,d>> $0 " + (i + 1) + ":n))");
		}
		
	}

}
// a :- NP/NP : (lambda $0:d (*:<d,<n,d>> $0 1:n))
/*
String[] durationBases = {"year", "month", "week", "day"};
String[] numbers = {"one", "two", "three", "four", "five", "six",
		"seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen"};
String[] outputDir = {"Y", "M", "W","D"};
for (int i = 0; i < durationBases.length; i++){
	for (int j = 0; j < numbers.length; j++){
		System.out.println(numbers[j] + " " + durationBases[i]);
		System.out.println("1998-01-14");
		System.out.println("P" + (j+1) + outputDir[i]);
		System.out.println();
	}
}
*/