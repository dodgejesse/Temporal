package edu.uw.cs.lil.tiny.tempeval.structures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataCollection;

public class TemporalDataset implements IDataCollection<NewTemporalSentence>, java.io.Serializable{
	LinkedList<NewTemporalSentence> sentences;
	private static final long serialVersionUID = -9138434230690313768L;

	public TemporalDataset() {
		sentences = new LinkedList<NewTemporalSentence>();
	}
	
	public TemporalDataset(List<NewTemporalSentence> sentences) {
		this.sentences = new LinkedList<NewTemporalSentence>(sentences);
	}

	public Iterator<NewTemporalSentence> iterator() {
		return sentences.iterator();
	}

	public int size() {
		return sentences.size();
	}

	public void addSentences(List<NewTemporalSentence> newSentences) {
		sentences.addAll(newSentences);
	}

	public void serialize(String filename) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();
		fileOut.close();
	}

	public static TemporalDataset deserialize(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		TemporalDataset dataset = (TemporalDataset) in.readObject();
		in.close();
		fileIn.close();
		return dataset;
	}

	public List<List<NewTemporalSentence>> partition(int k) {
		List<List<NewTemporalSentence>> partitions = new ArrayList<List<NewTemporalSentence>>(k);
		for(int i = 0 ; i < k ; i++)
			partitions.add(new LinkedList<NewTemporalSentence>());
		int count = 0;
		for (NewTemporalSentence s : this)
			partitions.get(count % k).add(s);
		return partitions;
	}
}
