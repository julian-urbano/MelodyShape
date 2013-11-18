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

package jurbano.melodyshape.comparison;

import java.util.HashMap;

/**
 * Provides a caching mechanism for an {@link NGramComparer} to speed up
 * computations.
 * <p>
 * Whenever a similarity score is computed between two {@link NGram}s, it is
 * cached so that the next time that two {@link NGram}s with the same
 * identifiers are computed the cached score is returned and the underlying
 * {@link NGramComparer} is not called again.
 * <p>
 * A similarity score is associated to a pair of {@link NGram}s according to
 * their identifiers, assuming a symmetric {@link NGramComparer}, that is,
 * {@code compare(n1, n2)=compare(n2, n1)}.
 * <p>
 * The internal caching mechanism employs a {@link HashMap} mapping the
 * {@code NGram}s' identifiers to their similarity score. This class does not
 * implement a replacement policy; whenever the cache is full no more similarity
 * scores are added, regardless of the age of cached scores.
 * 
 * @author Julián Urbano
 * @see NGramComparer
 */
public class CachedNGramComparer implements NGramComparer
{
	static final int DEFAULT_MAX_CACHE_SIZE = 2000000;
	
	protected NGramComparer comparer;
	
	protected HashMap<String, Double> cache;
	protected int maxCacheSize;
	
	/**
	 * Gets the maximum number of scores to cache.
	 * 
	 * @return the maximum number of scores to cache.
	 */
	public int getMaxCacheSize() {
		return maxCacheSize;
	}
	
	/**
	 * Sets the maximum number of scores to cache.
	 * <p>
	 * If the current cache is larger than {@code maxCacheSize} it is not
	 * reduced.
	 * 
	 * @param maxCacheSize
	 *            the maximum number of scores to cache.
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}
	
	/**
	 * Constructs a new {@code CachedNGramComparer} of the specified size.
	 * 
	 * @param comparer
	 *            the {@link NGramComparer} to cache.
	 * @param maxCacheSize
	 *            the maximum number of scores to cache.
	 */
	public CachedNGramComparer(NGramComparer comparer, int maxCacheSize) {
		this.comparer = comparer;
		this.maxCacheSize = maxCacheSize;
		this.cache = new HashMap<String, Double>();
	}
	
	/**
	 * Constructs a new {@code CachedNGramComparer} with the default maximum
	 * size (2 million scores).
	 * 
	 * @param comparer
	 *            the {@link NGramComparer} to cache.
	 */
	public CachedNGramComparer(NGramComparer comparer) {
		this(comparer, CachedNGramComparer.DEFAULT_MAX_CACHE_SIZE);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "Cache(comparer)"}, where
	 *         {@code comparer} is the name of the cached {@link NGramComparer}.
	 */
	@Override
	public String getName() {
		return "Cache(" + this.comparer.getName() + ")";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double compare(NGram n1, NGram n2) {
		String id1 = this.getNGramId(n1);
		String id2 = this.getNGramId(n2);
		String id = id1.compareTo(id2) < 0 ? id1 + "," + id2 : id2 + "," + id1;
		
		Double diff = this.cache.get(id);
		if (diff != null)
			return diff;
		else {
			double newDiff = this.comparer.compare(n1, n2);
			if (this.cache.size() < this.maxCacheSize)
				this.cache.put(id, newDiff);
			return newDiff;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNGramId(NGram g) {
		return this.comparer.getNGramId(g);
	}
	
	/**
	 * Gets the number of scores currently cached.
	 * 
	 * @return the number of scores currently cached.
	 */
	public int size() {
		return this.cache.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Cache(" + this.comparer.getName() + ")[size=" + this.cache.size() + "/" + maxCacheSize + "]";
	}
}
