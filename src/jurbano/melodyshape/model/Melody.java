// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.model;

import java.util.ArrayList;

/**
 * Represents a uniquely identified sequence of {@link Note}s.
 * <p>
 * This class does not guarantee that all melody identifiers are different
 * though; it is the caller's responsibility to guarantee it. It does not
 * guarantee either that the {@link Note} objects are sorted by their onset
 * time.
 * 
 * @author Julián Urbano
 * @see Note
 * @see MelodyReader
 */
@SuppressWarnings("serial")
public class Melody extends ArrayList<Note>
{
	protected String id;
	
	/**
	 * Gets the unique ID of the melody.
	 * 
	 * @return the ID of the melody.
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Constructs a new and empty {@code Melody} object.
	 * 
	 * @param id
	 *            the unique ID of the melody.
	 */
	public Melody(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Melody [id=" + id + ", size=" + this.size() + "]";
	}
}
