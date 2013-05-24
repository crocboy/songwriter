package com.ozone.songwriter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.Window;

/* This small-ish activity starts the app; it shows a simple image and then flows into the MainActivity */
 public class SplashScreen extends Activity 
 {
	/* Thread instance */
	private Thread mSplashThread;    

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
  	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        /* Open the layout */
        setContentView(R.layout.splash);
        
        final SplashScreen splashScreen = this;   
        
        // The thread to wait for splash screen events
        mSplashThread =  new Thread()
        {
            @Override
            public void run()
            {
                try 
                {
                    synchronized(this)
                    {
                        // Wait given period of time or exit on touch
                        wait(2000);
                    }
                    
                }
                catch(InterruptedException e)
                { 
                	
                }

                /* Start next activity when finished */
                Intent intent = new Intent();
                intent.setClass(splashScreen, MainActivity.class);
                startActivity(intent);
                
                
                finish();
                
            }
        };
        
        
        /* If the user has disabled the splash screen, just run into the next activity */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSplash = sharedPrefs.getBoolean("enable_splash", true);
        if(isSplash == true) 
        {
        	mSplashThread.start(); 
        }
        else 
        {
        	Intent myLoadIntent = new Intent(this, MainActivity.class);
            startActivityForResult(myLoadIntent, 0);
        }
    }
    
    /* If there are any touch events, then notify the thread so it can end */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread)
            {
                mSplashThread.notifyAll();
            }
        }
        return true;
    }    
} 
