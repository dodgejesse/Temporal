package edu.uw.cs.lil.tiny.data.lexicalgen.singlesentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.DatasetException;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpressionRuntimeException;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.IsWellTyped;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.Simplify;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.IEvidenceLexicalGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ISentenceLexiconGenerator;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;

/**
 * Dataset of single sentences labeled with logical forms.
 * 
 * @author Yoav Artzi
 */
public class LexicalGenerationSingleSentenceDataset implements
		IDataCollection<LexicalGenerationSingleSentence> {
	private final List<LexicalGenerationSingleSentence>	data;
	
	public LexicalGenerationSingleSentenceDataset(
			List<LexicalGenerationSingleSentence> data) {
		this.data = Collections.unmodifiableList(data);
	}
	
	public static LexicalGenerationSingleSentenceDataset read(
			File f,
			IStringFilter textFilter,
			IEvidenceLexicalGenerator<Sentence, LogicalExpression, LogicalExpression> semanticsLexicalGeneration,
			List<ISentenceLexiconGenerator<LogicalExpression>> textLexicalGeneration,
			boolean lockConstants) {
		try {
			// Open the file
			final BufferedReader in = new BufferedReader(new FileReader(f));
			final List<LexicalGenerationSingleSentence> data = new LinkedList<LexicalGenerationSingleSentence>();
			
			String line;
			String currentSentence = null;
			int readLineCounter = 0;
			while ((line = in.readLine()) != null) {
				++readLineCounter;
				if (line.startsWith("//") || line.equals("")) {
					// Case comment or empty line, skip
					continue;
				}
				line = line.trim();
				if (currentSentence == null) {
					// Case we don't have a sentence, so we are supposed to get
					// a sentence
					currentSentence = textFilter.filter(line);
				} else {
					// Case we don't have a logical expression, so we are
					// supposed to get it and create the data item
					final LogicalExpression exp;
					try {
						exp = Simplify.of(LogicalExpression.parse(line,
								LogicLanguageServices.getTypeRepository(),
								LogicLanguageServices.getTypeComparator(),
								lockConstants));
					} catch (final LogicalExpressionRuntimeException e) {
						// wrap with a dataset exception and throw
						in.close();
						throw new DatasetException(e, readLineCounter,
								f.getName());
					}
					if (!IsWellTyped.of(exp)) {
						// Throw exception
						in.close();
						throw new DatasetException(
								"Expression not well-typed: " + exp,
								readLineCounter, f.getName());
					}
					data.add(new LexicalGenerationSingleSentence(new Sentence(
							currentSentence), exp, semanticsLexicalGeneration,
							textLexicalGeneration));
					currentSentence = null;
				}
			}
			
			in.close();
			
			return new LexicalGenerationSingleSentenceDataset(data);
		} catch (final IOException e) {
			// Wrap with dataset exception and throw
			throw new DatasetException(e);
		}
	}
	
	@Override
	public Iterator<LexicalGenerationSingleSentence> iterator() {
		return data.iterator();
	}
	
	@Override
	public int size() {
		return data.size();
	}
}