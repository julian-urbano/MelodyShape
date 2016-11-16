[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/julian-urbano/MelodyShape/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/release/julian-urbano/MelodyShape.svg)](https://github.com/julian-urbano/MelodyShape/releases/latest)
[![JDK](https://img.shields.io/badge/JDK-1.7-blue.svg)]()
[![Build Status](https://travis-ci.org/julian-urbano/MelodyShape.png)](https://travis-ci.org/julian-urbano/MelodyShape)

MelodyShape
===========

MelodyShape is an open source Java library and tool to compute the melodic similarity between monophonic music pieces. It implements several algorithms that compute similarity based on the geometric shape that melodies describe in the pitch-time plane.

All these algorithms have obtained the best results in the [MIREX](http://music-ir.org/mirex/wiki/MIREX_HOME) Symbolic Melodic Similarity task in 2010, 2011, 2012, 2013, 2014 and 2015 editions, as well as the best results reported for the 2005 collection.

MelodyShape can be run both as a graphical user interface and as a command line tool:

	$ java -jar melodyshape-1.4.jar
	usage: melodyshape-1.4 -q <file/dir> -c <dir> -a <name> [-k <cutoff>] [-l] [-t <num>] [-v] [-vv] [-gui] [-h]
	-q <file/dir>  path to the query melody or melodies.
	-c <dir>       path to the collection of documents.
	-a <name>      algorithm to run:
	               - 2010-domain, 2010-pitchderiv, 2010-shape
	               - 2011-shape, 2011-pitch, 2011-time
	               - 2012-shapeh, 2012-shapel, 2012-shapeg, 2012-time, 2012-shapetime
	               - 2013-shapeh, 2013-time, 2013-shapetime
	               - 2014-shapeh, 2014-time, 2014-shapetime
	               - 2015-shapeh, 2015-time, 2015-shapetime
	-k <cutoff>    number of documents to retrieve.
	-l             show results in a single line (omits similarity scores).
	-t <num>       run a fixed number of threads.
	-v             verbose, to stderr.
	-vv            verbose a lot, to stderr.
	-gui           run with graphical user interface.
	-h             show this help message.
	
	MelodyShape 1.4  Copyright (C) 2013-2016  Julian Urbano <urbano.julian@gmail.com>
	This program is distributed under the terms of the MIT License.

A detailed user manual in PDF is available from the [releases page](https://github.com/julian-urbano/MelodyShape/releases).

Dependencies
------------

* The MelodyShape library uses the [Math library](http://commons.apache.org/proper/commons-math/) from Apache Commons.
* The command line tool uses the [CLI library](http://commons.apache.org/proper/commons-cli/) from Apache Commons.

Dependencies and building can be managed directly with Maven.

Current Version
---------------

The current version is [MelodyShape 1.4](https://github.com/julian-urbano/MelodyShape/releases/tag/v1.4), and it is compiled for Java 7. It can be redistributed and/or modified under the terms of the MIT License. Javadoc documentation and a user manual are available as well.

> This work was supported by an A4U postdoctoral grant and a Juan de la Cierva postdoctoral fellowship.
