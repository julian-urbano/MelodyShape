// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.ui;

import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.ranking.ResultRanker;

/**
 * Controls the execution of an algorithm with a user interface.
 * 
 * @author Julián Urbano
 * @see ConsoleUIObserver
 */
public interface UIObserver {
	/**
	 * Starts execution of the algorithm.
	 */
	public void start();

	/**
	 * Used to notify this {@code UIObserver} of the progress in executing a
	 * {@link MelodyComparer} with a particular query.
	 * 
	 * @param query
	 *            the query running.
	 * @param numQuery
	 *            the number of query running.
	 * @param totalQueries
	 *            the total number of queries to run.
	 * @param progress
	 *            a number indicating the execution progress from 0 (just
	 *            started) to 1 (completed).
	 */
	public void updateProgressComparer(Melody query, int numQuery, int totalQueries, double progress);

	/**
	 * Used to notify this {@code UIObserver} that the {@link MelodyComparer}
	 * just finished and a {@link ResultRanker} is about to start running with a
	 * particular query.
	 * 
	 * @param query
	 *            the query running.
	 * @param numQuery
	 *            the number of query running.
	 * @param totalQueries
	 *            the total number of queries to run.
	 */
	public void updateStartRanker(Melody query, int numQuery, int totalQueries);
}
