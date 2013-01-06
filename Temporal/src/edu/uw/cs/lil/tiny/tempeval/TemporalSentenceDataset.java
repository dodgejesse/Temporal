package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.DatasetException;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TemporalSentenceDataset implements
		IDataCollection<TemporalSentence> {
	private final List<TemporalSentence> data;

	public TemporalSentenceDataset(List<TemporalSentence> data) {
		this.data = Collections.unmodifiableList(data);
	}

	// TODO: Test this!
	// Make pointer to previous mention.
	public static TemporalSentenceDataset read(File f,
			IStringFilter textFilter, boolean lockConstants) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			List<TemporalSentence> data = new LinkedList<TemporalSentence>();
			
			String docID = null;
			String sentence = null;
			String phrase = null;
			String refDate = null;
			String type = null;
			String val = null;
			String line;
			TemporalSentence prev = null;
			while ((line = in.readLine()) != null) {
				if ((!line.startsWith("//")) && (!line.equals(""))) {
					line = line.trim();
					if (docID == null)
						docID = textFilter.filter(line);
					else if (sentence == null)
						sentence = textFilter.filter(line);
					else if (phrase == null) {
						// Case we don't have a phrase, so we are supposed to get one.
						line = line.replace("-", " ");
						phrase = textFilter.filter(line);
					} else if (refDate == null) {
						refDate = textFilter.filter(line);
					} else if (type == null){
						type = textFilter.filter(line);
					} else if (val == null){
						val = textFilter.filter(line);
						TemporalSentence current;
						if (prev != null && prev.getSample().first()[0].equals(docID))
							current = new TemporalSentence(docID, sentence, new Sentence(phrase), refDate, type, val, prev);
						else
							current = new TemporalSentence(docID, sentence, new Sentence(phrase), refDate, type, val, null);
						data.add(current);
						
						prev = current;
						// To reset the variables to null. 
						docID = null;
						sentence = null;
						phrase = null;
						refDate = null;
						type = null;
						val = null;
					}
				}
			}
			in.close();
			return new TemporalSentenceDataset(data);
		} catch (IOException e) {
			throw new DatasetException(e);
		}
	}

	public Iterator<TemporalSentence> iterator() {
		return this.data.iterator();
	}

	public int size() {
		return this.data.size();
	}
}
