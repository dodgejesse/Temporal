package edu.uw.cs.lil.tiny.tempeval.readdata;
import java.io.*;
import java.util.*;

import java.io.IOException;

import edu.stanford.nlp.pipeline.*;

public class SentenceSplitter {
	
	public void split(File f){
		// Want to set this PrintWriter to print to file f.name + ".ssplit"
		readFile(f);
	}
	
	private void readFile(File f){
		// if within <TEXT></TEXT> tags, split. If not, just print.
		BufferedReader br;
		String thisLine;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f.getPath() + ".ssplit"));
			boolean text = false;
			br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
			String s = "";
			String doc = "";
			while ((thisLine = br.readLine()) != null) {
				if (thisLine.equals("</TEXT>")){
					text = false;
					doc += (splitString(s));
					//out.println(thisLine);
					//System.exit(1);
				}
				if (text){
					s += thisLine + "\n";
				}else
					doc += thisLine + "\n";
				if (thisLine.equals("<TEXT>")){
					text = true;
				}
			}
			//System.out.println(doc);
			out.write(doc);
			out.close();
		} catch (IOException e){
			System.err.println("Error: " + e);
		}
	}
	
	private String splitString(String s){		
		OutputStream baos = new ByteArrayOutputStream();
		
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation a = new Annotation(s);
		
		pipeline.annotate(a);
		pipeline.prettyPrint(a, baos);
		
		String sents = getSentences(baos.toString());
		
		return sents;
	}
	
	//Extracts every third line from the prettyprinted output of the stanford parser.
	private String getSentences(String s){
		String outputSentences = "";
		String[] lines;
		String regex = "\\n";
		lines = s.split(regex);
		for (int i = 0; i < lines.length; i++){
			if ((i-1)%3 == 0)
				outputSentences += lines[i] + "\n";
		}
		return outputSentences;
	}
}


