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

import jurbano.melodyshape.comparison.alignment.MelodyAligner;

/**
 * A function that computes a similarity score between two {@link NGram}
 * objects.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see MelodyAligner
 */
public interface NGramComparer
{
	/**
	 * Gets the name of this {@code NGramComparer}.
	 * 
	 * @return the name of this n-gram comparer.
	 */
	public String getName();
	
	/**
	 * Computes a similarity score between two n-grams.
	 * <p>
	 * The similarity score is proportional to the melodic similarity between
	 * the notes in each n-gram, that is
	 * {@code compare(n1, n2) > compare(n1, n3)} means that n-gram {@code n2} is
	 * more similar to {@code n1} than {@code n3} is.
	 * 
	 * @param g1
	 *            the first n-gram to compare.
	 * @param g2
	 *            the second n-gram to compare.
	 * @return the similarity score.
	 */
	public double compare(NGram g1, NGram g2);
	
	/**
	 * Gets an identifier for an {@link NGram} object according to this
	 * {@link NGramComparer}. This method is intended to identify different
	 * n-grams that are considered as the same by this {@code NGramComparer} and
	 * thus speed up computations and tune similarity scores in different
	 * algorithms.
	 * <p>
	 * For instance, if this {@code NGramComparer} only considers the pitch
	 * dimension of melodies, no information about time should appear in an
	 * {@link NGram}'s identifier.
	 * 
	 * @param g
	 *            the n-gram to compute the identifier for.
	 * @return the identifier.
	 */
	public String getNGramId(NGram g);
}
