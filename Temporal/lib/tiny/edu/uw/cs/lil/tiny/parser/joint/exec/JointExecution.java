package edu.uw.cs.lil.tiny.parser.joint.exec;

import edu.uw.cs.lil.tiny.exec.IExecution;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.composites.Pair;

public class JointExecution<Y, Z> implements IExecution<Pair<Y, Z>> {
	
	private final IJointDataItemModel<Y, Z>	dataItemModel;
	private final IJointParse<Y, Z>			jointParse;
	
	public JointExecution(IJointParse<Y, Z> jointParse,
			IJointDataItemModel<Y, Z> dataItemModel) {
		this.jointParse = jointParse;
		this.dataItemModel = dataItemModel;
	}
	
	private static <Y, Z> String lexToString(
			Iterable<LexicalEntry<Y>> lexicalEntries,
			IJointDataItemModel<Y, Z> model) {
		final StringBuilder ret = new StringBuilder();
		ret.append("[LexEntries and scores:\n");
		for (final LexicalEntry<Y> entry : lexicalEntries) {
			ret.append("[").append(model.score(entry)).append("] ");
			ret.append(entry);
			ret.append(" [");
			ret.append(model.getTheta().printValues(
					model.computeFeatures(entry)));
			ret.append("]\n");
		}
		ret.append("]");
		return ret.toString();
	}
	
	@Override
	public Pair<Y, Z> getResult() {
		return jointParse.getResult();
	}
	
	@Override
	public double score() {
		return jointParse.getScore();
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	@Override
	public String toString(boolean verbose) {
		final StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("[S%.2f] %s", score(), getResult()));
		if (verbose) {
			sb.append('\n');
			sb.append(String.format("Features: %s\n", dataItemModel.getTheta()
					.printValues(jointParse.getAverageMaxFeatureVector())));
			sb.append(lexToString(jointParse.getMaxLexicalEntries(),
					dataItemModel));
		}
		
		return sb.toString();
	}
	
}
