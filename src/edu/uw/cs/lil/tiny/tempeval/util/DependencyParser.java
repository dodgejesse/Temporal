package edu.uw.cs.lil.tiny.tempeval.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Filters;

public class DependencyParser {
	StanfordCoreNLP pipeline;
	GrammaticalStructureFactory gsf;
	
	public DependencyParser (){
		Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit,parse");

		pipeline = new StanfordCoreNLP(props);
		
	    PennTreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    gsf = tlp.grammaticalStructureFactory(Filters.<String>acceptFilter(), new SemanticHeadFinder(false));
	}

	public String getParse(String l) {
		// to overwrite system.out, so i can store what's printed to it by the parser
		PrintStream out = System.out;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);


		Annotation a = new Annotation(l);

		pipeline.annotate(a);
		List<CoreMap> sentences = a
				.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			CoreMap sentence = sentences.get(0);
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		    
		    EnglishGrammaticalStructure.printDependencies(gs, gs.typedDependencies(false), tree, true, false);
		    System.out.flush();
		    System.setOut(out);
		}
		
		return baos.toString();
	}
}
