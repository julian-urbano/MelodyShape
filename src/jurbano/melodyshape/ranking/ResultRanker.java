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
	 * Ranks the specified list of {@link Result}s for the specified query
	 * {@link Melody}. Results are ranked in decreasing order of similarity to
	 * the query.
	 * 
	 * @param query
	 *            the query that originated the results.
	 * @param results
	 *            the unranked list of results.
	 */
	public void rank(Melody query, Result[] results);
}
