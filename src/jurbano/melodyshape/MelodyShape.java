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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import jurbano.melodyshape.alignment.GlobalAligner;
import jurbano.melodyshape.alignment.HybridAligner;
import jurbano.melodyshape.alignment.LocalAligner;
import jurbano.melodyshape.comparison.CombinedNGramComparer;
import jurbano.melodyshape.comparison.FrequencyNGramComparer;
import jurbano.melodyshape.comparison.IntervalPitchNGramComparer;
import jurbano.melodyshape.comparison.NGramMelodyComparer;
import jurbano.melodyshape.comparison.bspline.BSplinePitchNGramComparer;
import jurbano.melodyshape.comparison.bspline.BSplineShapeNGramComparer;
import jurbano.melodyshape.comparison.bspline.BSplineTimeNGramComparer;
import jurbano.melodyshape.model.InMemoryMelodyCollection;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;
import jurbano.melodyshape.model.MelodyReader;
import jurbano.melodyshape.model.MidiReader;
import jurbano.melodyshape.ranking.Algorithm;
import jurbano.melodyshape.ranking.Result;
import jurbano.melodyshape.ranking.SimpleResultRanker;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MelodyShape
{
	// TODO: handle exceptions
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		args = new String[] { "-q", "M:\\Research\\MIREX\\2010\\Symbolic melodic Similarity\\Documents\\0000.mid", "-c",
				"M:\\Research\\MIREX\\2010\\Symbolic melodic Similarity\\Documents", "-a", "2012-shapel", "-k", "10" };
		
		List<String> algs = Arrays.asList("2010-domain", "2010-pitch", "2010-shape", "2011-pitch", "2011-time", "2011-shape",
				"2012-shapeh", "2012-shapel", "2012-shapeg", "2012-time", "2012-shapetime", "2013-shapeh", "2013-time", "2013-shapetime");
		/**
		 * Create command line options
		 */
		Options options = new Options();
		// required arguments
		options.addOption(OptionBuilder.isRequired().hasArg().withArgName("file").withDescription("path to the query melody").create("q"));
		options.addOption(OptionBuilder.isRequired().hasArg().withArgName("dir").withDescription("path to collection of documents.").create("c"));
		options.addOption(OptionBuilder.isRequired().hasArg().withArgName("name").withDescription("algorithm to run:"
							+ "\n- 2010-domain, 2010-pitchderiv, 2010-shape" + "\n- 2011-shape, 2011-pitch, 2011-time"
							+ "\n- 2012-shapeh, 2012-shapel, 2012-shapeg, 2012-time, 2012-shapetime"
							+ "\n- 2013-shapeh, 2013-time, 2012-shapetime").create("a"));
		// optional arguments
		options.addOption(OptionBuilder.hasArg().withArgName("k").withDescription("number of documents to retrieve.").create("k"));
		options.addOption(OptionBuilder.withDescription("verbose, to stderr.").create("v"));
		options.addOption(OptionBuilder.withDescription("verbose a lot, to stderr.").create("vv"));
		
		CommandLineParser parser = new BasicParser();
		
		/**
		 * Parse command line options
		 */
		File qOpt = null;
		File dOpt = null;
		String aOpt = null;
		int kOpt = Integer.MAX_VALUE;
		try {
			CommandLine cmd = parser.parse(options, args);
			// query
			qOpt = new File(cmd.getOptionValue("q"));
			if (!qOpt.exists()) {
				System.err.println("Error: query file does not exist: " + cmd.getOptionValue("q"));
				System.exit(1);
			}
			// documents
			dOpt = new File(cmd.getOptionValue("c"));
			if (!dOpt.exists() || !dOpt.isDirectory()) {
				System.err.println("Error: documents directory does not exist: " + cmd.getOptionValue("c"));
				System.exit(1);
			}
			// algorithm
			aOpt = cmd.getOptionValue("a");
			if (!algs.contains(aOpt)) {
				System.err.println("Error: invalid algorithm name: " + cmd.getOptionValue("a"));
				System.exit(1);
			}
			// cutoff
			if (cmd.hasOption("k")) {
				try {
					kOpt = Integer.parseInt(cmd.getOptionValue("k"));
					if (kOpt < 1)
						throw new NumberFormatException();
				} catch (NumberFormatException ex) {
					System.err.println("Error: invalid cutoff k: " + cmd.getOptionValue("k"));
					System.exit(1);
				}
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			printUsage(options);
			System.exit(1);
		}
		
		/**
		 * Process request
		 */
		// query
		MelodyReader reader = new MidiReader();
		Melody query = null;
		try {
			query = reader.read("ID", qOpt.getAbsolutePath());// TODO: read
																// query id
		} catch (IOException ex) {
			System.err.println("Error: bad format in query file: " + ex.getMessage());
			System.exit(1);
		}
		// documents
		MelodyCollection coll = null;
		try {
			coll = new InMemoryMelodyCollection("NAME", dOpt.getAbsolutePath(), reader);
		} catch (IOException ex) {
			System.err.println("Error: bad format in document file: " + ex.getMessage());
			System.exit(1);
		}
		// algorithm
		// TODO:cache comparers
		Algorithm alg = null;
		if (aOpt.equals("2010-domain"))
			alg = new Algorithm(new NGramMelodyComparer(3, new HybridAligner(new FrequencyNGramComparer(coll, 3,
					new IntervalPitchNGramComparer()))), new SimpleResultRanker()); // TODO:not
																					// simple
																					// ranker
		else if (aOpt.equals("2010-pitch"))
			alg = new Algorithm(new NGramMelodyComparer(3, new HybridAligner(new FrequencyNGramComparer(coll, 3,
					new BSplinePitchNGramComparer()))), new SimpleResultRanker()); // TODO:not
																					// simple
																					// ranker
		else if (aOpt.equals("2010-shape"))
			alg = new Algorithm(new NGramMelodyComparer(3, new HybridAligner(new FrequencyNGramComparer(coll, 3,
					new BSplineShapeNGramComparer(8, 1, 0.5)))), new SimpleResultRanker()); // TODO:not
																							// simple
																							// ranker
		else if (aOpt.equals("2011-pitch"))
			alg = new Algorithm(new NGramMelodyComparer(4, new HybridAligner(new CombinedNGramComparer(new BSplinePitchNGramComparer(),
					new BSplineTimeNGramComparer(), 1, 0, 2.1838, 0.4772))), new SimpleResultRanker()); // TODO:not
																										// simple
																										// ranker
		else if (aOpt.equals("2011-time"))
			alg = new Algorithm(new NGramMelodyComparer(4, new HybridAligner(new CombinedNGramComparer(new BSplinePitchNGramComparer(),
					new BSplineTimeNGramComparer(), 1, 0.5, 2.1838, 0.4772))), new SimpleResultRanker()); // TODO:not
																											// simple
																											// ranker
		else if (aOpt.equals("2012-shapel"))
			alg = new Algorithm(new NGramMelodyComparer(3, new LocalAligner(new FrequencyNGramComparer(coll, 3,
					new BSplineShapeNGramComparer(8, 1, 0.5)))), new SimpleResultRanker()); // TODO:not
																							// simple
																							// ranker
		else if (aOpt.equals("2012-shapeg"))
			alg = new Algorithm(new NGramMelodyComparer(3, new GlobalAligner(new FrequencyNGramComparer(coll, 3,
					new BSplineShapeNGramComparer(8, 1, 0.5)))), new SimpleResultRanker()); // TODO:not
																							// simple
																							// ranker
		// run
		Result[] results = alg.runQuery(query, coll);
		// print results
		for (int k = 0; k < kOpt && k < results.length; k++) {
			Result res = results[k];
			System.out.println(query.getId() + "\t" + res.getMelody().getId() + "\t"
					+ String.format(Locale.ENGLISH, "%.5f", res.getScore()));
		}
	}
	
	static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		formatter.setOptionComparator(new Comparator<Option>() {
			public int compare(Option o1, Option o2) {
				if (o1.isRequired() && !o2.isRequired())
					return -1;
				if (o2.isRequired() && !o1.isRequired())
					return 1;
				return Integer.compare(o2.getId(), o1.getId());
			}
		});
		formatter.printHelp("jurbano.melodyshape", options);
	}
}
