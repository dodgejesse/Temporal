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
package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Creator for {@link LexicalFeatureSet}.
 * 
 * @author Yoav Artzi
 */
public class LexicalFeatureSetCreator<X, Y> implements
		IResourceObjectCreator<LexicalFeatureSet<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexicalFeatureSet<X, Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final LexicalFeatureSet.Builder<X, Y> builder = new LexicalFeatureSet.Builder<X, Y>();
		
		if (parameters.contains("tag")) {
			builder.setFeatureTag(parameters.get("tag"));
		}
		
		if (parameters.contains("initFixed")) {
			builder.setInitialFixedScorer((IScorer<LexicalEntry<Y>>) resourceRepo
					.getResource(parameters.get("initFixed")));
		}
		
		if (parameters.contains("init")) {
			builder.setInitialScorer((IScorer<LexicalEntry<Y>>) resourceRepo
					.getResource(parameters.get("init")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.lex";
	}
	
}
