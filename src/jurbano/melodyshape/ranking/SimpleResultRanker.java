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

import jurbano.melodyshape.model.Melody;

/**
 * A simple ranking function that sorts by decreasing similarity score, then by
 * increasing difference in length with the query, and then by melody
 * identifier.
 * 
 * @author Julián Urbano
 * @see ResultRanker
 */
public class SimpleResultRanker implements ResultRanker
{
	/**
	 * {@inheritDoc}
	 * 
	 * return the {@link String} {@code "Simple"}.
	 */
	@Override
	public String getName() {
		return "Simple";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rank(final Melody query, Result[] results, int k) {
		// We can ignore k, still need to sort them all to get the top k.
		// We could keep the top k as we go down, and update if the current
		// result is better, but it's just not worth it
		Arrays.sort(results, new Comparator<Result>() {
			@Override
			public int compare(Result r1, Result r2) {
				if (r1.getScore() != r2.getScore())
					return Double.compare(r2.getScore(), r1.getScore());
				if (r1.getMelody().size() != r2.getMelody().size())
					return Integer.compare(r1.getMelody().size() - query.size(), r2.getMelody().size() - query.size());
				return r1.getMelody().getId().compareTo(r2.getMelody().getId());
			}
		});
	}
}
