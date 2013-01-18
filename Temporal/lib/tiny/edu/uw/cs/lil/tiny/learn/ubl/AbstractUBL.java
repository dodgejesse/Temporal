package edu.uw.cs.lil.tiny.learn.ubl;

import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.learn.ILearner;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.IUBLSplitter;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.test.Tester;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * UBL code shared by the different UBL learners..
 * 
 * @author Yoav Artzi
 */
public abstract class AbstractUBL
		implements
		ILearner<Sentence, LogicalExpression, Model<Sentence, LogicalExpression>> {
	public static final String																	SPLITTING_LEXICAL_ORIGIN	= "splitting";
	
	private static final ILogger																LOG							= LoggerFactory
																																	.create(AbstractUBL.class);
	
	protected final ICategoryServices<LogicalExpression>										categoryServices;
	
	protected final boolean																		expandLexicon;
	
	protected final int																			maxSentenceLength;
	
	protected final AbstractCKYParser<LogicalExpression>										parser;
	
	protected final IUBLSplitter																splitter;
	
	protected final Tester<Sentence, LogicalExpression>											tester;
	
	protected final IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>	trainData;
	
	AbstractUBL(
			Tester<Sentence, LogicalExpression> tester,
			IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>> train,
			ICategoryServices<LogicalExpression> categoryServices,
			boolean expandLexicon, int maxSentLen, IUBLSplitter splitter,
			AbstractCKYParser<LogicalExpression> parser) {
		this.tester = tester;
		this.expandLexicon = expandLexicon;
		this.categoryServices = categoryServices;
		this.trainData = train;
		this.maxSentenceLength = maxSentLen;
		this.splitter = splitter;
		this.parser = parser;
	}
	
	protected static String lexToString(
			Iterable<LexicalEntry<LogicalExpression>> lexicalEntries,
			Model<Sentence, LogicalExpression> model) {
		final StringBuilder ret = new StringBuilder();
		ret.append("[LexEntries and scores:\n");
		
		for (final LexicalEntry<LogicalExpression> entry : lexicalEntries) {
			ret.append("[").append(model.score(entry)).append("] ");
			ret.append(entry).append(" [");
			ret.append(model.getTheta().printValues(
					model.computeFeatures(entry)));
			ret.append("]\n");
		}
		ret.append("]");
		return ret.toString();
	}
	
	public IParseResult<LogicalExpression> getSingleBestParseFor(
			LogicalExpression label,
			IParserOutput<LogicalExpression> parserOutput) {
		// TODO [yoav] Currently skipping in case of ambiguity
		final List<IParseResult<LogicalExpression>> maxParses = parserOutput
				.getMaxParses(label);
		if (maxParses.size() == 1) {
			return maxParses.get(0);
		} else {
			if (!maxParses.isEmpty()) {
				LOG.info("Best parses ambiguity, returning null");
			}
			return null;
		}
	}
}
