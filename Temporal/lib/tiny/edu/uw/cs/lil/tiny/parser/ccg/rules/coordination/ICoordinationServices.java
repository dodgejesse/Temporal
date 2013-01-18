package edu.uw.cs.lil.tiny.parser.ccg.rules.coordination;

public interface ICoordinationServices<Y> {
	
	Y applyCoordination(Y function, Y coordination);
	
	Y createPartialCoordination(Y coordinated, Y coordinator);
	
	Y createSimpleCoordination(Y coordinated, Y coordinator);
	
	@Override
	boolean equals(Object obj);
	
	Y expandCoordination(Y coordination);
	
	@Override
	int hashCode();
	
}
