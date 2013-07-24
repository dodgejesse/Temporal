package edu.uw.cs.lil.temporal.eval;

import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.temporal.eval.entities.TemporalEntity;
import edu.uw.cs.lil.temporal.eval.entities.TemporalSequence;
import edu.uw.cs.lil.temporal.eval.predicates.TemporalNth;
import edu.uw.cs.lil.temporal.eval.predicates.TemporalPredicate;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;

public class TemporalEvaluationConstants {
	final private static String[] TEMPORAL_UNITS = {"day", "month", "quarter", "week", "year"};
	final private Map<LogicalConstant, TemporalEntity> entities;
	final private Map<LogicalConstant, TemporalPredicate> predicates;
	final private LogicalExpressionCategoryServices	categoryServices;

	public TemporalEvaluationConstants() {	
		categoryServices = new LogicalExpressionCategoryServices(true, false);
		entities = new HashMap<LogicalConstant, TemporalEntity>();
		addAllEntities();
		predicates = new HashMap<LogicalConstant, TemporalPredicate>();	
		addAllPredicates();
	}

	private void addAllEntities() {
		for(String unit : TEMPORAL_UNITS)
			addEntity(unit + ":s", new TemporalSequence(unit));
	}

	private void addAllPredicates() {
		//addPredicate("nth<n<s,<s,s>>>", new TemporalNth());
	}

	private void addEntity(String semantics, TemporalEntity entity) {
		entities.put((LogicalConstant) categoryServices.parseSemantics(semantics), entity);
	}

	private void addPredicate(String semantics, TemporalPredicate predicate) {
		predicates.put((LogicalConstant) categoryServices.parseSemantics(semantics), predicate);
	}

	public TemporalEntity getEntity(LogicalConstant constant) {
		return entities.get(constant);
	}

	public TemporalPredicate getPredicate(LogicalExpression expression) {
		return predicates.get(expression);
	}
}
