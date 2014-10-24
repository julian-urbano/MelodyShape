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
 * absolute pitch values. The similarity is binary: either the two n-grams have
 * the exact same pitch sequence or not.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see NGramComparer
 */
public class EqualPitchNGramComparer implements NGramComparer
{
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Eq"}.
	 */
	@Override
	public String getName() {
		return "Eq";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return -1 if either n-gram is {@code null}, -1 if the i-th {@link Note}
	 *         in {@code n1} has a different pitch in {@code n2}, or 1 if both
	 *         pitch sequences are the same.
	 */
	public double compare(NGram n1, NGram n2) {
		if (n1 == null || n2 == null)
			return -1;
		
		for (int i = 0; i < n1.size(); i++)
			if (n1.get(i).getPitch() != n2.get(i).getPitch())
				return -1;
		
		return 1;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return a {@link String} with the format <code>{p1, p2, ..., pn}</code>,
	 *         where {@code pi} corresponds to the pitch of the i-th note inside
	 *         the n-gram. The {@link String} {@code "null"} is returned if the
	 *         n-gram is {@code null}.
	 */
	@Override
	public String getNGramId(NGram g) {
		if (g == null)
			return "null";
		
		String res = "{";
		for (Note n : g)
			res += n.getPitch() + " ";
		return res.trim() + "}";
	}
}
