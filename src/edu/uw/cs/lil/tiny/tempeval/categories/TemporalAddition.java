package edu.uw.cs.lil.tiny.tempeval.categories;

public class TemporalAddition
{
  int num1;
  int num2;

  public TemporalAddition()
  {
    this.num1 = -1;
    this.num2 = -1;
  }

  public int add() {
    return this.num1 + this.num2;
  }

  public void storeNum(int n) {
    if (this.num1 == -1)
      this.num1 = n;
    else if (this.num2 == -1)
      this.num2 = n;
    else
      throw new IllegalArgumentException(
        "Adding more than two numbers in TemporalAddition. This is a problem.");
  }
}

