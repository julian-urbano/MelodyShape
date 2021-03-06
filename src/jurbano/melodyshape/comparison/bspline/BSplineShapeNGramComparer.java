// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.comparison.bspline;

import jurbano.melodyshape.comparison.NGram;
import jurbano.melodyshape.comparison.NGramComparer;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * A similarity function between two {@link NGram} objects that interpolates
 * pitch sequences using a Uniform B-Spline of degree 2 and then compares them
 * by their shape. This comparison is qualitative, checking whether spline spans
 * are concave or convex.
 * 
 * @author Julián Urbano
 * @see UniformBSpline
 * @see NGramComparer
 */
public class BSplineShapeNGramComparer implements NGramComparer
{
	static final double DEFAULT_MAX_PENALIZATION = 8;
	static final double DEFAULT_AVG_PENALIZATION = 1;
	static final double DEFAULT_MIN_PENALIZATION = 0.5;
	
	protected double dMax;
	protected double dMed;
	protected double dMin;
	
	/**
	 * Constructs a {@code BSplineShapeNGramComparer} with the default maximum,
	 * medium and minimum penalizations (8, 1 and 0.5).
	 */
	public BSplineShapeNGramComparer() {
		this(BSplineShapeNGramComparer.DEFAULT_MAX_PENALIZATION, BSplineShapeNGramComparer.DEFAULT_AVG_PENALIZATION,
				BSplineShapeNGramComparer.DEFAULT_MIN_PENALIZATION);
	}
	
	/**
	 * Constructs a {@code BSplineShapeNGramComparer} with the specified
	 * maximum, medium and minimum penalizations.
	 * 
	 * @param dMax
	 *            the maximum penalization.
	 * @param dMed
	 *            the medium penalization.
	 * @param dMin
	 *            the minimum penalization.
	 */
	public BSplineShapeNGramComparer(double dMax, double dMed, double dMin) {
		this.dMax = dMax;
		this.dMed = dMed;
		this.dMin = dMin;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "BSplineShape(dMax,dMed,dMin)"}.
	 */
	@Override
	public String getName() {
		return "BSplineShape(" + this.dMax + "," + this.dMed + "," + this.dMin + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double compare(NGram g1, NGram g2) {
		if (g1 == null)
			g1 = g2.getNullSpan();
		if (g2 == null)
			g2 = g1.getNullSpan();
		if (g1.size() != 3)
			throw new IllegalArgumentException(this.getName() + " only supports n-grams with 3 notes.");
		if (this.getNGramId(g1).equals(this.getNGramId(g2)))
			return 0;
		
		PolynomialFunction p1 = new PolynomialFunction(new double[] { 0 });
		PolynomialFunction p2 = new PolynomialFunction(new double[] { 0 });
		for (int i = 1; i < g1.size(); i++) {
			PolynomialFunction basis = UniformBSpline.BASIS_FUNCTIONS[g1.size() - 1][i - 1];
			p1 = p1.add(basis.multiply(new PolynomialFunction(new double[] { g1.get(g1.size() - i).getPitch() - g1.get(0).getPitch() })));
			p2 = p2.add(basis.multiply(new PolynomialFunction(new double[] { g2.get(g2.size() - i).getPitch() - g2.get(0).getPitch() })));
		}
		PolynomialFunction p1_ = p1.polynomialDerivative();
		PolynomialFunction p2_ = p2.polynomialDerivative();
		
		/*
		double p1_0 = Math.signum(p1_.value(0));
		double p1_1 = Math.signum(p1_.value(1));
		double p2_0 = Math.signum(p2_.value(0));
		double p2_1 = Math.signum(p2_.value(1));
		
		if(p1_0 == p2_0){ // same at 0
			if(p1_1 == p2_1) // same at 1
				return -this.dMin;
			else if(p1_1*p2_1<0) // opposite at 1
				return -this.dMed;
			else // some flat at 1
				return -this.dMed;
		}else if(p1_0*p2_0 < 0) { // opposite at 0
			if(p1_1 == p2_1) // same at 1
				return -this.dMed;
			else if(p1_1*p2_1<0) // opposite at 1
				return -this.dMax;
			else // some flat at 1
				return -this.dMax;
		}else { // some flat at 0
			if(p1_1 == p2_1) // same at 1
				return -this.dMed;
			else if(p1_1*p2_1<0) // opposite at 1
				return -this.dMax;
			else // some flat at 1
				return -this.dMax;
		}*/		
		
		double p1_0 = p1_.value(0);
		double p1_1 = p1_.value(1);
		double p2_0 = p2_.value(0);
		double p2_1 = p2_.value(1);
		
		// TODO: this similarity function is not symmetric
		if (p1_0 <= 0 && p1_1 >= 0) { // p1 is \/
			if (p2_0 <= 0 && p2_1 >= 0) // p2 is \/
				return -this.dMin;
			else if (p2_0 >= 0 && p2_1 <= 0) // p2 is /\
				return -this.dMax;
			else
				return -this.dMed;
		}
		if (p1_0 >= 0 && p1_1 <= 0) { // p1 is /\
			if (p2_0 >= 0 && p2_1 <= 0) // p2 is /\
				return -this.dMin;
			else if (p2_0 <= 0 && p2_1 >= 0) // p2 is \/
				return -this.dMax;
			else
				return -this.dMed;
		}
		if (p1_0 >= 0 && p1_1 >= 0) { // p1 is /
			if (p2_0 >= 0 && p2_1 >= 0) // p2 is /
				return -this.dMin;
			else if (p2_0 <= 0 && p2_1 <= 0) // p2 is \
				return -this.dMax;
			else
				return -this.dMed;
		}
		if (p1_0 <= 0 && p1_1 <= 0) { // p1 is \
			if (p2_0 <= 0 && p2_1 <= 0) // p2 is \
				return -this.dMin;
			else if (p2_0 >= 0 && p2_1 >= 0) // p2 is /
				return -this.dMax;
			else
				return -this.dMed;
		}
		
		return -this.dMin;
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
