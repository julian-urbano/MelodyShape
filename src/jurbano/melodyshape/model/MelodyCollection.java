// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

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
