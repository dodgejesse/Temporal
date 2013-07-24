package edu.uw.cs.lil.temporal;

import edu.uw.cs.lil.temporal.preprocessing.TemporalReader;
import edu.uw.cs.lil.temporal.structures.TemporalDataset;
import edu.uw.cs.lil.temporal.structures.TemporalSentence;
import edu.uw.cs.lil.temporal.util.Debug;
import edu.uw.cs.lil.temporal.util.TemporalStatistics;
import edu.uw.cs.lil.temporal.util.Debug.Type;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class TemporalMain {
	private static final String DATASET_DIR = "data/TempEval3/TBAQ-cleaned/";
	private static final String[] DATASETS =  {"AQUAINT", "TimeBank"};
	//private static final String DATASET_DIR = "data/TempEval3/";
	//private static final String[] DATASETS =  {"TE3-Silver-data"};
	//private static final String[] DATASETS =  {"debug_dataset"};

	public static final int CV_FOLDS = 10;
	public static final boolean STRICT_MATCHING = false;
	public static final boolean GOLD_MENTIONS = false;
	public static final boolean FORCE_SERIALIZATION = false;
	public static final boolean CROSS_VALIDATION = true;
	public static final boolean PARALLEL_EXECUTION = true;

	private static void evaluate(TemporalDataset dataset) {
		Debug.printf (Type.PROGRESS,"Evaluating %d sentences...\n\n", dataset.size());
		TemporalStatistics stats = new TemporalStatistics();

		if (CROSS_VALIDATION){
			List<List<TemporalSentence>> partitions = dataset.partition(CV_FOLDS);
			TemporalTester[] threads = new TemporalTester[partitions.size()];
			for (int i = 0; i < partitions.size(); i++){
				TemporalDataset trainData = new TemporalDataset();
				TemporalDataset testData = new TemporalDataset(partitions.get(i));
				for (int j = 0; j < partitions.size(); j++)
					if (j != i)
						trainData.addSentences(partitions.get(j));

				threads[i] = new TemporalTester(trainData, testData, stats);
				if(PARALLEL_EXECUTION)
					threads[i].start();
				else
					threads[i].run();
			}
			for (int i = 0; i < threads.length; i++){
				try{
					threads[i].join();
				} catch (InterruptedException e){
					e.printStackTrace();
					System.err.println("Some problems getting the threads to join again!");
				}
			}
		} else {
			// Train and test on the same dataset for debugging
			new TemporalTester(dataset, dataset, stats).run();
		}
		Debug.println(Type.STATS, stats);
		Debug.println(Type.PROGRESS, "Done with analysis.");
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException {
		Debug.setLogs("relaxed");
		Debug.addFilter("", System.out, Type.PROGRESS, Type.STATS);
		Debug.addFilter("ERROR:", System.out, Type.ERROR);
		Debug.addFilter("DEBUG:", System.out, Type.DEBUG);
		Debug.addFilter("", "stats.txt", Type.STATS);
		Debug.addFilter("", "attributes.txt", Type.ATTRIBUTE);
		Debug.addFilter("", "detection.txt", Type.DETECTION);
		Debug.addFilter("", "incorrect_attribute.txt", Type.INCORRECT_ATTRIBUTE);
		Debug.addFilter("", "debug_attribute.txt", Type.DEBUG_ATTRIBUTE);
		Debug.addFilter("", "parse_selection.txt", Type.PARSE_SELECTION);
		Debug.addFilter("", "unknown_incorrect.txt", Type.UNKNOWN_INCORRECT);
		TemporalDataset dataset = new TemporalReader().getDataset(DATASET_DIR, DATASETS, FORCE_SERIALIZATION);
		evaluate(dataset);
	}
}
