package edu.uw.cs.lil.tiny.tempeval;


import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class TemporalResult {
	final LogicalExpression e;
	final String type;
	final String val;
	
	
	public TemporalResult(LogicalExpression e, String type, String val){
		this.e = e;
		this.type = type;
		this.val = val;
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
		return "(" + e.toString() + ") => (" + type + "," + val + ")";
	}
}
