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
