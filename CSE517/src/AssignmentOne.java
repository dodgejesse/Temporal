import java.io.*;
import java.util.*;


public class AssignmentOne {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numInputDatasets = 3;
		File[] inputFiles = new File[numInputDatasets];
		inputFiles[0] = new File("data/corpora/brown.txt");
		inputFiles[1] = new File("data/corpora/gutenberg.txt");
		inputFiles[2] = new File("data/corpora/reuters.txt");
		List<Map<String, Integer>> unigrams = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> bigrams = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> trigrams = new ArrayList<Map<String, Integer>>();
		
		readCounts(inputFiles, unigrams);
		
		
	}
	
	private static void readCounts(File[] inputFiles, List<Map<String, Integer>> counts){
		for (int i = 0; i < inputFiles.length; i++){
			Map<String, Integer> tmpMap = new HashMap<String, Integer>();
			countSingleDataset(inputFiles[i], tmpMap);
			counts.add(tmpMap);
		}
	}
	
	private static void countSingleDataset(File f, Map<String, Integer> count){
		BufferedReader br;
		try{
			String thisLine;
			br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
			while ((thisLine = br.readLine()) != null) {
				countSingleLine(thisLine.toLowerCase(), count);
			}
			br.close();

		} catch (Exception e){
			System.out.println("Problem in countSingleDataset!");
			System.out.println(e);
		}
	}
	
	private static void countSingleLine(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			if (tokens[i].matches("[a-zA-Z0-9]")){
				if (count.containsKey(tokens[i]))
					count.put(tokens[i], count.get(tokens[i]) + 1);
				else 
					count.put(tokens[i], 1);
			}
		}
		
	}
}
