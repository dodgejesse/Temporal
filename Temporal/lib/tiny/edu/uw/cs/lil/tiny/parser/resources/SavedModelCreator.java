package edu.uw.cs.lil.tiny.parser.resources;

import java.io.File;
import java.io.IOException;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderHelper;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderServices;

public class SavedModelCreator<X, Y> implements
		IResourceObjectCreator<Model<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public Model<X, Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		try {
			return DecoderServices.decode(new File(parameters.get("dir")),
					(DecoderHelper<LogicalExpression>) resourceRepo
							.getResource("decoderHelper"));
		} catch (final IOException e) {
			throw new IllegalStateException("failed to load model from: "
					+ parameters.get("dir"));
		}
	}
	
	@Override
	public String resourceTypeName() {
		return "model.saved";
	}
	
}
