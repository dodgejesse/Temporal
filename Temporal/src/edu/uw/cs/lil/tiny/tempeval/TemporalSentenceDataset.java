package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.DatasetException;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpressionRuntimeException;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.IsWellTyped;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.Simplify;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TemporalSentenceDataset
  implements IDataCollection<TemporalSentence>
{
  private final List<TemporalSentence> data;

  public TemporalSentenceDataset(List<TemporalSentence> data)
  {
    this.data = Collections.unmodifiableList(data);
  }

  public static TemporalSentenceDataset read(File f, IStringFilter textFilter, boolean lockConstants)
  {
    try
    {
      BufferedReader in = new BufferedReader(new FileReader(f));
      List data = new LinkedList();

      String currentSentence = null;
      String currentDate = null;
      int readLineCounter = 0;
      String line;
      while ((line = in.readLine()) != null)
      {
        String line;
        readLineCounter++;
        if ((!line.startsWith("//")) && (!line.equals("")))
        {
          line = line.trim();
          if (currentSentence == null)
          {
            currentSentence = textFilter.filter(line);
          } else if (currentDate == null)
          {
            currentDate = textFilter.filter(line);
          }
          else
          {
            try
            {
              exp = Simplify.of(LogicalExpression.parse(line, 
                LogicLanguageServices.getTypeRepository(), 
                LogicLanguageServices.getTypeComparator(), 
                lockConstants));
            }
            catch (LogicalExpressionRuntimeException e)
            {
              LogicalExpression exp;
              throw new DatasetException(e, readLineCounter, 
                f.getName());
            }
            LogicalExpression exp;
            if (!IsWellTyped.of(exp))
            {
              throw new DatasetException(
                "Expression not well-typed: " + exp, 
                readLineCounter, f.getName());
            }
            data.add(new TemporalSentence(
              new Sentence(currentSentence), exp, currentDate));
            currentSentence = null;
          }
        }
      }
      return new TemporalSentenceDataset(data);
    }
    catch (IOException e) {
      throw new DatasetException(e);
    }
  }

  public Iterator<TemporalSentence> iterator()
  {
    return this.data.iterator();
  }

  public int size()
  {
    return this.data.size();
  }
}

