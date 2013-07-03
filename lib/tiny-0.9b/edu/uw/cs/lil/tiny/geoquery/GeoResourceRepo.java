package edu.uw.cs.lil.tiny.geoquery;

import edu.uw.cs.lil.learn.simple.genlex.resources.FactoredGENLEXPerceptronCreator;
import edu.uw.cs.lil.tiny.data.resources.CompositeDatasetCreator;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.resources.SingleSentenceDatasetCreator;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.ExpLengthLexemeScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.FactoredLexiconCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.FactoredUniformScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexemeCooccurrenceScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexemeFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexicalEntryLexemeBasedScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexicalTemplateFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.ExpLengthLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.LexicalFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.OriginLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.RuleUsageFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.SkippingSensitiveLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.UniformScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionCooccurrenceFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionCoordinationFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionTypeFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.resources.CompositeLexiconCreator;
import edu.uw.cs.lil.tiny.parser.resources.LexiconCreator;
import edu.uw.cs.lil.tiny.parser.resources.ModelCreator;
import edu.uw.cs.lil.tiny.parser.resources.ModelLoggerCreator;
import edu.uw.cs.lil.tiny.parser.resources.SavedModelCreator;
import edu.uw.cs.lil.tiny.test.resources.TesterCreator;

public class GeoResourceRepo extends ResourceCreatorRepository {
	
	public GeoResourceRepo() {
		super();
		registerResourceCreator(new SingleSentenceDatasetCreator());
		registerResourceCreator(new UniformScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExpLengthLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExpLengthLexemeScorerCreator());
		registerResourceCreator(new FactoredUniformScorerCreator());
		registerResourceCreator(new LexemeFeatureSetCreator<Sentence>());
		registerResourceCreator(new LexicalFeatureSetCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new LexicalTemplateFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionCoordinationFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionCooccurrenceFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionTypeFeatureSetCreator<Sentence>());
		registerResourceCreator(new LexemeCooccurrenceScorerCreator());
		registerResourceCreator(new SkippingSensitiveLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new LexicalEntryLexemeBasedScorerCreator());
		registerResourceCreator(new LexiconCreator<LogicalExpression>());
		registerResourceCreator(new CompositeLexiconCreator<LogicalExpression>());
		registerResourceCreator(new OriginLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new FactoredLexiconCreator());
		registerResourceCreator(new ModelLoggerCreator());
		registerResourceCreator(new ModelCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new SavedModelCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new CompositeDatasetCreator<Sentence>());
		registerResourceCreator(new FactoredGENLEXPerceptronCreator());
		registerResourceCreator(new TesterCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new RuleUsageFeatureSetCreator<Sentence, LogicalExpression>());
		
	}
	
}
