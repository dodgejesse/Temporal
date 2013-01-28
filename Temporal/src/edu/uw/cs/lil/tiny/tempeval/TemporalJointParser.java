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
import edu.uw.cs.lil.tiny.parser.joint.JointParse;
import edu.uw.cs.lil.tiny.parser.joint.SingleExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
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

public class TemporalJointParser extends
		AbstractParser<Sentence, LogicalExpression>
		implements
		IJointParser<Sentence, String[], LogicalExpression, LogicalExpression, Pair<String, String>> {

	private AbstractCKYParser<LogicalExpression> baseParser;
	private final LogicalExpressionCategoryServices categoryServices;
	private TemporalISO prevISO;

	// Constructor takes the CKY parser.
	TemporalJointParser(AbstractCKYParser<LogicalExpression> baseParser) {
		this.baseParser = baseParser;
		categoryServices = new LogicalExpressionCategoryServices();
		prevISO = null;
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
	private LogicalExpression[] getArrayOfLabels(LogicalExpression l,
			boolean sameDocID) {
		int numOfFunctions = 4;
		LogicalExpression[] newLogicArray = new LogicalExpression[numOfFunctions + 1];
		LogicalExpression[] functionsS = new LogicalExpression[numOfFunctions];
		LogicalExpression[] functionsD = new LogicalExpression[numOfFunctions];
		// Making the Predicates to apply to the logical expressions for
		// SEQUENCES
		functionsS[0] = categoryServices
				.parseSemantics("(lambda $0:s (previous:<s,<r,s>> $0 ref_time:r))");
		functionsS[1] = categoryServices
				.parseSemantics("(lambda $0:s (this:<s,<r,s>> $0 ref_time:r))");
		functionsS[2] = categoryServices
				.parseSemantics("(lambda $0:s (next:<s,<r,s>> $0 ref_time:r))");
		if (sameDocID)
			functionsS[3] = categoryServices
					.parseSemantics("(lambda $0:s (temporal_ref:<s,s> $0))");
		else
			functionsS[3] = l;

		// Making the Predicates to apply to the logical expressions for
		// DURATIONS
		functionsD[0] = categoryServices
				.parseSemantics("(lambda $0:d (previous:<d,<r,s>> $0 ref_time:r))");
		functionsD[1] = categoryServices
				.parseSemantics("(lambda $0:d (this:<d,<r,s>> $0 ref_time:r))");
		functionsD[2] = categoryServices
				.parseSemantics("(lambda $0:d (next:<d,<r,s>> $0 ref_time:r))");
		if (sameDocID)
			functionsD[3] = categoryServices
					.parseSemantics("(lambda $0:d (temproal_ref:<d,s> $0))");
		else
			functionsD[3] = l;

		// Looping over the predicates, applying them each to the given logical
		// expression
		for (int i = 0; i < functionsS.length; i++) {
			newLogicArray[i + 1] = categoryServices.doSemanticApplication(
					functionsS[i], l);

			if (newLogicArray[i + 1] == null) {
				newLogicArray[i + 1] = categoryServices.doSemanticApplication(
						functionsD[i], l);
			}

			if (newLogicArray[i + 1] == null)
				newLogicArray[i + 1] = l;
		}
		newLogicArray[0] = l;

		return newLogicArray;
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, String[]>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model) {
		return parse(dataItem, model, false);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, String[]>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, String[]>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, -1);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, String[]>> dataItem,
			IJointDataItemModel<LogicalExpression, LogicalExpression> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon,
			Integer beamSize) {

		boolean sameDocID = dataItem.getSample().second()[0].equals(dataItem
				.getSample().second()[3]) && prevISO != null;

		Sentence phrase = dataItem.getSample().first();
		IParserOutput<LogicalExpression> CKYParserOutput = baseParser.parse(
				phrase, model);
		final List<IParseResult<LogicalExpression>> CKYModelParses = pruneLogicWithLambdas(CKYParserOutput
				.getBestParses());

		List<IJointParse<LogicalExpression, Pair<String, String>>> allExecutedParses = new ArrayList<IJointParse<LogicalExpression, Pair<String, String>>>();
		long startTime = System.currentTimeMillis();

		for (IParseResult<LogicalExpression> l : CKYModelParses) {
			LogicalExpression[] labels = getArrayOfLabels(l.getY(), sameDocID);
			for (int i = 0; i < labels.length; i++) {

				// execute the logical form to get a final Pair<String, String>.
				// score the logical form.
				// create a TemporalExecResultWrapper class using the
				// Pair<String, String> and the score.
				// use that wrapper to create
				String ref_time = dataItem.getSample().second()[2];
				TemporalISO tmp = TemporalVisitor.of(labels[i], ref_time,
						prevISO);
				SingleExecResultWrapper<LogicalExpression, Pair<String, String>> wrapper = new SingleExecResultWrapper<LogicalExpression, Pair<String, String>>(
						labels[i], model, Pair.of(tmp.getType(), tmp.getVal()));

				IJointParse<LogicalExpression, Pair<String, String>> jp = new JointParse<LogicalExpression, Pair<String, String>>(
						l, wrapper);
				allExecutedParses.add(jp);
			}

		}

		long parsingTime = System.currentTimeMillis() - startTime;
		JointOutput<LogicalExpression, Pair<String, String>> out = new JointOutput<LogicalExpression, Pair<String, String>>(
				CKYParserOutput, allExecutedParses, parsingTime);
		
		return out;

	}

}
