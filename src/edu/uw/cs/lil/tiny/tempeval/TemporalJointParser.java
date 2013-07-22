package edu.uw.cs.lil.tiny.tempeval;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.parser.AbstractParser;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.Pruner;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.JointOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;
import edu.uw.cs.utils.composites.Pair;

/**
 * @author Jesse Dodge and Yoav Artsi
 * @param <X>
 *            Sentence Type of the natural language structure (e.g., Sentence).
 * @param <W>
 *            String[] contains docID, complete sentence, refdate in the
 *            String[] phrase in the Sentence, and previous reference in the
 *            TemporalSentence Type of added input information (e.g., task,
 *            starting position, state of the world etc.).
 * @param <Y>
 *            LogicalExpression. Type of meaning representation (e.g.,
 *            LogicalExpression).
 * @param <Z>
 *            Pair<String, String>. pair.first() is the type, pair.second() is
 *            the value. Type of execution output.
 */

public class TemporalJointParser extends AbstractParser<Sentence, LogicalExpression> implements IJointParser<Sentence, TemporalMention, LogicalExpression, LogicalExpression, TemporalResult> {

	private AbstractCKYParser<LogicalExpression> baseParser;
	private final LogicalExpressionCategoryServices categoryServices;
	private TemporalISO prevISO;
	private String prevDocID;

	// Constructor takes the CKY parser.
	TemporalJointParser(AbstractCKYParser<LogicalExpression> baseParser) {
		this.baseParser = baseParser;
		categoryServices = new LogicalExpressionCategoryServices();
		prevISO = null;
		prevDocID = "";
	}

	@Override
	public IParserOutput<LogicalExpression> parse(IDataItem<Sentence> dataItem,
			Pruner<Sentence, LogicalExpression> pruner,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return baseParser.parse(dataItem, pruner, model, allowWordSkipping,
				tempLexicon, beamSize);
	}

	// To prune the parses that have lambdas. My logical expressions shouldn't
	// have a complex type, or they wont execute.
	private List<IParseResult<LogicalExpression>> pruneLogicWithLambdas(
			List<IParseResult<LogicalExpression>> bestModelParses) {
		List<IParseResult<LogicalExpression>> newBestModelParses = new ArrayList<IParseResult<LogicalExpression>>();
		for (IParseResult<LogicalExpression> p : bestModelParses) {
			if (!p.getY().getType().isComplex())
				newBestModelParses.add(p);
		}
		return newBestModelParses;
	}

	/*
	 * To perform the first step of the execution - getting all possible
	 * context-dependent logical forms.
	 */
	private List<LogicalExpression> getLabels(LogicalExpression l, boolean sameDocID) {
		List<LogicalExpression> expressions = new ArrayList<LogicalExpression>(8);
		expressions.add(l);
		if (!logicStartsWithContextDependentPredicate(l)){
			addFunction("(lambda $0:s (previous:<s,<r,s>> $0 ref_time:r))", l, expressions);
			addFunction("(lambda $0:s (this:<s,<r,s>> $0 ref_time:r))", l, expressions);
			addFunction("(lambda $0:s (next:<s,<r,s>> $0 ref_time:r))", l, expressions);
			if (sameDocID)
				addFunction("(lambda $0:d (temporal_ref:<d,s> $0))", l, expressions);
		}
		return expressions;
	}

	private void addFunction(String semantics, LogicalExpression l, List<LogicalExpression> expressions) {			
		LogicalExpression e = categoryServices.doSemanticApplication(categoryServices.parseSemantics(semantics), l);
		if (e != null)
			expressions.add(e);
	}


	private boolean logicStartsWithContextDependentPredicate(LogicalExpression l){
		return (l.toString().startsWith("(previous:") || 
				l.toString().startsWith("(this:") ||
				l.toString().startsWith("(next:"));
	}

	@Override
	public IJointOutput<LogicalExpression, TemporalResult> parse(
			IDataItem<Pair<Sentence, TemporalMention>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model) {
		return parse(dataItem, model, false);
	}

	@Override
	public IJointOutput<LogicalExpression, TemporalResult> parse(
			IDataItem<Pair<Sentence, TemporalMention>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}

	@Override
	public IJointOutput<LogicalExpression, TemporalResult> parse(
			IDataItem<Pair<Sentence, TemporalMention>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, -1);
	}

	// this is where the parsing happens. 
	@Override
	public IJointOutput<LogicalExpression, TemporalResult> parse(
			IDataItem<Pair<Sentence, TemporalMention>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon,
			Integer beamSize) {

		TemporalSentence ts = dataItem.getSample().second().getSentence();
		String docID = ts.getDocID();
		boolean hasPreviousISO = prevDocID.equals(docID) && prevISO != null;
		if (!hasPreviousISO)
			prevISO = null;
		Sentence phrase = dataItem.getSample().first();
		IParserOutput<LogicalExpression> CKYParserOutput = baseParser.parse(phrase, model);

		final List<IParseResult<LogicalExpression>> CKYModelParses = pruneLogicWithLambdas(CKYParserOutput
				.getBestParses());

		List<IJointParse<LogicalExpression, TemporalResult>> allExecutedParses = 
				new ArrayList<IJointParse<LogicalExpression, TemporalResult>>();

		long startTime = System.currentTimeMillis();

		TemporalISO bestISO = null;
		double bestScore = -Double.MAX_VALUE;

		for (IParseResult<LogicalExpression> parseResult : CKYModelParses) {
			for(LogicalExpression label : getLabels(parseResult.getY(), hasPreviousISO)) {
				TemporalISO iso;
				try {
					iso = TemporalVisitor.of(label, ts.getReferenceTime(), prevISO);
				}
				catch (ClassCastException e) {
					System.out.println("Error executing: " + label);
					throw e;
				}
				if  (iso != null) {
					TemporalResult tr = new TemporalResult(label, iso.getType(), iso.getVal(), parseResult.getAllLexicalEntries(), model, parseResult);
					IJointParse<LogicalExpression, TemporalResult> jp = tr.getJointParse();
					if (jp.getScore() > bestScore) {
						bestScore = jp.getScore();
						bestISO = iso;
					}
					allExecutedParses.add(jp);
				}
			}
		}

		if (bestISO != null) {
			prevISO = bestISO;
			prevDocID = docID;
		}
		else { 
			prevISO = null;
			prevDocID = "";
		}

		long parsingTime = System.currentTimeMillis() - startTime;
		JointOutput<LogicalExpression, TemporalResult> out = new JointOutput<LogicalExpression, TemporalResult>(
				CKYParserOutput, allExecutedParses, parsingTime);
		return out;
	}

}
