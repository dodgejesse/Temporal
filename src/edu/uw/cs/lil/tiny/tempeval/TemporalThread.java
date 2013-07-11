package edu.uw.cs.lil.tiny.tempeval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.uw.cs.lil.learn.simple.joint.JointSimplePerceptron;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.learn.ILearner;
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
import edu.uw.cs.lil.tiny.tempeval.util.OutputData;

public class TemporalThread extends Thread {
	final ILearner<Sentence, LogicalExpression, JointModel<Sentence, String[], LogicalExpression, LogicalExpression>> learner;
	final TemporalTester tester;
	final int cvIteration;
	final OutputData outputData;
	final JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;

	public TemporalThread( TemporalObservationDataset train,
			TemporalObservationDataset test,
			AbstractCKYParser<LogicalExpression> parser,
			ILexicon<LogicalExpression> fixed,
			LexicalFeatureSet<Sentence, LogicalExpression> lexPhi,
			int cvIteration, 
			int perceptronIterations,
			OutputData outputData){

		this.cvIteration = cvIteration;
		this.outputData = outputData;

		// Creating a joint parser.
		final TemporalJointParser jParser = new TemporalJointParser(parser);

		tester = TemporalTester.build(test, jParser);
		learner = new JointSimplePerceptron<Sentence, String[], LogicalExpression, LogicalExpression, TemporalResult>(
				perceptronIterations, train, jParser);
		model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
				//.addParseFeatureSet(
				//		new LogicalExpressionCoordinationFeatureSet<Sentence>(true, true, true))
				//.addParseFeatureSet(
				//		new LogicalExpressionTypeFeatureSet<Sentence>())
				.addJointFeatureSet(new TemporalContextFeatureSet())
				.addJointFeatureSet(new TemporalReferenceFeatureSet())
				//.addJointFeatureSet(new TemporalTypeFeatureSet())
				.addJointFeatureSet(new TemporalDayOfWeekFeatureSet())
				.addLexicalFeatureSet(lexPhi)//.addLexicalFeatureSet(lexemeFeats)
				//.addLexicalFeatureSet(templateFeats)
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		// Initialize lexical features. This is not "natural" for every lexical
		// feature set, only for this one, so it's done here and not on all
		// lexical feature sets.
		model.addFixedLexicalEntries(fixed.toCollection());
	}

	public void run(){
		// redirecting system.out. Unfortunately, the learner uses a LOG, so this doesn't actually do anything. 

		/*PrintStream oldSystemOut = System.out;
		PrintStream redirectToFile = null;
		try {
			redirectToFile = new PrintStream(new FileOutputStream("output/trainingIteration" + iteration + ".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Y'all got problems in TemporalThread when piping training to a file.");
			e1.printStackTrace();
			System.exit(0);
		}
		System.setOut(redirectToFile);
		 */
		learner.train(model);
		//System.setOut(oldSystemOut);

		PrintStream out = null;
		if (cvIteration == -1) {
			out = System.out;
		}
		else {
			try {
				out = new PrintStream(new File("output/partition" + cvIteration + ".txt"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Y'all got problems in TemporalThread.");
				System.exit(0);
			}
		}
		tester.test(model, out, outputData);
	}
}