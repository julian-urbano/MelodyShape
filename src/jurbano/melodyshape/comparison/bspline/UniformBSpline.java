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

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * Contains the base functions to compute Uniform B-Splines of degrees 1 to 6.
 * 
 * @author Julián Urbano
 */
public interface UniformBSpline
{
	public static final PolynomialFunction[][] BASIS_FUNCTIONS = new PolynomialFunction[][]{
		// degree 0
		new PolynomialFunction[]{
			new PolynomialFunction(new double[] { 1 }) // 1
		},
		// degree 1
		new PolynomialFunction[]{
			new PolynomialFunction(new double[] { 0, 1 }), // x
			new PolynomialFunction(new double[] { 1, -1 }) // 1-x
		},
		// degree 2
		new PolynomialFunction[]{
			new PolynomialFunction(new double[] { 0, 0, 0.5 }), // x^2/2
			new PolynomialFunction(new double[] { 0.5, 1, -1 }), // -x^2+x+1/2
			new PolynomialFunction(new double[] { 0.5, -1, 0.5 }) // x^2/2-x+1/2
		},
		// degree 3
		new PolynomialFunction[]{
			new PolynomialFunction(new double[] { 0, 0, 0, 1.0 / 6 }), // x^3/6
			new PolynomialFunction(new double[] { 1.0 / 6, 0.5, 0.5, -0.5 }), // -x^3/2+x^2/2+x/2+1/6
			new PolynomialFunction(new double[] { 2.0 / 3, 0, -1, 0.5 }), // x^3/2-x^2+2/3
			new PolynomialFunction(new double[] { 1.0 / 6, -0.5, 0.5, -1.0 / 6 }) // -x^3/6+x^2/2-x/2+1/6
		},
		// degree 4
		new PolynomialFunction[]{
			new PolynomialFunction(new double[] { 0, 0, 0, 0, 1.0 / 24 }), // x^4/24
			new PolynomialFunction(new double[] { 1.0 / 24, 1.0 / 6, 0.25, 1.0 / 6, -1.0 / 6 }), // -x^4/6+x^3/6+x^2/4+x/6+1/24
			new PolynomialFunction(new double[] { 11.0 / 24, 0.5, -0.25, -0.5, 0.25 }), // x^4/4-x^3/2-x^2/4+x/2+11/24
			new PolynomialFunction(new double[] { 11.0 / 24, -0.5, -0.25, 0.5, -1.0 / 6 }), // -x^4/6+x^3/2-x^2/4-x/2+11/24
			new PolynomialFunction(new double[] { 1.0 / 24, -1.0 / 6, 0.25, -1.0 / 6, 1.0 / 24 }) // x^4/24-x^3/6+x^2/4-x/6+1/24
		},
		// degree 5
		new PolynomialFunction[]{
            new PolynomialFunction(new double[] { 0, 0, 0, 0, 0, 1.0 / 120}), // x^5/120
            new PolynomialFunction(new double[] { 1.0 / 120, 1.0 / 24, 1.0 / 12, 1.0 / 12, 1.0 / 24, -1.0 / 24}), // -x^5/24+x^4/24+x^3/12+x^2/12+x/24+1/120
            new PolynomialFunction(new double[] { 13.0 / 60, 5.0 / 12, 1.0 / 6, -1.0 / 6, -1.0 / 6, 1.0 / 12}), // x^5/12-x^4/6-x^3/6+x^2/6+(5*x)/12+13/60
            new PolynomialFunction(new double[] { 11.0 / 20, 0, -0.5, 0, 0.25, -1.0 / 12}), // -x^5/12+x^4/4-x^2/2+11/20
            new PolynomialFunction(new double[] { 13.0 / 60, -5.0 / 12, 1.0 / 6, 1.0 / 6, -1.0 / 6, 1.0 / 24}), // x^5/24-x^4/6+x^3/6+x^2/6-(5*x)/12+13/60
            new PolynomialFunction(new double[] { 1.0 / 120, -1.0 / 24, 1.0 / 12, -1.0 / 12, 1.0 / 24, -1.0 / 120}) // -x^5/120+x^4/24-x^3/12+x^2/12-x/24+1/120
		},
		// degree 6
		new PolynomialFunction[]{
            new PolynomialFunction(new double[] { 0, 0, 0, 0, 0, 0, 1.0 / 720}), // x^6/720
            new PolynomialFunction(new double[] { 1.0 / 720, 6.0 / 720, 15.0 / 720, 20.0 / 720, 15.0 / 720, 6.0 / 720, -6.0 / 720}), // -(6*x^6-6*x^5-15*x^4-20*x^3-15*x^2-6*x-1)/720
            new PolynomialFunction(new double[] { 57.0 / 720, 150.0 / 720, 135.0 / 720, 20.0 / 720, -45.0 / 720, -30.0 / 720, 15.0 / 720}), // (15*x^6-30*x^5-45*x^4+20*x^3+135*x^2+150*x+57)/720
            new PolynomialFunction(new double[] { 151.0 / 360, 120.0 / 360, -75.0 / 360, -80.0 / 360, 15.0 / 360, 30.0 / 360, -10.0 / 360}), // -(10*x^6-30*x^5-15*x^4+80*x^3+75*x^2-120*x-151)/360
            new PolynomialFunction(new double[] { 302.0 / 720, -240.0 / 720, -150.0 / 720, 160.0 / 720, 30.0 / 720, -60.0 / 720, 15.0 / 720}), // (15*x^6-60*x^5+30*x^4+160*x^3-150*x^2-240*x+302)/720
            new PolynomialFunction(new double[] { 57.0 / 720, -150.0 / 720, 135.0 / 720, -20.0 / 720, -45.0 / 720, 30.0 / 720, -6.0 / 720}), // -(6*x^6-30*x^5+45*x^4+20*x^3-135*x^2+150*x-57)/720
            new PolynomialFunction(new double[] { 1.0 / 720, -6.0 / 720, 15.0 / 720, -20.0 / 720, 15.0 / 720, -6.0 / 720, 1.0 / 720}) // (x^6-6*x^5+15*x^4-20*x^3+15*x^2-6*x+1)/720
		}
	};
}
