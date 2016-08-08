// Copyright (C) 2013, 2015-2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.ui;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jurbano.melodyshape.MelodyShape;
import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;
import jurbano.melodyshape.ranking.Result;
import jurbano.melodyshape.ranking.ResultRanker;

/**
 * Runs an algorithm with options specified through the command line.
 * 
 * @author Julián Urbano
 * @see UIObserver
 */
public class ConsoleUIObserver implements UIObserver {
	protected String[] args;
	protected Options options;

	protected File qOpt;
	protected File cOpt;
	protected String aOpt;
	protected boolean lOpt;
	protected boolean hOpt;
	protected int tOpt;
	protected int kOpt;
	protected int vOpt;

	/**
	 * Constructs a new {@code ConsoleUIObserver} according to some command line
	 * arguments.
	 * 
	 * @param args
	 *            the command line arguments to configure the algorithm.
	 */
	@SuppressWarnings("static-access")
	public ConsoleUIObserver(String[] args) {
		this.args = args;

		this.qOpt = null;
		this.cOpt = null;
		this.aOpt = null;
		this.lOpt = false;
		this.hOpt = false;
		this.tOpt = Runtime.getRuntime().availableProcessors();
		this.kOpt = Integer.MAX_VALUE;
		this.vOpt = 0;

		this.options = new Options();
		// required arguments
		this.options.addOption(OptionBuilder.isRequired().hasArg().withArgName("file/dir")
				.withDescription("path to the query melody or melodies.").create("q"));
		this.options.addOption(OptionBuilder.isRequired().hasArg().withArgName("dir")
				.withDescription("path to the collection of documents.").create("c"));
		this.options.addOption(OptionBuilder.isRequired().hasArg().withArgName("name")
				.withDescription(
						"algorithm to run:" + "\n- 2010-domain, 2010-pitchderiv, 2010-shape"
								+ "\n- 2011-shape, 2011-pitch, 2011-time"
								+ "\n- 2012-shapeh, 2012-shapel, 2012-shapeg, 2012-time, 2012-shapetime"
								+ "\n- 2013-shapeh, 2013-time, 2013-shapetime"
								+ "\n- 2014-shapeh, 2014-time, 2014-shapetime"
								+ "\n- 2015-shapeh, 2015-time, 2015-shapetime").create("a"));
		// optional arguments
		this.options.addOption(OptionBuilder
				.withDescription("show results in a single line (omits similarity scores).").create("l"));
		this.options.addOption(OptionBuilder.hasArg().withArgName("num")
				.withDescription("run a fixed number of threads.").create("t"));
		this.options.addOption(OptionBuilder.hasArg().withArgName("cutoff")
				.withDescription("number of documents to retrieve.").create("k"));
		this.options.addOption(OptionBuilder.withDescription("verbose, to stderr.").create("v"));
		this.options.addOption(OptionBuilder.withDescription("verbose a lot, to stderr.").create("vv"));
		this.options.addOption(OptionBuilder.withDescription("show this help message.").create("h"));
		this.options.addOption(OptionBuilder.withDescription("run with graphical user interface.").create("gui"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		/**
		 * Parse Command Line Options
		 */
		boolean parsed = this.parseArguments();
		if (!parsed)
			System.exit(1);

		if (this.hOpt) {
			this.printUsage(this.options);
			return;
		}

		// queries
		ArrayList<Melody> queries = null;
		try {
			this.verbose(2, "Reading queries...");
			queries = MelodyShape.readQueries(this.qOpt);
			this.verbose(2, "done (" + queries.size() + " melodies).\n");
		} catch (IllegalArgumentException ex) {
			System.err.println("Error: " + ex.getMessage());
			System.exit(1);
		}
		// documents
		MelodyCollection coll = null;
		try {
			this.verbose(2, "Reading collection...");
			coll = MelodyShape.readCollection(this.cOpt);
			this.verbose(2, "done (" + coll.size() + " melodies).\n");
		} catch (IllegalArgumentException ex) {
			System.err.println("Error: " + ex.getMessage());
			System.exit(1);
		}
		// algorithm
		this.verbose(2, "Instantiating algorithm...");
		MelodyComparer comparer = MelodyShape.getMainComparer(this.aOpt, coll);
		ResultRanker ranker = MelodyShape.getMainRanker(this.aOpt, coll);
		MelodyComparer comparerRerank = MelodyShape.getRerankComparer(this.aOpt, coll); // for 201x-shapetime
		ResultRanker rankerRerank = MelodyShape.getRerankRanker(this.aOpt, coll); // for 201x-shapetime

		this.verbose(2, "done.\n\n");
		this.verbose(2, "  Comparer: " + comparer.getName() + "\n");
		if (this.aOpt.equals("2012-shapetime") || this.aOpt.equals("2013-shapetime") || this.aOpt.equals("2014-shapetime")
				 || this.aOpt.equals("2015-shapetime"))
			this.verbose(2, "    Ranker: " + comparerRerank.getName() + "\n");
		else
			this.verbose(2, "    Ranker: " + ranker.getName() + "\n");
		this.verbose(2, "   Threads: " + this.tOpt + "\n");

		/**
		 * Run Algorithm
		 **/
		for (int queryNum = 0; queryNum < queries.size(); queryNum++) {
			Melody query = queries.get(queryNum);

			this.verbose(1, "(" + (queryNum + 1) + "/" + queries.size() + ") " + query.getId() + "...");
			this.verbose(2, "(" + (queryNum + 1) + "/" + queries.size() + ") " + query.getId() + ":");

			long before = System.currentTimeMillis();
			Result[] results = MelodyShape.runAlgorithm(comparer, comparerRerank, ranker, rankerRerank, this.kOpt,
					queries, queryNum, coll, this.tOpt, this);
			long after = System.currentTimeMillis();

			this.verbose(1, "done.\n");
			this.verbose(2, "done (" + (after - before) / 1000 + " sec).\n");

			this.printResults(queries, queryNum, results);
		}
	}

	protected boolean parseArguments() {
		CommandLineParser parser = new BasicParser();

		// help? Cannot wait to parse args because it will throw exception
		// before
		for (String arg : this.args)
			if (arg.equals("-h")) {
				this.hOpt = true;
				return true;
			}
		try {
			CommandLine cmd = parser.parse(options, args);

			// query
			this.qOpt = new File(cmd.getOptionValue("q"));
			if (!this.qOpt.exists()) {
				System.err.println("Error: query file does not exist: '" + cmd.getOptionValue("q") + "'");
				return false;
			}
			// documents
			this.cOpt = new File(cmd.getOptionValue("c"));
			if (!this.cOpt.exists() || !this.cOpt.isDirectory()) {
				System.err.println("Error: documents directory does not exist: '" + cmd.getOptionValue("c") + "'");
				return false;
			}
			// algorithm
			this.aOpt = cmd.getOptionValue("a");
			if (!MelodyShape.ALGORITHMS.contains(this.aOpt)) {
				System.err.println("Error: invalid algorithm name: '" + cmd.getOptionValue("a") + "'");
				return false;
			}
			// single-line
			if (cmd.hasOption("l"))
				this.lOpt = true;
			// threads
			if (cmd.hasOption("t")) {
				try {
					int tOptArg = Integer.parseInt(cmd.getOptionValue("t"));
					if (tOptArg < 1) {
						System.err.println("Error: invalid number ot threads: '" + cmd.getOptionValue("t") + "'");
						return false;
					}
					this.tOpt = tOptArg;
				} catch (NumberFormatException ex) {
					System.err.println("Error: invalid number ot threads: '" + cmd.getOptionValue("t") + "'");
					return false;
				}
			}
			// cutoff
			if (cmd.hasOption("k")) {
				try {
					this.kOpt = Integer.parseInt(cmd.getOptionValue("k"));
					if (this.kOpt < 1) {
						System.err.println("Error: invalid cutoff k: '" + kOpt + "'");
						return false;
					}
				} catch (NumberFormatException ex) {
					System.err.println("Error: invalid cutoff k: '" + cmd.getOptionValue("k") + "'");
					return false;
				}
			}
			// verbose
			if (cmd.hasOption("v"))
				this.vOpt = 1;
			if (cmd.hasOption("vv"))
				this.vOpt = 2;

			return true;
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			this.printUsage(options);
			return false;
		}
	}

	protected void printResults(ArrayList<Melody> queries, int queryIndex, Result[] results) {
		for (int k = 0; k < this.kOpt && k < results.length; k++) {
			Result res = results[k];
			if (queries.size() == 1) {
				// just one query, don't output query ID
				if (this.lOpt)
					if (k + 1 < this.kOpt && k + 1 < results.length)
						System.out.print(res.getMelody().getId() + "\t");
					else
						System.out.println(res.getMelody().getId());
				else
					System.out.println(res.getMelody().getId() + "\t"
							+ String.format(Locale.ENGLISH, "%.8f", res.getScore()));
			} else {
				// several queries, output query IDs
				if (this.lOpt) {
					if (k == 0)
						System.out.print(queries.get(queryIndex).getId() + "\t");
					if (k + 1 < this.kOpt && k + 1 < results.length)
						System.out.print(res.getMelody().getId() + "\t");
					else
						System.out.println(res.getMelody().getId());
				} else
					System.out.println(queries.get(queryIndex).getId() + "\t" + res.getMelody().getId() + "\t"
							+ String.format(Locale.ENGLISH, "%.8f", res.getScore()));
			}
		}
	}

	protected void verbose(int level, String msg) {
		if (this.vOpt == level)
			System.err.print(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProgressComparer(Melody query, int numQuery, int totalQueries, double progress) {
		int width = 40;
		StringBuilder sb = new StringBuilder("\r");
		sb.append("(" + (numQuery + 1) + "/" + totalQueries + ") " + query.getId() + ": [");
		int done = (int) (width * progress);
		int pending = width - done;
		for (int i = 0; i < done; i++)
			sb.append("=");
		if (pending > 0)
			sb.append(">");
		for (int i = 1; i < pending; i++)
			sb.append(" ");
		sb.append("] " + String.format("%3s", (int) (100 * progress)) + "% comparing...");
		this.verbose(2, sb.toString());
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void updateStartRanker(Melody query, int numQuery, int totalQueries) {
		this.verbose(2, "ranking...");
	}

	/**
	 * Prints the usage message to stderr.
	 * 
	 * @param options
	 *            the command line options.
	 */
	protected void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new Comparator<Option>() {
			public int compare(Option o1, Option o2) {
				List<String> options = Arrays.asList("q", "c", "a", "k", "l", "t", "v", "vv", "gui", "h");

				return Integer.compare(options.indexOf(o1.getOpt()), options.indexOf(o2.getOpt()));
			}
		});
		formatter.printHelp(new PrintWriter(System.err, true), Integer.MAX_VALUE, "melodyshape-" + MelodyShape.VERSION,
				null, options, 0, 2, "\n" + MelodyShape.COPYRIGHT_NOTICE, true);
	}
}
