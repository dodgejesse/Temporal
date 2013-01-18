package edu.uw.cs.lil.tiny.learn.ubl.resources;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.composite.CompositeDataset;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.learn.ubl.UBLMarginConstrainedPerceptron;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.IUBLSplitter;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.single.CKYParser;
import edu.uw.cs.lil.tiny.test.Tester;

public class UBLMarginConstrainedPerceptronCreator implements
		IResourceObjectCreator<UBLMarginConstrainedPerceptron> {
	
	@SuppressWarnings("unchecked")
	@Override
	public UBLMarginConstrainedPerceptron create(Parameters parameters,
			IResourceRepository resourceRepo) {
		
		// Get training sets
		final IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>> trainingSet;
		{
			final List<IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>> trainingSets = new LinkedList<IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>>();
			for (final String datasetId : parameters.getSplit("data")) {
				// [yoav] [17/10/2011] Store in Object to javac known bug
				final Object dataCollection = resourceRepo
						.getResource(datasetId);
				if (dataCollection == null
						|| !(dataCollection instanceof IDataCollection<?>)) {
					throw new RuntimeException(
							"Unknown or not a labeled dataset: " + datasetId);
				} else {
					trainingSets
							.add((IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>) dataCollection);
				}
			}
			trainingSet = new CompositeDataset<ILabeledDataItem<Sentence, LogicalExpression>>(
					trainingSets);
		}
		
		// Get debug dataset, if exists
		final Map<Sentence, LogicalExpression> debug = new HashMap<Sentence, LogicalExpression>();
		final String debugDatasetId = parameters.get("debugData");
		if (debugDatasetId != null) {
			// [yoav] [17/10/2011] Store in Object to javac known bug
			final Object dataCollection = resourceRepo
					.getResource(debugDatasetId);
			if (dataCollection == null
					|| !(dataCollection instanceof IDataCollection<?>)) {
				throw new RuntimeException(
						"Unknown or unlabeled dataset for debug: "
								+ debugDatasetId);
			} else {
				for (final ILabeledDataItem<Sentence, LogicalExpression> dataItem : (IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>) dataCollection) {
					debug.put(dataItem.getSample(), dataItem.getLabel());
				}
			}
		}
		
		final UBLMarginConstrainedPerceptron.Builder builder = new UBLMarginConstrainedPerceptron.Builder(
				(ICategoryServices<LogicalExpression>) resourceRepo
						.getResource("categoryServices"),
				trainingSet, Double.parseDouble(parameters.get("margin")),
				Integer.parseInt(parameters.get("iter")),
				(IUBLSplitter) resourceRepo.getResource(parameters
						.get("splitter")),
				(CKYParser<LogicalExpression>) resourceRepo
						.getResource("parser"));
		
		if ("true".equals(parameters.get("conservative"))) {
			builder.setConservativeSplitting(true);
		}
		
		if (parameters.get("maxSentenceLength") != null) {
			builder.setMaxSentenceLength(Integer.valueOf(parameters
					.get("maxSentenceLen")));
		}
		
		if (parameters.get("tester") != null) {
			builder.setTester((Tester<Sentence, LogicalExpression>) resourceRepo
					.getResource(parameters.get("tester")));
		}
		
		if ("true".equals(parameters.get("prune"))) {
			builder.setPruneLexicon(true);
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "learner.ubl.marginperceptron";
	}
}
