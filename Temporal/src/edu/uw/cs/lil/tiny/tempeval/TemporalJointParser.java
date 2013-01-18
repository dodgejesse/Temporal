package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.AbstractParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.Pruner;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.composites.Pair;

/**
 * @author Jesse Dodge
 * @param <X>
 * 			  Sentence
 *            Type of the natural language structure (e.g., Sentence).
 * @param <W>
 * 			  Pair<String[], Pair<Sentence, TemporalSentence>>
 * 			  contains docID, complete sentence, refdate in the String[]
 * 			  phrase in the Sentence, and previous reference in the TemporalSentence
 *            Type of added input information (e.g., task, starting position,
 *            state of the world etc.).
 * @param <Y>
 * 			  LogicalExpression. 
 *            Type of meaning representation (e.g., LogicalExpression).
 * @param <Z>
 * 			  Pair<String, String>.
 * 			  pair.first() is the type, pair.second() is the value.
 *            Type of execution output.
 */

public class TemporalJointParser extends AbstractParser<Sentence, LogicalExpression> implements IJointParser<Sentence, 
Pair<String[], Pair<Sentence, TemporalSentence>>, LogicalExpression, Pair<String, String>> {
	
	AbstractCKYParser<LogicalExpression> baseParser;

	// Constructor instantiates (or takes?) a JointParser. I THINK!! Not sure.
	TemporalJointParser (AbstractCKYParser<LogicalExpression> baseParser){
		this.baseParser = baseParser;
	}
	
	@Override
	public IParserOutput<LogicalExpression> parse(IDataItem<Sentence> dataItem,
			Pruner<Sentence, LogicalExpression> pruner,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return baseParser.parse(dataItem, pruner, model, allowWordSkipping,
				tempLexicon, beamSize);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>>> dataItem,
			IJointDataItemModel<LogicalExpression, Pair<String, String>> model) {
		return parse(dataItem, model, false);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>>> dataItem,
			IJointDataItemModel<LogicalExpression, Pair<String, String>> model,
			boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>>> dataItem,
			IJointDataItemModel<LogicalExpression, Pair<String, String>> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, -1);
	}

	@Override
	public IJointOutput<LogicalExpression, Pair<String, String>> parse(
			IDataItem<Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>>> dataItem,
			IJointDataItemModel<LogicalExpression, Pair<String, String>> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon,
			Integer beamSize) {
		// TODO Auto-generated method stub
		return null;
	}
}
