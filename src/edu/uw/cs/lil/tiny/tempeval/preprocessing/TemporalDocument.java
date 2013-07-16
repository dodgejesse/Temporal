package edu.uw.cs.lil.tiny.tempeval.preprocessing;

import java.util.HashMap;
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
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.util.Debug;
import edu.uw.cs.lil.tiny.tempeval.util.Debug.Type;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDocument {
	// Temporary class used before flattening all documents
	// Necessary intermediate class since XML reader is event-driven

	private String docID;
	private String text;
	private List<TemporalMention> mentions;
	private List<TemporalSentence> sentences;
	private String referenceTime;

	public TemporalDocument() {
		this.mentions = new LinkedList<TemporalMention>();
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

	public void insertMention(String type, String value, int offset) {
		mentions.add(new TemporalMention(type, value, offset));
	}

	public void setLastMentionText(String text) {
		mentions.get(mentions.size() - 1).setText(text);
	}

	public List<TemporalSentence> getSentences() {
		return sentences;
	}

	public String toString() {
		return text;
	}

	public void doPreprocessing(StanfordCoreNLP pipeline, GrammaticalStructureFactory gsf) {
		Annotation a = new Annotation(text.replace('-', ' '));
		pipeline.annotate(a);

		Map<Integer, Pair<TemporalSentence, Integer>> startCharIndexToTokenIndex = new HashMap<Integer, Pair<TemporalSentence, Integer>>();
		Map<Integer, Pair<TemporalSentence, Integer>> endCharIndexToTokenIndex = new HashMap<Integer, Pair<TemporalSentence, Integer>>();

		sentences = new LinkedList<TemporalSentence>();

		for(CoreMap sentence: a.get(SentencesAnnotation.class)) {
			TemporalSentence newSentence = new TemporalSentence(docID, referenceTime);
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				if (!(token.get(TextAnnotation.class).equals("-LRB-") || token.get(TextAnnotation.class).equals("-RRB-"))) {
					startCharIndexToTokenIndex.put(token.beginPosition(), Pair.of(newSentence, newSentence.getNumTokens()));
					endCharIndexToTokenIndex.put(token.endPosition(), Pair.of(newSentence, newSentence.getNumTokens()));
					newSentence.insertToken(token.get(TextAnnotation.class).toLowerCase());
				}
			}
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			String dp = EnglishGrammaticalStructure.dependenciesToString(gs, gs.typedDependencies(false), tree, true, false);
			newSentence.saveDependencyParse(dp);
			sentences.add(newSentence);
		}

		for(TemporalMention t : mentions) {
			if(t.getStartChar() != -1) {
				if(startCharIndexToTokenIndex.containsKey(t.getStartChar()) && endCharIndexToTokenIndex.containsKey(t.getEndChar())) {
					Pair<TemporalSentence, Integer> startIndexes = startCharIndexToTokenIndex.get(t.getStartChar());
					Pair<TemporalSentence, Integer> endIndexes = endCharIndexToTokenIndex.get(t.getEndChar());
					t.setTokenRange(startIndexes.second(), endIndexes.second() + 1);
					// Assume start and end occur in the same sentence
					startIndexes.first().insertMention(t);
				}
				else if (endCharIndexToTokenIndex.containsKey(t.getEndChar() + 1)) {
					// Hack to accommodate mistakes in annotation where "10 p.m", rather than "10 p.m." is labeled as the mention
					Pair<TemporalSentence, Integer> startIndexes = startCharIndexToTokenIndex.get(t.getStartChar());
					Pair<TemporalSentence, Integer> endIndexes = endCharIndexToTokenIndex.get(t.getEndChar() + 1);
					t.setTokenRange(startIndexes.second(), endIndexes.second() + 1);
					// Assume start and end occur in the same sentence
					startIndexes.first().insertMention(t);
				}
				else
					Debug.printf(Type.ERROR, "Unable to find offset for mention [#%d - #%d]: (%s)\n", t.getStartChar(), t.getEndChar(), t.getText());
			}
		}
	}

	public void setReferenceTime(String referenceTime) {
		this.referenceTime = referenceTime;
	}
	public String getReferenceTime() {
		return referenceTime;
	}
}
