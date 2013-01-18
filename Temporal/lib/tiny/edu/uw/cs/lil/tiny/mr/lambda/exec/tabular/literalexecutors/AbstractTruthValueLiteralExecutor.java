package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.Table;
import edu.uw.cs.utils.collections.ListUtils;

public abstract class AbstractTruthValueLiteralExecutor implements
		ILiteralExecutor {
	
	@Override
	public void execute(Literal literal, Table table) {
		for (final Map<LogicalExpression, Object> row : table) {
			final List<LogicalExpression> args = literal.getArguments();
			final List<Object> objects = ListUtils.map(args,
					new ListUtils.Mapper<LogicalExpression, Object>() {
						
						@Override
						public Object process(LogicalExpression obj) {
							return row.get(obj);
						}
					});
			final boolean rowResult = doExecute(objects);
			row.put(literal, rowResult);
		}
	}
	
	protected abstract boolean doExecute(List<Object> objects);
	
}
