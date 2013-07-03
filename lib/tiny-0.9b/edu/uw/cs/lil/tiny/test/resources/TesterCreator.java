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
package edu.uw.cs.lil.tiny.test.resources;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.test.Tester;
import edu.uw.cs.utils.filter.IFilter;

public class TesterCreator<X, Y> implements
		IResourceObjectCreator<Tester<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public Tester<X, Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		
		// Get the testing set
		final IDataCollection<? extends ILabeledDataItem<X, Y>> testSet;
		{
			// [yoav] [17/10/2011] Store in Object to javac known bug
			final Object dataCollection = resourceRepo.getResource(parameters
					.get("data"));
			if (dataCollection == null
					|| !(dataCollection instanceof IDataCollection<?>)) {
				throw new RuntimeException("Unknown or non labeled dataset: "
						+ parameters.get("data"));
			} else {
				testSet = (IDataCollection<? extends ILabeledDataItem<X, Y>>) dataCollection;
			}
		}
		
		final Tester.Builder<X, Y> builder = new Tester.Builder<X, Y>(testSet,
				(IParser<X, Y>) resourceRepo.getResource("parser"));
		
		if (parameters.get("skippingFilter") != null) {
			builder.setSkipParsingFilter((IFilter<ILabeledDataItem<X, Y>>) resourceRepo
					.getResource(parameters.get("skippingFilter")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "tester";
	}
	
}
