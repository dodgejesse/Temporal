package edu.uw.cs.lil.tiny.learn;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.explat.IJobListener;
import edu.uw.cs.lil.tiny.explat.Job;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelPostProcessor;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.Logger;
import edu.uw.cs.utils.log.LoggerFactory;

public class TrainJob<X, Z, M extends Model<X, Z>> extends Job {
	private static final ILogger			LOG	= LoggerFactory
														.create(TrainJob.class);
	
	private final ILearner<X, Z, M>			learner;
	private final M							model;
	
	private final IModelPostProcessor<X, Z>	modelPostProcessor;
	
	private TrainJob(String id, Set<String> dependencyIds,
			ILearner<X, Z, M> learner, M model,
			IModelPostProcessor<X, Z> modelPostProcessor,
			PrintStream outputStream, Log log, IJobListener jobListener) {
		super(id, dependencyIds, jobListener, outputStream, log);
		this.learner = learner;
		this.model = model;
		this.modelPostProcessor = modelPostProcessor;
	}
	
	@Override
	public void doJob() {
		// Record start time
		final long startTime = System.currentTimeMillis();
		
		// Start job
		LOG.info("============ (Job %s started)", getId());
		
		// Do the learning
		learner.train(model);
		
		// Post-process the model
		modelPostProcessor.process(model);
		
		// Log the final model
		LOG.info("Final model:\n%s", model);
		
		// Output total run time
		LOG.info("Total run time %.4f seconds",
				(System.currentTimeMillis() - startTime) / 1000.0);
		
		// Job completed
		LOG.info("============ (Job %s completed)", getId());
	}
	
	public static class Builder<X, Z, M extends Model<X, Z>> {
		private final Set<String>			dependencyIds		= new HashSet<String>();
		
		private final String				id;
		private final IJobListener			jobListener;
		private final ILearner<X, Z, M>		learner;
		
		private Log							log					= Logger.DEFAULT_LOG;
		private final M						model;
		private IModelPostProcessor<X, Z>	modelPostProcessor	= new IModelPostProcessor<X, Z>() {
																	
																	@Override
																	public void process(
																			Model<X, Z> modelLocal) {
																		// Stub
																	}
																};
		
		private final PrintStream			outputStream		= System.out;
		
		public Builder(String id, IJobListener jobListener,
				ILearner<X, Z, M> learner, M model) {
			this.id = id;
			this.jobListener = jobListener;
			this.learner = learner;
			this.model = model;
		}
		
		public Builder<X, Z, M> addDependencyId(String dependencyId) {
			this.dependencyIds.add(dependencyId);
			return this;
		}
		
		public TrainJob<X, Z, M> build() {
			return new TrainJob<X, Z, M>(id, dependencyIds, learner, model,
					modelPostProcessor, outputStream, log, jobListener);
		}
		
		public Builder<X, Z, M> setLog(Log log) {
			this.log = log;
			return this;
		}
		
		public Builder<X, Z, M> setModelPostProcessor(
				IModelPostProcessor<X, Z> modelPostProcessor) {
			this.modelPostProcessor = modelPostProcessor;
			return this;
		}
		
	}
	
}
