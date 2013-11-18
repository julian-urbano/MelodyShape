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
