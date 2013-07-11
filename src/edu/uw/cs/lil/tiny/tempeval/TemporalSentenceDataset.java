package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.DatasetException;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.tempeval.structures.GoldSentence;
import edu.uw.cs.lil.tiny.tempeval.util.DependencyParser;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TemporalSentenceDataset implements IDataCollection<GoldSentence>{
	private final List<GoldSentence> data;
	// needed to serialize

	public TemporalSentenceDataset(List<GoldSentence> data) {
		this.data = Collections.unmodifiableList(data);
	}

	public static TemporalSentenceDataset read(File f,
			IStringFilter textFilter, boolean lockConstants) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			List<GoldSentence> data = new LinkedList<GoldSentence>();
			
			String docID = null;
			String sentence = null;
			String charNum = null;
			String phrase = null;
			String refDate = null;
			String type = null;
			String val = null;
			String line;
			GoldSentence prev = null;
			DependencyParser dp = new DependencyParser();
			System.out.println("Reading in the dataset, and running a dependency parser on each sentence... ");
			int sentenceCounter = 0;
			while ((line = in.readLine()) != null) {
				if ((!line.startsWith("//")) && (!line.equals(""))) {
					line = line.trim();
					if (docID == null)
						docID = textFilter.filter(line);
					else if (sentence == null)
						sentence = textFilter.filter(line);
					else if (charNum == null)
						charNum = textFilter.filter(line);
					else if (phrase == null) {
						// Case we don't have a phrase, so we are supposed to get one.
						line = line.replace("-", " ");
						line = line.replace(",","");
						phrase = textFilter.filter(line);
					} else if (refDate == null) {
						refDate = textFilter.filter(line);
					} else if (type == null){
						type = textFilter.filter(line);
					} else if (val == null){
						val = textFilter.filter(line);
						GoldSentence current;
						String depParse = dp.getParse(sentence);
						if (prev != null && prev.getSample().second()[0].equals(docID))
							current = new GoldSentence(docID, sentence, charNum, new Sentence(phrase), refDate, type, val, prev, depParse);
						else
							current = new GoldSentence(docID, sentence, charNum, new Sentence(phrase), refDate, type, val, null, depParse);
						data.add(current);
						
						prev = current;
						// To reset the variables to null. 
						docID = null;
						sentence = null;
						charNum = null;
						phrase = null;
						refDate = null;
						type = null;
						val = null;
						sentenceCounter++;
						System.out.print(".");
						if (sentenceCounter % 75 == 0)
							System.out.println();
					}
				}
			}
			System.out.println();
			System.out.println();
			in.close();
			return new TemporalSentenceDataset(data);
		} catch (IOException e) {
			throw new DatasetException(e);
		}
	}

	public Iterator<GoldSentence> iterator() {
		return this.data.iterator();
	}

	public int size() {
		return this.data.size();
	}
	
	public static void save(String nameOfFile, TemporalSentenceDataset data) throws FileNotFoundException, IOException{
		
		OutputStream file = new FileOutputStream(nameOfFile);
		OutputStream buffer = new BufferedOutputStream( file );
		ObjectOutput output = new ObjectOutputStream( buffer );

		output.writeObject(new SerializableTemporalDataset(data));
		output.close();
	}

	public static TemporalSentenceDataset readSerialized(String nameOfFile) throws IOException, ClassNotFoundException {
		InputStream file = new FileInputStream(nameOfFile);
		InputStream buffer = new BufferedInputStream( file );
		ObjectInput input = new ObjectInputStream ( buffer );
		
		
		SerializableTemporalDataset std = (SerializableTemporalDataset) input.readObject();
		input.close();
		
		return std.makeTemporalSentenceDataset();
		
	}
}
