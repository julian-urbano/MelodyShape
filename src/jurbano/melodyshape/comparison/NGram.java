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

import java.util.ArrayList;

import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.Note;

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
