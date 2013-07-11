package edu.uw.cs.lil.tiny.tempeval.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.util.Filters;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;

public class TemporalReader extends DefaultHandler {
	final private static String SERIALIZED_DIR = "data/serialized_data/";
	private String currentText;
	private String currentMention;
	private String currentDocID;
	private boolean isReadingText;
	private boolean isReadingMention;
	private boolean isReadingDocID;
	private TemporalDocument currentDocument;
	private StanfordCoreNLP pipeline;
	private GrammaticalStructureFactory gsf;
	private SAXParser sp;

	public TemporalReader(){
	}

	private void initLibraries() throws ParserConfigurationException, SAXException {
		// Initialize annotation pipeline for preprocessing
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, parse");
		pipeline = new StanfordCoreNLP(props);
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory(Filters.<String>acceptFilter(), new SemanticHeadFinder(false));

		// Initialize xml parser
		SAXParserFactory spfac = SAXParserFactory.newInstance();
		sp = spfac.newSAXParser();
	}

	public TemporalDataset getDataset(String datasetRoot, String[] datasetNames, boolean forceSerialize) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException {
		String serializedName = "";
		for(String s : datasetNames) {
			if (serializedName.length() > 0)
				serializedName += "_";
			serializedName += s;
		}
		File serializedFile = new File(SERIALIZED_DIR + serializedName + ".ser");
		if(forceSerialize || !serializedFile.exists()) {
			if (forceSerialize)			
				System.out.println("Forcing serialization.");
			else
				System.out.println("Serialized data unavailable.");

			initLibraries();

			System.out.println("Reading and dependency parsing data...");

			long startTime = System.nanoTime();

			TemporalDataset dataset = new TemporalDataset();

			for(String datasetName : datasetNames) {
				File xmlDir = new File(datasetRoot + datasetName);
				File[] xmlFiles = xmlDir.listFiles();
				System.out.printf("Reading %d files from %s\n", xmlFiles.length, datasetName);
				int count = 0;
				for(File f: xmlFiles) {
					count ++;
					System.out.printf("[%d/%d] Parsing %s\n", count, xmlFiles.length, f.getName());
					currentDocument = new TemporalDocument();
					sp.parse(xmlDir.getPath() + "/" + f.getName(), this);
					currentDocument.doPreprocessing(pipeline, gsf);
					dataset.addSentences(currentDocument.getSentences());
				}
			}
			dataset.serialize(serializedFile.getPath());

			long endTime = System.nanoTime();
			System.out.printf("%d sentences parsed and serialized (%.2f seconds)\n", dataset.size(), (endTime - startTime)*1.0e-9);
			return dataset;
		}
		else {
			System.out.println("Serialized data found. Deserializing data...");
			return TemporalDataset.deserialize(serializedFile.getPath());
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("TIMEX3")) {
			String value = attributes.getValue("value");

			if (attributes.getValue("tid").equals("t0"))
				currentDocument.setReferenceTime(value);
			else {
				String type = attributes.getValue("type");
				int offset;
				if (isReadingText)
					offset = currentText.length();
				else
					offset = -1;

				currentDocument.insertMention(type, value, offset);
				isReadingMention = true;
			}
			currentMention = "";
		}
		else if (qName.equals("TEXT")) {
			isReadingText = true;
			currentText = "";
		}
		else if (qName.equals("DOCID")){
			isReadingDocID = true;
			currentDocID = "";
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (isReadingMention && qName.equals("TIMEX3")) {
			currentDocument.setLastMentionText(currentMention);
			isReadingMention = false;
		}
		else if (qName.equals("TEXT")) {
			currentDocument.setText(currentText);
			isReadingText = false;
		}
		else if (qName.equals("DOCID")){
			currentDocument.setDocID(currentDocID);
			isReadingDocID = false;
		}
	}

	public void characters(char[] buffer, int start, int length) {
		if (isReadingText)
			currentText += new String(buffer, start, length);
		if (isReadingMention)
			currentMention += new String(buffer, start, length);
		if (isReadingDocID)
			currentDocID += new String(buffer, start, length);
	}

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException {
		//String[] datasets = {"AQUAINT", "Timebank"};
		String[] datasets = {"debug_dataset"};
		TemporalDataset dataset = new TemporalReader().getDataset("data/TempEval3/TBAQ-cleaned/", datasets, true);
		for (TemporalSentence s : dataset) {
			if (s.getMentions().size() < 0)
				System.out.println(s);
		}
	}
}
