package edu.uw.cs.lil.tiny.geoquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.DistributedExperiment;
import edu.uw.cs.lil.tiny.explat.Job;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.learn.ILearner;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.lil.tiny.parser.SimpleFullParseFilter;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.multi.MultiCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexiconServices;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry.Origin;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.ModelLogger;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderHelper;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.RuleSetBuilder;
import edu.uw.cs.lil.tiny.parser.ccg.rules.coordination.CoordinationRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.coordination.lambda.LogicalExpressionCoordinationServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.BackwardSkippingRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.ForwardSkippingRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.AdverbialTopicalisationTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.AdverbialTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.PluralTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.PrepositionTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.SententialAdverbialTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.templated.ForwardTypeRaisedComposition;
import edu.uw.cs.lil.tiny.test.Tester;
import edu.uw.cs.lil.tiny.test.stats.ExactMatchTestingStatistics;
import edu.uw.cs.lil.tiny.utils.concurrency.TinyExecutorService;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.LogLevel;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Distributed experiment for the Geoquery navigation project.
 * 
 * @author Luke Zettlemoyer
 */
public class GeoExperiment extends DistributedExperiment {
	
	public static final String						EVAL_SERVICES_FACTORY	= "evalServicesFactory";
	
	public static final String						MAPS_RESOURCE			= "maps";
	
	public static final String						PARSER_RESOURCE			= "parser";
	
	public static final String						SINGLE_EVALUATOR		= "singleEval";
	
	private static final ILogger					LOG						= LoggerFactory
																					.create(GeoExperiment.class);
	
	private final LogicalExpressionCategoryServices	categoryServices;
	private final DecoderHelper<LogicalExpression>	decoderHelper;
	private final TinyExecutorService				executor;
	
	private final ResourceCreatorRepository			resCreatorRepo			= new GeoResourceRepo();
	
	public GeoExperiment(File initFile) throws IOException, SAXException {
		super(initFile);
		
		LogLevel.DEV.set();
		
		// //////////////////////////////////////////
		// Get parameters
		// //////////////////////////////////////////
		final File typesFile = new File(globalParams.get("types"));
		final List<File> ontologyFiles = globalParams
				.getFileNamesFromParameter("ont");
		final List<File> seedLexiconFiles = globalParams
				.getFileNamesFromParameter("seedlex");
		final int parserBeamSize = Integer.parseInt(globalParams.get("beam"));
		
		// //////////////////////////////////////////
		// Init typing system
		// //////////////////////////////////////////
		
		// Init the logical expression type system
		LogicLanguageServices.setInstance(new LogicLanguageServices(
				new TypeRepository(typesFile), "i"));
		
		// //////////////////////////////////////////////////
		// Category services for logical expressions
		// //////////////////////////////////////////////////
		
		this.categoryServices = new LogicalExpressionCategoryServices(true,
				false);
		storeResource(CATEGORY_SERVICES_RESOURCE, categoryServices);
		
		// //////////////////////////////////////////////////
		// Decoder helper for decoding tasks
		// //////////////////////////////////////////////////
		
		this.decoderHelper = new DecoderHelper<LogicalExpression>(
				categoryServices);
		storeResource("decoderHelper", decoderHelper);
		
		// //////////////////////////////////////////////////
		// Read ontology (loads all constants)
		// //////////////////////////////////////////////////
		
		try {
			// Ontology is currently not used, so we are just reading it, not
			// storing
			storeResource(ONTOLOGY_RESOURCE, new Ontology(ontologyFiles));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
		// //////////////////////////////////////////////////
		// Lexical factoring services
		// //////////////////////////////////////////////////
		
		final Set<LogicalConstant> unfactoredConstants = new HashSet<LogicalConstant>();
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("io:<<e,t>,e>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("a:<<e,t>,e>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("exists:<<e,t>,t>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("equals:<e,<e,t>>"));
		FactoredLexiconServices.set(unfactoredConstants);
		
		// //////////////////////////////////////////////////
		// Initial lexicon
		// //////////////////////////////////////////////////
		
		final Lexicon<LogicalExpression> readLexicon = new Lexicon<LogicalExpression>();
		for (final File file : seedLexiconFiles) {
			readLexicon.addEntriesFromFile(file, categoryServices,
					Origin.FIXED_DOMAIN);
		}
		final Lexicon<LogicalExpression> semiFactored = new Lexicon<LogicalExpression>();
		for (final LexicalEntry<LogicalExpression> entry : readLexicon
				.toCollection()) {
			semiFactored.add(FactoredLexicon.factor(entry));
		}
		storeResource("initialLexicon", semiFactored);
		
		final List<File> unfactoredLexiconFiles = globalParams
				.getFileNamesFromParameter("unfactoredlex");
		final Lexicon<LogicalExpression> unfactoredLexicon = new Lexicon<LogicalExpression>();
		for (final File file : unfactoredLexiconFiles) {
			unfactoredLexicon.addEntriesFromFile(file, categoryServices,
					Origin.FIXED_DOMAIN);
		}
		storeResource("unfactoredLexicon", unfactoredLexicon);
		
		// //////////////////////////////////////////////////
		// CKY Parser
		// //////////////////////////////////////////////////
		
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
		ruleSetBuilder.add(CoordinationRule
				.create(new LogicalExpressionCoordinationServices(
						(LogicalConstant) categoryServices
								.parseSemantics("conj:e"),
						(LogicalConstant) categoryServices
								.parseSemantics("disj:e"), categoryServices)));
		
		// Type shifting functions
		ruleSetBuilder.add("shift_pp", new PrepositionTypeShifting());
		ruleSetBuilder.add("shift_ap", new AdverbialTypeShifting());
		ruleSetBuilder.add("ishift_ap",
				new AdverbialTopicalisationTypeShifting());
		ruleSetBuilder.add("shift_s", new SententialAdverbialTypeShifting());
		ruleSetBuilder
				.add("shift_pl", new PluralTypeShifting(categoryServices));
		
		// Executor for multi threading
		executor = new TinyExecutorService(Runtime.getRuntime()
				.availableProcessors());
		
		LOG.info("Using %d threads", Runtime.getRuntime().availableProcessors());
		
		final Set<Syntax> syntaxSet = new HashSet<Syntax>();
		syntaxSet.add(Syntax.S);
		syntaxSet.add(Syntax.N);
		syntaxSet.add(Syntax.NP);
		syntaxSet.add(Syntax.PP);
		syntaxSet.add(new ComplexSyntax(Syntax.S, Syntax.NP, Slash.BACKWARD));
		
		final SimpleFullParseFilter<LogicalExpression> parseFilter = new SimpleFullParseFilter<LogicalExpression>(
				syntaxSet);
		
		// Create the parser -- support empty rule
		final AbstractCKYParser<LogicalExpression> parser = new MultiCKYParser.Builder<LogicalExpression>(
				categoryServices, executor)
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
				.addBinaryParseRule(
						new CKYBinaryParsingRule<LogicalExpression>(
								new ForwardTypeRaisedComposition(
										categoryServices)))
				// .addBinaryParseRule(
				// new CKYBinaryParsingRule<LogicalExpression>(
				// new ForwardTopicalizedApplication(
				// categoryServices)))
				.setMaxNumberOfCellsInSpan(parserBeamSize)
				.setPreChartPruning(true).setPruneLexicalCells(true)
				.setCompleteParseFilter(parseFilter).build();
		
		storeResource(PARSER_RESOURCE, parser);
		
		// //////////////////////////////////////////////////
		// Read resources
		// //////////////////////////////////////////////////
		
		for (final Parameters params : resourceParams) {
			final String type = params.get("type");
			final String id = params.get("id");
			if (resCreatorRepo.getCreator(type) == null) {
				throw new IllegalArgumentException("Invalid resource type: "
						+ type);
			} else {
				storeResource(id,
						resCreatorRepo.getCreator(type).create(params, this));
			}
		}
		
		// //////////////////////////////////////////////////
		// Create jobs
		// //////////////////////////////////////////////////
		
		for (final Parameters params : jobParams) {
			addJob(createJob(params));
		}
	}
	
	private Job createJob(Parameters params) throws FileNotFoundException {
		final String type = params.get("type");
		if (type.equals("train")) {
			return createTrainJob(params);
		} else if (type.equals("test")) {
			return createTestJob(params);
		} else if (type.equals("save")) {
			return createSaveJob(params);
		} else if (type.equals("log")) {
			return createModelLoggingJob(params);
		} else if (type.equals("shutdown")) {
			return createShutdownJob(params);
		} else {
			throw new RuntimeException("Unsupported job type: " + type);
		}
	}
	
	private Job createModelLoggingJob(Parameters params)
			throws FileNotFoundException {
		final IModelImmutable<?, ?> model = getResource(params.get("model"));
		final ModelLogger modelLogger = getResource(params.get("logger"));
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputStream(params.get("id")), new Log(
						createJobLogStream(params.get("id")))) {
			
			@Override
			protected void doJob() {
				modelLogger.log(model, getOutputStream());
			}
		};
	}
	
	private Job createSaveJob(Parameters params) throws FileNotFoundException {
		final IModelImmutable<?, ?> model = getResource(params.get("model"));
		final File directory = new File(params.get("dir"));
		
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputStream(params.get("id")), new Log(
						createJobLogStream(params.get("id")))) {
			
			@Override
			protected void doJob() {
				try {
					DecoderServices.encode(model, directory, decoderHelper);
				} catch (final IOException e) {
					throw new IllegalStateException("failed to save model to: "
							+ directory);
				}
			}
		};
	}
	
	private Job createShutdownJob(Parameters params)
			throws FileNotFoundException {
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputStream(params.get("id")), new Log(
						createJobLogStream(params.get("id")))) {
			
			@Override
			protected void doJob() {
				executor.shutdown();
			}
		};
	}
	
	private <X, Z> Job createTestJob(Parameters params)
			throws FileNotFoundException {
		
		// Make the stats
		final ExactMatchTestingStatistics<Sentence, LogicalExpression> stats = new ExactMatchTestingStatistics<Sentence, LogicalExpression>();
		
		// Get the tester
		final Tester<Sentence, LogicalExpression> tester = getResource(params
				.get("tester"));
		
		// The model to use
		final Model<Sentence, LogicalExpression> model = getResource(params
				.get("model"));
		
		// Create and return the job
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputStream(params.get("id")), new Log(
						createJobLogStream(params.get("id")))) {
			
			@Override
			protected void doJob() {
				
				// Record start time
				final long startTime = System.currentTimeMillis();
				
				// Job started
				LOG.info("============ (Job %s started)", getId());
				
				tester.test(model, stats);
				LOG.info("%s", stats);
				
				// Output total run time
				LOG.info("Total run time %.4f seconds",
						(System.currentTimeMillis() - startTime) / 1000.0);
				
				// Job completed
				LOG.info("============ (Job %s completed)", getId());
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private Job createTrainJob(Parameters params) throws FileNotFoundException {
		// The model to use
		final Model<Sentence, LogicalExpression> model = (Model<Sentence, LogicalExpression>) getResource(params
				.get("model"));
		
		// The learning
		final ILearner<Sentence, LogicalExpression, Model<Sentence, LogicalExpression>> learner = (ILearner<Sentence, LogicalExpression, Model<Sentence, LogicalExpression>>) getResource(params
				.get("learner"));
		
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputStream(params.get("id")), new Log(
						createJobLogStream(params.get("id")))) {
			
			@Override
			protected void doJob() {
				final long startTime = System.currentTimeMillis();
				
				// Start job
				LOG.info("============ (Job %s started)", getId());
				
				// Do the learning
				learner.train(model);
				
				// Log the final model
				LOG.info("Final model:\n%s", model);
				
				// Output total run time
				LOG.info("Total run time %.4f seconds",
						(System.currentTimeMillis() - startTime) / 1000.0);
				
				// Job completed
				LOG.info("============ (Job %s completed)", getId());
				
			}
		};
	}
}
