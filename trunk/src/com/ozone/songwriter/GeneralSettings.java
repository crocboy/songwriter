package com.ozone.songwriter;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

/* The main activity's settings */
public class GeneralSettings extends PreferenceActivity 
{
	@Override
	 protected void onCreate(Bundle savedInstanceState) 
	{
		try 
		{
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.layout.preferences);
		}
		catch(Exception e) 
		{
			Log.e("ERRORS","GeneralSettings error: " + String.valueOf(e));
		}
	}
}
