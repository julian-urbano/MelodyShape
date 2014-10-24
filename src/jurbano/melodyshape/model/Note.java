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
 * Represents a note in a {@link Melody}.
 * <p>
 * It is defined by an onset time when the note starts sounding, a duration, and
 * the pitch of the sound. In addition, if the note is accompanied by rests, the
 * {@code Note} object specifies the fraction of the total duration that
 * corresponds to silence.
 * <p>
 * The units for pitch, onset and duration are determined by the
 * {@link MelodyReader} used.
 * 
 * @author Julián Urbano
 * @see Melody
 * @see MelodyReader
 */
public class Note
{
	protected long duration;
	protected long onset;
	protected byte pitch;
	protected double restFraction;
	
	/**
	 * Constructs a new {@code Note}.
	 * 
	 * @param pitch
	 *            the pitch of the note.
	 * @param onset
	 *            the onset time of the note.
	 * @param duration
	 *            the duration of the note.
	 * @param restFraction
	 *            the fraction of the note's duration that corresponds do rests.
	 */
	public Note(byte pitch, long onset, long duration, double restFraction) {
		this.pitch = pitch;
		this.onset = onset;
		this.duration = duration;
		this.restFraction = restFraction;
	}
	
	/**
	 * Gets the duration of the note.
	 * 
	 * @return the duration of the note.
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * Gets the onset time of the note.
	 * 
	 * @return the onset time of the note.
	 */
	public long getOnset() {
		return this.onset;
	}
	
	/**
	 * Gets the pitch of the note.
	 * 
	 * @return the pitch of the note.
	 */
	public byte getPitch() {
		return this.pitch;
	}
	
	/**
	 * Gets the fraction of the note's duration that corresponds to rests.
	 * 
	 * @return the rest fraction.
	 */
	public double getRestFraction() {
		return this.restFraction;
	}
	
	@Override
	public String toString() {
		return "Note [@" + this.onset + ": pitch=" + this.pitch + ", duration=" + this.duration + ", rest=" + this.restFraction + "]";
	}
	
}
