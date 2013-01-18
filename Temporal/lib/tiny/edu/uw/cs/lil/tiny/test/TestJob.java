package edu.uw.cs.lil.tiny.test;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.explat.IJobListener;
import edu.uw.cs.lil.tiny.explat.Job;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.Logger;
import edu.uw.cs.utils.log.LoggerFactory;

public class TestJob<X, Y> extends Job {
	private static final ILogger			LOG	= LoggerFactory
														.create(TestJob.class);
	
	private final Model<X, Y>				model;
	private final ITester<X, Y>				tester;
	private final ITestingStatistics<X, Y>	testStatistics;
	
	private TestJob(String id, Set<String> dependencyIds, Log log,
			ITester<X, Y> tester, ITestingStatistics<X, Y> testStatistics,
			Model<X, Y> model, PrintStream outputStream,
			IJobListener jobListener) {
		super(id, dependencyIds, jobListener, outputStream, log);
		this.tester = tester;
		this.testStatistics = testStatistics;
		this.model = model;
	}
	
	@Override
	public void doJob() {
		// Record start time
		final long startTime = System.currentTimeMillis();
		
		// Job started
		LOG.info("============ (Job %s started)", getId());
		
		// Test the final model
		tester.test(model, testStatistics);
		LOG.info("%s\n", testStatistics);
		getOutputStream().println(testStatistics.toTabDelimitedString());
		
		// Output total run time
		LOG.info("Total run time %.4f seconds",
				(System.currentTimeMillis() - startTime) / 1000.0);
		
		// Job completed
		LOG.info("============ (Job %s completed)", getId());
	}
	
	public static class TestJobBuilder<X, Y> {
		private final Set<String>				dependencyIds	= new HashSet<String>();
		private final String					id;
		private final IJobListener				jobListener;
		private Log								log				= Logger.DEFAULT_LOG;
		private final Model<X, Y>				model;
		private PrintStream						outputStream	= System.out;
		private final ITester<X, Y>				tester;
		private final ITestingStatistics<X, Y>	testStatistics;
		
		public TestJobBuilder(String id, IJobListener jobListener,
				ITester<X, Y> tester, ITestingStatistics<X, Y> testStatistics,
				Model<X, Y> model) {
			this.id = id;
			this.jobListener = jobListener;
			this.tester = tester;
			this.testStatistics = testStatistics;
			this.model = model;
		}
		
		public TestJob<X, Y> build() {
			return new TestJob<X, Y>(id, dependencyIds, log, tester,
					testStatistics, model, outputStream, jobListener);
		}
		
		public TestJobBuilder<X, Y> setDependencyId(String dependencyId) {
			this.dependencyIds.add(dependencyId);
			return this;
		}
		
		public TestJobBuilder<X, Y> setLog(Log log) {
			this.log = log;
			return this;
		}
		
		public TestJobBuilder<X, Y> setOutputStream(PrintStream outputStream) {
			this.outputStream = outputStream;
			return this;
		}
	}
}
