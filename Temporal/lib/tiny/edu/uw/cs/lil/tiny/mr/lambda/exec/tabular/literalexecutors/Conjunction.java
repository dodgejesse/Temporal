package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import java.util.List;

public class Conjunction extends AbstractTruthValueLiteralExecutor {
	
	@Override
	protected boolean doExecute(List<Object> objects) {
		for (final Object obj : objects) {
			if (!Boolean.TRUE.equals(obj)) {
				return false;
			}
		}
		return true;
	}
	
}
