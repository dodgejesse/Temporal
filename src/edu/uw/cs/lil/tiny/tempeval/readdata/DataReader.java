package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class DataReader extends DefaultHandler {
	final private static String XML_DIR = "data/TempEval3/TBAQ-cleaned/TimeBank/";
	private static String currentText;
	private static String currentTimex;
	private static String currentDocID;
	private static int lastTimexID;
	private static boolean isReadingText;
	private static boolean isReadingTimex;
	private static boolean isReadingDocID;
	private static TemporalDocument currentDocument;

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		List<TemporalDocument> docs = readDocuments(XML_DIR);
		System.out.printf("%d documents read", docs.size());
	}
	
	public static List<TemporalDocument> readDocuments(String xmlDir) throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory spfac = SAXParserFactory.newInstance();
		SAXParser sp = spfac.newSAXParser();

		DataReader handler = new DataReader();
		
		LinkedList<TemporalDocument> documents = new LinkedList<TemporalDocument>();
		
		// Read in raw xml files
		File folder = new File(xmlDir);
		for(File f: folder.listFiles()) {
			currentDocument = new TemporalDocument(xmlDir + f.getName());
			sp.parse(currentDocument.getSource(), handler);
			documents.add(currentDocument);
		}
		
		// Only initialize pipeline for preprocessing once
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		for(TemporalDocument doc : documents)
			doc.doPreprocessing(pipeline);
		
		return documents;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("TIMEX3")) {
			int id = Integer.parseInt(attributes.getValue("tid").substring(1));
			String type = attributes.getValue("type");
			String value = attributes.getValue("value");
			int anchor;
			if (attributes.getIndex("anchorTimeID") == -1)
				anchor = -1;
			else
				anchor = Integer.parseInt(attributes.getValue("anchorTimeID").substring(1));
			int offset;
			if (isReadingText)
				offset = currentText.length();
			else
				offset = -1;
			lastTimexID = id;
			currentDocument.insertTimex(id, type, value, anchor, offset);
			isReadingTimex = true;
			currentTimex = "";
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
		if (qName.equals("TIMEX3")) {
			currentDocument.setTimexText(lastTimexID, currentTimex);
			isReadingTimex = false;
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
		if (isReadingTimex)
			currentTimex += new String(buffer, start, length);
		if (isReadingDocID)
			currentDocID += new String(buffer, start, length);
	}
}
