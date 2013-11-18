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

import jurbano.melodyshape.model.Melody;

/**
 * Represents a function that computes a similarity score between two
 * {@link Melody} objects.
 * 
 * @author Julián Urbano
 * @see Melody
 */
public interface MelodyComparer
{
	/**
	 * Gets the name of this {@code MelodyComparer}
	 * 
	 * @return the name of the melody comparer.
	 */
	public String getName();
	
	/**
	 * Computes a similarity score between two melodies.
	 * <p>
	 * The similarity score is proportional to the melodic similarity between
	 * the melodies, that is {@code compare(m1, m2) > compare(m1, m3)} means
	 * that melody {@code m2} is more similar to {@code m1} than {@code m3} is.
	 * 
	 * @param m1
	 *            the first melody to compare.
	 * @param m2
	 *            the second melody to compare.
	 * @return the similarity score.
	 */
	public double compare(Melody m1, Melody m2);
}