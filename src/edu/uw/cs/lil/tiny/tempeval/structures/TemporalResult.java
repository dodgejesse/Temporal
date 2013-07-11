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
	public final LogicalExpression e;
	public final String type;
	public final String val;
	public final LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries;
	private final IJointDataItemModel<LogicalExpression, LogicalExpression> model;
	private final IParseResult<LogicalExpression> baseParse;
	private IJointParse<LogicalExpression, TemporalResult> jp;
	
	
	public TemporalResult(LogicalExpression e, String type, String val, LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries, 
			IJointDataItemModel<LogicalExpression, LogicalExpression> model, IParseResult<LogicalExpression> baseParse){
		this.e = e;
		this.type = type;
		this.val = val;
		this.lexicalEntries = lexicalEntries;
		this.model = model;
		this.baseParse = baseParse;
		jp = null;
	}
	
	private void makeJointParse(){
		SingleExecResultWrapper<LogicalExpression, TemporalResult> wrapper = 
				new SingleExecResultWrapper<LogicalExpression, TemporalResult>(
				e, model, this);
		
		jp = new JointParse<LogicalExpression, TemporalResult>(
				baseParse, wrapper);
	}
	
	public IJointParse<LogicalExpression, TemporalResult> getJointParse(){
		if (jp == null)
			makeJointParse();
		return jp;
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
        	return other.type.equals(this.type) && other.val.equals(this.val);
        } else
        	return false;
    }
	
	public String toString(){
		//jp.getAverageMaxFeatureVector();
		//jp.getAverageMaxFeatureVector();
		//jp.getAverageMaxFeatureVector();
	
		return "(" + e.toString() + ") => (" + type + "," + val + ")"; //+ model.getTheta().printValues(jp.getAverageMaxFeatureVector());
	}
}
