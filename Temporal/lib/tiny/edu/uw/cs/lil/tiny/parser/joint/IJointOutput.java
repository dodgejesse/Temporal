package edu.uw.cs.lil.tiny.parser.joint;

import java.util.List;

import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.composites.Pair;

public interface IJointOutput<Y, Z> extends IParserOutput<Y> {
	
	/**
	 * All joint parses. Including failed executions.
	 * 
	 * @return
	 */
	List<? extends IJointParse<Y, Z>> getAllJointParses();
	
	/**
	 * All joint parses.
	 * 
	 * @param includeFails
	 *            exclude failed execution iff 'false'
	 * @return
	 */
	List<? extends IJointParse<Y, Z>> getAllJointParses(boolean includeFails);
	
	IParserOutput<Y> getBaseParserOutput();
	
	List<? extends IJointParse<Y, Z>> getBestJointParses();
	
	List<? extends IJointParse<Y, Z>> getBestJointParses(boolean includeFails);
	
	List<IJointParse<Y, Z>> getBestParsesFor(Pair<Y, Z> label);
	
	List<IJointParse<Y, Z>> getBestParsesForY(Y partialLabel);
	
	List<IJointParse<Y, Z>> getBestParsesForZ(Z partialLabel);
	
	List<LexicalEntry<Y>> getMaxLexicalEntries(Pair<Y, Z> label);
	
	/**
	 * Get all parses for the given result.
	 * 
	 * @param label
	 * @return null if no parse has this label
	 */
	List<IJointParse<Y, Z>> getParsesFor(Pair<Y, Z> label);
	
	List<IJointParse<Y, Z>> getParsesForY(Y partialLabel);
	
	List<IJointParse<Y, Z>> getParsesForZ(Z partialLabel);
}
