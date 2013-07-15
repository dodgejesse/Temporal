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

public class TemporalDataset implements IDataCollection<TemporalSentence>, java.io.Serializable{
	LinkedList<TemporalSentence> sentences;
	private static final long serialVersionUID = -9138434230690313768L;

	public TemporalDataset() {
		sentences = new LinkedList<TemporalSentence>();
	}
	
	public TemporalDataset(List<TemporalSentence> sentences) {
		this.sentences = new LinkedList<TemporalSentence>(sentences);
	}

	public Iterator<TemporalSentence> iterator() {
		return sentences.iterator();
	}

	public int size() {
		return sentences.size();
	}

	public void addSentences(List<TemporalSentence> newSentences) {
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

	public List<List<TemporalSentence>> partition(int k) {
		List<List<TemporalSentence>> partitions = new ArrayList<List<TemporalSentence>>(k);
		for(int i = 0 ; i < k ; i++)
			partitions.add(new LinkedList<TemporalSentence>());
		double count = 0;
		for (TemporalSentence s : this) {
			partitions.get((int)(k * (count / size()))).add(s);
			count++;
		}
		return partitions;
	}
	
	public TemporalObservationDataset getObservations() {
		TemporalObservationDataset observations = new TemporalObservationDataset();
		for(TemporalSentence ts : sentences)
			observations.addObservations(ts.getObservations());
		return observations;
	}
}
