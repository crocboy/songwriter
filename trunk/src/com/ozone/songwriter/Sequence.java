package com.ozone.songwriter;
import com.ozone.songwriter.Circle.Chord;

/** This small, simple class is defined in its on file so it can be easily used globally. **/
public class Sequence
{
	public Chord[] Chords;
	public String[] ChordStrings;
	
	public Sequence(Chord one, Chord two, Chord three, Chord four)
	{
		Chords = new Chord[] {one, two, three, four};
		ChordStrings = new String[] {one.toString(), two.toString(), three.toString(), four.toString()};
	}
	
	public Sequence(String one, String two, String three, String four)
	{
		ChordStrings = new String[] {one, two, three, four};
	}
	
	public String[] getChords()
	{
		return ChordStrings;
	}
}
