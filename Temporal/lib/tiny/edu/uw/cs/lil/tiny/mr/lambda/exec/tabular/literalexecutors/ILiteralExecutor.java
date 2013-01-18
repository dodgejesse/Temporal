package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.literalexecutors;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.exec.tabular.Table;

public interface ILiteralExecutor {
	void execute(Literal literal, Table table);
}
