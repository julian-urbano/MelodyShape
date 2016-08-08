// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.comparison;

import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.Note;

import java.util.ArrayList;

/**
 * An n-gram of notes, that is, a sequence of n consecutive {@link Note}
 * objects.
 * 
 * @author Julián Urbano
 * @see Note
 */
@SuppressWarnings("serial")
public class NGram extends ArrayList<Note>
{
	/**
	 * Gets a null n-gram of this object. A null n-gram contains the same notes
	 * but with all notes set to the pitch of this n-gram's first note.
	 * 
	 * @return the null n-gram.
	 */
	public NGram getNullSpan() {
		NGram s = new NGram();
		for (Note n : this)
			s.add(new Note(this.get(0).getPitch(), n.getOnset(), n.getDuration(), n.getRestFraction()));
		return s;
	}
	
	/**
	 * Gets the sequence of {@code n}-grams, each containing {@code n}
	 * {@link Note} objects, from the {@link Melody} specified.
	 * 
	 * @param m
	 *            the melody.
	 * @param n
	 *            the length of the n-grams.
	 * @return the sequence of {@code n}-grams.
	 */
	public static ArrayList<NGram> getNGrams(Melody m, int n) {
		ArrayList<NGram> list = new ArrayList<NGram>();

		for (int i = 0; i <= m.size() - n; i++) {
			NGram gram = new NGram();
			for (int j = 0; j < n; j++)
				gram.add(m.get(i + j));
			list.add(gram);
		}

		return list;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String res = "";
		for (Note n : this)
			res += "{" + n.getPitch() + "," + n.getDuration() + "," + n.getRestFraction() + "}";
		return res;
	}
}
