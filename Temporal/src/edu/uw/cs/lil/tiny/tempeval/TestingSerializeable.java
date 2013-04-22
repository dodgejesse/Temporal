package edu.uw.cs.lil.tiny.tempeval;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.DatasetException;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;

public class TestingSerializeable implements java.io.Serializable {

	/**
	 * 
	 */
	private final List<TemporalSentence> data;
	// needed to serialize
	private static final long serialVersionUID = 1L;

	public TestingSerializeable(List<TemporalSentence> data) {
		this.data = Collections.unmodifiableList(data);
	}
	
	public static void save(String nameOfFile, TestingSerializeable data) throws FileNotFoundException, IOException{
		OutputStream file = new FileOutputStream(nameOfFile);
		OutputStream buffer = new BufferedOutputStream( file );
		ObjectOutput output = new ObjectOutputStream( buffer );

		output.writeObject(data);
		output.close();
	}
	
	
	public static TemporalSentenceDataset read(File f,
			IStringFilter textFilter, boolean lockConstants) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			List<TemporalSentence> data = new LinkedList<TemporalSentence>();
			
			String docID = null;
			String sentence = null;
			String charNum = null;
			String phrase = null;
			String refDate = null;
			String type = null;
			String val = null;
			String line;
			TemporalSentence prev = null;
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
						phrase = textFilter.filter(line);
					} else if (refDate == null) {
						refDate = textFilter.filter(line);
					} else if (type == null){
						type = textFilter.filter(line);
					} else if (val == null){
						val = textFilter.filter(line);
						TemporalSentence current;
						String depParse = dp.getParse(sentence);
						if (prev != null && prev.getSample().second()[0].equals(docID))
							current = new TemporalSentence(docID, sentence, charNum, new Sentence(phrase), refDate, type, val, prev, depParse);
						else
							current = new TemporalSentence(docID, sentence, charNum, new Sentence(phrase), refDate, type, val, null, depParse);
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

	
	
}
