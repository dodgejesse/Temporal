package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import java.util.List;


public class Disjunction extends AbstractTruthValueLiteralExecutor {
	
	@Override
	protected boolean doExecute(List<Object> objects) {
		for (final Object obj : objects) {
			if (obj.equals(Boolean.TRUE)) {
				return true;
			}
		}
		return false;
	}
	
}
