package edu.uw.cs.lil.tiny.test.stats;


public abstract class AbstractTestingStatistics<X, Y> implements
		ITestingStatistics<X, Y> {
	
	private final String	metricName;
	private final String	prefix;
	
	public AbstractTestingStatistics(String metricName) {
		this(null, metricName);
	}
	
	public AbstractTestingStatistics(String prefix, String metricName) {
		this.prefix = prefix;
		this.metricName = metricName;
	}
	
	protected String getMericName() {
		return metricName;
	}
	
	protected String getPrefix() {
		return prefix == null ? "" : prefix;
	}
	
}
