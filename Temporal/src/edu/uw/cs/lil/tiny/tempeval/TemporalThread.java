package edu.uw.cs.lil.tiny.tempeval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.learn.ILearner;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;

public class TemporalThread extends Thread {
	final ILearner<Sentence, LogicalExpression, JointModel<Sentence, String[], LogicalExpression, LogicalExpression>> learner;
	final TemporalTesterSmall tester;
	final int iteration;
	final OutputData outputData;
	final JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;
	
	public TemporalThread(
			ILearner<Sentence, LogicalExpression, JointModel<Sentence, String[], LogicalExpression, LogicalExpression>> learner, 
			TemporalTesterSmall tester, int iteration, OutputData outputData, 
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model){
		this.learner = learner;
		this.tester = tester;
		this.iteration = iteration;
		this.outputData = outputData;
		this.model = model;
	}
	
	public void run(){
		learner.train(model);
		PrintStream out = null;
		try {
			out = new PrintStream(new File("output/iteration" + iteration + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Y'all got problems in TemporalThread.");
			System.exit(0);
		}
		tester.test(model, out, outputData);
	}
}