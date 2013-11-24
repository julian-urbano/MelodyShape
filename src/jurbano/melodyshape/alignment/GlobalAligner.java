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

package jurbano.melodyshape.alignment;

import java.util.ArrayList;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;

/**
 * An implementation of the Needleman-Wunsch alignment algorithm for sequences of {@link NGram}s.
 * 
 * @author Julián Urbano
 * @see NGram
 * @see NGramComparer
 */
public class GlobalAligner implements MelodyAligner
{
	protected NGramComparer comparer;
	
	/**
	 * Constructs a new {@code GlobalAligner} with the specified
	 * {@link NGramComparer}.
	 * 
	 * @param comparer
	 *            the n-gram comparer to use.
	 */
	public GlobalAligner(NGramComparer comparer) {
		this.comparer = comparer;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Global(comparer)"}, where
	 *         {@code comparer} is the name of the underlying
	 *         {@link NGramComparer}.
	 */
	@Override
	public String getName() {
		return "Global("+this.comparer.getName()+")";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double align(ArrayList<NGram> s1, ArrayList<NGram> s2) {
		
		double[][] matrix = new double[s1.size() + 1][s2.size() + 1];
		
		for (int i = 1; i <= s1.size(); i++)
			matrix[i][0] = matrix[i - 1][0] + this.comparer.compare(s1.get(i - 1), null);
		for (int j = 1; j <= s2.size(); j++)
			matrix[0][j] = matrix[0][j - 1] + this.comparer.compare(null, s2.get(j - 1));
		
		for (int i = 1; i <= s1.size(); i++) {
			for (int j = 1; j <= s2.size(); j++) {
				double left = matrix[i - 1][j] + this.comparer.compare(s1.get(i - 1), null);
				double up = matrix[i][j - 1] + this.comparer.compare(null, s2.get(j - 1));
				double diag = matrix[i - 1][j - 1] + this.comparer.compare(s1.get(i - 1), s2.get(j - 1));
				matrix[i][j] = Math.max(left, Math.max(up, diag));
			}
		}
		
		return matrix[s1.size()][s2.size()];
	}	
}
