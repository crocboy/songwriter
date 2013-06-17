package com.ozone.songwriter;


/** Contains static functions that provide simple functionality */
public class Util 
{
	
	/** Convert BPM to millis */
	public static int BpmToMillis(int bpm) 
	{

		/* How often does a beat occur? */
		double delaySeconds = 60f / bpm;
		
		/* Make it into millis! */
		double millisDelay = delaySeconds * 1000;
		
		return (int) millisDelay;
	}
	
	/** Convert Chord name to resource name: Am to am, G#m to gsm, etc **/
	public static String toResource(String chord, boolean isGuitar)
	{
		/* Replace '#' with 's'. lower case it, and trim that baby */
		chord = chord.replace('#', 's').toLowerCase().trim();
		
		if(isGuitar) //Add 'gut' if needed
			chord += "gut";
		
		return chord;
	}
	
	
	/** Convert resource name to chord name: asm to A#m, gm to Gm, etc **/
	public static String toChord(String res)
	{
		return null;
	}

}
