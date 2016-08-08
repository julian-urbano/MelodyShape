// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.ranking;

import jurbano.melodyshape.model.Melody;

/**
 * A structure holding the similarity score for a particular document and query,
 * for ranking and printing purposes.
 * 
 * @author Julián Urbano
 * @see Melody
 */
public class Result
{
	protected Melody melody;
	protected double score;
	
	/**
	 * Gets the {@link Melody} to which this result refers.
	 * 
	 * @return the melody.
	 */
	public Melody getMelody() {
		return melody;
	}
	
	/**
	 * Gets the similarity score of this result.
	 * 
	 * @return the similarity score.
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * Constructs a new {@link Result} for the specified melody with the
	 * specified similarity score.
	 * 
	 * @param melody
	 *            the melody.
	 * @param score
	 *            the similarity score.
	 */
	public Result(Melody melody, double score) {
		this.melody = melody;
		this.score = score;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Result [melody=" + melody.getId() + ", score=" + score + "]";
	}
}
