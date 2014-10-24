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

import java.util.Arrays;
import java.util.Comparator;

import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.model.Melody;

/**
 * A ranking function that sorts by decreasing similarity score, then by
 * decreasing similarity according to a second {@link MelodyComparer}, then by
 * increasing difference in length with the query, and then by melody
 * identifier.
 * 
 * @author Julián Urbano
 * @see ResultRanker
 */
public class UntieResultRanker implements ResultRanker
{
	protected MelodyComparer comparer;
	
	/**
	 * Constructs a new {@code UntieResultRanker} with the specified
	 * {@link MelodyComparer}.
	 * 
	 * @param comparer
	 *            the n-gram comparer to use.
	 */
	public UntieResultRanker(MelodyComparer comparer) {
		this.comparer = comparer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rank(final Melody query, Result[] results, int k) {
		// first sort by score
		Arrays.sort(results, new Comparator<Result>() {
			@Override
			public int compare(Result r1, Result r2) {
				return Double.compare(r2.getScore(), r1.getScore());
			}
		});
		// now traverse and sort groups with the same score
		for (int i = 1, top = 0; i < results.length; i++) {
			if (results[i].getScore() < results[top].getScore()) {
				// re-sort from top to i-1
				Arrays.sort(results, top, i, new Comparator<Result>() {
					@Override
					public int compare(Result r1, Result r2) {
						// sort by new comparer
						double score1 = UntieResultRanker.this.comparer.compare(query, r1.getMelody());
						double score2 = UntieResultRanker.this.comparer.compare(query, r2.getMelody());
						if (score1 != score2)
							return Double.compare(score2, score1);
						// then by length
						if (r1.getMelody().size() != r2.getMelody().size())
							return Integer.compare(Math.abs(r1.getMelody().size() - query.size()),
									Math.abs(r2.getMelody().size() - query.size()));
						// then by doc id
						return r1.getMelody().getId().compareTo(r2.getMelody().getId());
					}
				});
				top = i;
				if (top >= k)
					break; // no need to re-sort beyond the top k
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Untie(comparer)"}.
	 */
	@Override
	public String getName() {
		return "Untie(" + this.comparer.getName() + ")";
	}
}
