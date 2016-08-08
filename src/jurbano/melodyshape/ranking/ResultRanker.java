// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.ranking;

import jurbano.melodyshape.model.Melody;

/**
 * An algorithm to rank the similarity scores for a given query according to
 * different criteria.
 * 
 * @author Julián Urbano
 * @see Result
 * @see Melody
 */
public interface ResultRanker
{
	/**
	 * Gets the name of this {@code ResultRanker}.
	 * 
	 * @return the name of this result ranker.
	 */
	public String getName();
	
	/**
	 * Ranks the top {@code k} {@link Result}s for the specified query
	 * {@link Melody}. Results are ranked in decreasing order of similarity to
	 * the query.
	 * 
	 * @param query
	 *            the query that originated the results.
	 * @param results
	 *            the unranked list of results.
	 * @param k
	 *            the cutoff.
	 */
	public void rank(Melody query, Result[] results, int k);
}
