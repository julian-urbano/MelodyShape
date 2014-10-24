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

import java.util.ArrayList;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * A similarity function between two {@link NGram} objects that interpolates
 * pitch sequences using a Uniform B-Spline and then computes the area between
 * the first derivatives of the two splines.
 * 
 * @author Julián Urbano
 * @see BSplineTimeNGramComparer
 * @see UniformBSpline
 * @see NGramComparer
 */
public class BSplinePitchNGramComparer implements NGramComparer
{
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "BSplinePitch"}.
	 */
	@Override
	public String getName() {
		return "BSplinePitch";
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
		
		PolynomialFunction p1p = new PolynomialFunction(new double[] { 0 });
		PolynomialFunction p2p = new PolynomialFunction(new double[] { 0 });
		for (int i = 1; i < g1.size(); i++) {
			PolynomialFunction basis = UniformBSpline.BASIS_FUNCTIONS[g1.size() - 1][i - 1];
			p1p = p1p.add(basis.multiply(new PolynomialFunction(new double[] { g1.get(g1.size() - i).getPitch() - g1.get(0).getPitch() })));
			p2p = p2p.add(basis.multiply(new PolynomialFunction(new double[] { g2.get(g2.size() - i).getPitch() - g2.get(0).getPitch() })));
		}
		PolynomialFunction pp = p1p.polynomialDerivative().subtract(p2p.polynomialDerivative());
		
		Laguerre laguerre = new Laguerre();
		ArrayList<Double> realRoots = laguerre.findRoots(pp);
		return -laguerre.computeAreaBetweenDerivatives(p1p, p2p, realRoots);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return a {@link String} with the format
	 *         <code>{p2-p1, p3-p1, ..., p(n-1)-p1}</code>, where {@code pi}
	 *         corresponds to the pitch of the i-th note inside the n-gram. The
	 *         {@link String} {@code "null"} is returned if the n-gram is
	 *         {@code null}.
	 */
	@Override
	public String getNGramId(NGram g) {
		if (g == null)
			return "null";
		
		String res = "{";
		for (int i = 1; i < g.size(); i++)
			res += (g.get(i).getPitch() - g.get(0).getPitch()) + " ";
		return res.trim() + "}";
	}
}
