package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.joint.IExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.JointParse;

public class TemporalJointParse<LF, ERESULT> extends JointParse<LF, ERESULT> {

	public TemporalJointParse(IParseResult<LF> innerParse,
			IExecResultWrapper<ERESULT> execResult) {
		super(innerParse, execResult);
	}

	/*
	 * this doesn't work. 
	 * Goal: return new string that's execResult.toString() where 
	public String toString() {
		this.getResult().second();
		
		return new StringBuilder(innerParse.toString()).append(" => ")
				.append(execResult).toString();
	}
	*/
}
