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

package jurbano.melodyshape.comparison.alignment;

import java.util.ArrayList;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;
import jurbano.melodyshape.model.Melody;

/**
 * A sequence alignment algorithm that can be applied upon the sequences of
 * {@link NGram}s defined from two {@link Melody} objects.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see NGramComparer
 */
public interface MelodyAligner
{
	/**
	 * Gets the name of this {@code MelodyAligner}.
	 * 
	 * @return the name of this melody aligner.
	 */
	public String getName();
	
	/**
	 * Computes an alignment score between two sequences of {@link NGram}s.
	 * <p>
	 * The alignment score is proportional to the similarity between the
	 * sequences, that is {@code align(s1, s2) > align(s1, s3)} means that
	 * sequence {@code s2} is more similar to {@code s1} than {@code s3} is.
	 * 
	 * @param s1
	 *            the first sequence of n-grams.
	 * @param s2
	 *            the second sequence of n-grams.
	 * @return the alignment score.
	 */
	public double align(ArrayList<NGram> s1, ArrayList<NGram> s2);
}
