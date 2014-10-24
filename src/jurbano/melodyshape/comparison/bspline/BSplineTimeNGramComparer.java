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

package jurbano.melodyshape.comparison.bspline;

import java.text.DecimalFormat;
import java.util.ArrayList;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * A similarity function between two {@link NGram} objects that interpolates
 * time interval sequences using a Uniform B-Spline and then computes the area
 * between the first derivatives of the two splines.
 * 
 * @author Julián Urbano
 * @see BSplineTimeNGramComparer
 * @see UniformBSpline
 * @see NGramComparer
 */
public class BSplineTimeNGramComparer implements NGramComparer
{
	protected final DecimalFormat format = new DecimalFormat("#.###");
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "BSplineTime"}.
	 */
	@Override
	public String getName() {
		return "BSplineTime";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return 0 if both {@link NGram}s are equivalent or the area between the
	 *         first derivatives of their corresponding Uniform B-Splines.
	 */
	@Override
	public double compare(NGram g1, NGram g2) {
		if (g1 == null)
			g1 = g2.getNullSpan();
		if (g2 == null)
			g2 = g1.getNullSpan();
		if (g1.size() < 2 || g1.size() > UniformBSpline.BASIS_FUNCTIONS.length)
			throw new IllegalArgumentException(this.getName() + " only supports n-grams with 2 to " + UniformBSpline.BASIS_FUNCTIONS.length
					+ " notes.");
		if (this.getNGramId(g1).equals(this.getNGramId(g2)))
			return 0;
		
		PolynomialFunction p1t = new PolynomialFunction(new double[] { 0 });
		PolynomialFunction p2t = new PolynomialFunction(new double[] { 0 });
		for (int i = 0; i < g1.size(); i++) {
			PolynomialFunction basis = UniformBSpline.BASIS_FUNCTIONS[g1.size() - 1][i];
			p1t = p1t.add(basis.multiply(new PolynomialFunction(new double[] { g1.get(g1.size() - i - 1).getDuration()
					/ (double) g1.get(0).getDuration() })));
			p2t = p2t.add(basis.multiply(new PolynomialFunction(new double[] { g2.get(g2.size() - i - 1).getDuration()
					/ (double) g2.get(0).getDuration() })));
		}
		PolynomialFunction pt = p1t.polynomialDerivative().subtract(p2t.polynomialDerivative());
		
		Laguerre laguerre = new Laguerre();
		ArrayList<Double> realRoots = laguerre.findRoots(pt);
		return -laguerre.computeAreaBetweenDerivatives(p1t, p2t, realRoots);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return a {@link String} with the format
	 *         <code>{d2/d1, d3/d1, ..., d(n-1)/d1}</code>, where {@code di}
	 *         corresponds to the duration of the i-th note inside the n-gram.
	 *         The {@link String} {@code "null"} is returned if the n-gram is
	 *         {@code null}.
	 */
	@Override
	public String getNGramId(NGram g) {
		if (g == null)
			return "null";
		
		String res = "{";
		for (int i = 1; i < g.size(); i++)
			res += this.format.format(g.get(i).getDuration() / (double) g.get(0).getDuration()) + " ";
		return res.trim() + "}";
	}
}
