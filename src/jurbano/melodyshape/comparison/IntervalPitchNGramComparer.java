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

package jurbano.melodyshape.comparison;

import jurbano.melodyshape.model.Note;

/**
 * A similarity function between two {@link NGram} objects that only considers
 * pitch intervals between successive {@link Note}s. The (dis)similarity is
 * measured as the average absolute difference between intervals.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see NGramComparer
 */
public class IntervalPitchNGramComparer implements NGramComparer
{
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Int"}.
	 */
	@Override
	public String getName() {
		return "Int";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return -1 if either n-gram is {@code null}; if not, the (negative)
	 *         average absolute difference between {@code n1}'s and {@code n2}'s
	 *         i-th pitch interval with their respective first pitch.
	 */
	@Override
	public double compare(NGram g1, NGram g2) {
		if (g1 == null || g2 == null)
			return -1;
		
		double diffPitch = 0;
		for (int i = 1; i < g1.size(); i++)
			diffPitch += Math.abs((g1.get(i).getPitch() - g1.get(i - 1).getPitch()) - (g2.get(i).getPitch() - g2.get(i - 1).getPitch()));
		
		return -diffPitch / (g1.size() - 1);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return a {@link String} with the format
	 *         <code>{p2-p1, p3-p1, ..., p(n-1)-p1}</code>, where {@code pi}
	 *         corresponds to the pitch of the i-th note inside the n-gram. The
	 *         {@link String} {@code "null"} is returned if the n-gram is
	 *         {@code null}.
	 */
	@Override
	public String getNGramId(NGram g) {
		if (g == null)
			return "null";
		
		String res = "{";
		for (int i = 1; i < g.size(); i++)
			res += (g.get(i).getPitch() - g.get(i - 1).getPitch()) + " ";
		return res.trim() + "}";
	}
}
