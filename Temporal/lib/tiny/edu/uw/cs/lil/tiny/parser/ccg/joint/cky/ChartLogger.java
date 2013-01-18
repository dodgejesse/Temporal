package edu.uw.cs.lil.tiny.parser.ccg.joint.cky;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Chart;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutputLogger;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.collections.CollectionUtils;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class ChartLogger<Z> implements IJointOutputLogger<LogicalExpression, Z> {
	private static final ILogger	LOG	= LoggerFactory
												.create(ChartLogger.class);
	private final File				outputDir;
	
	public ChartLogger(File outputDir) {
		this.outputDir = outputDir;
	}
	
	@Override
	public void log(IJointOutput<LogicalExpression, Z> output,
			IJointDataItemModel<LogicalExpression, Z> dataItemModel) {
		
		final Chart<LogicalExpression> chart = ((CKYParserOutput<LogicalExpression>) output
				.getBaseParserOutput()).getChart();
		try {
			final File file = new File(outputDir, String.format("chart-%d.txt",
					System.currentTimeMillis()));
			final FileWriter writer = new FileWriter(file);
			
			final Iterator<Cell<LogicalExpression>> iterator = chart
					.iterator(new Comparator<Cell<LogicalExpression>>() {
						
						@Override
						public int compare(Cell<LogicalExpression> o1,
								Cell<LogicalExpression> o2) {
							final int compare = Double.compare(
									o1.getPruneScore(), o2.getPruneScore());
							return compare == 0 ? o1.getCategroy().toString()
									.compareTo(o2.getCategroy().toString())
									: -compare;
						}
					});
			while (iterator.hasNext()) {
				final Cell<LogicalExpression> cell = iterator.next();
				writer.append(
						cell.toString(
								false,
								ListUtils.join(
										chart.getTokens().subList(
												cell.getStart(),
												cell.getEnd() + 1), " ")))
						.append(dataItemModel
								.getTheta()
								.printValues(
										cell.computeMaxAvgFeaturesRecursively(dataItemModel)))
						.append("\n");
			}
			
			writer.write("\n\n");
			for (final IJointParse<LogicalExpression, Z> parse : CollectionUtils
					.sorted(output.getAllJointParses(),
							new Comparator<IJointParse<LogicalExpression, Z>>() {
								
								@Override
								public int compare(
										IJointParse<LogicalExpression, Z> o1,
										IJointParse<LogicalExpression, Z> o2) {
									final int comp = Double.compare(
											o1.getScore(), o2.getScore());
									return comp == 0 ? o1.getY().toString()
											.compareTo(o2.getY().toString())
											: comp;
								}
							})) {
				writer.write(String.format("[%.2f] %s\n", parse.getScore(),
						parse.getY()));
				for (final LexicalEntry<LogicalExpression> entry : parse
						.getMaxLexicalEntries()) {
					writer.write(String.format(
							"\t[%f] %s [%s]\n",
							dataItemModel.score(entry),
							entry,
							dataItemModel.getTheta().printValues(
									dataItemModel.computeFeatures(entry))));
					writer.write(String.format(
							"\t%s\n",
							dataItemModel.getTheta().printValues(
									parse.getAverageMaxFeatureVector())));
				}
			}
			
			writer.close();
			LOG.info("Dumped chart to %s", file.getAbsolutePath());
		} catch (final IOException e) {
			LOG.error("Failed to write chart");
		}
	}
	
}
