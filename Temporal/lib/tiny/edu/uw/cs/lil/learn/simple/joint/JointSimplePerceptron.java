package edu.uw.cs.lil.learn.simple.joint;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.learn.ILearner;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Joint perceptron learner for parameter update only.
 * 
 * @author Yoav Artzi
 * @param <X>
 * @param <Z>
 */
public class JointSimplePerceptron<X extends IDataItem<X>, W, Y, Z> implements
		ILearner<X, Y, JointModel<X, W, Y, Z>> {
	private static final ILogger												LOG	= LoggerFactory
																							.create(JointSimplePerceptron.class);
	
	private final int															numIterations;
	private final IJointParser<X, W, Y, Z>										parser;
	private final IDataCollection<? extends ILabeledDataItem<Pair<X, W>, Z>>	trainingData;
	
	public JointSimplePerceptron(
			int numIterations,
			IDataCollection<? extends ILabeledDataItem<Pair<X, W>, Z>> trainingData,
			IJointParser<X, W, Y, Z> parser) {
		this.numIterations = numIterations;
		this.trainingData = trainingData;
		this.parser = parser;
	}
	
	@Override
	public void train(JointModel<X, W, Y, Z> model) {
		for (int iterationNumber = 0; iterationNumber < numIterations; ++iterationNumber) {
			// Training iteration, go over all training samples
			LOG.info("=========================");
			LOG.info("Training iteration %d", iterationNumber);
			LOG.info("=========================");
			int itemCounter = -1;
			
			for (final ILabeledDataItem<Pair<X, W>, Z> dataItem : trainingData) {
				final long startTime = System.currentTimeMillis();
				
				LOG.info("%d : ================== [%d]", ++itemCounter,
						iterationNumber);
				LOG.info("Sample type: %s", dataItem.getClass().getSimpleName());
				LOG.info("%s", dataItem);
				
				final IJointOutput<Y, Z> parserOutput = parser.parse(dataItem,
						model.createJointDataItemModel(dataItem));
				final List<? extends IJointParse<Y, Z>> bestParses = parserOutput
						.getBestJointParses();
				
				// Best correct parses
				final List<IJointParse<Y, Z>> correctParses = parserOutput
						.getBestParsesForZ(dataItem.getLabel());
				
				// Violating parses
				final List<IJointParse<Y, Z>> violatingBadParses = new LinkedList<IJointParse<Y, Z>>();
				for (final IJointParse<Y, Z> parse : bestParses) {
					if (!dataItem.getLabel().equals(parse.getResult().second())) {
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
					for (final IJointParse<Y, Z> parse : correctParses) {
						parse.getAverageMaxFeatureVector().addTimesInto(
								(1.0 / correctParses.size()), update);
					}
					
					// Negative update
					for (final IJointParse<Y, Z> parse : violatingBadParses) {
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