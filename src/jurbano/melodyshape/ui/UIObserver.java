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
