package edu.uw.cs.lil.tiny.parser.ccg.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexiconImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;

/**
 * Immutable parsing model.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            type of sample
 * @param <Y>
 *            Type of semantics.
 */
public interface IModelImmutable<X, Y> {
	
	/**
	 * Compute features for a given parsing step
	 * 
	 * @param parseStep
	 *            Parsing step to compute features for.
	 * @return
	 */
	IHashVector computeFeatures(IParseStep<Y> parseStep, IDataItem<X> dataItem);
	
	/**
	 * Compute features for a given parsing step.
	 * 
	 * @param parseStep
	 *            Parsing step to compute features for.
	 * @param features
	 *            Feature vector to load with features. The features will be
	 *            added to the given vector.
	 * @return 'features' vector
	 */
	IHashVector computeFeatures(IParseStep<Y> parseStep, IHashVector features,
			IDataItem<X> dataItem);
	
	/**
	 * Compute features for a lexical item,
	 * 
	 * @param lexicalEntry
	 *            Lexical entry to compute features for.
	 * @return
	 */
	IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry);
	
	/**
	 * Compute feature for a lexical item.
	 * 
	 * @param lexicalEntry
	 *            Lexical entry to compute features for
	 * @param features
	 *            Feature vector to load with features. The features will be
	 *            added to the given vector.
	 * @return the 'features' vector
	 */
	IHashVector computeFeatures(LexicalEntry<Y> lexicalEntry,
			IHashVector features);
	
	IDataItemModel<Y> createDataItemModel(IDataItem<X> dataItem);
	
	/** Return the lexicon of the model. The returned lexicon is immutable. */
	ILexiconImmutable<Y> getLexicon();
	
	/**
	 * @return Parameters vectors (immutable).
	 */
	IHashVectorImmutable getTheta();
	
	double score(IParseStep<Y> parseStep, IDataItem<X> dataItem);
	
	double score(LexicalEntry<Y> entry);
}
