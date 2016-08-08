// Copyright (C) 2013, 2015-2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

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
	protected HashMap<String, Melody> melodies;
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
		this(name);

		// Read all midi files
		File file = new File(path);
		if (file.isDirectory()) {
			// it's a directory
			for (File f : file.listFiles(reader)) {
				try {
					String id = f.getName();
					Melody m = reader.read(id, f.getAbsolutePath());
					this.melodies.put(m.getId(), m);
				} catch (IOException ex) {
					throw new IllegalArgumentException("bad format in document file '" + f.getAbsolutePath() + "': "
							+ ex.getMessage());
				}
			}
		} else if (file.getName().toLowerCase().endsWith(".zip")) {
			// it's a zip file
			ZipFile zip = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry e = entries.nextElement();
				if (!e.isDirectory() && reader.accept(null, e.getName())) {
					InputStream inStream = null;
					try {
						String id = e.getName();
						inStream = zip.getInputStream(e);
						Melody m = reader.read(id, inStream);
						this.melodies.put(m.getId(), m);
						inStream.close();
					} catch (IOException ex) {
						if (inStream != null)
							inStream.close();
						zip.close();
						throw new IllegalArgumentException("bad format in document file '" + e.getName() + "': "
								+ ex.getMessage());
					}
				}
			}
			zip.close();
		}
	}
	
	/**
	 * Constructs a new and empty {@code InMemoryMelodyCollection}.
	 * 
	 * @param name the name of the collection.
	 */
	public InMemoryMelodyCollection(String name) {
		this.name = name;
		this.melodies = new HashMap<String, Melody>();
	}
	
	/**
	 * Adds a {@link Melody} to the collection. If a melody already exists with
	 * the same ID, no changes are made.
	 * 
	 * @param m
	 *            the melody to add.
	 * @return {@code false} if there already is a melody with the same ID, or
	 *         {@code true} if not.
	 */
	public boolean add(Melody m) {
		if (this.melodies.containsKey(m.getId()))
			return false;
		this.melodies.put(m.getId(), m);
		return true;
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
