import java.io.*;
import java.util.*;


public class AssignmentOne {
	static Map<String, Map<String, Integer>> unigrams;
	static Map<String, Map<String, Integer>> bigrams;
	static Map<String, Map<String, Integer>> trigrams;
	static double unkMass = 0.0001;
	static double l1 = 1.0/6;
	static double l2 = 1.0/6;
	static double l3 = 2.0/3;
	static int totalWordsInTest;
	
	public static void main(String[] args) throws IOException {
		unigrams = new HashMap<String, Map<String, Integer>>();
		bigrams = new HashMap<String, Map<String, Integer>>();
		trigrams = new HashMap<String, Map<String, Integer>>();
		
		double[][] lambdas = setLambdas();
		String[][] datasets = getDatasets();
		
		for (int j = 0; j < datasets.length; j++){
			countSingleDataset(new File(datasets[j][0]), datasets[j][2]);
			for (int i = 0; i < datasets.length; i++){
				System.out.println("Now testing on the " + datasets[i][2]+ " corpus, trained on the " + datasets[j][2] + " corpus...");
				
				// Passes the file for testing, the name of the training corpus,
				//and a boolean indicating true=linear interpolation, false=proposed backoff
				
				testing(new File(datasets[i][1]), datasets[j][2], true);
				System.out.println();
			}
		}
	}
	
	// To set the location of the data files, in the folder data/corpora/brown.train.txt, for example.
	private static String[][] getDatasets(){
		String[][] strs = new String[3][3];
		String[] names = {"brown", "gutenberg", "reuters"};
		for (int i = 0; i < strs.length; i++){
			strs[i][0] = "data/corpora/"+names[i]+".train.txt";
			strs[i][1] = "data/corpora/"+names[i]+".test.txt";
			strs[i][2] = names[i];
		}
		return strs;
	}
	
	// To set the lambdas for a grid search
	private static double[][] setLambdas(){
		double[][] tmp = new double[7][3];
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < tmp[i].length; j++){
				tmp[i][j] = .05;
			}
			tmp[i][i] = .9;
			tmp[tmp.length-1][i] = 1.0/3;
		}
		for (int i = 3; i < 6; i++){
			for (int j = 0; j < tmp[i].length; j++){
				tmp[i][j] = 1.0/6;
			}
			tmp[i][i-3] = 2.0/3;
		}
		return tmp;
	}
	
	// For testing the trigram models.
	private static void printSomeTrigrams(){
		String mostCommon = "";
		int count = 0;
		for (String s : trigrams.get("brown").keySet()){
			if (trigrams.get("brown").get(s) > count){
				mostCommon = s;
				count = trigrams.get("brown").get(s);
				System.out.println(count + " : " + s);
			}
			
		}
		System.out.println(mostCommon);
		System.exit(0);
		
	}
	
	private static void testing(File f, String trainingCorpus, boolean interpolation) throws IOException{
		totalWordsInTest = 0;
		BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		String thisLine;
		double logProb = 0;
		int lineCounter = 0;
		while ((thisLine = br.readLine()) != null){
			lineCounter++;
			thisLine = thisLine.toLowerCase();
			logProb += calcInterpolationProb(thisLine, trainingCorpus, interpolation);
			if (lineCounter % 3300 == 0){
				//System.out.println(lineCounter*1.0 / 100 + "% complete");
			}
		}
		// "START START" within bigrams is the number of sentences.
		double perplexity = Math.pow(2, (-logProb / totalWordsInTest));
		System.out.println("Perplexity: " + perplexity);
		
		br.close();
	}
	
	private static double calcInterpolationProb(String l, String trainingCorpus, boolean interpolation){		
		double logProb = 0;
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			double curTri = QMLOfTri(tokens, i, trainingCorpus);
			double curBi = QMLOfBi(tokens, i, trainingCorpus);
			double curUni = QMLOfUni(tokens, i, trainingCorpus);
			double prob = 0;
			if (interpolation)
				prob = l1 * curTri + l2 * curBi + l3 * curUni;
			else
				prob = backoffProb(tokens, i, trainingCorpus);

			totalWordsInTest++;
			logProb += Math.log(prob) / Math.log(2);
		}
		return logProb;
	}
	
	private static double backoffProb(String[] tokens, int i, String trainingCorpus){
		if (trigrams.get(trainingCorpus).containsKey(getTrigram(tokens,i))){
			return QMLOfTri(tokens, i, trainingCorpus);
		} else if (bigrams.get(trainingCorpus).containsKey(getBigram(tokens,i))){
			double sum = computeBiSum(tokens, i, trainingCorpus);
			return QMLOfBi(tokens, i, trainingCorpus) / sum;
		} else if (unigrams.get(trainingCorpus).containsKey(tokens[i])){
			double sum = computeUniSum(tokens, i, trainingCorpus);
			return QMLOfUni(tokens, i, trainingCorpus) / sum;
		} else 
			return QMLOfUni(tokens, i, trainingCorpus);
	}
	
	private static double computeUniSum(String[] tokens, int i, String trainingCorpus){
		double sum = 0;
		for (String s : unigrams.get(trainingCorpus).keySet()){
			String curBi;
			if (i == 0){
				curBi = "START " + s;
			} else 
				curBi = tokens[i-1] + " " + s;
			if (!bigrams.get(trainingCorpus).containsKey(curBi))
				sum += (double) unigrams.get(trainingCorpus).get(s) / unigrams.get(trainingCorpus).get("TOTAL");
		}
		return sum;
	}
	
	private static double computeBiSum(String[] tokens, int i, String trainingCorpus){
		double sum = 0;
		for (String s : unigrams.get(trainingCorpus).keySet()){
			String curTri;
			if (i == 0){
				curTri = "START START " + s;
			} else if (i == 1){
				curTri = "START " + tokens[i-1] + " " + s;
			} else {
				curTri = tokens[i-2] + " " + tokens[i-1] + " " + s;
			}
			String curBi;
			String prevUni;
			if (i == 0){
				curBi = "START " + s;
				prevUni = "START";
			} else {
				curBi = tokens[i-1] + " " + s;
				prevUni = tokens[i-1];
			}
			if (!trigrams.get(trainingCorpus).containsKey(curTri) && bigrams.get(trainingCorpus).containsKey(curBi)){
				sum += (double) bigrams.get(trainingCorpus).get(curBi) / unigrams.get(trainingCorpus).get(prevUni);
			}
		}
		return sum;
	}
	
	private static double QMLOfUni(String[] tokens, int i, String trainingCorpus){
		if (!unigrams.get(trainingCorpus).containsKey(tokens[i]))
			return (double) unigrams.get(trainingCorpus).get("UNK") / unigrams.get(trainingCorpus).get("TOTAL");
		else
			return (double) unigrams.get(trainingCorpus).get(tokens[i]) / unigrams.get(trainingCorpus).get("TOTAL");
					
	}
	
	private static double QMLOfBi(String[] tokens, int i, String trainingCorpus){
		String curBi;
		String prevUni;
		if (i == 0){
			curBi = "START " + tokens[i];
			prevUni = "START";
		} else {
			curBi = tokens[i-1] + " " + tokens[i];
			prevUni = tokens[i-1];
		}
		if (!bigrams.get(trainingCorpus).containsKey(curBi) || ! unigrams.get(trainingCorpus).containsKey(prevUni))
			return 0;
		else
			return (double) bigrams.get(trainingCorpus).get(curBi) / unigrams.get(trainingCorpus).get(prevUni);
	}
	
	private static double QMLOfTri(String[] tokens, int i, String trainingCorpus){
		String curTri;
		String prevBi;
		if (i == 0){
			curTri = "START START " + tokens[i];
			prevBi = "START START";
		} else if (i == 1){
			curTri = "START " + tokens[i-1] + " " + tokens[i];
			prevBi = "START " + tokens[i-1];
		} else{
			curTri = tokens[i-2] + " " + tokens[i-1] + " " + tokens[i];
			prevBi = tokens[i-2] + " " + tokens[i-1];
		}
		if (!trigrams.get(trainingCorpus).containsKey(curTri) || !bigrams.get(trainingCorpus).containsKey(prevBi))
			return 0;
		else{
			double QML = (double)trigrams.get(trainingCorpus).get(curTri) / bigrams.get(trainingCorpus).get(prevBi);
			return QML;
		}
	}
	
	private static void countSingleDataset(File f, String name) throws IOException {
		System.out.println("Reading in the " + name + " corpus...");
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
		uni.put("TOTAL", (int)Math.round(uni.get("TOTAL")*1 + unkMass));
		uni.put("UNK", (int)Math.round(uni.get("TOTAL")*unkMass));
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
			increaseCountInMap("TOTAL", count);
		}
		increaseCountInMap("START", count);
	}
	
	private static void countSingleLineBi(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			String cur = getBigram(tokens, i);
			increaseCountInMap(cur, count);
		}
		String end = tokens[tokens.length-1] + " " + "STOP";
		increaseCountInMap(end, count);
		increaseCountInMap("START START", count);
	}
	
	private static void countSingleLineTri(String l, Map<String, Integer> count){
		String[] tokens = l.split(" ");
		for (int i = 0; i < tokens.length; i++){
			String cur = getTrigram(tokens, i); 
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
	
	private static String getBigram(String[] tokens, int i){
		String cur;
		if (i == 0){
			cur = "START" + " " + tokens[i];
		} else{
			cur = tokens[i-1] + " " + tokens[i];
		}
		return cur;
	}
	
	private static String getTrigram(String[] tokens, int i){
		String cur;
		if (i == 0)
			cur = "START START " + tokens[i];
		else if (i == 1)
			cur = "START " + tokens[i-1] + " " + tokens[i];
		else
			cur = tokens[i-2] + " " + tokens[i-1] + " " + tokens[i];
		return cur;
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