package edu.uw.cs.lil.tiny.parser.joint.exec;

import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.exec.IExecOutput;
import edu.uw.cs.lil.tiny.exec.IExecution;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

public class JointExecutionOutput<Y, Z> implements IExecOutput<Pair<Y, Z>> {
	
	private List<IExecution<Pair<Y, Z>>>	allExecutions;
	private List<IExecution<Pair<Y, Z>>>	bestExecutions;
	private final IJointDataItemModel<Y, Z>	dataItemModel;
	private final IJointOutput<Y, Z>		jointOutput;
	
	public JointExecutionOutput(IJointOutput<Y, Z> jointOutput,
			final IJointDataItemModel<Y, Z> dataItemModel, boolean pruneFails) {
		this.jointOutput = jointOutput;
		this.dataItemModel = dataItemModel;
		this.allExecutions = Collections
				.unmodifiableList(ListUtils.map(
						jointOutput.getAllJointParses(!pruneFails),
						new ListUtils.Mapper<IJointParse<Y, Z>, IExecution<Pair<Y, Z>>>() {
							
							@Override
							public IExecution<Pair<Y, Z>> process(
									IJointParse<Y, Z> obj) {
								return new JointExecution<Y, Z>(obj,
										dataItemModel);
							}
						}));
		this.bestExecutions = Collections
				.unmodifiableList(ListUtils.map(
						jointOutput.getBestJointParses(!pruneFails),
						new ListUtils.Mapper<IJointParse<Y, Z>, IExecution<Pair<Y, Z>>>() {
							
							@Override
							public IExecution<Pair<Y, Z>> process(
									IJointParse<Y, Z> obj) {
								return new JointExecution<Y, Z>(obj,
										dataItemModel);
							}
						}));
	}
	
	@Override
	public List<IExecution<Pair<Y, Z>>> getAllExecutions() {
		return allExecutions;
	}
	
	@Override
	public List<IExecution<Pair<Y, Z>>> getBestExecutions() {
		return bestExecutions;
	}
	
	@Override
	public long getExecTime() {
		return jointOutput.getParsingTime();
	}
	
	@Override
	public List<IExecution<Pair<Y, Z>>> getExecutions(Pair<Y, Z> label) {
		final List<IJointParse<Y, Z>> bases = jointOutput.getParsesFor(label);
		return ListUtils
				.map(bases,
						new ListUtils.Mapper<IJointParse<Y, Z>, IExecution<Pair<Y, Z>>>() {
							
							@Override
							public IExecution<Pair<Y, Z>> process(
									IJointParse<Y, Z> obj) {
								return new JointExecution<Y, Z>(obj,
										dataItemModel);
							}
							
						});
	}
	
}
