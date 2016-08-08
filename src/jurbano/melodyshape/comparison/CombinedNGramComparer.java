// Copyright (C) 2013, 2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package jurbano.melodyshape.comparison;

/**
 * The weighted combination of two {@link NGramComparer}s.
 * 
 * @author Julián Urbano
 * @see NGramComparer
 */
public class CombinedNGramComparer implements NGramComparer
{
	protected double w_1;
	protected double w_2;
	protected double mu_1;
	protected double mu_2;
	
	protected NGramComparer comparer1;
	protected NGramComparer comparer2;
	
	/**
	 * Constructs a new {@code CombinedNGramComparer}.
	 * 
	 * @param comparer1
	 *            the first comparer to combine.
	 * @param w_1
	 *            the weight for the first comparer's scores.
	 * @param mu_1
	 *            the average score returned by the first comparer.
	 * @param comparer2
	 *            the second comparer to combine.
	 * @param w_2
	 *            the weight for the second comparer's score.
	 * @param mu_2
	 *            the average score returned by the second comparer.
	 */
	public CombinedNGramComparer(NGramComparer comparer1, double w_1, double mu_1, NGramComparer comparer2, double w_2, double mu_2) {
		if (w_1 == 0 && w_2 == 0)
			throw new IllegalArgumentException("Weights w_1 and w_t cannot both be zero");
		
		this.w_1 = w_1;
		this.w_2 = w_2;
		this.mu_1 = mu_1;
		this.mu_2 = mu_2;
		
		this.comparer1 = comparer1;
		this.comparer2 = comparer2;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String}
	 *         {@code "(comparer1(w_1,mu_1),comparer2(w_2,mu_2))"}.
	 */
	@Override
	public String getName() {
		return "(" + this.comparer1.getName() + "(" + this.w_1 + "," + this.mu_1 + ")," + this.comparer2.getName() + "(" + this.w_2 + ","
				+ this.mu_2 + "))";
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If both {@link NGram}s are equal according to the i-th underlying
	 * {@link NGramComparer}, {@code 2*mu_i} is the similarity score for the
	 * i-th component. The second component is always normalized by multiplying
	 * it by {@code mu_1/mu_2}. The final similarity score is computed as
	 * {@code w_1*similarity_1 + w_2*similarity_2}, where {@code similarity_2}
	 * is the normalized second component.
	 */
	@Override
	public double compare(NGram g1, NGram g2) {
		double diff1 = 0, diff2 = 0;
		
		if (this.w_1 != 0) {
			if (g1 != null && g2 != null && this.comparer1.getNGramId(g1).equals(this.comparer1.getNGramId(g2)))
				diff1 = 2 * this.mu_1;
			else
				diff1 = this.comparer1.compare(g1, g2);
		}
		if (this.w_2 != 0) {
			if (g1 != null && g2 != null && this.comparer2.getNGramId(g1).equals(this.comparer2.getNGramId(g2)))
				diff2 = 2 * this.mu_2;
			else
				diff2 = this.comparer2.compare(g1, g2);
		}
		
		diff2 *= this.mu_1 / this.mu_2;
		return diff1 * this.w_1 + diff2 * this.w_2;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link String} {@code "(id_1,id_2)"}, where {@code id_i} is
	 *         the n-gram identifier according to to the i-th comparer.
	 */
	@Override
	public String getNGramId(NGram g) {
		return "(" + this.comparer1.getNGramId(g) + "," + this.comparer2.getNGramId(g) + ")";
	}
}
