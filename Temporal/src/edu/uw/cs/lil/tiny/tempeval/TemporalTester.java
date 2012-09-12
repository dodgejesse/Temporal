package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.SingleSentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.filter.IFilter;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

public class TemporalTester
{
  private final IFilter<ILabeledDataItem<Sentence, LogicalExpression>> skipParsingFilter = new IFilter()
  {
    public boolean isValid(ILabeledDataItem<Sentence, LogicalExpression> e)
    {
      return true;
    }
  };
  IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, LogicalExpression>> dataset;
  Model<Sentence, LogicalExpression> model;
  CKYParser<LogicalExpression> parser;

  public TemporalTester(IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, LogicalExpression>> dataset, CKYParser<LogicalExpression> parser, Model<Sentence, LogicalExpression> model) {
    this.dataset = dataset;
    this.parser = parser;
    this.model = model;
  }

  public void test()
  {
    int itemCounter = 0;
    for (ILabeledDataItem item : this.dataset) {
      itemCounter++;
      SingleSentence withoutDate = new SingleSentence(
        (Sentence)((Pair)item
        .getSample()).first(), (LogicalExpression)item.getLabel());
      test(itemCounter, withoutDate, this.model);
    }
  }

  private String lexToString(Iterable<LexicalEntry<LogicalExpression>> lexicalEntries, IModelImmutable<Sentence, LogicalExpression> model)
  {
    StringBuilder ret = new StringBuilder();
    ret.append("[LexEntries and scores:\n");
    for (LexicalEntry entry : lexicalEntries) {
      ret.append("[").append(model.score(entry)).append("] ");
      ret.append(entry);
      ret.append(" [");
      ret.append(model.getTheta().printValues(
        model.computeFeatures(entry)));
      ret.append("]\n");
    }
    ret.append("]");
    return ret.toString();
  }

  private void processSingleBestParse(ILabeledDataItem<Sentence, LogicalExpression> dataItem, IModelImmutable<Sentence, LogicalExpression> model, IParserOutput<LogicalExpression> modelParserOutput, IParseResult<LogicalExpression> parse, boolean withWordSkipping)
  {
    Set lexicalEntries = parse
      .getMaxLexicalEntries();
    LogicalExpression label = (LogicalExpression)parse.getY();

    if (withWordSkipping) {
      System.out.println("parse with word skipping!");
    }
    else {
      System.out.println("parse without word skipping");
    }

    System.out.println("dataItem:");
    System.out.println(dataItem);
    System.out.println("parse:");
    System.out.println(parse.getY());
    if (dataItem.isCorrect(label))
    {
      System.out.println("CORRECT");
    }
    else
    {
      System.out.println("WRONG: " + label);

      IParseResult correctParse = modelParserOutput
        .getParseFor((LogicalExpression)dataItem.getLabel());
      System.out.println("Had correct parse: " + correctParse != null);
    }
  }

  private void test(int itemCounter, ILabeledDataItem<Sentence, LogicalExpression> dataItem, Model<Sentence, LogicalExpression> model)
  {
    IParserOutput modelParserOutput = this.parser
      .parse(dataItem, model.createDataItemModel(dataItem));

    List bestModelParses = modelParserOutput
      .getBestParses();
    if (bestModelParses.size() == 1)
    {
      processSingleBestParse(dataItem, model, modelParserOutput, 
        (IParseResult)bestModelParses.get(0), false);
    }
    else if (bestModelParses.size() > 1)
    {
      System.out.println("too many parses");
      System.out.println(bestModelParses.size() + " parses:");
      for (IParseResult parse : bestModelParses)
      {
        System.out
          .println("something about logging parses here...");
      }

      IParseResult correctParse = modelParserOutput
        .getParseFor((LogicalExpression)dataItem.getLabel());
      System.out
        .println("Had correct parse: " + correctParse != null);
      if (correctParse != null) {
        System.out.println("Correct parse lexical items:\n" + 
          lexToString(correctParse.getMaxLexicalEntries(), 
          model));
      }

    }
    else
    {
      System.out.println("no parses");
      if (this.skipParsingFilter.isValid(dataItem)) {
        IParserOutput parserOutputWithSkipping = this.parser
          .parse(dataItem, 
          model.createDataItemModel(dataItem), true);
        System.out
          .println("EMPTY Parsing time " + 
          parserOutputWithSkipping
          .getParsingTime() / 1000.0D);
        List bestEmptiesParses = parserOutputWithSkipping
          .getBestParses();

        if (bestEmptiesParses.size() == 1) {
          processSingleBestParse(dataItem, model, 
            parserOutputWithSkipping, 
            (IParseResult)bestEmptiesParses.get(0), true);
        }
        else
        {
          System.out.println("WRONG: " + bestEmptiesParses.size() + 
            " parses");
          for (IParseResult parse : bestEmptiesParses)
          {
            System.out
              .println("something about a parse here...");
          }

          IParseResult correctParse = parserOutputWithSkipping
            .getParseFor((LogicalExpression)dataItem.getLabel());
          System.out
            .println("Had correct parse: " + correctParse != null);
          if (correctParse != null) {
            System.out
              .println("Correct parse lexical items:\n" + 
              lexToString(correctParse
              .getMaxLexicalEntries(), 
              model));
            System.out
              .println("Correct feats: " + 
              correctParse
              .getAverageMaxFeatureVector());
          }
        }
      } else {
        System.out
          .println("Skipping word-skip parsing due to length");
      }
    }
  }
}

