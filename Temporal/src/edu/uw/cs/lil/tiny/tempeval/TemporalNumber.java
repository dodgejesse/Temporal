package edu.uw.cs.lil.tiny.tempeval;

public class TemporalNumber extends TemporalISO
{
  public TemporalNumber(int n)
  {
    super("num", n);
  }

  public boolean stringInFields(String f)
  {
    return f.equals("num");
  }
}

