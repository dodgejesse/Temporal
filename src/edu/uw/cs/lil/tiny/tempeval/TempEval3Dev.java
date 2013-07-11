package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
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
import edu.uw.cs.lil.tiny.tempeval.structures.GoldSentence;
import edu.uw.cs.lil.tiny.utils.string.StubStringFilter;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.LogLevel;
import edu.uw.cs.utils.log.Logger;
import edu.uw.cs.utils.log.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TempEval3Dev {
	private static final ILogger LOG = LoggerFactory.create(TempEval3Dev.class);

	// Files
	private static final String DEBUG_DATASET = "tempeval3.debug.txt";
	private static final String FULL_DATASET = "tempeval3.aquaintAndTimebank.txt";
	private static final String AQ_DATASET = "tempeval3.timebank.txt";
	private static final String TB_DATASET = "tempeval3.aquaint.txt";
	private static final String SERIALIZED_TRAINING = "trainingData.ser";
	private static final String SERIALIZED_TESTING = "testingData.ser";
	
	// Directories
	private static final String DATASET_DIR = "data/dataset/";
	private static final String RESOURCES_DIR = "data/resources/";
	private static final String SERIALIZED_DIR = "data/serialized_data/";

	// Flags
	private static final String DATASET = DEBUG_DATASET;
	//private static final String DATASET = FULL_DATASET;
	private static final boolean READ_SERIALIZED_DATASETS = true; // this takes precedence over booleans testingDataset, timebank, and crossVal.
	private static final boolean SERIALIZE_DATASETS = false;
	private static final boolean CROSS_VAL = true;
	private static final int PERCEPTRON_ITERATIONS = 1;
	private static final double CV_FOLDS = 10;

	public static List<List<GoldSentence>> getCVPartitions(TemporalSentenceDataset dataset, double numberOfPartitions) {
		// make a list
		// use the constructor with TemporalSentenceDataset to make a new dataset. 
		System.out.println("Splitting the data...");
		List<List<GoldSentence>> splitData = new LinkedList<List<GoldSentence>>();
		Iterator<GoldSentence> iter = dataset.iterator();
		int sentenceCount = 1;
		List<GoldSentence> tmp = new LinkedList<GoldSentence>();

		while (iter.hasNext()){
			tmp.add(iter.next());
			// for testing:
			if (sentenceCount % Math.round(dataset.size() / numberOfPartitions) == 0){
				splitData.add(tmp);
				tmp = new LinkedList<GoldSentence>();
				System.out.println();
				System.out.println("sentenceCount: " + sentenceCount);
				System.out.println("Train size: " + dataset.size());
				System.out.println("size / " + numberOfPartitions + ": " + Math.round(dataset.size() / numberOfPartitions));
			}
			sentenceCount++;
		}
		System.out.println(" done.");
		return splitData;
	}

	public static TemporalSentenceDataset getSerializedData(String datasetName) throws IOException, ClassNotFoundException {
		return TemporalSentenceDataset.readSerialized(SERIALIZED_DIR + datasetName);
	}

	public static TemporalSentenceDataset getData(String datasetName, String serializedName) throws FileNotFoundException, IOException {
		//dataLoc = "tempeval.dataset.corrected.txt";
		// these train and test should be of type
		// IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>> 
		// reading in the serialized datasets so we don't have to run the dependency parser again.

		TemporalSentenceDataset dataset = TemporalSentenceDataset
				.read(new File(DATASET_DIR + datasetName),
						new StubStringFilter(), true);

		if (SERIALIZE_DATASETS){
			System.out.print("Serializing the training data... ");
			TemporalSentenceDataset.save(SERIALIZED_DIR + serializedName, dataset);
			System.out.println("Done!");
		}
		LOG.info(datasetName + " size: " + dataset.size());
		return dataset;
	}

	public static ILexicon<LogicalExpression> getFixedLexicon(ICategoryServices<LogicalExpression> categoryServices) {
		// Init the lexicon
		// final Lexicon<LogicalExpression> fixed = new
		// Lexicon<LogicalExpression>();
		final ILexicon<LogicalExpression> fixedInput = new Lexicon<LogicalExpression>();
		fixedInput.addEntriesFromFile(new File(RESOURCES_DIR
				+ "tempeval.lexicon.txt"), new StubStringFilter(),
				categoryServices, LexicalEntry.Origin.FIXED_DOMAIN);

		final ILexicon<LogicalExpression> fixed = new Lexicon<LogicalExpression>();

		// factor the fixed lexical entries
		//for (final LexicalEntry<LogicalExpression> lex : fixedInput
		//		.toCollection()) {
		//	fixed.add(FactoredLexicon.factor(lex));
		//}
		//System.out.println(fixed);

		// try two at factoring the fixed lexical entries:
		for (final LexicalEntry<LogicalExpression> lex : fixedInput.toCollection()){
			fixed.add(lex);
		}
		return fixed;
	}

	public static LexicalFeatureSet<Sentence, LogicalExpression> getLexPhi(ICategoryServices<LogicalExpression> categoryServices) {
		// Below is the code from Geo880DevParameterEstimation. I am replacing this with code from Geo880Dev.
		/*
		// Init the lexicon
		final ILexicon<LogicalExpression> lexicon = new Lexicon<LogicalExpression>();
		lexicon.addEntriesFromFile(
				new File(
						resourcesDir + "tempeval.lexicon.txt"),
				new StubStringFilter(), categoryServices,
				EntryOrigin.FIXED_DOMAIN);

		LOG.info("Size of lexicon: %d", lexicon.size());

		LOG.info("Size of lexicon: %d", lexicon.size());

		final LexicalFeatureSet<LogicalExpression> lexPhi = new LexicalFeatureSetBuilder<LogicalExpression>()
				.setInitialFixedScorer(
						new ExpLengthLexicalEntryScorer<LogicalExpression>(
								10.0, 1.1))
				.setInitialScorer(
						new SkippingSensitiveLexicalEntryScorer<LogicalExpression>(
								categoryServices,
								-1.0,
								new UniformScorer<LexicalEntry<LogicalExpression>>(
										0.0))).build();

		// Create the model
		final Model<Sentence, LogicalExpression> model = new Model.Builder<Sentence, LogicalExpression>()
				.addParseFeatureSet(
						new LogicalExpressionCoordinationFeatureSet<Sentence>())
				.addParseFeatureSet(
						new LogicalExpressionTypeFeatureSet<Sentence>())
				.addLexicalFeatureSet(lexPhi)
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		 */
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
														// .setInitialWeightScorer(gizaScores)
														.build();
	}
	
	public static AbstractCKYParser<LogicalExpression> getParser (ICategoryServices<LogicalExpression> categoryServices) {
		// Create the lexeme feature set
		//final LexemeCooccurrenceScorer gizaScores;
		// final DecoderHelper<LogicalExpression> decoderHelper = new
		// DecoderHelper<LogicalExpression>(
		// categoryServices);
		//try {
		//	gizaScores = new LexemeCooccurrenceScorer(new File(resourcesDir
		//			+ "/geo600.dev.giza_probs"));
		//} catch (final IOException e) {
		//	System.err.println(e);
		//	throw new RuntimeException(e);
		//}
		//final LexemeFeatureSet lexemeFeats = new LexemeFeatureSet.Builder()
		//		.setInitialFixedScorer(new UniformScorer<Lexeme>(0.0))
		//		.setInitialScorer(new ScalingScorer<Lexeme>(10.0, gizaScores))
		//		.build();

		// This was used for initializing the factored lexicon
		//final LexicalTemplateFeatureSet templateFeats = new LexicalTemplateFeatureSet.Builder()
		//.setScale(0.1)
		// .setInitialWeightScorer(new LexicalSyntaxPenaltyScorer(-0.1))
		//.build();

		// Create the entire feature collection
		// Adjusted to move away from a factored lexicon

		// Parsing rules
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

		// Executor for multi-threading
		//final TinyExecutorService executor = new TinyExecutorService(Runtime
		//		.getRuntime().availableProcessors());

		LOG.info("Using %d threads", Runtime.getRuntime().availableProcessors());

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

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Logger.DEFAULT_LOG = new Log(System.out);
		Logger.setSkipPrefix(true);
		LogLevel.INFO.set();

		TemporalSentenceDataset train, test;
		if (READ_SERIALIZED_DATASETS) {
			train = getSerializedData(SERIALIZED_TRAINING);
			test = getSerializedData(SERIALIZED_TESTING);
		}
		else {
			train = getData(DATASET, SERIALIZED_TRAINING);
			test = getData(DATASET, SERIALIZED_TESTING);
		}
		//long startTime = System.currentTimeMillis();

		// Init the logical expression type system
		// LogicLanguageServices.setInstance(new LogicLanguageServices(
		//		new TypeRepository(
		//				new File(resourcesDir + "tempeval.types.txt")), "i"));

		LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(
				new TypeRepository(new File(RESOURCES_DIR + "tempeval.types.txt"))).setNumeralTypeName("i")
				.setTypeComparator(new FlexibleTypeComparator()).build());

		final ICategoryServices<LogicalExpression> categoryServices = new LogicalExpressionCategoryServices();

		// Load the ontology
		List<File> ontologyFiles = new LinkedList<File>();
		ontologyFiles.add(new File(RESOURCES_DIR + "tempeval.predicates.txt"));
		ontologyFiles.add(new File(RESOURCES_DIR + "tempeval.constants.txt"));
		try {
			// Ontology is currently not used, so we are just reading it, not
			// storing
			new Ontology(ontologyFiles);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ILexicon<LogicalExpression> fixed = getFixedLexicon(categoryServices);
		LexicalFeatureSet<Sentence, LogicalExpression> lexPhi = getLexPhi(categoryServices);
		AbstractCKYParser<LogicalExpression> parser = getParser(categoryServices);

		if (CROSS_VAL){
			List<List<GoldSentence>> splitData = getCVPartitions (train, CV_FOLDS);
			OutputData[] outList = new OutputData[splitData.size()];

			// to make the threads
			TemporalThread[] threads = new TemporalThread[splitData.size()];

			for (int i = 0; i < splitData.size(); i++){

				// to make the training and testing corpora
				List<GoldSentence> newTrainList = new LinkedList<GoldSentence>();
				List<GoldSentence> newTestList = new LinkedList<GoldSentence>();
				for (int j = 0; j < splitData.size(); j++){
					if (i == j)
						newTestList.addAll(splitData.get(i));
					else
						newTrainList.addAll(splitData.get(j));
				}

				TemporalSentenceDataset newTest = new TemporalSentenceDataset(newTestList);
				TemporalSentenceDataset newTrain = new TemporalSentenceDataset(newTrainList);

				outList[i] = new OutputData();

				threads[i] = new TemporalThread(newTrain, newTest, parser, fixed, lexPhi, i, PERCEPTRON_ITERATIONS, outList[i]);
				threads[i].start();
			}

			for (int i = 0; i < threads.length; i++){
				try{
					threads[i].join();
				} catch (InterruptedException e){
					e.printStackTrace();
					System.err.println("Some problems getting the threads to join again!");
				}
			}

			PrintStream out = new PrintStream(new File("output/totals.txt"));
			OutputData averaged = OutputData.average(outList);
			out.println(averaged);
			out.close();
		} else {
			new TemporalThread(train, test, parser, fixed, lexPhi, -1, PERCEPTRON_ITERATIONS, new OutputData()).run();
		}
		//LOG.info("Total runtime %.4f seconds", Double.valueOf(System
		//		.currentTimeMillis() - startTime / 1000.0D));
	}
}
