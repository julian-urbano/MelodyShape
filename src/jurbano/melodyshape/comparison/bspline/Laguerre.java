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
// -----------------------------------------------------------------------------
// Class jurbano.melodyshape.comparison.Laguerre.ComplexSolver is adapted
// from org.apache.commons.math3.analysis.solvers.LaguererSolver.ComplexSolver
// as found in the Apache Commons Math library version 3.2, licensed as:
//
//     Licensed to the Apache Software Foundation (ASF) under one or more
//     contributor license agreements.  See the NOTICE file distributed with
//     this work for additional information regarding copyright ownership.
//     The ASF licenses this file to You under the Apache License, Version 2.0
//     (the "License"); you may not use this file except in compliance with
//     the License.  You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License.
// -----------------------------------------------------------------------------

package jurbano.melodyshape.comparison.bspline;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.solvers.*;

/**
 * Implements Laguerre's method to find the roots of a polynomial. Only real
 * roots between 0 and 1 are computed.
 * 
 * @author Julián Urbano
 */
public class Laguerre extends LaguerreSolver
{
	static final double DEFAULT_EPSILON = 1e-5;
	static final int DEFAULT_MAX_ITERATIONS = 100;
	
	protected double epsilon;
	protected int maxIterations;
	protected final ComplexSolver solver;
	
	/**
	 * Constructs a new {@code Laguerre} solver with the default acceptable
	 * error and maximum number of iterations (1e-5 and 100);
	 */
	public Laguerre() {
		this(Laguerre.DEFAULT_EPSILON, Laguerre.DEFAULT_MAX_ITERATIONS);
	}
	
	/**
	 * Constructs a new {@code Laguerre} solver.
	 * 
	 * @param epsilon
	 *            the acceptable error to compute polynomial roots.
	 * @param maxIterations
	 *            the maximum number of iterations to compute a root.
	 */
	public Laguerre(double epsilon, int maxIterations) {
		this.epsilon = epsilon;
		this.maxIterations = maxIterations;
		this.solver = new ComplexSolver();
	}
	
	/**
	 * Finds the real roots of a {@link PolynomialFunction} between 0 and 1,
	 * including these.
	 * 
	 * @param f
	 *            the polynomial to find the roots.
	 * @return the list of real roots.
	 */
	public ArrayList<Double> findRoots(PolynomialFunction f) {
		ArrayList<Double> realRoots = new ArrayList<Double>();
		realRoots.add(0d);
		if (f.degree() > 0) {
			Complex[] roots = this.findAllComplexRoots(f);
			for (Complex root : roots) {
				if (Math.abs(root.getImaginary()) < this.epsilon) {
					double real = root.getReal();
					if (real > 0 && real < 1)
						realRoots.add(real);
				}
			}
		}
		realRoots.add(1d);
		return realRoots;
	}
	
	protected Complex[] findAllComplexRoots(PolynomialFunction f) {
		// This call to super.setup is the change we need in the Laguerre
		// implementation from the Apache Commons Math library. Need to set
		// up the maximum number of iterations and the bounds 0 and 1.
		// The original implementation had the maximum number of iterations set
		// to Integer.MAX_VALUE
		super.setup(this.maxIterations, f, 0, 1, 0);
		return this.solver.solveAll(ComplexUtils.convertToComplex(f.getCoefficients()), new Complex(0, 0d));
	}
	
	/**
	 * Computes the area between two {@link PolynomialFunction}s' first
	 * derivatives, knowing the set of roots of their derivatives' difference.
	 * 
	 * @param p1
	 *            the first polynomial.
	 * @param p2
	 *            the second polynomial.
	 * @param roots
	 *            the known list of roots of the difference.
	 * @return the area between the polynomials.
	 */
	public double computeAreaBetweenDerivatives(PolynomialFunction p1, PolynomialFunction p2, ArrayList<Double> roots) {
		double area = 0;
		for (int i = 1; i < roots.size(); i++) {
			area += Math.abs((p1.value(roots.get(i)) - p1.value(roots.get(i - 1))) - (p2.value(roots.get(i)) - p2.value(roots.get(i - 1))));
		}
		return area;
	}
	
	/**
	 * Class for searching all (complex) roots.
	 */
	private class ComplexSolver
	{		
		/**
		 * Find all complex roots for the polynomial with the given
		 * coefficients, starting from the given initial value.
		 * 
		 * @param coefficients
		 *            Polynomial coefficients.
		 * @param initial
		 *            Start value.
		 * @return the point at which the function value is zero.
		 * @throws org.apache.commons.math3.exception.TooManyEvaluationsException
		 *             if the maximum number of evaluations is exceeded.
		 * @throws NullArgumentException
		 *             if the {@code coefficients} is {@code null}.
		 * @throws NoDataException
		 *             if the {@code coefficients} array is empty.
		 */
		public Complex[] solveAll(Complex coefficients[], Complex initial) throws NullArgumentException, NoDataException,
				TooManyEvaluationsException {
			if (coefficients == null) {
				throw new NullArgumentException();
			}
			final int n = coefficients.length - 1;
			if (n == 0) {
				throw new NoDataException(LocalizedFormats.POLYNOMIAL);
			}
			// Coefficients for deflated polynomial.
			final Complex c[] = new Complex[n + 1];
			for (int i = 0; i <= n; i++) {
				c[i] = coefficients[i];
			}
			
			// Solve individual roots successively.
			final Complex root[] = new Complex[n];
			for (int i = 0; i < n; i++) {
				final Complex subarray[] = new Complex[n - i + 1];
				System.arraycopy(c, 0, subarray, 0, subarray.length);
				try {
					root[i] = solve(subarray, initial);
					// Polynomial deflation using synthetic division.
					Complex newc = c[n - i];
					Complex oldc = null;
					for (int j = n - i - 1; j >= 0; j--) {
						oldc = c[j];
						c[j] = newc;
						newc = oldc.add(newc.multiply(root[i]));
					}
				} catch (TooManyEvaluationsException ex) {
					Complex subRoots[] = new Complex[i];
					System.arraycopy(root, 0, subRoots, 0, i);
					return subRoots;
				}
			}
			
			return root;
		}
		
		/**
		 * Find a complex root for the polynomial with the given coefficients,
		 * starting from the given initial value.
		 * 
		 * @param coefficients
		 *            Polynomial coefficients.
		 * @param initial
		 *            Start value.
		 * @return the point at which the function value is zero.
		 * @throws org.apache.commons.math3.exception.TooManyEvaluationsException
		 *             if the maximum number of evaluations is exceeded.
		 * @throws NullArgumentException
		 *             if the {@code coefficients} is {@code null}.
		 * @throws NoDataException
		 *             if the {@code coefficients} array is empty.
		 */
		public Complex solve(Complex coefficients[], Complex initial) throws NullArgumentException, NoDataException,
				TooManyEvaluationsException {
			if (coefficients == null) {
				throw new NullArgumentException();
			}
			
			final int n = coefficients.length - 1;
			if (n == 0) {
				throw new NoDataException(LocalizedFormats.POLYNOMIAL);
			}
			
			final double absoluteAccuracy = getAbsoluteAccuracy();
			final double relativeAccuracy = getRelativeAccuracy();
			final double functionValueAccuracy = getFunctionValueAccuracy();
			
			final Complex nC = new Complex(n, 0);
			final Complex n1C = new Complex(n - 1, 0);
			
			Complex z = initial;
			Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			while (true) {
				// Compute pv (polynomial value), dv (derivative value), and
				// d2v (second derivative value) simultaneously.
				Complex pv = coefficients[n];
				Complex dv = Complex.ZERO;
				Complex d2v = Complex.ZERO;
				for (int j = n - 1; j >= 0; j--) {
					d2v = dv.add(z.multiply(d2v));
					dv = pv.add(z.multiply(dv));
					pv = coefficients[j].add(z.multiply(pv));
				}
				d2v = d2v.multiply(new Complex(2.0, 0.0));
				
				// Check for convergence.
				final double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
				if ((z.subtract(oldz)).abs() <= tolerance) {
					return z;
				}
				if (pv.abs() <= functionValueAccuracy) {
					return z;
				}
				
				// Now pv != 0, calculate the new approximation.
				final Complex G = dv.divide(pv);
				final Complex G2 = G.multiply(G);
				final Complex H = G2.subtract(d2v.divide(pv));
				final Complex delta = n1C.multiply((nC.multiply(H)).subtract(G2));
				// Choose a denominator larger in magnitude.
				final Complex deltaSqrt = delta.sqrt();
				final Complex dplus = G.add(deltaSqrt);
				final Complex dminus = G.subtract(deltaSqrt);
				final Complex denominator = dplus.abs() > dminus.abs() ? dplus : dminus;
				// Perturb z if denominator is zero, for instance,
				// p(x) = x^3 + 1, z = 0.
				if (denominator.equals(new Complex(0.0, 0.0))) {
					z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
					oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
				} else {
					oldz = z;
					z = z.subtract(nC.divide(denominator));
				}
				incrementEvaluationCount();
			}
		}
	}
}