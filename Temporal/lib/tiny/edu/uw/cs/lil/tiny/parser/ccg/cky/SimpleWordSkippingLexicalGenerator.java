package edu.uw.cs.lil.tiny.parser.ccg.cky;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ISentenceLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

/**
 * Generate an EMPTY lexical entry for each token.
 * 
 * @author Yoav Artzi
 */
public class SimpleWordSkippingLexicalGenerator<Y> implements
		ISentenceLexiconGenerator<Y> {
	public static final String			SKIPPING_LEXICAL_ORIGIN	= "skipping";
	
	private final ICategoryServices<Y>	categoryServices;
	
	public SimpleWordSkippingLexicalGenerator(
			ICategoryServices<Y> categoryServices) {
		this.categoryServices = categoryServices;
	}
	
	@Override
	public Set<LexicalEntry<Y>> generateLexicon(Sentence sample,
			Sentence evidence) {
		final Set<LexicalEntry<Y>> lexicalEntries = new HashSet<LexicalEntry<Y>>();
		final List<String> tokens = evidence.getTokens();
		for (int j = 0; j < tokens.size(); j++) {
			// Single token empty lexical entry
			lexicalEntries.add(new LexicalEntry<Y>(tokens.subList(j, j + 1),
					categoryServices.getEmptyCategory(),
					SKIPPING_LEXICAL_ORIGIN));
		}
		return lexicalEntries;
	}
	
}
