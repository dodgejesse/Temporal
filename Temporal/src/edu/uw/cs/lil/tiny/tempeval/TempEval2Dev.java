package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYParserBuilder;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYUnaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexemeFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexemeFeatureSet.Builder;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexicalTemplateFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexicalTemplateFeatureSet.Builder;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSetBuilder;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LogicalExpressionCoordinationFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LogicalExpressionTypeFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.SkippingSensitiveLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.UniformScorer;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry.EntryOrigin;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model.Builder;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.BackwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardApplication;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ForwardComposition;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.BackwardSkippingRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.ForwardSkippingRule;
import edu.uw.cs.lil.tiny.utils.string.StubStringFilter;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LogLevel;
import edu.uw.cs.utils.log.Logger;
import edu.uw.cs.utils.log.Logger.IOutputStreamGetter;
import edu.uw.cs.utils.log.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TempEval2Dev
{
  private static final ILogger LOG = LoggerFactory.create(TempEval2Dev.class);

  public static void main(String[] args) {
    Logger.setOutputStream(new Logger.IOutputStreamGetter()
    {
      public PrintStream get()
      {
        return System.out;
      }
    });
    Logger.setSkipPrefix(true);
    LogLevel.INFO.set();

    long startTime = System.currentTimeMillis();

    String expDir = "/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval";
    String resourcesDir = "/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval";

    LogicLanguageServices.setInstance(new LogicLanguageServices(
      new TypeRepository(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.types")), 
      "i"));

    ICategoryServices categoryServices = new LogicalExpressionCategoryServices(
      false);

    List ontologyFiles = new LinkedList();
    ontologyFiles.add(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.predicates"));
    ontologyFiles.add(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.constants"));
    try
    {
      new Ontology(ontologyFiles);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    IDataCollection train = 
      TemporalSentenceDataset.read(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.full_dataset"), 
      new StubStringFilter(), true);
    IDataCollection test = 
      TemporalSentenceDataset.read(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.full_dataset"), 
      new StubStringFilter(), true);
    LOG.info("Train Size: " + train.size());
    LOG.info("Test Size: " + test.size());

    ILexicon fixedInput = new Lexicon();
    fixedInput.addEntriesFromFile(new File("/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.lexicon"), 
      new StubStringFilter(), 
      categoryServices, LexicalEntry.EntryOrigin.FIXED_DOMAIN);

    ILexicon fixed = new Lexicon();

    Iterator localIterator = fixedInput
      .toCollection().iterator();

    while (localIterator.hasNext()) {
      LexicalEntry lex = (LexicalEntry)localIterator.next();
      fixed.add(FactoredLexicon.factor(lex));
    }

    LexicalFeatureSet lexPhi = new LexicalFeatureSetBuilder()
      .setInitialFixedScorer(
      new ExpLengthLexicalEntryScorer(
      10.0D, 1.1D))
      .setInitialScorer(
      new SkippingSensitiveLexicalEntryScorer(
      categoryServices, 
      -1.0D, 
      new UniformScorer(
      0.0D)))
      .build();

    LexemeFeatureSet lexemeFeats = new LexemeFeatureSet.Builder()
      .setInitialFixedScorer(new UniformScorer(0.0D))
      .build();

    LexicalTemplateFeatureSet templateFeats = new LexicalTemplateFeatureSet.Builder()
      .setScale(0.1D)
      .build();

    Model model = new Model.Builder()
      .addParseFeatureSet(
      new LogicalExpressionCoordinationFeatureSet())
      .addParseFeatureSet(
      new LogicalExpressionTypeFeatureSet())
      .addLexicalFeatureSet(lexPhi).addLexicalFeatureSet(lexemeFeats)
      .addLexicalFeatureSet(templateFeats)
      .setLexicon(new FactoredLexicon()).build();

    model.addFixedLexicalEntries(fixed.toCollection());

    CKYParser parser = new CKYParserBuilder(
      categoryServices)
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new ForwardComposition(
      categoryServices)))
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new BackwardComposition(
      categoryServices)))
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new ForwardApplication(
      categoryServices)))
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new BackwardApplication(
      categoryServices)))
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new ForwardSkippingRule(
      categoryServices)))
      .addBinaryParseRule(
      new CKYBinaryParsingRule(
      new BackwardSkippingRule(
      categoryServices)))
      .addUnaryParseRule(
      new CKYUnaryParsingRule(
      new SentenceCompilation(categoryServices), true))
      .setMaxNumberOfCellsInSpan(100).build();

    TemporalTester tester = new TemporalTester(test, parser, model);

    LOG.info("Total runtime %.4f seconds", 
      Double.valueOf(System.currentTimeMillis() - startTime / 1000.0D));
  }
}

