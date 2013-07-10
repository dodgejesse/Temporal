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
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Filters;
import edu.uw.cs.lil.tiny.tempeval.DependencyParser;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDocument {
	// Temporary class used before flattening all documents
	// Necessary intermediate class since XML reader is event-driven

	private String docID;
	private String text; //raw text, do not use after preprocessing
	private Map<Integer, Timex> timexes; // from tid to Timex. timexes[1] is reference time
	private List<NewTemporalSentence> sentences;

	public TemporalDocument() {
		this.timexes = new LinkedHashMap<Integer, Timex>();
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
		timexes.get(timexID).setText(text);
	}

	public List<NewTemporalSentence> getSentences() {
		return sentences;
	}

	public String toString() {
		return text;
	}

	public void doPreprocessing(StanfordCoreNLP pipeline, GrammaticalStructureFactory gsf) {
		Annotation a = new Annotation(text);
		pipeline.annotate(a);

		Map<Integer, Pair<NewTemporalSentence, Integer>> startCharIndexToTokenIndex = new HashMap<Integer, Pair<NewTemporalSentence, Integer>>();
		Map<Integer, Pair<NewTemporalSentence, Integer>> endCharIndexToTokenIndex = new HashMap<Integer, Pair<NewTemporalSentence, Integer>>();

		sentences = new LinkedList<NewTemporalSentence>();

		for(CoreMap sentence: a.get(SentencesAnnotation.class)) {
			NewTemporalSentence newSentence = new NewTemporalSentence(docID);
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				startCharIndexToTokenIndex.put(token.beginPosition(), Pair.of(newSentence, newSentence.getNumTokens()));
				endCharIndexToTokenIndex.put(token.endPosition(), Pair.of(newSentence, newSentence.getNumTokens()));
				newSentence.insertToken(token.get(TextAnnotation.class));
			}
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			String dp = EnglishGrammaticalStructure.dependenciesToString(gs, gs.typedDependencies(false), tree, true, false);
			newSentence.saveDependencyParse(dp);
			sentences.add(newSentence);
		}
		
		for(Timex t : timexes.values()) {
			if(t.getStartChar() != -1) {
				if(startCharIndexToTokenIndex.containsKey(t.getStartChar()) && endCharIndexToTokenIndex.containsKey(t.getEndChar())) {
					Pair<NewTemporalSentence, Integer> startIndexes = startCharIndexToTokenIndex.get(t.getStartChar());
					Pair<NewTemporalSentence, Integer> endIndexes = endCharIndexToTokenIndex.get(t.getEndChar());
					t.setTokenRange(startIndexes.second(), endIndexes.second());
					// Assume start and end occur in the same sentence
					startIndexes.first().insertTimex(t);
				}
				else
					System.out.printf("Unable to find offset for timex [#%d - #%d] (%s)\n", t.getStartChar(), t.getEndChar(), t.getText());
			}
		}
	}
}
