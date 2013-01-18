package edu.uw.cs.lil.tiny.parser.joint;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.utils.composites.Pair;

public interface IJointParse<Y, Z> extends IParseResult<Y> {
	
	double getBaseScore();
	
	Pair<Y, Z> getResult();
	
}
