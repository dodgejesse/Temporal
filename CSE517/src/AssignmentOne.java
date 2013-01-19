import java.io.*;
import java.util.*;


public class AssignmentOne {
	static Map<String, Map<String, Integer>> unigrams;
	static Map<String, Map<String, Integer>> bigrams;
	static Map<String, Map<String, Integer>> trigrams;
	
	public static void main(String[] args) throws IOException {
		

		unigrams = new HashMap<String, Map<String, Integer>>();
		bigrams = new HashMap<String, Map<String, Integer>>();
		trigrams = new HashMap<String, Map<String, Integer>>();
		
		countSingleDataset(new File("data/corpora/brown.txt"), "brown");
		countSingleDataset(new File("data/corpora/gutenberg.txt"), "gutenberg");
		countSingleDataset(new File("data/corpora/reuters.txt"), "reuters");
		
		testing(new File("data/corpora/brown.txt"), true);
	}
	
	private static void testing(File f, boolean interpolation) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		String thisLine;
		while ((thisLine = br.readLine()) != null){
			if (interpolation){
				calcInterpolationProb(thisLine);
			}
		}
	}
	
	private static void calcInterpolationProb(String l){
		
	}
	
	private static void countSingleDataset(File f, String name) throws IOException {
		Map<String, Integer> uni = new HashMap<String, Integer>();
		Map<String, Integer> bi = new HashMap<String, Integer>();
		Map<String, Integer> tri = new HashMap<String, Integer>();
		BufferedReader br;
		String thisLine;
		br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		while ((thisLine = br.readLine()) != null) {
			thisLine = thisLine.toLowerCase();
			countSingleLineUni(thisLine, uni);
			countSingleLineBi(thisLine, bi);
			countSingleLineTri(thisLine, tri);
			
		}
		br.close();
		unigrams.put(name, uni);
		bigrams.put(name, bi);
		trigrams.put(name, tri);
	}
	
	private static void countSingleLineUni(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			if (count.containsKey(tokens[i]))
				count.put(tokens[i], count.get(tokens[i]) + 1);
			else 
				count.put(tokens[i], 1);
		}
	}
	
	private static void countSingleLineBi(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			String cur;
			if (i == 0){
				cur = "START" + " " + tokens[i];
			} else{
				cur = tokens[i-1] + " " + tokens[i];
			}
			increaseCountInMap(cur, count);
		}
		String end = tokens[tokens.length-1] + " " + "STOP";
		increaseCountInMap(end, count);
		increaseCountInMap("START START", count);
	}
	
	private static void countSingleLineTri(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			String cur; 
			if (i == 0)
				cur = "START START " + tokens[i];
			else if (i == 1)
				cur = "START " + tokens[i-1] + " " + tokens[i];
			else
				cur = tokens[i-2] + " " + tokens[i-1] + " " + tokens[i];
			increaseCountInMap(cur, count);
		}
		if (tokens.length > 2){
			String end = tokens[tokens.length-2] + " " + tokens[tokens.length-1] + " STOP";
			increaseCountInMap(end, count);
		}
	}
	
	private static void increaseCountInMap(String key, Map<String, Integer> count){
		if (count.containsKey(key))
			count.put(key,  count.get(key) + 1);
		else
			count.put(key, 1);
	}
}


/*
List<String> s1 = new ArrayList<String>();
s1.add("one");
s1.add("two");
List<String> s2 = new ArrayList<String>();
s2.add("on"+"e");
s2.add("t"+"wo");
System.out.println(s1)

System.exit(0);

*/