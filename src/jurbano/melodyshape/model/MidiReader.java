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
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Reads {@link Melody} objects from files or streams in MIDI format.
 * <p>
 * The melody is read from the track with the most events. There can only be one
 * note sounding at the same time. A {@code NOTE_ON} event with velocity equal
 * zero is considered as a {@code NOTE_OFF} event. Onset time and duration are
 * measured in MIDI ticks.
 * 
 * @author Julián Urbano
 * @see MelodyReader
 * @see Melody
 */
public class MidiReader implements MelodyReader
{
	/**
	 * Processes {@code NOTE_ON} and {@code NOTE_OFF} events in a MIDI
	 * {@link Sequence} and adds the corresponding {@link Note}s to a
	 * {@link Melody} object.
	 * <p>
	 * Only the MIDI {@link Track} with the most events is processed.
	 * 
	 * @param m
	 *            the melody to add notes to.
	 * @param seq
	 *            the MIDI sequence to read events from.
	 * @throws IOException
	 *             if an I/O error occurs or there is more than one sound at the
	 *             same time.
	 */
	protected void readSequence2(Melody m, Sequence seq) throws IOException {
		// Get the track with the most events
		Track[] tracks = seq.getTracks();
		Track track = tracks[0];
		for (Track t : tracks)
			if (t.size() > track.size())
				track = t;
		
		long lastOff = 0, lastOn = -1;
		int lastPitch = -1;
		// Read and process all events
		for (int i = 0; i < track.size(); i++) {
			MidiEvent me = track.get(i);
			MidiMessage mm = me.getMessage();
			if (mm instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) mm;
				long time = me.getTick();
				int pitch = sm.getData1();
				int velocity = sm.getData2();
				
				// A note_on with velocity=0 will be treated like a note_off
				if (sm.getCommand() == ShortMessage.NOTE_ON && velocity != 0) {
					if (lastPitch != -1)
						throw new IOException("Several notes at time " + time);
					else {
						lastOn = time;
						lastPitch = pitch;
					}
				} else if (sm.getCommand() == ShortMessage.NOTE_OFF || (sm.getCommand() == ShortMessage.NOTE_ON && velocity == 0)) {
					if (lastPitch != -1) {
						Note n = new Note((byte) pitch, lastOff, time - lastOn, (double) (lastOn - lastOff) / (time - lastOff));
						m.add(n);
					}
					lastPitch = -1;
					lastOff = time;
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Melody read(String id, String path) throws IOException {
		Melody m = new Melody(id);
		try {
			Sequence seq = MidiSystem.getSequence(new File(path));
			readSequence2(m, seq);
		} catch (InvalidMidiDataException e) {
			IOException ex = new IOException(e);
			throw ex;
		}
		return m;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Melody read(String id, InputStream stream) throws IOException {
		Melody m = new Melody(id);
		try {
			Sequence seq = MidiSystem.getSequence(stream);
			readSequence2(m, seq);
		} catch (InvalidMidiDataException e) {
			IOException ex = new IOException(e);
			throw ex;
		}
		return m;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".mid") || name.endsWith(".midi");
	}
}
