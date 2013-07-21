package edu.uw.cs.lil.tiny.tempeval.structures;


import java.util.LinkedHashSet;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.JointParse;
import edu.uw.cs.lil.tiny.parser.joint.SingleExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;

public class TemporalResult {
	public final LogicalExpression expression;
	public final String type;
	public final String value;
	public final LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries;
	private final IJointDataItemModel<LogicalExpression, LogicalExpression> model;
	private final IParseResult<LogicalExpression> baseParse;
	private IJointParse<LogicalExpression, TemporalResult> jointParse;
	
	
	public TemporalResult(LogicalExpression expression, String type, String value, LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries, 
			IJointDataItemModel<LogicalExpression, LogicalExpression> model, IParseResult<LogicalExpression> baseParse){
		this.expression = expression;
		this.type = type;
		this.value = value;
		this.lexicalEntries = lexicalEntries;
		this.model = model;
		this.baseParse = baseParse;
		jointParse = null;
	}
	
	private void makeJointParse(){
		SingleExecResultWrapper<LogicalExpression, TemporalResult> wrapper = 
				new SingleExecResultWrapper<LogicalExpression, TemporalResult>(
				expression, model, this);
		
		jointParse = new JointParse<LogicalExpression, TemporalResult>(
				baseParse, wrapper);
	}
	
	public IJointParse<LogicalExpression, TemporalResult> getJointParse(){
		if (jointParse == null)
			makeJointParse();
		return jointParse;
	}
	
	/* 
	 * Returns true if this an identical instance as obj, 
	 * or if obj is a TemporalResult and its val and type fields are the same as this objects val and type fields.
	 * Note: Doesn't check the LogicalExpression!!
	 * 
	 */
	@Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof TemporalResult) {
        	TemporalResult other = (TemporalResult) obj;
        	return other.type.equals(this.type) && other.value.equals(this.value);
        } else
        	return false;
    }
	
	public String toString(){
		return "(" + expression.toString() + ") => (" + type + "," + value + ")";
	}
}
