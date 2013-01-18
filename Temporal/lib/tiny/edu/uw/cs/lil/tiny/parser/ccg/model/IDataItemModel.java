package edu.uw.cs.lil.tiny.parser.ccg.model;

import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexiconImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;

public interface IDataItemModel<Y> {
	
	IHashVector computeFeatures(IParseStep<Y> parseStep);
	
	IHashVector computeFeatures(IParseStep<Y> parseStep, IHashVector features);
	
	IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry);
	
	IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry,
			IHashVector features);
	
	ILexiconImmutable<Y> getLexicon();
	
	IHashVectorImmutable getTheta();
	
	double score(IParseStep<Y> parseStep);
	
	double score(LexicalEntry<Y> entry);
	
}
