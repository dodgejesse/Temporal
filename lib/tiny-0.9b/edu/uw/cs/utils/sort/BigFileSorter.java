package edu.uw.cs.utils.sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.io.CombinedSortedFileReader;

/**
 * Provides a sort service for very large files. Sort is done over lines with a
 * cap on the amount of memory used. However, it comes at the cost of heavy
 * usage of temporary files. Basically, the service requires a temporary space
 * that is equivalent to the size of the file to sort. The directory for the
 * input file must be writable. This object is thread safe.
 * 
 * @author Yoav Artzi
 */
public class BigFileSorter<T> {
	private final ILineServices<T>	lineServices;
	
	public BigFileSorter(ILineServices<T> lineServices) {
		this.lineServices = lineServices;
	}
	
	public void sort(String inputFilename, String outputFilename,
			int maxLinesPerSplit) throws IOException {
		// Open the input file
		final BufferedReader reader = new BufferedReader(new FileReader(
				inputFilename));
		
		// Used for the names of the temporary files
		final List<File> tmpFiles = new LinkedList<File>();
		int tempFileCounter = 0;
		final int randomNum = Math.abs(new Random().nextInt());
		final String baseTmpFilename = inputFilename + "." + randomNum + ".";
		
		// Split the input file into sorted temp files
		String line;
		final PriorityQueue<Pair<T, String>> currentLines = new PriorityQueue<Pair<T, String>>(
				maxLinesPerSplit, lineServices);
		while ((line = reader.readLine()) != null) {
			final Pair<T, String> keyLinePair = lineServices.lineToPair(line);
			currentLines.add(keyLinePair);
			
			if (currentLines.size() >= maxLinesPerSplit) {
				// We collected enough lines, dump into a file
				final File tmpFile = writeTmpFile(currentLines,
						tempFileCounter++, baseTmpFilename);
				tmpFiles.add(tmpFile);
			}
		}
		reader.close();
		
		// If we have lines stored, write them to a new temp file
		if (currentLines.size() > 0) {
			final File tmpFile = writeTmpFile(currentLines, tempFileCounter++,
					baseTmpFilename);
			tmpFiles.add(tmpFile);
		}
		
		// Take all the temporary files and merge them into the output file
		final CombinedSortedFileReader tmpReader = new CombinedSortedFileReader(
				tmpFiles.toArray(new File[0]), new Comparator<String>() {
					public int compare(String o1, String o2) {
						final Pair<T, String> p1 = lineServices.lineToPair(o1);
						final Pair<T, String> p2 = lineServices.lineToPair(o2);
						
						return lineServices.compare(p1, p2);
					}
				});
		
		// Open the output file
		final BufferedWriter out = new BufferedWriter(new FileWriter(
				outputFilename));
		
		Pair<String, Integer> line2;
		while ((line2 = tmpReader.readLine()) != null) {
			out.write(line2.first() + "\n");
		}
		
		out.close();
		tmpReader.close();
		
		// Delete all the temporary files
		for (final File f : tmpFiles) {
			f.delete();
		}
		
	}
	
	private File writeTmpFile(PriorityQueue<Pair<T, String>> currentLines,
			int i, String baseTmpFilename) throws IOException {
		// Create the temporary file
		final File tmpFile = new File(baseTmpFilename + i + ".tmp");
		final BufferedWriter tmpWriter = new BufferedWriter(new FileWriter(
				tmpFile));
		// Write all pairs in order
		while (!currentLines.isEmpty()) {
			final Pair<T, String> p = currentLines.remove();
			tmpWriter.write(p.second() + "\n");
		}
		
		tmpWriter.close();
		currentLines.clear();
		
		return tmpFile;
	}
	
	public interface ILineServices<T> extends Comparator<Pair<T, String>> {
		/**
		 * The created pair should have the key as the first member and the
		 * second should be the entire line.
		 * 
		 * @param line
		 * @return
		 */
		Pair<T, String> lineToPair(String line);
	}
}
