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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A set of {@link Melody} objects completely stored in main memory.
 * <p>
 * {@link Melody} objects can be read from individual files in a directory or in
 * a ZIP file.
 * 
 * @author Julián Urbano
 * @see MelodyCollection
 * @see Melody
 */
public class InMemoryMelodyCollection implements MelodyCollection
{
	// TODO: javadoc
	private static String filenameWithoutExtension(String f) {
		if (f.indexOf('/') > 0)
			f = f.substring(f.lastIndexOf('/') + 1);
		if (f.indexOf('\\') > 0)
			f = f.substring(f.lastIndexOf('\\') + 1);
		if (f.indexOf('.') > 0)
			f = f.substring(0, f.lastIndexOf('.'));
		return f;
	}
	
	protected HashMap<String, Melody> melodies;
	protected MelodyReader melodyReader;
	protected String name;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * Constructs a new {@code InMemoryMelodyCollection} from the specified path
	 * and using the specified {@link MelodyReader}.
	 * <p>
	 * The {@code path} can be either a directory or a ZIP file. One
	 * {@link Melody} object will be read from each individual file matching the
	 * {@link FilenameFilter} of the {@link MelodyReader}. No files are read
	 * from subdirectories.
	 * 
	 * @param name
	 *            the name of the collection.
	 * @param path
	 *            the path to read melodies from.
	 * @param reader
	 *            the {@link MelodyReader} to use to filter files and read
	 *            melodies.
	 * @throws IOException
	 *             if an I/O or format error occurs.
	 */
	public InMemoryMelodyCollection(String name, String path, MelodyReader reader) throws IOException {
		this.name = name;
		this.melodyReader = reader;
		this.melodies = new HashMap<String, Melody>();
		
		// Read all midi files
		File file = new File(path);
		if (file.isDirectory()) {
			// it's a directory
			for (File f : file.listFiles(reader)) {
				String id = filenameWithoutExtension(f.getName());
				Melody m = this.melodyReader.read(id, f.getAbsolutePath());
				this.melodies.put(m.getId(), m);
			}
		} else if (file.getName().toLowerCase().endsWith(".zip")) {
			// it's a zip file
			ZipFile zip = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry e = entries.nextElement();
				if (!e.isDirectory() && reader.accept(null, e.getName())) {
					String id = filenameWithoutExtension(e.getName());
					InputStream inStream = zip.getInputStream(e);
					Melody m = this.melodyReader.read(id, inStream);
					this.melodies.put(m.getId(), m);
					inStream.close();
				}
			}
			zip.close();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Melody get(String id) {
		return this.melodies.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Melody> iterator() {
		return this.melodies.values().iterator();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.melodies.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "InMemoryMelodyCollection [name=" + name + ", size=" + melodies.size() + "]";
	}
}
