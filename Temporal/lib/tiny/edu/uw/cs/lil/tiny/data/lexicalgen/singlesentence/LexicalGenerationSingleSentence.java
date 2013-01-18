package edu.uw.cs.lil.tiny.data.lexicalgen.singlesentence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.data.lexicalgen.ILexicalGenerationLabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.SingleSentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.IEvidenceLexicalGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ISentenceLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;

public class LexicalGenerationSingleSentence extends SingleSentence
		implements
		ILexicalGenerationLabeledDataItem<Sentence, LogicalExpression, LogicalExpression> {
	
	private final IEvidenceLexicalGenerator<Sentence, LogicalExpression, LogicalExpression>	semanticsLexicalGeneration;
	private final List<ISentenceLexiconGenerator<LogicalExpression>>						textLexicalGenerators;
	
	public LexicalGenerationSingleSentence(
			Sentence sentence,
			LogicalExpression semantics,
			IEvidenceLexicalGenerator<Sentence, LogicalExpression, LogicalExpression> semanticsLexicalGeneration,
			List<ISentenceLexiconGenerator<LogicalExpression>> textLexicalGenerators) {
		super(sentence, semantics);
		this.semanticsLexicalGeneration = semanticsLexicalGeneration;
		this.textLexicalGenerators = textLexicalGenerators;
	}
	
	@Override
	public ILexicon<LogicalExpression> generateLexicon() {
		final Set<LexicalEntry<LogicalExpression>> generatedEntries = new HashSet<LexicalEntry<LogicalExpression>>(
				semanticsLexicalGeneration.generateLexicon(getSample(),
						getLabel()));
		for (final IEvidenceLexicalGenerator<Sentence, LogicalExpression, Sentence> generator : textLexicalGenerators) {
			generatedEntries.addAll(generator.generateLexicon(getSample(),
					getSample()));
		}
		return new Lexicon<LogicalExpression>(generatedEntries);
	}
	
}
