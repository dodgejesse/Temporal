package edu.uw.cs.lil.tiny.tempeval;

public class TemporalMultiplication extends TemporalPredicate
{
  TemporalDuration dur;
  int num;

  public void addDur(TemporalDuration d)
  {
    this.dur = d;
  }

  public void addInt(int n) {
    this.num = n;
  }

  public TemporalDuration perform()
  {
    throw new IllegalArgumentException("the 'multiply' method within TemporalMultiplication hasn't been implemented yet.");
  }
}

