package com.ozone.songwriter;

/** Circle defines methods and variables to represent the Circle of Fifths **/
public class Circle 
{
	/* The  major note ints */
	public static final int A = 0;
	public static final int B = 2;
	public static final int C = 3;
	public static final int D = 5;
	public static final int E = 7;
	public static final int F = 8;
	public static final int G = 10;
	 
	/* The  minor note ints */
	public static final int As = 1;
	public static final int Cs = 4;
	public static final int Ds = 6;
	public static final int Fs = 9;
	public static final int Gs = 12;
	
	/* The  major key arrays */
	public static final String[] aChords = {"A","Bm","C#m","D","E","F#m"};
	public static final String[] bChords = {"B","C#m","D#m","E","F#","G#m"};
	public static final String[] cChords = {"C","Dm","Em","F","G","Am"};
	public static final String[] dChords = {"D", "Em","F#m","G","A","Bm"};
	public static final String[] eChords = {"E","F#m","G#m","A","B","C#m"};
	public static final String[] fChords = {"F","Gm","Am","Bb","C","Dm"};
	public static final String[] gChords = {"G","Am","Bm","C","D","Em"};
	 
	/* The  minor key arrays */
	public static final String[] amChords = {"Am","Dm","Em","F","G","C"};
	public static final String[] bmChords = {"Bm","Em","F#m","G","A","D"};
	public static final String[] cmChords = {"C#m","F#m","G#m","A","B","E"};
	public static final String[] dmChords = {"Dm","Gm","Am","Bb","C","F"};
	public static final String[] emChords = {"Em","Am","Bm","C","D","G"};
	public static final String[] fmChords = {"F#m","Bm","C#m","D","E","A"};
	public static final String[] gmChords = {"G#m","C#m","D#m","E","F#","B"};
	
	/* Major scales */
	public static int[] aMajor = {A,B,Cs,D,E,Fs,Gs};
	public static int[] bMajor = {B,Cs,Ds,E,Fs,Gs,As};
	public static int[] cMajor = {C,D,E,F,G,A,B};
	public static int[] dMajor = {D,E,Fs,G,A,B,Cs};
	public static int[] eMajor = {E,Fs,Gs,A,B,Cs,Ds};
	public static int[] fMajor = {F,G,A,As,C,D,E};
	public static int[] gMajor = {G,A,B,C,D,E,Fs};
	
	/* Minor scales */
	public static int[] aMinor = {A,B,C,D,E,F,G};
	public static int[] bMinor = {B,Cs,D,E,Fs,G,A};
	public static int[] cMinor = {C,D,Ds,F,G,Gs,As};
	public static int[] dMinor = {D,E,F,G,A,As,C};
	public static int[] eMinor = {E,Fs,G,A,B,C,D};
	public static int[] fMinor = {F,G,Gs,As,C,Cs,Ds};
	public static int[] gMinor = {G,A,As,C,D,Ds,F};
	
	/** Arrays of chord arrays **/
	public static final String[][] MajorChords = {aChords, bChords, cChords, dChords, eChords, fChords, gChords};
	public static final String[][] MinorChords = {amChords, bmChords, cmChords, dmChords, emChords, fmChords, gmChords};
	
	/** Array of all chords **/
	public static final String[][][] AllChords = { MajorChords, MinorChords };
	
	
	/* Static Major Key objects */
	public static final Key KeyA = new Key(A, false);
	public static final Key KeyB = new Key(B, false);
	public static final Key KeyC = new Key(C, false);
	public static final Key KeyD = new Key(D, false);
	public static final Key KeyE = new Key(E, false);
	public static final Key KeyF = new Key(F, false);
	public static final Key KeyG = new Key(G, false);
	
	/* Static Minor Key objects */
	public static final Key KeyAm = new Key(A, true);
	public static final Key KeyBm = new Key(B, true);
	public static final Key KeyCm = new Key(C, true);
	public static final Key KeyDm = new Key(D, true);
	public static final Key KeyEm = new Key(E, true);
	public static final Key KeyFm = new Key(F, true);
	public static final Key KeyGm = new Key(G, true);
	
	public static final Key[][] AllKeys = { {KeyA, KeyB, KeyC, KeyD, KeyE, KeyF, KeyG} , {KeyAm, KeyBm, KeyCm, KeyDm, KeyEm, KeyFm, KeyGm}};
	
	/* Holds all note integers */
	public static final int[] NOTES = {A,As,B,C,Cs,D,Ds,E,F,Fs,G,Gs};
	

	public static Sequence getChordSequence(Key key)
	{
		String[] chords = chordsFromBase(key.Base, key.isMinor);
		
		return new Sequence(chords[0], chords[1], chords[2], chords[3]);
	}
	
	
	
	
	
	
	/** Return the chords for a given key **/
	public static String[] chordsFromBase(int base, boolean isMinor)
	{
		if(isMinor)
		{
			switch(base)
			{
				case A:
					return amChords;
				case B:
					return bmChords;
				case C:
					return cmChords;
				case D:
					return dmChords;
				case E:
					return emChords;
				case F:
					return fmChords;
				case G:
					return gmChords;
			}
		}
		else
		{
			switch(base)
			{
				case A:
					return aChords;
				case B:
					return bChords;
				case C:
					return cChords;
				case D:
					return dChords;
				case E:
					return eChords;
				case F:
					return fChords;
				case G:
					return gChords;
			}
		}
		
		return null;
	}
	
	
	/** Return the scale for a given key **/
	public static int[] scaleFromBase(int base, boolean isMinor)
	{
		if(isMinor)
		{
			switch(base)
			{
				case A:
					return aMinor;
				case B:
					return bMinor;
				case C:
					return cMinor;
				case D:
					return dMinor;
				case E:
					return eMinor;
				case F:
					return fMinor;
				case G:
					return gMinor;
			}
		}
		else
		{
			switch(base)
			{
				case A:
					return aMajor;
				case B:
					return bMajor;
				case C:
					return cMajor;
				case D:
					return dMajor;
				case E:
					return eMajor;
				case F:
					return fMajor;
				case G:
					return gMajor;
			}
		}
		
		return null;
	}
	
	
	
	
	/* Helper subclasses are defined here */
	/** Defines a key (Scales, etc) **/
	public static class Key
	{
		/** The data for this key **/
		public boolean isMinor = false;
		public int Base = -1;
		public int[] Scale;
		public String[] Chords;
		
		public Key(int baseNote, boolean minor)
		{
			this.isMinor = minor;
			this.Base = baseNote;
			this.Chords = chordsFromBase(Base, isMinor);
			this.Scale = scaleFromBase(Base, isMinor);
		}
	}
	
	/** Defines a chord (Triads, key, etc) **/
	public static class Chord
	{
		public int[] Components = new int[3];
		
		public Chord(int base)
		{
			Key key = new Key(base, false);
			int[] scale = key.Scale;
			
			this.Components[0] = scale[0];
			this.Components[1] = scale[2];
			this.Components[2] = scale[4];
		}
		
		/** Provide a suitable implementation of toString(), return the chord name: Am, or D7th, or G **/
		@Override
		public String toString()
		{
			return "";
		}
	}
}
