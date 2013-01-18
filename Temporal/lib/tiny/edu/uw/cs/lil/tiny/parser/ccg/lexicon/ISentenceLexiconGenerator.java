package edu.uw.cs.lil.tiny.parser.ccg.lexicon;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Generates lexical items from the sentence being parsed.
 * 
 * @author Yoav Artzi
 */
public interface ISentenceLexiconGenerator<Y> extends
		IEvidenceLexicalGenerator<Sentence, Y, Sentence> {
	
}
