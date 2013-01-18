package edu.uw.cs.lil.tiny.parser.ccg.genlex;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

public interface ILexiconGenerator<X extends IDataItem<Sentence>, Y> {
	public static final String	GENLEX_LEXICAL_ORIGIN	= "genlex";
	
	ILexicon<Y> generate(X dataItem);
}
