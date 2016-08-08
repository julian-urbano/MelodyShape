// Copyright (C) 2014, 2015-2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape;

import jurbano.melodyshape.ui.ConsoleUIObserver;
import jurbano.melodyshape.ui.UIObserver;

/**
 * Main class used for MIREX submissions; just wraps arguments for {@link MelodyShape}.
 * Not to be used elsewhere.
 * 
 * @author Julián Urbano
 */
@Deprecated
public class Mirex {

	@SuppressWarnings("javadoc")
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("usage: melodyshape-mirex2015 <algorithm> <path-to-collection> <path-to-query>");
			System.err.println("       where <algorithm> is one of shapeh, time or shapetime");
			System.exit(1);
		}
		// Parse options
		String aStr = args[0];
		String cStr = args[1];
		String qStr = args[2];

		if (aStr.equals("shapeh"))
			aStr = "2013-shapeh";
		else if (aStr.equals("time"))
			aStr = "2013-time";
		else if (aStr.equals("shapetime"))
			aStr = "2013-shapetime";
		else {
			System.err.println("Error: unknown algorithm: " + aStr);
			System.exit(1);
		}
		args = new String[] { "-q", qStr, "-c", cStr, "-a", aStr, "-k", "10", "-l", "-vv" };

		UIObserver observer = new ConsoleUIObserver(args);
		observer.start();
	}
}
