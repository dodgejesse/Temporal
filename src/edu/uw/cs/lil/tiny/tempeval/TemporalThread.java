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
	final TemporalTester tester;
	final int iteration;
	final OutputData outputData;
	final JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;
	
	public TemporalThread(
			ILearner<Sentence, LogicalExpression, JointModel<Sentence, String[], LogicalExpression, LogicalExpression>> learner, 
			TemporalTester tester, int iteration, OutputData outputData, 
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model){
		this.learner = learner;
		this.tester = tester;
		this.iteration = iteration;
		this.outputData = outputData;
		this.model = model;
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