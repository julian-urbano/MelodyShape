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

/**
 * A set of uniquely identified {@link Melody} objects.
 * 
 * @author Julián Urbano
 * @see Melody
 */
public interface MelodyCollection extends Iterable<Melody>
{
	/**
	 * Gets the name of the collection.
	 * 
	 * @return the name of the collection.
	 */
	public String getName();
	
	/**
	 * Gets the {@link Melody} with the specified ID or {@code null} if there is
	 * no {@link Melody} with that ID.
	 * 
	 * @param id
	 *            the ID of the melody to get.
	 * @return the melody with the specified ID.
	 */
	public Melody get(String id);
	
	/**
	 * Gets the number of {@link Melody} objects in the collection.
	 * 
	 * @return the number of melodies.
	 */
	public int size();
}
