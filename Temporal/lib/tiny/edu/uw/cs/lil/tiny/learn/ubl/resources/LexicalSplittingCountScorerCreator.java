package edu.uw.cs.lil.tiny.learn.ubl.resources;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.learn.ubl.LexicalSplittingCountScorer;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.IUBLSplitter;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.AbstractScaledScorerCreator;

public class LexicalSplittingCountScorerCreator extends
		AbstractScaledScorerCreator<Lexeme, LexicalSplittingCountScorer> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexicalSplittingCountScorer createScorer(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new LexicalSplittingCountScorer.Builder(
				(IUBLSplitter) resourceRepo.getResource(parameters
						.get("splitter")),
				(IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>) resourceRepo
						.getResource(parameters.get("data")),
				(ICategoryServices<LogicalExpression>) resourceRepo
						.getResource("categoryServices")).build();
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lexeme.allsplits";
	}
	
}
