package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadXMLFiles{
	
	// Author: Jesse Dodge
	// Input: An XML file containing temporal expressions.
	// Output: A file in the format readable by my grounding system. 
	// Output format:
	// "//sentence"
	// phrase 
	// document id (this is useful to make sure context model doesn't look at previous documents.)
	// document time
	// gold type
	// gold value
	
	public static void main(String[] args) throws IOException{
		int numInputDatasets = 4;
		File[] inputFiles = new File[numInputDatasets];
		inputFiles[0] = new File("data/TempEval3/TBAQ-cleaned/TimeBank");
		inputFiles[1] = new File("data/TempEval3/TBAQ-cleaned/AQUAINT");
		inputFiles[2] = new File("data/TempEval3/TE3-Silver-data");
		inputFiles[3] = new File("data/TempEval3/testingSentenceSplit");
		
		
		//for (int i = 0; i < inputFiles.length; i++){
		//	readFiles(inputFiles[i]);
		//	System.out.println("\n\n\n\n\n");
		//}
		//System.out.println("Testing TimeBank");
		Map<String, Map<String, String[]>> info = new TreeMap<String, Map<String, String[]>>();
		//readFiles(inputFiles[0], info);
		//readFiles(inputFiles[1], info);
		readFiles(inputFiles[3], info);
		
		printDataset(info);
		//System.out.println("\n\n\n\n\nTesting AQUAINT");
		//readFiles(inputFiles[1]);
		//System.out.println("\n\n\n\n\nTesting Silver");
		//readFiles(inputFiles[2]);
	}
	
	private static void printDataset(Map<String, Map<String, String[]>> info){
		for (String doc : info.keySet()){
			for (String tid : info.get(doc).keySet()){
				if (tid == "t0")
					continue;
				System.out.println(doc);
				System.out.println(info.get(doc).get(tid)[1]);
				System.out.println(info.get(doc).get(tid)[2].toLowerCase());
				System.out.println(info.get(doc).get("t0")[1]);
				System.out.println(info.get(doc).get(tid)[3]);
				System.out.println(info.get(doc).get(tid)[4]);
				System.out.println();
			}
		}
	}
	
	private static Map<String, Map<String, String[]>> readFiles(File doc, Map<String, Map<String, String[]>> info) throws IOException{
		SentenceSplitter splitter = new SentenceSplitter();
		String[] fList = doc.list();
		// Map from document name to tempID to string[] of data, which will be the output. 
		for (int i = 0; i < fList.length; i++){
			File f = new File(doc.toString() + "/" + fList[i]);
			
			//Splitting the sentences within the <TEXT></TEXT> tags. 
			splitter.split(f);
			f = new File(doc.toString() + "/" + fList[i] + ".ssplit");
			Map<String, String[]> oneDocData = new TreeMap<String, String[]>();
			getDataFromDoc(f, oneDocData);
			info.put(f.toString(), oneDocData);
			if (!f.delete())
				throw new FileNotFoundException("WARNING: I'm not sure this is the right exception. But the program couldn't delete the file of split sentences made by SentenceSplitter.");
		}
		
		// For testing
		//for (String s : info.keySet()){
		//	for (String s2 : info.get(s).keySet()){
		//		System.out.println(info.get(s).get(s2)[1]);
		//	}
		//}
		
		return info;
	}
	
	private static void getDataFromDoc(File f, Map<String, String[]> oneDocData) throws IOException{
		BufferedReader br;
		String thisLine;
		br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		while ((thisLine = br.readLine()) != null) {
			processLine(thisLine, oneDocData);
			//System.out.println(thisLine);
		}
		br.close();
	}
	
	private static void processLine(String l, Map<String, String[]> oneDocData){
		// Idea for when there are multiple TIMEX3s on a line:
		// Split on "/TIMEX3", then pass each of them to this function.
		if (l.contains("TIMEX3")){
			if (l.contains("functionInDocument=\"CREATION_TIME\"")){
				String[] strs = getCreationTime(l);
				oneDocData.put(strs[0], strs);
			} else if (l.contains("TIMEX3 tid=")){
				//System.out.println(l);
				Pattern p = Pattern.compile("<TIMEX3 tid=.+?</TIMEX3>");
				Matcher m = p.matcher(l);
				String sent = l.replaceAll("\\<.*?\\>", "");
				while(m.find()){
					String[] strs = getTimex(m.group(0));
					// To replace the place holder with the actual sentence
					strs[1] = sent;
					oneDocData.put(strs[0], strs);
				}
				//System.out.println();
				// Split into multiple timexs
				// Process each one individually
			}
		}
	}
	
	
	private static String[] getTimex(String l){
		// To get the tid #.
		// Don't think I'll need this, but might be useful.
		String tid = extractInfo(l, "tid=\".+?\"");
		tid = extractInfo(tid, "\".+");
		
		// To get the phrase that's to be parsed.
		String phrase = extractInfo(l, ">.+?<");
		
		// To get the type.
		String type = extractInfo(l, "type=\".+?\"");
		type = extractInfo(type, "\".+");
		
		// To get the value.
		String val = extractInfo(l, "value=\".+?\"");
		val = extractInfo(val, "\".+");
				
		String[] strs = {tid, "sentence", phrase, type, val};
		
		// A hack because my regex is ghetto.
		for (int i = 0; i < strs.length; i++){
			strs[i] = strs[i].substring(0,strs[i].length()-1);
			//System.out.println(strs[i]);
		}
		return strs;
	}
	
	private static String extractInfo(String l, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(l);
		String val = "";
		if (m.find())
			val = m.group();
		if (val == "")
			throw new IllegalArgumentException("Problem getting info from a Timex!");
		val = val.substring(1, val.length());
		return val;
	}
	
	private static String[] getCreationTime(String l){
		//System.out.println(l);
		Pattern p = Pattern.compile("value=\"[0-9-:T]+\"");
		Matcher m = p.matcher(l);
		String val = "";
		if (m.find())
			val = m.group();
		if (val == "")
			throw new IllegalArgumentException("Problem getting creation time!");
		val = val.substring(7, val.length()-1);
		val = val.substring(0,10);
		String[] strs = {"t0", val};
		//System.out.println(val);
		return strs;

	}
}


/*
 * 		String[] strs;
		if(l.contains("functionInDocument=\"CREATION_TIME\"")){
			String val = getValue(l);
			//strs[0] = 
			System.out.println(l);
		}
		//return null;
		return strs;
		*/
