package edu.uw.cs.lil.tiny.tempeval;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.learn.simple.joint.JointSimplePerceptron;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.SimpleFullParseFilter;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.single.CKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.SkippingSensitiveLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.UniformScorer;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.rules.RuleSetBuilder;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.BackwardSkippingRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.ForwardSkippingRule;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalContextFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalDayOfWeekFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalReferenceFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMentionDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;
import edu.uw.cs.lil.tiny.utils.string.StubStringFilter;

public class TemporalEvaluation extends Thread {
	private static final String RESOURCES_DIR = "resources/";
	private static final int PERCEPTRON_ITERATIONS = 10;

	final private TemporalDataset trainData, testData;
	final private TemporalJointParser jointParser;
	final private LexicalFeatureSet<Sentence, LogicalExpression> lexPhi;
	final private ILexicon<LogicalExpression> fixed;
	final private TemporalStatistics stats;

	static {
		LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(
				new TypeRepository(new File(RESOURCES_DIR + "tempeval.types.txt"))).setNumeralTypeName("i")
				.setTypeComparator(new FlexibleTypeComparator()).build());
	}

	public TemporalEvaluation(TemporalDataset trainData, TemporalDataset testData, TemporalStatistics stats){
		this.trainData = trainData;
		this.testData = testData;
		this.stats = stats;


		LogicalExpressionCategoryServices categoryServices = new LogicalExpressionCategoryServices();
		fixed = getFixedLexicon(categoryServices);
		lexPhi = getLexPhi(categoryServices);
		jointParser = new TemporalJointParser(getParser(categoryServices));
	}

	public ILexicon<LogicalExpression> getFixedLexicon(ICategoryServices<LogicalExpression> categoryServices) {
		final ILexicon<LogicalExpression> fixedInput = new Lexicon<LogicalExpression>();
		fixedInput.addEntriesFromFile(new File(RESOURCES_DIR
				+ "tempeval.lexicon.txt"), new StubStringFilter(),
				categoryServices, LexicalEntry.Origin.FIXED_DOMAIN);

		final ILexicon<LogicalExpression> fixed = new Lexicon<LogicalExpression>();
		for (final LexicalEntry<LogicalExpression> lex : fixedInput.toCollection()){
			fixed.add(lex);
		}
		return fixed;
	}

	public LexicalFeatureSet<Sentence, LogicalExpression> getLexPhi(ICategoryServices<LogicalExpression> categoryServices) {
		return new LexicalFeatureSet.Builder<Sentence,LogicalExpression>()
				.setInitialFixedScorer(
						new ExpLengthLexicalEntryScorer<LogicalExpression>(
								10.0, 1.1))
								.setInitialScorer(
										new SkippingSensitiveLexicalEntryScorer<LogicalExpression>(
												categoryServices,
												-1.0,
												new UniformScorer<LexicalEntry<LogicalExpression>>(
														0.0)))
														.build();
	}

	public AbstractCKYParser<LogicalExpression> getParser (ICategoryServices<LogicalExpression> categoryServices) {
		final RuleSetBuilder<LogicalExpression> ruleSetBuilder = new RuleSetBuilder<LogicalExpression>();
		// Binary rules
		ruleSetBuilder.add(new ForwardComposition<LogicalExpression>(
				categoryServices));
		ruleSetBuilder.add(new BackwardComposition<LogicalExpression>(
				categoryServices));
		ruleSetBuilder.add(new ForwardApplication<LogicalExpression>(
				categoryServices));
		ruleSetBuilder.add(new BackwardApplication<LogicalExpression>(
				categoryServices));

		final Set<Syntax> syntaxSet = new HashSet<Syntax>();
		syntaxSet.add(Syntax.NP);
		final SimpleFullParseFilter<LogicalExpression> parseFilter = new SimpleFullParseFilter<LogicalExpression>(
				syntaxSet);
		return new CKYParser.Builder<LogicalExpression>(
				categoryServices, parseFilter)//, executor)
				.addBinaryParseRule(
						new CKYBinaryParsingRule<LogicalExpression>(
								ruleSetBuilder.build()))
								.addBinaryParseRule(
										new CKYBinaryParsingRule<LogicalExpression>(
												new ForwardSkippingRule<LogicalExpression>(
														categoryServices)))
														.addBinaryParseRule(
																new CKYBinaryParsingRule<LogicalExpression>(
																		new BackwardSkippingRule<LogicalExpression>(
																				categoryServices)))
																				.setMaxNumberOfCellsInSpan(100).build();


	}

	private JointModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression> learnModel(TemporalDataset dataset) {
		TemporalMentionDataset observations = dataset.getMentions();
		JointSimplePerceptron<Sentence, TemporalMention, LogicalExpression, LogicalExpression, TemporalResult> learner = 
				new JointSimplePerceptron<Sentence, TemporalMention, LogicalExpression, LogicalExpression, TemporalResult>(
						PERCEPTRON_ITERATIONS, observations, jointParser);
		JointModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression> model = new JointModel.Builder<Sentence, TemporalMention, LogicalExpression, LogicalExpression>()
				.addJointFeatureSet(new TemporalContextFeatureSet())
				.addJointFeatureSet(new TemporalReferenceFeatureSet())
				.addJointFeatureSet(new TemporalDayOfWeekFeatureSet())
				.addLexicalFeatureSet(lexPhi)
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		model.addFixedLexicalEntries(fixed.toCollection());
		learner.train(model);
		return model;
	}


	public void run(){		
		JointModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression> model = learnModel(trainData);
		TemporalMentionDataset attributeData;
		if (!TemporalMain.GOLD_MENTIONS) {
			TemporalDetectionTester detectionTester = new TemporalDetectionTester (testData, jointParser, model);
			detectionTester.test(stats);
			attributeData = detectionTester.getCorrectObservations();
		}
		else
			attributeData = testData.getMentions();

		TemporalAttributeTester attributeTester = TemporalAttributeTester.build(attributeData, jointParser);
		attributeTester.test(model, stats);
	}
}