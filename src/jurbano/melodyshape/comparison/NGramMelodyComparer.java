// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.comparison;

import java.util.ArrayList;

import jurbano.melodyshape.comparison.alignment.MelodyAligner;
import jurbano.melodyshape.model.Melody;

/**
 * A function that computes a similarity score between two {@link Melody}
 * objects by applying an alignment algorithm upon the sequence of n-grams for
 * each melody.
 * 
 * @author Julián Urbano
 * @see Melody
 * @see MelodyAligner
 */
public class NGramMelodyComparer implements MelodyComparer
{
	protected int nGramLength;
	protected MelodyAligner aligner;
	
	/**
	 * Constructs a new {@code NGramMelodyComparer}.
	 * 
	 * @param nGramLength
	 *            the n-gram length, that is, number of {@code Note}s.
	 * @param aligner
	 *            the alignment algorithm to use.
	 */
	public NGramMelodyComparer(int nGramLength, MelodyAligner aligner) {
		this.nGramLength = nGramLength;
		this.aligner = aligner;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double compare(Melody m1, Melody m2) {
		ArrayList<NGram> n1 = NGram.getNGrams(m1, this.nGramLength);
		ArrayList<NGram> n2 = NGram.getNGrams(m2, this.nGramLength);
		return this.aligner.align(n1, n2);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "nGram(length,aligner)"}.
	 */
	@Override
	public String getName() {
		return "nGram(" + this.nGramLength + "," + this.aligner.getName() + ")";
	}
}
