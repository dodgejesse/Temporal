package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.io.*;
import java.util.*;

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
	
	public static void main(String[] args){
		File[] inputFiles = new File[3];
		inputFiles[0] = new File("data/TempEval3/TBAQ-cleaned/TimeBank");
		inputFiles[1] = new File("data/TempEval3/TBAQ-cleaned/AQUAINT");
		inputFiles[2] = new File("/home/jesse/workspace/Temporal/data/TempEval3/TE3-Silver-data");
		
		
		//for (int i = 0; i < inputFiles.length; i++){
		//	readFiles(inputFiles[i]);
		//}
		readFiles(inputFiles[0]);
	}
	
	private static void readFiles(File doc){
		String[] fList = doc.list();
		System.out.println(doc.getAbsolutePath());
		System.out.println(fList[0]);
		// Map from document name to iso to string[] of data, which will be the output. 
		Map<String, Map<String, String[]>> info = new TreeMap<String, Map<String, String[]>>();
		for (int i = 0; i < fList.length; i++){
			File f = new File(doc.toString() + "/" + fList[i]);
			Map<String, String[]> oneDocData = new TreeMap<String, String[]>();
			getDataFromDoc(f, oneDocData);
			info.put(f.toString(), oneDocData);
		}
	}
	
	private static void getDataFromDoc(File f, Map<String, String[]> oneDocData){
		BufferedReader br;
		String thisLine;
		try {
			br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
			while ((thisLine = br.readLine()) != null) {
				System.out.println(thisLine);
				System.exit(0);
			}
		} catch (IOException e){
			System.err.println("Error: " + e);
		}
	}
}
