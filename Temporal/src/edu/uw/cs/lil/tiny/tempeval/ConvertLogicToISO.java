package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.TemporalVisitor;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConvertLogicToISO
{
  public static void convertLogicToISO(LogicalExpression[] logicExprs)
  {
    for (int i = 0; i < logicExprs.length; i++) {
      System.out.println("The logical expression: " + 
        logicExprs[i].toString());
      System.out.println("Now the execution of the visitor:");
      TemporalVisitor.of(logicExprs[i]);
    }
  }

  public static void main(String[] args)
  {
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

    List logicalForms = readLogicFromFile();

    LogicalExpression[] logicExprs = parseSemantics(logicalForms, 
      categoryServices);
    convertLogicToISO(logicExprs);
  }

  public static LogicalExpression[] parseSemantics(List<String> logicalForms, ICategoryServices<LogicalExpression> categoryServices)
  {
    LogicalExpression[] logicExprs = new LogicalExpression[logicalForms
      .size()];
    int counter = 0;
    for (String str : logicalForms) {
      logicExprs[counter] = ((LogicalExpression)categoryServices.parseSemantics(str));
      counter++;
    }
    return logicExprs;
  }

  public static List<String> readLogicFromFile()
  {
    List logicalForms = new ArrayList();
    try {
      BufferedReader in = new BufferedReader(
        new FileReader(
        "/home/Jesse/UW/LSZ_research/parsing_time/parser_files/tempeval/tempeval.logic_with_context"));
      String str;
      while ((str = in.readLine()) != null)
      {
        String str;
        if (!str.startsWith("//"))
        {
          logicalForms.add(str);
        }
      }
      in.close();
    } catch (IOException e) {
      System.out
        .println("Failed reading text file in readLogicFromFile()");
    }
    return logicalForms;
  }
}

