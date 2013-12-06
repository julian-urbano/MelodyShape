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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;

/**
 * A similarity function between two {@link NGram} objects that uses a second
 * {@link NGramComparer} for mismatches and the frequency of n-grams in the
 * collection for insertions, deletions and matches.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see NGramComparer
 */
public class FrequencyNGramComparer implements NGramComparer {
	protected HashMap<String, Long> nGramCounts;
	protected long nGramCountSum;

	protected NGramComparer mismatchComparer;

	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Freq(comparer)"}, where
	 *         {@code comparer} is the name of the underlying
	 *         {@link NGramComparer}.
	 */
	@Override
	public String getName() {
		return "Freq(" + this.mismatchComparer.getName() + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNGramId(NGram g) {
		return this.mismatchComparer.getNGramId(g);
	}

	/**
	 * Constructs a new {@code FrequencyNGramComparer} for the specified
	 * {@link MelodyCollection} and using the specified
	 * {@link NGramMelodyComparer}. The frequency of all n-grams in the
	 * collection are computed here.
	 * 
	 * @param coll
	 *            the collection of melodies.
	 * @param nGramLength
	 *            the length of the n-grams to compute.
	 * @param mismatchComparer
	 *            the n-gram comparer for mismatches.
	 */
	public FrequencyNGramComparer(MelodyCollection coll, int nGramLength, NGramComparer mismatchComparer) {
		this.mismatchComparer = mismatchComparer;
		this.nGramCounts = new HashMap<String, Long>();
		this.nGramCountSum = 0;

		for (Melody m : coll) {
			for (NGram n : NGram.getNGrams(m, nGramLength)) {
				String nGramId = this.getNGramId(n);
				Long l = this.nGramCounts.get(nGramId);
				if (l == null)
					this.nGramCounts.put(nGramId, 1l);
				else
					this.nGramCounts.put(nGramId, l + 1);
				this.nGramCountSum++;
			}
		}
	}

	/**
	 * Constructs a new {@code FrequencyNGramComparer} for the specified
	 * {@link NGramMelodyComparer} reading n-gram frequencies from a file.
	 * 
	 * @param statsPath
	 *            the path to a file containing the n-gram frequencies in a
	 *            collection of melodies.
	 * @param mismatchComparer
	 *            the n-gram comparer for mismatches.
	 * @throws IOException
	 *             if an I/O or format error occurs.
	 * @see FrequencyNGramComparer#saveStatistics(String)
	 */
	public FrequencyNGramComparer(String statsPath, NGramComparer mismatchComparer) throws IOException {
		this.mismatchComparer = mismatchComparer;

		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(new FileInputStream(statsPath));
			this.nGramCountSum = objStream.readLong();
			@SuppressWarnings("unchecked")
			HashMap<String, Long> counts = (HashMap<String, Long>) objStream.readObject();
			this.nGramCounts = counts;
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e2) {
			throw new IOException("Incorrect format in statistics file " + statsPath);
		} finally {
			if (objStream != null)
				objStream.close();
		}
	}

	/**
	 * Saves the frequency of all n-grams in this {@code FrequencyNGramComparer}
	 * to the specified file.
	 * 
	 * @param path
	 *            the path of the file to save the n-gram frequencies.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void saveStatistics(String path) throws IOException {
		ObjectOutputStream objStream = null;
		try {
			objStream = new ObjectOutputStream(new FileOutputStream(path));
			objStream.writeLong(this.nGramCountSum);
			objStream.writeObject(this.nGramCounts);
		} catch (IOException e) {
			throw e;
		} finally {
			if (objStream != null)
				objStream.close();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code -(1-f)} if either {@link NGram} is {@code null},
	 *         {@code (1-f)} if they are the same, and the (dis)similarity
	 *         returned by the underlying {@link NGramComparer} if they are
	 *         different.
	 */
	@Override
	public double compare(NGram n1, NGram n2) {
		if (n1 == null) {
			Long f2 = this.nGramCounts.get(this.getNGramId(n2));
			if (f2 != null)
				return -1.0d + f2.doubleValue() / this.nGramCountSum;
			else
				return -1.0d / this.nGramCountSum;
		}
		if (n2 == null) {
			Long f1 = this.nGramCounts.get(this.getNGramId(n1));
			if (f1 != null)
				return -1.0d + f1.doubleValue() / this.nGramCountSum;
			else
				return -1.0d / this.nGramCountSum;
		}
		if (this.getNGramId(n1).equals(this.getNGramId(n2))) {
			Long freq = this.nGramCounts.get(this.getNGramId(n1));
			if (freq == null)
				return 1.0d - 1.0d / this.nGramCountSum;
			else
				return 1.0d - freq.doubleValue() / this.nGramCountSum;
		}

		return this.mismatchComparer.compare(n1, n2);
	}

	@Override
	public String toString() {
		return "Freq(" + this.mismatchComparer.getName() + ")[count=" + nGramCounts.size() + ", countSum=" + nGramCountSum + "]";
	}
}
