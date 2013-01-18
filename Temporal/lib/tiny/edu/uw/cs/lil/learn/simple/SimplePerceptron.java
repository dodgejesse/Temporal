package edu.uw.cs.lil.learn.simple;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.learn.ILearner;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Perceptron learner for parameter update only.
 * 
 * @author Yoav Artzi
 * @param <X>
 * @param <Z>
 */
public class SimplePerceptron<X, Z> implements ILearner<X, Z, Model<X, Z>> {
	private static final ILogger									LOG	= LoggerFactory
																				.create(SimplePerceptron.class);
	
	private final int												numIterations;
	private final IParser<X, Z>										parser;
	private final IDataCollection<? extends ILabeledDataItem<X, Z>>	trainingData;
	
	public SimplePerceptron(int numIterations,
			IDataCollection<? extends ILabeledDataItem<X, Z>> trainingData,
			IParser<X, Z> parser) {
		this.numIterations = numIterations;
		this.trainingData = trainingData;
		this.parser = parser;
	}
	
	@Override
	public void train(Model<X, Z> model) {
		for (int iterationNumber = 0; iterationNumber < numIterations; ++iterationNumber) {
			// Training iteration, go over all training samples
			LOG.info("=========================");
			LOG.info("Training iteration %d", iterationNumber);
			LOG.info("=========================");
			int itemCounter = -1;
			
			for (final ILabeledDataItem<X, Z> dataItem : trainingData) {
				final long startTime = System.currentTimeMillis();
				
				LOG.info("%d : ================== [%d]", ++itemCounter,
						iterationNumber);
				LOG.info("Sample type: %s", dataItem.getClass().getSimpleName());
				LOG.info("%s", dataItem);
				
				final IDataItemModel<Z> dataItemModel = model
						.createDataItemModel(dataItem);
				final IParserOutput<Z> parserOutput = parser.parse(dataItem,
						dataItemModel);
				final List<IParseResult<Z>> bestParses = parserOutput
						.getBestParses();
				
				// Correct parse
				final List<IParseResult<Z>> correctParses = parserOutput
						.getMaxParses(dataItem.getLabel());
				
				// Violating parses
				final List<IParseResult<Z>> violatingBadParses = new LinkedList<IParseResult<Z>>();
				for (final IParseResult<Z> parse : bestParses) {
					if (!dataItem.isCorrect(parse.getY())) {
						violatingBadParses.add(parse);
						LOG.info("Bad parse: %s", parse.getY());
					}
				}
				
				if (!violatingBadParses.isEmpty() && !correctParses.isEmpty()) {
					// Case we have bad best parses and a correct parse, need to
					// update.
					
					// Create the parameter update
					final IHashVector update = HashVectorFactory.create();
					
					// Positive update
					for (final IParseResult<Z> parse : correctParses) {
						parse.getAverageMaxFeatureVector().addTimesInto(
								(1.0 / correctParses.size()), update);
					}
					
					// Negative update
					for (final IParseResult<Z> parse : violatingBadParses) {
						parse.getAverageMaxFeatureVector().addTimesInto(
								-1.0 * (1.0 / violatingBadParses.size()),
								update);
					}
					
					// Prune small entries from the update
					update.dropSmallEntries();
					
					// Update the parameters vector
					LOG.info("Update: %s", update);
					update.addTimesInto(1.0, model.getTheta());
				} else if (correctParses.isEmpty()) {
					LOG.info("No correct parses. No update.");
				} else {
					LOG.info("Correct. No update.");
				}
				LOG.info("Sample processing time %.4f",
						(System.currentTimeMillis() - startTime) / 1000.0);
			}
		}
	}
	
}
