package edu.uw.cs.lil.tiny.tempeval;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemporalIntersect extends TemporalPredicate
{
  public TemporalDate perform()
  {
    if ((this.first == null) || (this.second == null)) {
      throw new IllegalArgumentException(
        "Trying to intersect less than two dates!");
    }
    Map<String, Set<Integer>> intersectedDate = new HashMap<String, Set<Integer>>();
    //System.out.println("First: \n " + first);
    //System.out.println("Second: \n " + second);
    addStuff((TemporalDate)this.first, intersectedDate);
    addStuff((TemporalDate)this.second, intersectedDate);
    return new TemporalDate(intersectedDate);
  }

  public String toString()
  {
    String s = "TemporalPredicate with up to two dates:\n";
    s = s + this.first + "\n";
    s = s + this.second;
    return s;
  }

  private void addStuff(TemporalDate d, Map<String, Set<Integer>> iDate) {
    for (String s : d.getKeys())
      if (iDate.containsKey(s))
        (iDate.get(s)).addAll(d.getVal(s));
      else
        iDate.put(s, d.getVal(s));
  }
}

