package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDocument {
	private String docID;
	private String src;
	private String text; //raw text, do not use after preprocessing
	private Map<Integer, Timex> timexes; // from tid to Timex. timexes[1] is reference time
	private List<List<String>> tokens;

	public TemporalDocument(String src) {
		this.src = src;
		this.timexes = new LinkedHashMap<Integer, Timex>();
	}

	public String getSource() {
		return src;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	public String getDocID() {
		return docID;
	}

	public void insertTimex(int timexID, String type, String value, int anchorID, int offset) {
		Timex anchor;
		if (anchorID == -1)
			anchor = null;
		else
			anchor = timexes.get(anchorID);
		timexes.put(timexID, new Timex(type, value, anchor, offset));
	}

	public void setTimexText(int timexID, String text) {
		timexes.get(timexID).text = text;
	}

	public String toString() {
		return text;
	}

	private class Timex {
		public String type;
		public String value;
		public Timex anchor;
		public int offset;

		// Fill in afterwards
		public String text;
		public Pair<Integer, Integer> tokenIndex;

		public Timex(String type, String value, Timex anchor, int offset) {
			this.type = type;
			this.value = value;
			this.anchor = anchor;
			this.offset = offset;
		}
	}

	public void doPreprocessing(StanfordCoreNLP pipeline) {
		Annotation a = new Annotation(text);
		pipeline.annotate(a);

		Map<Integer, Pair<Integer, Integer>> offsetToTokenIndex = new HashMap<Integer, Pair<Integer, Integer>>();

		tokens = new LinkedList<List<String>>();

		for(CoreMap sentence: a.get(SentencesAnnotation.class)) {
			//System.out.printf("Sentence: '%s'\n", sentence);
			List<String> newSentence = new LinkedList<String>();
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				offsetToTokenIndex.put(token.beginPosition(), Pair.of(tokens.size(), newSentence.size()));
				newSentence.add(token.get(TextAnnotation.class));
			}
			//SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}
		for(Timex t : timexes.values()) {
			if(t.offset != -1) {
				if(offsetToTokenIndex.containsKey(t.offset)) {
					t.tokenIndex = offsetToTokenIndex.get(t.offset);
					//System.out.printf("Timex (%s) at %dth token\n", t.text, t.tokenIndex.second());
				}
				else
					System.out.printf("Unable to find offset for timex [#%d] (%s)\n", t.offset, t.text);
			}
		}
	}
}
