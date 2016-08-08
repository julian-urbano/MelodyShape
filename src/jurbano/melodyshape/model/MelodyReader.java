// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.model;

import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads {@link Melody} objects from files or streams with a particular format.
 * 
 * @author Julián Urbano
 * @see Melody
 */
public interface MelodyReader extends FilenameFilter
{
	/**
	 * Reads a {@link Melody} from the specified path.
	 * 
	 * @param id
	 *            the unique ID of the melody to read.
	 * @param path
	 *            the path to read the melody.
	 * @return the melody read.
	 * @throws IOException
	 *             if an I/O or formatting error occurs.
	 */
	public Melody read(String id, String path) throws IOException;
	
	/**
	 * Reads a {@link Melody} from the specified input stream.
	 * 
	 * @param id
	 *            the unique ID of the melody to read.
	 * @param stream
	 *            the input stream to read the melody.
	 * @return the melody read.
	 * @throws IOException
	 *             if an I/O or formatting error occurs.
	 */
	public Melody read(String id, InputStream stream) throws IOException;
}
