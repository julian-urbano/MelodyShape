// Copyright (C) 2013, 2015-2016  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

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
				
//				System.out.print("@"+time+": pitch="+pitch+" ");
//				if(sm.getCommand() == ShortMessage.NOTE_ON && velocity != 0)
//					System.out.println("ON");
//				else if(sm.getCommand() == ShortMessage.NOTE_OFF || (sm.getCommand() == ShortMessage.NOTE_ON && velocity == 0))
//					System.out.println("OFF");
					
				// A note_on with velocity=0 will be treated like a note_off
				if (sm.getCommand() == ShortMessage.NOTE_ON && velocity != 0) {
					if (lastPitch != -1){
						// Found a note_on when we expected a note_off. Two options here:
						// 1) if the next event is close in time and it is the missing note_off, we just use this as note_off too.
						// 2) if the next event is far or is not the missing note_off, we have polyphony.
						boolean asNote_off = false;
						if(i+1 < track.size()){
							MidiEvent meNext = track.get(i+1);
							MidiMessage mmNext = meNext.getMessage();
							if(mmNext instanceof ShortMessage){
								ShortMessage smNext = (ShortMessage)mmNext;
								long timeNext = meNext.getTick();
								int pitchNext = smNext.getData1();
								int velocityNexy = smNext.getData2();
								if((smNext.getCommand() == ShortMessage.NOTE_OFF || (smNext.getCommand() == ShortMessage.NOTE_ON && velocityNexy == 0)) &&
									timeNext - time <= 1 && pitchNext == lastPitch){ // more than 1 tick away is too far
									// We are in case 1)
									Note n = new Note((byte) lastPitch, lastOff, time - lastOn, (double) (lastOn - lastOff) / (time - lastOff));
									m.add(n);
									
									lastOff=time;
									i++; // to skip the next note_off message processed here
									asNote_off = true;
								}
							}
						}
						if(!asNote_off){
							// We are in case 2)
							throw new IOException("several notes at time " + time);							
						}
					}
					lastOn = time;
					lastPitch = pitch;
				} else if (sm.getCommand() == ShortMessage.NOTE_OFF || (sm.getCommand() == ShortMessage.NOTE_ON && velocity == 0)) {
					if (lastPitch == pitch) {
						Note n = new Note((byte) lastPitch, lastOff, time - lastOn, (double) (lastOn - lastOff) / (time - lastOff));
						m.add(n);
						lastPitch = -1;
						lastOff = time;
					}else{
						// We have a note_off for a pitch different from last note_on's
						throw new IOException("several notes at time " + time);							
					}
				}
			}
		}
		
//		for(Note n : m){
//			System.out.println(n.toString());
//		}
//		System.out.println();
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
		return name.toLowerCase().endsWith(".mid") || name.toLowerCase().endsWith(".midi");
	}
}
