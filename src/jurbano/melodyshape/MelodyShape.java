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

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import jurbano.melodyshape.ranking.Result;
import jurbano.melodyshape.ranking.ResultRanker;
import jurbano.melodyshape.ranking.UntieResultRanker;
import jurbano.melodyshape.ui.ConsoleUIObserver;
import jurbano.melodyshape.ui.GraphicalUIObserver;
import jurbano.melodyshape.ui.UIObserver;

public class MelodyShape
{
	/**
	 * Update progress to user interface every so melodies compared.
	 */
	public static int VERBOSE_PERIOD = 50;
	/**
	 * The release version number.
	 */
	public static String VERSION = "1.1";
	/**
	 * The list of algorithms available in the release.
	 */
	public static List<String> ALGORITHMS = Arrays.asList("2010-domain", "2010-pitchderiv", "2010-shape", "2011-pitch",
			"2011-time", "2011-shape", "2012-shapeh", "2012-shapel", "2012-shapeg", "2012-time", "2012-shapetime",
			"2013-shapeh", "2013-time", "2013-shapetime");

	/**
	 * Returns the list of queries found in a path.
	 * 
	 * @param path
	 *            the path to read queries from (a single file or a directory).
	 * @return the list of melodies read.
	 */
	public static ArrayList<Melody> readQueries(File path) {
		MelodyReader reader = new MidiReader();
		try {
			ArrayList<Melody> queries = new ArrayList<Melody>();
			if (path.isDirectory()) {
				// query path is a directory, read all files
				for (File qFile : path.listFiles())
					if (reader.accept(path, qFile.getName()))
						queries.add(reader.read(qFile.getName(), qFile.getAbsolutePath()));
			} else {
				// query path is a single file
				queries.add(reader.read(path.getName(), path.getAbsolutePath()));
			}
			return queries;
		} catch (IOException ex) {
			throw new IllegalArgumentException("bad format in query file:" + ex.getMessage(), ex);
		}
	}

	/**
	 * Returns a melody collection found in a path.
	 * 
	 * @param path
	 *            the path to the directory containing all melodies.
	 * @return the melody collection.
	 */
	public static MelodyCollection readCollection(File path) {
		MelodyReader reader = new MidiReader();
		try {
			return new InMemoryMelodyCollection(path.getName(), path.getAbsolutePath(), reader);
		} catch (IOException ex) {
			throw new IllegalArgumentException("bad format in document file: " + ex.getMessage());
		}
	}

	/**
	 * Returns a melody comparer given its name.
	 * 
	 * @param name
	 *            the name of the melody comparer.
	 * @param coll
	 *            the collection of melodies to use with the comparer.
	 * @return the melody comparer.
	 */
	public static MelodyComparer getComparer(String name, MelodyCollection coll) {
		if (name.equals("2010-domain"))
			return new NGramMelodyComparer(3, new HybridAligner(new FrequencyNGramComparer(coll, 3,
					new IntervalPitchNGramComparer()))); // faster without cache
		else if (name.equals("2010-pitchderiv"))
			return new NGramMelodyComparer(3, new HybridAligner(new CachedNGramComparer(new FrequencyNGramComparer(
					coll, 3, new BSplinePitchNGramComparer()))));
		else if (name.equals("2010-shape") || name.equals("2011-shape") || name.equals("2012-shapeh")
				|| name.equals("2013-shapeh"))
			return new NGramMelodyComparer(3, new HybridAligner(new CachedNGramComparer(new FrequencyNGramComparer(
					coll, 3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else if (name.equals("2011-pitch"))
			return new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(new CombinedNGramComparer(
					new BSplinePitchNGramComparer(), 1, 2.1838, new BSplineTimeNGramComparer(), 0, 0.4772))));
		else if (name.equals("2011-time") || name.equals("2012-time") || name.equals("2013-time"))
			return new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(new CombinedNGramComparer(
					new BSplinePitchNGramComparer(), 1, 2.1838, new BSplineTimeNGramComparer(), 0.5, 0.4772))));
		else if (name.equals("2012-shapel"))
			return new NGramMelodyComparer(3, new LocalAligner(new CachedNGramComparer(new FrequencyNGramComparer(coll,
					3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else if (name.equals("2012-shapeg"))
			return new NGramMelodyComparer(3, new GlobalAligner(new CachedNGramComparer(new FrequencyNGramComparer(
					coll, 3, new BSplineShapeNGramComparer(8, 1, 0.5)))));
		else if (name.equals("2012-shapetime") || name.equals("2013-shapetime"))
			return new NGramMelodyComparer(4, new HybridAligner(new CachedNGramComparer(new CombinedNGramComparer(
					new BSplinePitchNGramComparer(), 1, 2.1838, new BSplineTimeNGramComparer(), 0.5, 0.4772))));
		else
			throw new IllegalArgumentException("unrecognized algorithm name: " + name);
	}

	/**
	 * Returns a results ranker given its name.
	 * 
	 * @param name
	 *            the name of the results ranker.
	 * @param coll
	 *            the collection of melodies to compare.
	 * 
	 * @return the retults ranker.
	 */
	public static ResultRanker getRanker(String name, MelodyCollection coll) {
		if (Arrays.asList("2010-domain", "2010-pitchderiv", "2010-shape", "2011-shape", "2012-shapeh", "2012-shapel",
				"2012-shapeg", "2013-shapeh").contains(name))
			return new UntieResultRanker(new NGramMelodyComparer(3, new HybridAligner(new EqualPitchNGramComparer())));
		else
			return new UntieResultRanker(new NGramMelodyComparer(4, new HybridAligner(new EqualPitchNGramComparer())));
	}
		
	/**
	 * Runs an algorithm (comparer and ranker) as specified.
	 * 
	 * @param melodyCmp
	 *            the base melody comparer.
	 * @param melodyCmpRerank
	 *            the melody comparer to rerank, or {@code null} if no reranking
	 *            is needed.
	 * @param ranker
	 *            the base results ranker.
	 * @param rankerRerank
	 *            the results ranker to rerank, or {@code null} if no reranking
	 *            is needed.
	 * @param kOpt
	 *            the cutoff.
	 * @param queries
	 *            the list of queries.
	 * @param numQuery
	 *            the index of the particular query to run.
	 * @param coll
	 *            the collection of melodies to compare with the query.
	 * @param tOpt
	 *            the number of threads to use.
	 * @param observer
	 *            the user interface observer to notify changes.
	 * @return the ranked list of results.
	 */
	public static Result[] runAlgorithm(MelodyComparer melodyCmp, MelodyComparer melodyCmpRerank, ResultRanker ranker,
			ResultRanker rankerRerank, int kOpt, ArrayList<Melody> queries, int numQuery, MelodyCollection coll, int tOpt, UIObserver observer){		
		Melody query = queries.get(numQuery);
		Result[] results = MelodyShape.runComparer(melodyCmp, query, coll, coll.size(), numQuery, queries.size(), tOpt, observer);
		observer.updateStartRanker(query, numQuery, queries.size());
		ranker.rank(query, results, kOpt);

		if (melodyCmpRerank!=null && rankerRerank!=null) {
			// Get top results with score as large as the k-th (can be more than k due to ties)
			double kScore = results[Math.min(kOpt, results.length)].getScore();
			ArrayList<Melody> melodiesRerank = new ArrayList<Melody>();
			for (int k = 0; k < results.length; k++)
				if (results[k].getScore() >= kScore)
					melodiesRerank.add(results[k].getMelody());
				else
					break;
			// rerun
			results = MelodyShape.runComparer(melodyCmpRerank, query, melodiesRerank, melodiesRerank.size(),
					numQuery, queries.size(), tOpt, null);
			// rerank
			rankerRerank.rank(query, results, kOpt);
		}
		return results;
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
	 * @param tOpt
	 *            the number of threads to use.
	 * @param observer
	 *            the user interface observe to notify of progress.
	 * @return the array of results, not necessarily sorted by similarity.
	 * @throws RuntimeException
	 *             if there is some error or an {@link InterruptedException} is
	 *             received.
	 */
	public static Result[] runComparer(final MelodyComparer melodyCmp, final Melody query, final Iterable<Melody> coll,
			final int collSize, final int numQuery, final int totalQueries, int tOpt, final UIObserver observer)
			throws RuntimeException {
		// Create one callable per melody
		ArrayList<Callable<Result>> callables = new ArrayList<>(collSize);
		CountDownLatch latch = new CountDownLatch(collSize);

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
					synchronized (this.latch) { // don't know if we need to sync here, but just in case
						this.latch.countDown();
						if (observer != null && this.latch.getCount() % MelodyShape.VERBOSE_PERIOD == 0)
							observer.updateProgressComparer(query, numQuery, totalQueries, 1.0 - ((double) this.latch.getCount()) / collSize);
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
			if (observer!=null)
				observer.updateProgressComparer(query, numQuery, totalQueries, 1);
			for (int i = 0; i < futures.size(); i++)
				res[i] = futures.get(i).get();
			service.shutdown();
		} catch (InterruptedException | ExecutionException ex) {
			service.shutdownNow();
			throw new RuntimeException(ex);
		}
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UIObserver observer = null;
		
		// if -gui is present, run graphical ui
		for (String arg : args)
			if (arg.equals("-gui"))
				observer = new GraphicalUIObserver();
		
		if(observer==null){
			Console c = System.console();
			// if there is no console, run graphical ui, otherwise console ui
			if(c==null)
				observer=new GraphicalUIObserver();
			else
				observer = new ConsoleUIObserver(args);
		}
		observer.start();
	}
}
