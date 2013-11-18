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

import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;

/**
 * An algorithm to compute the melodic similarity between a query {@link Melody}
 * and all melodies in a {@link MelodyCollection}, and rank all of them
 * accordingly.
 * 
 * @author Julián Urbano
 * @see MelodyComparer
 * @see ResultRanker
 */
public class Algorithm
{
	protected ResultRanker ranker;
	protected MelodyComparer comparer;
	
	/**
	 * Constructs a new {@code Algorithm} that runs a {@link MelodyComparer} and
	 * a {@link ResultRanker}.
	 * 
	 * @param comparer
	 *            the melody comparer to use.
	 * @param ranker
	 *            the result ranker to use.
	 */
	public Algorithm(MelodyComparer comparer, ResultRanker ranker) {
		this.ranker = ranker;
		this.comparer = comparer;
	}
	
	/**
	 * Computes the melodic similarity between the given query {@link Melody}
	 * and all melodies in a {@link MelodyCollection}.
	 * 
	 * @param query
	 *            the query melody.
	 * @param coll
	 *            the collection of melodies to rank.
	 * @return the list of melodies ranked by decreasing melodic similarity to
	 *         the query.
	 */
	public Result[] runQuery(Melody query, MelodyCollection coll) {
		Result[] res = new Result[coll.size()];
		
		int i = 0;
		for (Melody m : coll) {
			double score = this.comparer.compare(query, m);
			res[i] = new Result(m, score);
			i++;
		}
		if (this.ranker != null)
			this.ranker.rank(query, res);
		
		return res;
	}
}
