package edu.uw.cs.lil.tiny.parser.ccg.model.lexical;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.ILexicalParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

public abstract class AbstractLexicalFeatureSet<X, Y> implements
		IIndependentLexicalFeatureSet<X, Y> {
	
	@Override
	public final double score(IParseStep<Y> obj, IHashVector theta,
			IDataItem<X> dataItem) {
		if (obj instanceof ILexicalParseStep) {
			return score(((ILexicalParseStep<Y>) obj).getLexicalEntry(), theta);
		} else {
			return 0;
		}
	}
	
	@Override
	public final void setFeats(IParseStep<Y> obj, IHashVector features,
			IDataItem<X> dataItem) {
		if (obj instanceof ILexicalParseStep) {
			setFeats(((ILexicalParseStep<Y>) obj).getLexicalEntry(), features);
		}
	}
}
