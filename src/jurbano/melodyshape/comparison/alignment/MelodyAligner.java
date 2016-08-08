// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.comparison.alignment;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;
import jurbano.melodyshape.model.Melody;

import java.util.ArrayList;

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
