package com.ozone.songwriter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
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
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		catch(Exception e) 
		{
			Log.e("ERRORS",String.valueOf(e));
		}
	}
}
