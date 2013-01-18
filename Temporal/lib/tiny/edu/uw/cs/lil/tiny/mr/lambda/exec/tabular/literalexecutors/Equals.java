package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import java.util.List;


public class Equals extends AbstractTruthValueLiteralExecutor {
	
	@Override
	protected boolean doExecute(List<Object> objects) {
		if (objects.size() == 2) {
			return objects.get(0).equals(objects.get(1));
		} else {
			return false;
		}
	}
	
}
