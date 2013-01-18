package edu.uw.cs.lil.tiny.mr.lambda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.utils.LispReader;

/**
 * A collection of constants forms and ontology. The set of constants is
 * immutable.
 * 
 * @author Yoav Artzi
 */
public class Ontology implements Iterable<LogicalConstant> {
	private final Set<LogicalConstant>	constants;
	
	public Ontology(File file) throws IOException {
		this(readConstantsFromFile(file));
	}
	
	public Ontology(List<File> files) throws IOException {
		this(readConstantsFromFiles(files));
	}
	
	public Ontology(Set<LogicalConstant> constants) {
		this.constants = Collections.unmodifiableSet(constants);
	}
	
	private static Set<LogicalConstant> readConstantsFromFile(File file)
			throws IOException {
		// First, strip the comments and prepare a clean LISP string to
		// parse
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		final StringBuilder strippedFile = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			line.trim();
			line = line.split("\\s*//")[0];
			if (!line.equals("")) {
				strippedFile.append(line).append(" ");
			}
		}
		reader.close();
		
		// Read the constants
		final Set<LogicalConstant> constants = new HashSet<LogicalConstant>();
		final LispReader lispReader = new LispReader(new StringReader(
				strippedFile.toString()));
		while (lispReader.hasNext()) {
			final LogicalExpression exp = LogicalExpression.parse(
					lispReader.next(),
					LogicLanguageServices.getTypeRepository(),
					LogicLanguageServices.getTypeComparator(), false);
			if (exp instanceof LogicalConstant) {
				constants.add((LogicalConstant) exp);
			} else {
				throw new RuntimeException(
						"Ontology file including a non constant: " + exp);
			}
		}
		
		return constants;
	}
	
	private static Set<LogicalConstant> readConstantsFromFiles(List<File> files)
			throws IOException {
		final Set<LogicalConstant> constants = new HashSet<LogicalConstant>();
		for (final File file : files) {
			constants.addAll(readConstantsFromFile(file));
		}
		return constants;
	}
	
	public Set<LogicalConstant> getAllPredicates() {
		final Set<LogicalConstant> predicates = new HashSet<LogicalConstant>();
		
		for (final LogicalConstant constant : constants) {
			if (constant.getType().isComplex()) {
				predicates.add(constant);
			}
		}
		
		return predicates;
	}
	
	@Override
	public Iterator<LogicalConstant> iterator() {
		return constants.iterator();
	}
}
