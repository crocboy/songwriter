package com.ozone.songwriter;

import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/* The main activity of the app */
/* MOST users will be here all of the time */
public class MainActivity extends Activity 
{
	/* General global variables */
	public int viewID;
	public int tempo = 100;
	boolean isGuitar=false;
	boolean isMinor=false;
	boolean isPlaying = false;
	public Circle.Key currentKey = new Circle.Key(Circle.A, false);
	boolean isLoop=false;
	PlayTask playThread = null;
	
	/* SoundPool objects */
	SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	HashMap<String, Integer> soundMap = new HashMap<String, Integer>();
	AudioManager audioManager;
	
	/* Static constants */
	public static final int TEXTVIEW_SELECTED_SIZE = 40;
	public static final int TEXTVIEW_UNSELECTED_SIZE = 35;
	public static final int MIN_BPM = 40;
	public static final int START_BPM = 80;
	public static final int MAX_BPM = 160;
	
	public static final String[] KeyArray = {"A","B","C","D","E","F","G"};
    public static final String[] ModeArray = {"Major","Minor"};
	
	 
	/* Global TextViews */
	TextView chordOne;
	TextView chordTwo;
	TextView chordThree;
	TextView chordFour;
	TextView statusView;
	TextView tempoView;
	
	/* Other global views */
	SeekBar tempoBar;
	Button playButton;
	Button createButton;
	Button settingsButton;
	CheckBox isLoopBox;
	RadioButton pianoBox;
	RadioButton guitarBox;
	Spinner keySpinner;
	Spinner modeSpinner;
	
	/* Hold our view in an array for ease of use */
	public TextView[] allTextViews;
	
	/** Array for all affectedViews **/
	public View[] affectedViews;
	
	
    /** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle bundle) 
    {
    	try 
    	{
    		/* Classic two lines */
    		super.onCreate(bundle);
    		setContentView(R.layout.main);
    		
	    	final Random random = new Random();
    	
	    	/* Do the Changelog business */
	    	ChangeLog cl = new ChangeLog(this);
	        if (cl.firstRun()) {  cl.getLogDialog().show();  }
	        
	        Circle.Key key = new Circle.Key(Circle.C, false);
	        
	        /* Load sounds and instantiate AudioManager */
	        loadSounds();
	        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	        
	        /* This line causes the hardware controls to adjust the media volume and not the ringer volume */
	        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
	        /* Pull views from XML file */
	        chordOne = (TextView)findViewById(R.id.randchord1);
		    chordTwo = (TextView)findViewById(R.id.randchord2);
			chordThree = (TextView)findViewById(R.id.randchord3);
			chordFour = (TextView)findViewById(R.id.randchord4);
			
			isLoopBox = (CheckBox) findViewById(R.id.loop_box);
			  
			statusView = (TextView)findViewById(R.id.textview);
			tempoView = (TextView)findViewById(R.id.tempoView);
			
			keySpinner = (Spinner) findViewById( R.id.choose_key);
		    modeSpinner = (Spinner) findViewById( R.id.choose_major);
		    
		    playButton = (Button)findViewById(R.id.play_button);
	        createButton = (Button)findViewById(R.id.create_button);
	        settingsButton = (Button) findViewById(R.id.settings_button);
			
			/* SeekBars have a min value of zero, so we need to use basic math to set the min as a non-zero value */ 
			tempoBar = (SeekBar)findViewById(R.id.tempoBar); 
			tempoBar.setMax(MAX_BPM - MIN_BPM);
			tempoBar.setProgress(START_BPM - MIN_BPM);
			  
			/* Initialize the TextViews */
			chordOne.setText("A");
			chordTwo.setText("B");
			chordThree.setText("C");
			chordFour.setText("D");
			
			allTextViews = new TextView[] {chordOne, chordTwo, chordThree, chordFour}; //Refresh our global array of TextViews
		  
		    /* Check to see if another activity pre-started us with chords already selected! */
		    Bundle b = getIntent().getExtras();
		  
		    if(b != null)
		    {
			    String data = b.getString("data");

			    if(data != null) 
			    {
				    String[] chords = data.split(",");
				    chordOne.setText(chords[1]);
				    chordTwo.setText(chords[2]);
				    chordThree.setText(chords[3]);
				    chordFour.setText(chords[4]);
			        statusView.setText(chords[0] + ":   ");
				    Toast.makeText(getApplicationContext(), chords[0] + " loaded!", Toast.LENGTH_SHORT).show();
			    }
		    } 
		    
		    
			guitarBox = (RadioButton)findViewById(R.id.guitar_box);
			pianoBox = (RadioButton)findViewById(R.id.piano_box);
		    isLoopBox = (CheckBox)findViewById(R.id.loop_box);
		    
	      	isGuitar= guitarBox.isChecked();
	      	pianoBox.setChecked(true);
	      	
	      	affectedViews = new View[] { keySpinner, modeSpinner, createButton, settingsButton, isLoopBox, tempoBar, pianoBox, guitarBox};
	        
	        /* Set RadioButton listeners */
	        pianoBox.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
				public void onCheckedChanged(CompoundButton paramCompoundButton, boolean isChecked) 
				{
					isGuitar = !isChecked;
				}
	        });
	        
	        guitarBox.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
				public void onCheckedChanged(CompoundButton paramCompoundButton, boolean isChecked) 
				{
					isGuitar = isChecked;
				}
	        });
	        
	        /* Called when the SeekBar value changes */
	        tempoBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
	        {
	        	public void onStopTrackingTouch(SeekBar arg0) {}
	        	public void onStartTrackingTouch(SeekBar arg0) {}
			
	        	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) 
	        	{
					//setTempoView(getTempo());
					tempo = getTempo();
	        	}
	        });

	        
	        /* Open the Options menu when the button is pressed */
	        Button settingsButton = (Button)findViewById(R.id.settings_button);
	        settingsButton.setOnClickListener(new View.OnClickListener() 
	        {
	        	public void onClick(View v)
	        	{
	        		openOptionsMenu();
	        	}
	        });
		  
	        /* Set up spinners */
		    ArrayAdapter<String> keyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KeyArray);
		    ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ModeArray);
		    keyAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		    modeAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

		    keySpinner.setAdapter(keyAdapter); 
		    modeSpinner.setAdapter(modeAdapter);
		    
		    /* Register the TextView's for long-press menus */
		    registerForContextMenu(chordTwo); 
		    registerForContextMenu(chordThree);
		    registerForContextMenu(chordFour);
		    
		    /* A global click listener that can be applied to all four TextViews */
		    View.OnClickListener chordViewListener = new View.OnClickListener() {
				
				public void onClick(View arg0) {
					
					try 
					{
						TextView chordView = (TextView) arg0;
						
						/* Get the Texview's contents, convert it to a resource name and play the related sound */
						playSound(chordView.getText().toString());
					} 
					catch(Exception e) 
					{
						Log.e("ERROR","ChordView onClick error: " + String.valueOf(e));
						Toast.makeText(getApplicationContext(), "Sorry,  an error occured.  The chord cannot be played.", Toast.LENGTH_SHORT).show();
					}
				}
			};
		   
		    /* Apply this global listener to our four TextView's */
		    chordOne.setOnClickListener(chordViewListener);
		    chordTwo.setOnClickListener(chordViewListener);
		    chordThree.setOnClickListener(chordViewListener);
		    chordFour.setOnClickListener(chordViewListener);
		    
		    /* The listener for the key spinner */
		    keySpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		    {
		        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		        {
		        	currentKey = Circle.AllKeys[modeSpinner.getSelectedItemPosition()][keySpinner.getSelectedItemPosition()];
		        }
		        
				public void onNothingSelected(AdapterView<?> arg0) { } // A useless method 

		    });

		    /* The listener for the major/minor spinner */
		    modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		    {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			    {
			    	if(position == 0) 
			    		isMinor=false;
			    	else if(position == 1)
			    		isMinor=true;
			    	
			    	currentKey = Circle.AllKeys[modeSpinner.getSelectedItemPosition()][keySpinner.getSelectedItemPosition()];
			    }
			
				public void onNothingSelected(AdapterView<?> arg0) {}  //Useless! 
				
		    });
		    
		  
		    /* Method is called when "Create" button is pressed */
		    /* Essentially, we want to generate a new chord sequence, then update the UI */
		    createButton.setOnClickListener(new View.OnClickListener() 
		    {
		    	public void onClick(View arg0) 
		    	{
		    		statusView.setText("");
		    		int chord_2;
		    		int chord_3;
		    		int chord_4;
		    		String sound_1;
		    		String sound_2;
		    		String sound_3;
		    		String sound_4;
		    		String[] chords = null;
		    		
		    		Circle.Key key = Circle.AllKeys[modeSpinner.getSelectedItemPosition()][keySpinner.getSelectedItemPosition()];

		    		chords = key.Chords; //allChords[currentKey];
				
		    		chord_2 = random.nextInt(chords.length);
		    		while(chord_2 == 0) 
		    		{
		    			chord_2 = random.nextInt(chords.length);
		    		}
		    		
		    		chord_3 = random.nextInt(chords.length);
		    		while(chord_3 == chord_2 ) 
		    		{
		    			chord_3 = random.nextInt(chords.length);
		    		}
		    		
		    		chord_4 = random.nextInt(chords.length);
		    		while(chord_4 == chord_3 || chord_4 == chord_2 || chord_4 == 0) 
		    		{
		    			chord_4 = random.nextInt(chords.length);
		    		}
		    		
		    		sound_1= chords[0];
		    		sound_2= chords[chord_2];
		    		sound_3= chords[chord_3];
		    		sound_4= chords[chord_4];
				  
		    		setChords(sound_1,sound_2,sound_3,sound_4);
		    	}
		    }); 

		    /* Method is called when the "Play" button is pressed */
		    /* Essentially, we want to get the current chord sequence and then play the files, in order */
		    /* Additionally, we will read in the isLoop and tempo variables */
		    playButton.setOnClickListener(new View.OnClickListener() 
		    {
		    	public void onClick(View v) 
		    	{
	    		 	/* Refresh variables */ 
					isGuitar = guitarBox.isChecked();
					isLoop = isLoopBox.isChecked();
					
					/* Switch based on our current "purpose" of the button */
					String text = playButton.getText().toString();
					
					/* Button is in Play mode */
					if(!isPlaying) 
					{
						/* Initiate our object and kick 'er off! */
						if(playThread != null) 
						{
							playThread.cancel(true);
							playThread = null;
						}
						
						playThread = new PlayTask(tempo, getCurrentChordArray());
						playThread.execute();
						
						playButton.setText("Stop");
						disableViews();
						isPlaying = true;
					}
					
					/* Button is in Stop mode */
					else if(isPlaying) 
					{
						if(playThread != null) 
						{
							playThread.cancel(true);
						}
						
						playButton.setText("Play");
						clearViews();
						
						enableViews();
						isPlaying = false;
					}
		    	}
		    });
    	} 
    	
    	/* Main catch */
    	catch(Exception e) 
    	{
    		Log.e("ERRORS","onCreate ERROR: " + String.valueOf(e));
    	}
    	
    } /* End of the giant onCreate */
    
    
    /** Enter into Play mode **/
    public void disableViews()
    {
    	for(View v : affectedViews)
    		v.setEnabled(false);
    }
    
    /** Exit Play mode **/
    public void enableViews()
    {
    	for(View v : affectedViews)
    		v.setEnabled(true);
    }
    
    /* The Play task */
    public class PlayTask extends AsyncTask<Void, Integer, Void> 
    {
    	/* These values are used for publishing progress */
    	public static final int PLAY_START = 0;
    	public static final int PLAY_ONE = 1;
    	public static final int PLAY_TWO = 2;
    	public static final int PLAY_THREE = 3;
    	public static final int PLAY_FOUR = 4;
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	public static final int PLAY_END = 5;
    	
    	private int BPM = START_BPM;
    	private String[] chords = {""};
    	private int delay = 100;
    	
    	/** Public constructor **/
    	public PlayTask(int tempo, String[] currentChordArray) 
    	{
    		this.BPM = tempo;
    		this.chords = currentChordArray;
    		this.delay = Util.BpmToMillis(this.BPM);
		}

		/* The loop */
    	/* We need to often check if the task has been cancelled */
		@Override
		protected Void doInBackground(Void... args) 
		{
    		if(chords.length != 4) //Size check
    		{
				return null;
			}

			try 
			{
				while(!isCancelled()) 
				{
					//Play sounds with calculated delay 
					//For now, check for an "isCancelled" after every line 
					publishProgress(PLAY_START);
		    		playSound(chords[0]);
		    		if(isCancelled()) { return null; }
		    		Thread.sleep(delay);
		    		
		    		publishProgress(PLAY_ONE);
		    		if(isCancelled()) { return null; }
		    		playSound(chords[1]);
		    		if(isCancelled()) { return null; }
		    		Thread.sleep(delay);
		    		
		    		publishProgress(PLAY_TWO);
		    		if(isCancelled()) { return null; }
		    		playSound(chords[2]);
		    		if(isCancelled()) { return null; }
		    		Thread.sleep(delay);
		    		
		    		publishProgress(PLAY_THREE);
		    		if(isCancelled()) { return null; }
		    		playSound(chords[3]);
		    		if(isCancelled()) { return null; }
		    		
		    		Thread.sleep(delay);
		    		publishProgress(PLAY_FOUR);
		    		
		    		//If we're not supposed to loop, cancel 
		    		if(!isLoop) 
		    		{
		    			publishProgress(PLAY_END); //Send the ending signal
		    			cancel(true);
		    		}
	    		
				}
    		
    		} catch(Exception e) {
    			Log.e("ERROR", "PlayThread Error: " + String.valueOf(e));
    		}
			
			publishProgress(5); //Send the ending signal
			return null;
		}
		
		
		/* Update the appropriate TextView */
		/* The following are publish codes: 0-3 are textViews to update, 4 signals the last chord, 5 is final exit */
		@Override
		protected void onProgressUpdate(Integer... params) 
		{
			int val = params[0].intValue();
			
			/* Go through cases and bold/unbold TextViews */
			if(val == 0) 
			{
				//enterPlay();
				highlightText(allTextViews[val]);
			}
			
			else if(val == 4) 
			{
				unhighlightText(chordFour);
			}
			
			/* 5 is exit code, also catch all other invalid codes */
			else if(val == PLAY_END || val < PLAY_START || val > PLAY_END) 
			{
				isPlaying = false;
				clearViews();
				enableViews();
				playButton.setText("Play");
			}
			
			else 
			{
				highlightText(allTextViews[val]);
				unhighlightText(allTextViews[val-1]);
			}
		}
    }
    
    
    /* Open up the options menu */
    public boolean onCreateOptionsMenu(Menu menu) 
    {
  		super.onCreateOptionsMenu(menu);
  		MenuInflater mi = getMenuInflater();
  		mi.inflate(R.menu.list_menu, menu);
  		return true;
    }
    
    
    
    /* Handle Options menu selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.gen_settings_item:
        		/* Start the settings activity */
        		Intent mySettingsIntent = new Intent(this, GeneralSettings.class);
                startActivityForResult(mySettingsIntent, 0);
                break;
        	
            case R.id.help_item:
            	/* Start the "Help" activity */
            	Intent myHelpIntent = new Intent(this, HelpActivity.class);
                startActivityForResult(myHelpIntent, 0);
                break;
        
            case R.id.load_item:
	        	/* Start the "LoadChords" activity */
	        	Intent myLoadIntent = new Intent(this, LoadChords.class);
	            startActivityForResult(myLoadIntent, 0);
	        	break; 
        	
            case R.id.save_item:
        	
        	try 
        	{
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		AlertDialog saveDialog;
        		
        		/* Get the layout inflater */
        		
        		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        		final View layout = inflater.inflate(R.layout.save_dialog_layout,null);
        		
        		builder.setView(layout);
        		
        		/* Save the current text into a database */
        		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() 
        		{
        			  public void onClick(DialogInterface arg0, int i) 
        			  {
        		    		try 
        		    		{
        		    			final EditText nameBox = (EditText)layout.findViewById(R.id.chords_name_box);
        		    			String title  = nameBox.getText().toString();
        		    			
        		    			 if(title.length() == 0) 
        		    			 {
        		    				 Toast.makeText( getApplicationContext(),"No Named Entered!",Toast.LENGTH_SHORT).show();
        		    			 }
        		    			 else 
        		    			 {
        		    				 saveChords(title);
        		    			 }
        		    		
        					} 
        		    		
        		    		catch (Exception e) 
        		    		{
        						Log.e("ERROR","Save error: " + String.valueOf(e));
        						Toast.makeText( getApplicationContext(),"An error occured.  Could not save.",Toast.LENGTH_SHORT).show();
        					} 
        			   }
        		});
        		
        		/* Add the "Cancel" button */
        		builder.setNegativeButton("Cancel",null);
        		
        		/* Put on the finishing details, and show it! */
        		builder.setTitle("Save Chords");
        		saveDialog = builder.create();
        		saveDialog.show();
        	} 
        	
        	catch (Exception e) 
        	{
        		Log.e("ERROR", "Save error: " + String.valueOf(e));
        		Toast.makeText(this, "Error saving.", Toast.LENGTH_SHORT).show();
        	}
        	
            	break;
        }
        	
		return true;  
    }
    
    /* Called when a long press is performed on a TextView */
    /* We want to bring up a context menu, allowing the user to change individual chords */
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
    {  
	    /* Get the current key's chords; add them to the popup menu */
		TextView textView = (TextView)v;
		viewID = textView.getId();
		String[] chords= currentKey.Chords;
		  	  
		for(String s : chords) 
		{
		  menu.add(s);
		}
		  
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_chord, menu);  
     }
    
    
	     
    /* Called when one of the chords is selected */
    /* Simply swap out the current chord with the new, selected one */
    public boolean onContextItemSelected(MenuItem item) 
    {
    	if(viewID == chordOne.getId()) {
    		chordOne.setText(String.valueOf(item));
		}
    	if(viewID == chordTwo.getId()) {
    		chordTwo.setText(String.valueOf(item));
		}
    	if(viewID == chordThree.getId()) {
    		chordThree.setText(String.valueOf(item));
		}
    	if(viewID == chordFour.getId()) {
    		chordFour.setText(String.valueOf(item));
		}
		  
    	return super.onContextItemSelected(item);
    }
    
    
    
    /* Here begins the activity-specific functions like saving chords, etc */
    
    
    /** Sets the content of the tempo text view **/
    public void setTempoView(int tempo) 
    {
    	if(tempoView != null) 
    	{
    		tempoView.setText("Tempo: " + String.valueOf(tempo));
    	}
    }
    
    /** Retrieves the value of the tempo slider **/
    public int getTempo() 
    {
    	return tempoBar.getProgress() + MIN_BPM; //SeekBars have a min value of zero, so we need basic math to get the values
    }
    
    
    /** Plays the audio file of a given name ( A , Bm , C#m) **/
    public void playSound(String name) 
    {
    	name = Util.toResource(name, isGuitar);
    	
        /*float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;  */
 
        soundPool.play(soundMap.get(name), 1, 1, 1, 0, 1);
        
    	/*int id = getResources().getIdentifier("com.ozone.songwriter:raw/" + name,null,null);
    	final MediaPlayer mediaPlayer = MediaPlayer.create(this,id);
    	mediaPlayer.start();
    	
    	mediaPlayer.setOnCompletionListener(new OnCompletionListener() 
    	{
			public void onCompletion(MediaPlayer mp) 
			{
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		});*/
    }
    
    
    /** Returns the current chords as a CSV string */
    public String getCurrentChordString() 
    {
    	/* Return the total of all four TextView's */
    	return chordOne.getText().toString().trim() + "," + chordTwo.getText().toString().trim() + "," + chordThree.getText().toString().trim() + "," + chordFour.getText().toString().trim();
    }
    
    /** Returns the current chords as a String array */
    public String[] getCurrentChordArray() 
    {
    	/* Return the total of all four TextView's */
    	String[] chords = {chordOne.getText().toString().trim(),chordTwo.getText().toString().trim(),chordThree.getText().toString().trim(),chordFour.getText().toString().trim()};
    	return chords;
    }
    
    
    
    /** Sets the chords, given a CSV chord string */
    public void setChords(String chordString) 
    {
    	/* Provide a way out if it just so happens that the split doesn't work out */
    	String[] defaultChords = {"A","B","C","D"};
    	String[] newChords = chordString.split(",");
    	
    	if(newChords.length != 4) 
    	{
    		newChords = defaultChords;
    	}
    	
    	/* Apply the changes to the TextView's */
    	chordOne.setText(newChords[0].trim());
    	chordTwo.setText(newChords[1].trim());
    	chordThree.setText(newChords[2].trim());
    	chordFour.setText(newChords[3].trim());
    }
    
    /** Another way to set the chords */
    public void setChords(String... newChords) 
    {
    	if(newChords.length == 4) 
    	{
    		setChords(newChords[0] + "," + newChords[1] + "," + newChords[2] + "," + newChords[3]);
    	}
    }
    
    
    
    /** Saves the given CSV string of chords into the database */
    public void saveChords(String title) 
    {
    	/* Get a a new DatabaseHelper/ Database with name "names" */
    	DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		
		/* Put all of the values into a ContentValues object */
		ContentValues values = new ContentValues();
		values.put("title", title);
		String[] chords = getCurrentChordArray();
		values.put("c1", chords[0]);
		values.put("c2",chords[1]);
		values.put("c3",  chords[2]);
		values.put("c4",  chords[3]);
			
		/* Save it into the table */
		db.insert("names",null, values);
		
		/* Indicate success and close the DB */
		Toast.makeText( getApplicationContext(),"Chords saved as " + title, Toast.LENGTH_LONG).show();
		db.close();
    }
    
    /** Make a TextView big & bold */
    public void highlightText(TextView t) 
    {
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextSize(TEXTVIEW_SELECTED_SIZE);
    }
    
    /** Shrink a TextView down to it's original size */
    public void unhighlightText(TextView t) 
    {
    	t.setTypeface(Typeface.DEFAULT);
		t.setTextSize(TEXTVIEW_UNSELECTED_SIZE);
    }
    
    
    /** Unhighlight all TextViews **/
    public void clearViews() 
    {
    	unhighlightText(chordOne);
    	unhighlightText(chordTwo);
    	unhighlightText(chordThree);
    	unhighlightText(chordFour);
    }
    
    
    /** Load all resources into our SoundPool object **/
    public void loadSounds()
    {
    	/* Put all sounds and ID's into the HashMap */
    	try
    	{
    		/* Open a ProgessDialog */
    		/*ProgressDialog progress;
    		progress = ProgressDialog.show(this, "Songwriter", "Loading sounds...", true);*/
    		
    		/* Load sounds */
    		soundMap.put("a", soundPool.load(this, R.raw.a, 1));
    		soundMap.put("a7th", soundPool.load(this, R.raw.a7th, 1));
    		soundMap.put("a7thgut", soundPool.load(this, R.raw.a7thgut, 1));
    		soundMap.put("agut", soundPool.load(this, R.raw.agut, 1));
    		soundMap.put("am", soundPool.load(this, R.raw.am, 1));
    		soundMap.put("amgut", soundPool.load(this, R.raw.amgut, 1));
    		soundMap.put("b", soundPool.load(this, R.raw.b, 1));
    		soundMap.put("b7th", soundPool.load(this, R.raw.b7th, 1));
    		soundMap.put("b7thgut", soundPool.load(this, R.raw.b7thgut, 1));
    		soundMap.put("bb", soundPool.load(this, R.raw.bb, 1));
    		soundMap.put("bbgut", soundPool.load(this, R.raw.bbgut, 1));
    		soundMap.put("bgut", soundPool.load(this, R.raw.bgut, 1));
    		soundMap.put("bm", soundPool.load(this, R.raw.bm, 1));
    		soundMap.put("bmgut", soundPool.load(this, R.raw.bmgut, 1));
    		soundMap.put("c", soundPool.load(this, R.raw.c, 1));
    		soundMap.put("c7th", soundPool.load(this, R.raw.c7th, 1));
    		soundMap.put("c7thgut", soundPool.load(this, R.raw.c7thgut, 1));
    		soundMap.put("cgut", soundPool.load(this, R.raw.cgut, 1));
    		soundMap.put("csm", soundPool.load(this, R.raw.csm, 1));
    		soundMap.put("csmgut", soundPool.load(this, R.raw.csmgut, 1));
    		soundMap.put("d", soundPool.load(this, R.raw.d, 1));
    		soundMap.put("d7th", soundPool.load(this, R.raw.d7thgut, 1));
    		soundMap.put("d7thgut", soundPool.load(this, R.raw.d7thgut, 1));
    		soundMap.put("dgut", soundPool.load(this, R.raw.dgut, 1));
    		soundMap.put("dm", soundPool.load(this, R.raw.dm, 1));
    		soundMap.put("dmgut", soundPool.load(this, R.raw.dmgut, 1));
    		soundMap.put("dsm", soundPool.load(this, R.raw.dsm, 1));
    		soundMap.put("dsmgut", soundPool.load(this, R.raw.dsmgut, 1));
    		soundMap.put("e", soundPool.load(this, R.raw.e, 1));
    		soundMap.put("e7th", soundPool.load(this, R.raw.e7th, 1));
    		soundMap.put("e7thgut", soundPool.load(this, R.raw.e7thgut, 1));
    		soundMap.put("egut", soundPool.load(this, R.raw.egut, 1));
    		soundMap.put("em", soundPool.load(this, R.raw.em, 1));
    		soundMap.put("emgut", soundPool.load(this, R.raw.emgut, 1));
    		soundMap.put("f", soundPool.load(this, R.raw.f, 1));
    		soundMap.put("fgut", soundPool.load(this, R.raw.fgut, 1));
    		soundMap.put("fs", soundPool.load(this, R.raw.fs, 1));
    		soundMap.put("f7th", soundPool.load(this, R.raw.fs7th, 1));
    		soundMap.put("f7thgut", soundPool.load(this, R.raw.fs7thgut, 1));
    		soundMap.put("fsgut", soundPool.load(this, R.raw.fsgut, 1));
    		soundMap.put("fsm", soundPool.load(this, R.raw.fsm, 1));
    		soundMap.put("fsmgut", soundPool.load(this, R.raw.fsmgut, 1));
    		soundMap.put("g", soundPool.load(this, R.raw.g, 1));
    		soundMap.put("g7th", soundPool.load(this, R.raw.g7th, 1));
    		soundMap.put("g7thgut", soundPool.load(this, R.raw.g7thgut, 1));
    		soundMap.put("ggut", soundPool.load(this, R.raw.ggut, 1));
    		soundMap.put("gm", soundPool.load(this, R.raw.gm, 1));
    		soundMap.put("gmgut", soundPool.load(this, R.raw.gmgut, 1));
    		soundMap.put("gsm", soundPool.load(this, R.raw.gsm, 1));
    		soundMap.put("gsmgut", soundPool.load(this, R.raw.gsmgut, 1));
    		
    		/* Close our dialog */
    		//progress.dismiss();
    	}
    	catch(Exception e)
    	{
    		Log.e("ERROR","loadSounds() error: " + String.valueOf(e));
    	}
    }
}



/*  Current Issues */

/* When changing individual chord via long-press, the following error occurs:
 * 06-06 20:25:37.741: E/ViewRootImpl(11937): sendUserActionEvent() mView == null
 * 
 * Error also happens when LoadChords is opened and a save is long pressed
*/


/* ----- TO-DO ----- */

/* 1.  Restrict some features while chords are playing */
/* 2.  Fade-out all GUT chords */






