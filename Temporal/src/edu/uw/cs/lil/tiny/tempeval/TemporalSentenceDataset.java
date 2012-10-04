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

	public static TemporalSentenceDataset read(File f,
			IStringFilter textFilter, boolean lockConstants) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			List<TemporalSentence> data = new LinkedList<TemporalSentence>();

			String currentSentence = null;
			String currentRefDate = null;
			String currentISO = null;
			String line;
			while ((line = in.readLine()) != null) {
				if ((!line.startsWith("//")) && (!line.equals(""))) {
					line = line.trim();
					if (currentSentence == null) {
						// Case we don't have a sentence, so we are supposed to get
						// a sentence
						line = line.replace("-", " ");
						currentSentence = textFilter.filter(line);
					} else if (currentRefDate == null) {
						currentRefDate = textFilter.filter(line);
					} else {
						currentISO = textFilter.filter(line);
						data.add(new TemporalSentence(new Sentence(
								currentSentence), currentRefDate, currentISO));
						currentSentence = null;
						currentRefDate = null;
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
