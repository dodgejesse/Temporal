package edu.uw.cs.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.sort.PrimitiveComparators;

/**
 * Takes sorted files and reads them in a merged sorted way. To avoid ordering
 * conflicts, it's recommended that the input files will be sorted by Java
 * first. Pay attention, Linux 'sort' command line uses different ordering of
 * characters, so result is unpredictable.
 * 
 * @author Yoav Artzi
 */
public class CombinedSortedFileReader {
	private final BufferedReader[]			readers;
	PriorityQueue<Pair<String, Integer>>	nextLines;
	
	/**
	 * Uses the default comparator. This is the most efficient option.
	 * 
	 * @param files
	 * @throws IOException
	 */
	public CombinedSortedFileReader(File[] files) throws IOException {
		this(files, PrimitiveComparators.STRING_COMPARATOR);
	}
	
	/**
	 * @param files
	 *            Arrays of sorted files to read from. Each file must be sorted
	 *            on its own.
	 * @param lineComparator
	 *            Comparator for lines. If a comparator is given the reader
	 *            might be significantly slower.
	 * @throws IOException
	 */
	public CombinedSortedFileReader(File[] files,
			Comparator<String> lineComparator) throws IOException {
		final int len = files.length;
		this.readers = new BufferedReader[len];
		this.nextLines = new PriorityQueue<Pair<String, Integer>>(len,
				new Pair.PairComparator<String, Integer>(lineComparator,
						PrimitiveComparators.INTEGER_COMPARATOR));
		
		for (int i = 0; i < len; ++i) {
			readers[i] = new BufferedReader(new FileReader(files[i]));
			
			final String line = readers[i].readLine();
			
			if (line == null) {
				readers[i].close();
			} else {
				nextLines.add(Pair.of(line, i));
			}
		}
	}
	
	public void close() throws IOException {
		for (final BufferedReader reader : readers) {
			reader.close();
		}
	}
	
	/**
	 * @return Pair of the line and the index of the file from which it came.
	 * @throws IOException
	 */
	public Pair<String, Integer> readLine() throws IOException {
		final Pair<String, Integer> ret = nextLines.poll();
		
		if (ret != null) {
			// Try to get the next item for this file and place it in the queue
			final String newLine = readers[ret.second()].readLine();
			
			if (newLine != null) {
				// Add the new line to the next lines queue
				nextLines.add(Pair.of(newLine, ret.second()));
			}
		}
		
		return ret;
	}
	
	/**
	 * Optimized to read from two files without using any heavy data structures.
	 * 
	 * @author Yoav Artzi
	 */
	public static class DoubleSortedFileReader {
		private final BufferedReader	f1Reader;
		private final BufferedReader	f2Reader;
		private String					nextF1Line;
		private String					nextF2Line;
		
		/**
		 * @param f1
		 *            Line sorted file
		 * @param f2
		 *            Line sorted file
		 * @throws IOException
		 */
		public DoubleSortedFileReader(File f1, File f2) throws IOException {
			this.f1Reader = new BufferedReader(new FileReader(f1));
			this.f2Reader = new BufferedReader(new FileReader(f2));
			this.nextF1Line = f1Reader.readLine();
			this.nextF2Line = f2Reader.readLine();
		}
		
		public void close() throws IOException {
			f1Reader.close();
			f2Reader.close();
		}
		
		/**
		 * @return Pair of the line and the index of the file from which it
		 *         came. The index is 1 or 2, for the first or second argument
		 *         in the constructor.
		 * @throws IOException
		 */
		public Pair<String, Integer> readLine() throws IOException {
			final Pair<String, Integer> ret;
			if (nextF1Line == null && nextF2Line == null) {
				ret = null;
			} else if (nextF1Line == null) {
				// Return line from f2 and progress
				ret = Pair.of(nextF2Line, 2);
				nextF2Line = f2Reader.readLine();
			} else if (nextF2Line == null) {
				// Return line from f1 and progress
				ret = Pair.of(nextF1Line, 1);
				nextF1Line = f1Reader.readLine();
			} else {
				if (nextF1Line.compareTo(nextF2Line) <= 0) {
					// Return next from f1
					ret = Pair.of(nextF1Line, 1);
					nextF1Line = f1Reader.readLine();
				} else {
					// Return next from f2
					ret = Pair.of(nextF2Line, 2);
					nextF2Line = f2Reader.readLine();
				}
			}
			
			return ret;
		}
	}
}
