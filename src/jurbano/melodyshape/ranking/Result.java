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
 * A structure holding the similarity score for a particular document and query,
 * for ranking and printing purposes.
 * 
 * @author Julián Urbano
 * @see Melody
 * @see Algorithm
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
