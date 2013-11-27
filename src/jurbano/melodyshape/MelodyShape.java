// Copyright (C) 2013  Julián Urbano <urbano.julian@gmail.com>
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.

package jurbano.melodyshape;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jurbano.melodyshape.comparison.CachedNGramComparer;
import jurbano.melodyshape.comparison.CombinedNGramComparer;
import jurbano.melodyshape.comparison.EqualPitchNGramComparer;
import jurbano.melodyshape.comparison.FrequencyNGramComparer;
import jurbano.melodyshape.comparison.IntervalPitchNGramComparer;
import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.comparison.NGramMelodyComparer;
import jurbano.melodyshape.comparison.alignment.GlobalAligner;
import jurbano.melodyshape.comparison.alignment.HybridAligner;
import jurbano.melodyshape.comparison.alignment.LocalAligner;
import jurbano.melodyshape.comparison.bspline.BSplinePitchNGramComparer;
import jurbano.melodyshape.comparison.bspline.BSplineShapeNGramComparer;
import jurbano.melodyshape.comparison.bspline.BSplineTimeNGramComparer;
import jurbano.melodyshape.model.InMemoryMelodyCollection;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;
import jurbano.melodyshape.model.MelodyReader;
import jurbano.melodyshape.model.MidiReader;
import jurbano.melodyshape.ranking.UntieResultRanker;
import jurbano.melodyshape.ranking.Result;
import jurbano.melodyshape.ranking.ResultRanker;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@SuppressWarnings("unused")
public class MelodyShape
{
	static int VERBOSE_PERIOD = 50;
	static String VERSION = "1.0";
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {		
		List<String> algorithms = Arrays.asList(
				"2010-domain", "2010-pitchderiv", "2010-shape",
				"2011-pitch", "2011-time", "2011-shape",
				"2012-shapeh", "2012-shapel", "2012-shapeg", "2012-time", "2012-shapetime",
				"2013-shapeh", "2013-time", "2013-shapetime");
		/**
		 * Create command line options
		 */
		Options options = new Options();
		// required arguments
		options.addOption(OptionBuilder
				.isRequired().hasArg().withArgName("file/dir")
				.withDescription("path to the query melody or melodies.").create("q"));
		options.addOption(OptionBuilder
				.isRequired().hasArg().withArgName("dir")
				.withDescription("path to the collection of documents.").create("c"));
		options.addOption(OptionBuilder
				.isRequired().hasArg().withArgName("name")
				.withDescription("algorithm to run:" + "\n- 2010-domain, 2010-pitchderiv, 2010-shape"
						+ "\n- 2011-shape, 2011-pitch, 2011-time"
						+ "\n- 2012-shapeh, 2012-shapel, 2012-shapeg, 2012-time, 2012-shapetime"
						+ "\n- 2013-shapeh, 2013-time, 2013-shapetime").create("a"));
		// optional arguments
		options.addOption(OptionBuilder.withDescription("show results in a single line (omits similarity scores).").create("l"));
		options.addOption(OptionBuilder.hasArg().withArgName("num").withDescription("run a fixed number of threads.").create("t"));
		options.addOption(OptionBuilder.hasArg().withArgName("cutoff").withDescription("number of documents to retrieve.").create("k"));
		options.addOption(OptionBuilder.withDescription("verbose, to stderr.").create("v"));
		options.addOption(OptionBuilder.withDescription("verbose a lot, to stderr.").create("vv"));
		options.addOption(OptionBuilder.withDescription("show this help message.").create("h"));
		
		CommandLineParser parser = new BasicParser();
		
		/********************************************************************************
		 * P a r s e   C o m m a n d   L i n e   A r g u m e n t s                      *
		 ********************************************************************************/
		// help? Cannot wait to parse args because it will throw exception before
		for(String arg :args)
			if(arg.equals("-h")) {
				printUsage(options);
				System.exit(0);
			}
		
		File qOpt = null;
		File cOpt = null;
		String aOpt = null;
		boolean lOpt = false;
		int tOpt = Runtime.getRuntime().availableProcessors();
		int kOpt = Integer.MAX_VALUE;
		int vOpt = 0;
		try {
			CommandLine cmd = parser.parse(options, args);
			
			// query
			qOpt = new File(cmd.getOptionValue("q"));
			if (!qOpt.exists())
				MelodyShape.errorAndExit("query file does not exist: " + cmd.getOptionValue("q"));
			// documents
			cOpt = new File(cmd.getOptionValue("c"));
			if (!cOpt.exists() || !cOpt.isDirectory())
				MelodyShape.errorAndExit("documents directory does not exist: " + cmd.getOptionValue("c"));
			// algorithm
			aOpt = cmd.getOptionValue("a");
			if (!algorithms.contains(aOpt))
				MelodyShape.errorAndExit("invalid algorithm name: " + cmd.getOptionValue("a"));
			// single-line
			if (cmd.hasOption("l"))
				lOpt = true;
			// threads
			if (cmd.hasOption("t")) {
				try {
					int tOptArg = Integer.parseInt(cmd.getOptionValue("t"));
					if (tOptArg < 1)
						MelodyShape.errorAndExit("invalid number ot threads: " + cmd.getOptionValue("t"));
					tOpt = tOptArg;
				} catch (NumberFormatException ex) {
					MelodyShape.errorAndExit("invalid number ot threads: " + cmd.getOptionValue("t"));
				}
			}
			// cutoff
			if (cmd.hasOption("k")) {
				try {
					kOpt = Integer.parseInt(cmd.getOptionValue("k"));
					if (kOpt < 1)
						MelodyShape.errorAndExit("invalid cutoff k: " + kOpt);
				} catch (NumberFormatException ex) {
					MelodyShape.errorAndExit("invalid cutoff k: " + cmd.getOptionValue("k"));
				}
			}
			// verbose
			if (cmd.hasOption("v"))
				vOpt = 1;
			if (cmd.hasOption("vv"))
				vOpt = 2;
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			printUsage(options);
			System.exit(1);
		}
		
		/********************************************************************************
		 * P r o c e s s   R e q u e s t                                                *
		 ********************************************************************************/
		// query
		MelodyReader reader = new MidiReader();
		ArrayList<Melody> queries = new ArrayList<Melody>();		
		try {
			if (vOpt == 2)
				System.err.print("Reading queries...");
			if (qOpt.isDirectory()) {
				// query path is a directory, read all files
				for (File qFile : qOpt.listFiles())
					if (reader.accept(qOpt, qFile.getName()))
						queries.add(reader.read(qFile.getName(), qFile.getAbsolutePath()));
			} else {
				// query path is a single file
				queries.add(reader.read(qOpt.getName(), qOpt.getAbsolutePath()));
			}
			if (vOpt == 2)
				System.err.println("done (" + queries.size() + " melodies).");
		} catch (IOException ex) {
			MelodyShape.errorAndExit("bad format in query file: " + ex.getMessage());
		}
		// documents
		MelodyCollection coll = null;
		try {
			if (vOpt == 2)
				System.err.print("Reading collection...");
			coll = new InMemoryMelodyCollection("NAME", cOpt.getAbsolutePath(), reader);
			if (vOpt == 2)
				System.err.println("done (" + coll.size() + " melodies).");
		} catch (IOException ex) {
			MelodyShape.errorAndExit("bad format in document file: " + ex.getMessage());
		}
		// comparer
		NGramMelodyComparer melodyCmp = null;
		NGramMelodyComparer melodyCmpRerank = null; // for 201x-shapetime
		if (vOpt == 2)
			System.err.print("Instantiating algorithm...");
		if (aOpt.equals("2010-domain"))
			melodyCmp = new NGramMelodyComparer(3, new HybridAligner(
					new FrequencyNGramComparer(coll, 3, new IntervalPitchNGramComparer()))); // faster without cache
		else if (aOpt.equals("2010-pitchderiv"))
			melodyCmp = new NGramMelodyComparer(3, new HybridAligner(new CachedNGramComparer(
					new FrequencyNGramComparer(coll, 3,	new BSplinePitchNGramComparer()))));
		else if (aOpt.equals("2010-shape") || aOpt.equals("2011-shape") || aOpt.equals("2012-shapeh") || aOpt.equals("2013-shapeh")
				|| aOpt.equals("2012-shapetime") || aOpt.equals("2013-shapetime"))
			melodyCmp = new NGramMelodyComparer(3, new HybridAligner(new CachedNGramComparer(
					new FrequencyNGramComparer(coll, 3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else if (aOpt.equals("2011-pitch"))
			melodyCmp = new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(
					new CombinedNGramComparer(
							new BSplinePitchNGramComparer(), 1, 2.1838,
							new BSplineTimeNGramComparer(), 0, 0.4772))));
		else if (aOpt.equals("2011-time") || aOpt.equals("2012-time") || aOpt.equals("2013-time"))
			melodyCmp = new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(
					new CombinedNGramComparer(
							new BSplinePitchNGramComparer(), 1, 2.1838,
							new BSplineTimeNGramComparer(), 0.5, 0.4772))));
		else if (aOpt.equals("2012-shapel"))
			melodyCmp = new NGramMelodyComparer(3, new LocalAligner(new CachedNGramComparer(
					new FrequencyNGramComparer(coll, 3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else if (aOpt.equals("2012-shapeg"))
			melodyCmp = new NGramMelodyComparer(3, new GlobalAligner(new CachedNGramComparer(
					new FrequencyNGramComparer(coll, 3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else 
			MelodyShape.errorAndExit("unrecognized algorithm name: " + aOpt);
		if(aOpt.equals("2012-shapetime") || aOpt.equals("2013-shapetime"))
			melodyCmpRerank = new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(
					new CombinedNGramComparer(
							new BSplinePitchNGramComparer(), 1, 2.1838,
							new BSplineTimeNGramComparer(), 0.5, 0.4772))));		
		// ranker
		ResultRanker ranker = null;
		ResultRanker rankerRerank = null; // for 201x-shapetime
		if (Arrays.asList("2010-domain", "2010-pitchderiv", "2010-shape", "2011-shape", "2012-shapeh", "2012-shapel", "2012-shapeg",
				"2012-shapetime", "2013-shapeh", "2013-shapetime").contains(aOpt))
			ranker = new UntieResultRanker(new NGramMelodyComparer(3, new HybridAligner(new EqualPitchNGramComparer())));
		else
			ranker = new UntieResultRanker(new NGramMelodyComparer(4, new HybridAligner(new EqualPitchNGramComparer())));
		if(aOpt.equals("2012-shapetime") || aOpt.equals("2013-shapetime"))
			rankerRerank = new UntieResultRanker(new NGramMelodyComparer(4, new HybridAligner(new EqualPitchNGramComparer())));

		if (vOpt == 2) {
			System.err.println("done.");
			System.err.println();
			System.err.println("  Comparer: " + melodyCmp.getName());
			if(aOpt.equals("2012-shapetime") || aOpt.equals("2013-shapetime"))
				System.err.println("    Ranker: "+melodyCmpRerank.getName());
			else
				System.err.println("    Ranker: " + ranker.getName());
			System.err.println("   Threads: " + tOpt);
		}
		
		/********************************************************************************
		 * R u n   A l g o r i t h m                                                    *
		 ********************************************************************************/
		for (int queryNum = 0; queryNum < queries.size(); queryNum++) {
			Melody query = queries.get(queryNum);
			// run comparer
			long before = System.currentTimeMillis();
			Result[] results = MelodyShape.runQuery(melodyCmp, query, coll, coll.size(), queryNum + 1, queries.size(), vOpt, tOpt);
			// run ranker
			if (vOpt == 2)
				System.err.print("ranking...");
			ranker.rank(query, results, kOpt);
			
			// Rerank top-k results in 201x-shapetime
			if (aOpt.equals("2012-shapetime") || aOpt.equals("2013-shapetime")) {
				// Get top results with score as large as the k-th (can be more than k due to ties)
				double kScore = results[Math.min(kOpt, results.length)].getScore();
				ArrayList<Melody> melodiesRerank = new ArrayList<Melody>();
				for (int k = 0; k < results.length; k++)
					if (results[k].getScore() >= kScore)
						melodiesRerank.add(results[k].getMelody());
					else
						break;
				// rerun
				results = MelodyShape.runQuery(melodyCmpRerank, query, melodiesRerank, melodiesRerank.size(), queryNum + 1, queries.size(),
						0, tOpt);
				// rerank
				rankerRerank.rank(query, results, kOpt);
			}
			
			long after = System.currentTimeMillis();
			if (vOpt == 1)
				System.err.println("done.");
			else if (vOpt == 2)
				System.err.println("done (" + (after - before) / 1000 + " sec).");
			
			// output results
			for (int k = 0; k < kOpt && k < results.length; k++) {
				Result res = results[k];
				if (queries.size() == 1) {
					// just one query, don't output query ID
					if (lOpt)
						if (k + 1 < kOpt && k + 1 < results.length)
							System.out.print(res.getMelody().getId() + "\t");
						else
							System.out.println(res.getMelody().getId());
					else
						System.out.println(res.getMelody().getId() + "\t" + String.format(Locale.ENGLISH, "%.8f", res.getScore()));
				} else {
					// several queries, output query IDs
					if (lOpt) {
						if (k == 0)
							System.out.print(query.getId() + "\t");
						if (k + 1 < kOpt && k + 1 < results.length)
							System.out.print(res.getMelody().getId() + "\t");
						else
							System.out.println(res.getMelody().getId());
					} else
						System.out.println(query.getId() + "\t" + res.getMelody().getId() + "\t"
								+ String.format(Locale.ENGLISH, "%.8f", res.getScore()));
				}
			}
		}
	}
	
	/**
	 * Runs a {@link MelodyComparer} for a query {@link Melody} and a collection
	 * of melodies.
	 * 
	 * @param melodyCmp
	 *            the comparer to use.
	 * @param query
	 *            the query melody.
	 * @param coll
	 *            the collection of melodies.
	 * @param collSize
	 *            the number of melodies in the collection.
	 * @param numQuery
	 *            the query number, for verbosing purposes.
	 * @param totalQueries
	 *            the total number of queries, for verbosing purposes.
	 * @param vOpt
	 *            the verbosing level.
	 * @param tOpt
	 *            the number of threads to use.
	 * @return
	 */
	static Result[] runQuery(final MelodyComparer melodyCmp, final Melody query, final Iterable<Melody> coll, final int collSize,
			final int numQuery, final int totalQueries, final int vOpt, int tOpt) {
		// Create one callable per melody
		ArrayList<Callable<Result>> callables = new ArrayList<>(collSize);
		CountDownLatch latch = new CountDownLatch(collSize);
		
		if (vOpt == 1)
			System.err.print("(" + numQuery + "/" + totalQueries + ") " + query.getId() + "...");
		
		for (final Melody m : coll) {
			callables.add(new Callable<Result>() {
				CountDownLatch latch;
				
				Callable<Result> init(CountDownLatch latch) {
					this.latch = latch;
					return this;
				}
				
				@Override
				public Result call() throws Exception {
					Result r = new Result(m, melodyCmp.compare(query, m));
					synchronized (this.latch) { // don't know if we need to sync
												// here, but just in case
						this.latch.countDown();
						if (vOpt == 2 && this.latch.getCount() % MelodyShape.VERBOSE_PERIOD == 0)
							printProgress(1.0 - ((double) this.latch.getCount()) / collSize, "(" + numQuery + "/" + totalQueries + ") "
									+ query.getId() + ":", "comparing...");
					}
					return r;
				}
			}.init(latch));
		}
		// Execute all callables and collect results
		ExecutorService service = Executors.newFixedThreadPool(tOpt);
		
		Result[] res = new Result[collSize];
		try {
			List<Future<Result>> futures = service.invokeAll(callables);
			if (vOpt >= 2)
				printProgress(1, "(" + numQuery + "/" + totalQueries + ") " + query.getId() + ":", "comparing...");
			for (int i = 0; i < futures.size(); i++)
				res[i] = futures.get(i).get();
			service.shutdown();
		} catch (InterruptedException | ExecutionException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}
	
	/**
	 * Prints a progress bar to {@code stderr}.
	 * 
	 * @param progress
	 *            the current progress, from 0 to 1.
	 * @param msgLeft
	 *            a message to display before the progress bar.
	 * @param msgRight
	 *            a message to display after the progress bar.
	 */
	static void printProgress(double progress, String msgLeft, String msgRight) {
		int width = 40;
		StringBuilder sb = new StringBuilder("\r");
		sb.append(msgLeft);
		sb.append(" [");
		int done = (int) (width * progress);
		int pending = width - done;
		for (int i = 0; i < done; i++)
			sb.append("=");
		if (pending > 0)
			sb.append(">");
		for (int i = 1; i < pending; i++)
			sb.append(" ");
		sb.append("] " + String.format("%3s", (int) (100 * progress)) + "% " + msgRight);
		System.err.print(sb.toString());
	}
	
	/**
	 * Prints an error message and stops the process execution.
	 * 
	 * @param msg
	 *            the error message.
	 */
	static void errorAndExit(String msg) {
		System.err.println("Error: " + msg);
		System.exit(1);
	}
	
	/**
	 * Prints the usage message to stderr.
	 * 
	 * @param options
	 *            the command line options.
	 */
	static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new Comparator<Option>() {
			public int compare(Option o1, Option o2) {
				List<String> options = Arrays.asList("q", "c", "a", "k", "l", "t", "v", "vv", "h");
				
				return Integer.compare(options.indexOf(o1.getOpt()), options.indexOf(o2.getOpt()));
			}
		});
		formatter.printHelp(new PrintWriter(System.err, true), Integer.MAX_VALUE, "melodyshape-" + MelodyShape.VERSION, null, options, 0,
				2, "\nMelodyShape 1.0  Copyright (C) 2013  Julian Urbano <urbano.julian@gmail.com>\n"
						+ "This program comes with ABSOLUTELY NO WARRANTY.\n"
						+ "This is free software, and you are welcome to redistribute it\n"
						+ "under the terms of the GNU General Public License version 3.", true);
	}
}
