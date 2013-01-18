package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import java.util.List;
import java.util.Set;


public class Exist extends AbstractTruthValueLiteralExecutor {
	
	@Override
	protected boolean doExecute(List<Object> objects) {
		return objects.size() == 1 && objects.get(0) instanceof Set<?>
				&& !((Set<?>) objects.get(0)).isEmpty();
	}
	
}
