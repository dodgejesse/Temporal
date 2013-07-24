package edu.uw.cs.lil.temporal.structures;

import edu.uw.cs.lil.tiny.data.IDataCollection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TemporalMentionDataset implements IDataCollection<TemporalMention>{
	private List<TemporalMention> mentions;
	
	public TemporalMentionDataset(List<TemporalMention> mentions) {
		this.mentions = mentions;
	}
	
	public TemporalMentionDataset() {
		mentions = new LinkedList<TemporalMention>();
	}
	
	public Iterator<TemporalMention> iterator() {
		return mentions.iterator();
	}
	
	public void addMentions(List<TemporalMention> newMentions) {
		mentions.addAll(newMentions);
	}

	public String toString() {
		String s = "";
		for(TemporalMention m : mentions)
			s += "[" + m.getSentence().getReferenceTime() + "]\n";
		return s;
	}

	public int size() {
		return mentions.size();
	}
}
