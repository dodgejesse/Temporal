package edu.uw.cs.lil.tiny.parser.ccg.model.init;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;

/**
 * Init a lexicon with a set lexical entries.
 * 
 * @author Yoav Artzi
 * @param <X>
 * @param <Y>
 */
public class LexiconModelInit<X, Y> implements IModelInit<X, Y> {
	
	private final boolean		fixed;
	private final ILexicon<Y>	lexicon;
	
	public LexiconModelInit(ILexicon<Y> lexicon, boolean fixed) {
		this.lexicon = lexicon;
		this.fixed = fixed;
	}
	
	@Override
	public void init(Model<X, Y> model) {
		if (fixed) {
			model.addFixedLexicalEntries(lexicon.toCollection());
		} else {
			model.addLexEntries(lexicon.toCollection());
		}
	}
	
}
