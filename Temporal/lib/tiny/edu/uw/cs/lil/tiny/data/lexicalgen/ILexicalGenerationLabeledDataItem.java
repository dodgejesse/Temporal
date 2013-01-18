package edu.uw.cs.lil.tiny.data.lexicalgen;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;

public interface ILexicalGenerationLabeledDataItem<X, Y, Z> extends
		ILabeledDataItem<X, Z>, ILexicalGenerationDataItem<X, Y> {
	
}
