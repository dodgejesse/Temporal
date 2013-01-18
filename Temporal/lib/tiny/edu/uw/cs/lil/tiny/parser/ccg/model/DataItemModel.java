package edu.uw.cs.lil.tiny.parser.ccg.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexiconImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;

public class DataItemModel<X, Y> implements IDataItemModel<Y> {
	
	private final IDataItem<X>			dataItem;
	private final IModelImmutable<X, Y>	model;
	
	public DataItemModel(IModelImmutable<X, Y> model, IDataItem<X> dataItem) {
		this.model = model;
		this.dataItem = dataItem;
	}
	
	@Override
	public IHashVector computeFeatures(IParseStep<Y> parseStep) {
		return model.computeFeatures(parseStep, dataItem);
	}
	
	@Override
	public IHashVector computeFeatures(IParseStep<Y> parseStep,
			IHashVector features) {
		return model.computeFeatures(parseStep, features, dataItem);
	}
	
	@Override
	public IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry) {
		return model.computeFeatures(lexicalEntry);
	}
	
	@Override
	public IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry,
			IHashVector features) {
		return model.computeFeatures(lexicalEntry, features);
	}
	
	@Override
	public ILexiconImmutable<Y> getLexicon() {
		return model.getLexicon();
	}
	
	@Override
	public IHashVectorImmutable getTheta() {
		return model.getTheta();
	}
	
	@Override
	public double score(IParseStep<Y> parseStep) {
		return model.score(parseStep, dataItem);
	}
	
	@Override
	public double score(LexicalEntry<Y> entry) {
		return model.score(entry);
	}
	
}
