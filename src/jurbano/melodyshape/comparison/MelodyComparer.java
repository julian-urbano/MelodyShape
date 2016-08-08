// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

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