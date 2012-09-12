package edu.uw.cs.lil.tiny.tempeval;

public abstract class TemporalPredicate
{
  TemporalISO first;
  TemporalISO second;

  public abstract TemporalISO perform();

  public void storeISO(TemporalISO other)
  {
    if (this.first == null)
      this.first = other;
    else if (this.second == null)
      this.second = other;
    else
      throw new IllegalArgumentException(
        "We have a problem in storeISO(TemporalISO d), in TemporalPredicate! There are already two ISOs stored, and we're trying to add another!");
  }
}

