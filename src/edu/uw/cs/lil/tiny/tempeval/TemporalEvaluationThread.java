package edu.uw.cs.lil.tiny.tempeval;

import java.io.PrintStream;

import edu.uw.cs.lil.learn.simple.joint.JointSimplePerceptron;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalContextFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalDayOfWeekFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalReferenceFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;

public class TemporalEvaluationThread extends Thread {
	final private int cvFold;
	final private TemporalDataset trainData, testData;
	final private AbstractCKYParser<LogicalExpression> parser;
	final private LexicalFeatureSet<Sentence, LogicalExpression> lexPhi;
	final private int perceptronIterations;
	final private ILexicon<LogicalExpression> fixed;
	public TemporalEvaluationThread(TemporalDataset trainData,
			TemporalDataset testData,
			AbstractCKYParser<LogicalExpression> parser,
			ILexicon<LogicalExpression> fixed,
			LexicalFeatureSet<Sentence, LogicalExpression> lexPhi,
			int perceptronIterations,
			int cvFold){

		this.cvFold = cvFold;
		this.trainData = trainData;
		this.testData = testData;
		this.parser = parser;
		this.lexPhi = lexPhi;
		this.fixed = fixed;
		this.perceptronIterations = perceptronIterations;
	}

	private JointModel<Sentence, String[], LogicalExpression, LogicalExpression> learnModel(TemporalDataset dataset) {
		TemporalObservationDataset observations = dataset.getObservations();
		final TemporalJointParser jParser = new TemporalJointParser(parser);
		JointSimplePerceptron<Sentence, String[], LogicalExpression, LogicalExpression, TemporalResult> learner = new JointSimplePerceptron<Sentence, String[], LogicalExpression, LogicalExpression, TemporalResult>(
				perceptronIterations, observations, jParser);
		JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
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
		TemporalDetectionTester detectionTester = new TemporalDetectionTester (testData, parser, fixed);
		TemporalStatistics detectionStats = detectionTester.test();
		System.out.println("\nMention detection stats:");
		System.out.println(detectionStats);
		JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model = learnModel(trainData);

		TemporalObservationDataset conditionalData = detectionTester.getCorrectObservations();
		TemporalTester attributeTester = TemporalTester.build(conditionalData, new TemporalJointParser(parser));
		attributeTester.test(model, System.out, new OutputData());
	}
}