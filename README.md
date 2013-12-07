MelodyShape
===========

MelodyShape is an open source Java library and tool to compute the melodic similarity between monophonic music pieces. It implements several algorithms that compute similarity based on the geometric shape that melodies describe in the pitch-time plane.

All these algorithms have obtained the best results in the [MIREX](http://music-ir.org/mirex/wiki/MIREX_HOME) Symbolic Melodic Similarity task in 2010, 2011, 2012 and 2013 editions, as well as the best results reported for the 2005 collection.

MelodyShape can be run both as a graphical user interface and as a command line tool:

	$ java -jar melodyshape-1.1.jar
	usage: melodyshape-1.1 -q <file/dir> -c <dir> -a <name> [-k <cutoff>] [-l]
	                         [-t <num>] [-v] [-vv] [-gui] [-h]
	-q <file/dir>  path to the query melody or melodies.
	-c <dir>       path to the collection of documents.
	-a <name>      algorithm to run:
	               - 2010-domain, 2010-pitchderiv, 2010-shape
	               - 2011-shape, 2011-pitch, 2011-time
	               - 2012-shapeh, 2012-shapel, 2012-shapeg, 2012-time, 2012-shapetime
	               - 2013-shapeh, 2013-time, 2013-shapetime
	-k <cutoff>    number of documents to retrieve.
	-l             show results in a single line (omits similarity scores).
	-t <num>       run a fixed number of threads.
	-v             verbose, to stderr.
	-vv            verbose a lot, to stderr.
	-gui           run with graphical user interface.
	-h             show this help message.
	
	MelodyShape 1.1  Copyright (C) 2013  Julian Urbano <urbano.julian@gmail.com>
	This program comes with ABSOLUTELY NO WARRANTY.
	This is free software, and you are welcome to redistribute it
	under the terms of the GNU General Public License version 3.

Dependencies
------------

* The MelodyShape library uses the [Math library](http://commons.apache.org/proper/commons-math/) from Apache Commons.

* The command line tool uses the [CLI library](http://commons.apache.org/proper/commons-cli/) from Apache Commons.

Current Version
---------------

The current version is [MelodyShape 1.1](https://github.com/julian-urbano/MelodyShape/releases/tag/v1.1), and it is compiled for Java 7. It can be redistributed and/or modified under the terms of the GNU General Public License version 3. Javadoc documentation and a user manual are available as well.
