package edu.uw.cs.lil.temporal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.cs.lil.temporal.structures.TemporalMention;
import edu.uw.cs.utils.composites.Pair;

public class GovernerVerbPOSExtractor {
	// @Param: a String[] that contains world knowledge about a given parse, including the sentence, the dependency parse,
	// 		   and the character number that the phrase starts on.
	public static Pair<String, String> getGovVerbTag(TemporalMention mention) {
		String depParse = mention.getSentence().getDependencyParse();
		// tokenNum is the number of the starting token of the temporal phrase. starts at 0.
		int tokenIndex = mention.getStartToken();
		
		// split the dependency parse into a string[], splitting on new lines.
		String[] depParseArray = depParse.split("\n");
		Pair<String, String> verb = findVerb(depParseArray, tokenIndex);
			
		return verb;
	}
	
	private static Pair<String,String> findVerb(String[] depParseArray, int tokenNum){
		int curTokenNum = tokenNum;
		// while loop that traverses the dependency parse to find the governer verb.
		String curPOS = getPOS(depParseArray[curTokenNum]);
		while (!curPOS.startsWith("VB")){
			curTokenNum = getDep(depParseArray[curTokenNum]);
			// in case there is no verb in the 'sentence'.
			if (curTokenNum == -1){
				curPOS = "noVerb";
				break;
			}
			curPOS = getPOS(depParseArray[curTokenNum]);
		}
		String mod = "";
		if (curTokenNum != -1)
			mod += findMod(depParseArray, curTokenNum);
		return Pair.of(mod, curPOS);
	}
	
	private static String findMod(String[] depParseArray, int tokenNum){
		for (int i = 0; i < depParseArray.length; i++){
			if (getDep(depParseArray[i]) == tokenNum && getPOS(depParseArray[i]).equals("MD")){
				return "MD";
			}
		}
		return "";
	}
	
	// @param: a single line from the dependency parse
	// extracts the POS of a given line.
	private static String getPOS(String s){
		String partOfLineEndingWithPOS = regexResult(s, "^[0-9]+\t[^\t]+\t_\t[^\t]+");
		String POS = regexResult(partOfLineEndingWithPOS, "_\t[^\t]+$");
		String result = POS.substring(1).trim();
		return result;
	}
	
	// @param: a single line from the dependency parse
	// extracts the dependency arc (number) from a given line.
	private static int getDep(String s){
		String partOfLineEndingWithDepNum = regexResult(s, "^[0-9]+\t[^\t]+\t_\t[^\t]+\t[^\t]+\t_\t[0-9]+");
		String depNum = regexResult(partOfLineEndingWithDepNum, "[0-9]+$");
		
		return Integer.parseInt(depNum) - 1;
	}
	
	// given a regex and a string, returns the result of the regex applied to the string.
	private static String regexResult (String s, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		String val = "";
		if (m.find())
			val = m.group();
		if (val.equals(""))
			throw new IllegalArgumentException("Problem getting info from regexResult! Probably used on the dependency parse");
		return val;
	}
}
