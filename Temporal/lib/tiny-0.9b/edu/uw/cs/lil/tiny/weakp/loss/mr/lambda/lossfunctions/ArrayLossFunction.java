/*******************************************************************************
 * tiny - a semantic parsing framework. Copyright (C) 2013 Yoav Artzi
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 ******************************************************************************/
package edu.uw.cs.lil.tiny.weakp.loss.mr.lambda.lossfunctions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.learn.weakp.loss.AbstractScaledLossFunction;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.AllSubExpressions;

/**
 * Generates a loss for arrays. First, if there is an array of size 1, it's a
 * waste, so we generate a relatively high loss. If we don't have an array,
 * generate no loss. If there an array of size 2 and above generate a loss that
 * is relative to the size of the array, the larger the array is the loss should
 * be bigger, but should always be smaller than the case of an array with a
 * single entity.
 * <p>
 * TODO [yoav] [fix_hack] This one is very problematic and assumes a single
 * lambda operator !!!!!
 * 
 * @author Yoav Artzi
 */
public class ArrayLossFunction extends
		AbstractScaledLossFunction<LogicalExpression> {
	
	public ArrayLossFunction(double scale) {
		super(scale);
	}
	
	@Override
	public String toString() {
		return ArrayLossFunction.class.getSimpleName();
	}
	
	@Override
	protected double doLossCalculation(IDataItem<Sentence> dataItem,
			LogicalExpression label) {
		final Map<LogicalExpression, Set<Literal>> indexLiterals = new HashMap<LogicalExpression, Set<Literal>>();
		for (final LogicalExpression subExp : AllSubExpressions.of(label)) {
			if (subExp instanceof Literal) {
				final Literal literal = (Literal) subExp;
				if (LogicLanguageServices.isArrayIndexPredicate(literal
						.getPredicate())) {
					final LogicalExpression arrayArg = literal.getArguments()
							.get(0);
					if (!indexLiterals.containsKey(literal.getArguments()
							.get(0))) {
						indexLiterals.put(arrayArg, new HashSet<Literal>());
					}
					indexLiterals.get(arrayArg).add(literal);
				}
			}
		}
		
		double loss = 0;
		for (final Entry<LogicalExpression, Set<Literal>> entry : indexLiterals
				.entrySet()) {
			if (entry.getValue().size() == 1) {
				// Case array with one entity -- bad
				++loss;
			}
			if (!(entry.getKey() instanceof Variable)
					|| !((Variable) entry.getKey()).getType().isArray()) {
				// Case array with a variable not of type array
				++loss;
			}
		}
		return loss;
	}
	
}
