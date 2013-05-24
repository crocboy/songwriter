package com.ozone.songwriter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/* This activity starts when the user wants to load some saved chords */
/* It will open a database */
/* It will then start MainActivity with a bundle, and MainActivity will open with these chords */
public class LoadChords extends ListActivity 
{
	SQLiteDatabase db;
	ArrayAdapter<String> arrayAdapter;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		try 
		{
			/* Classic two-liner */
			super.onCreate(savedInstanceState);
			setContentView(R.layout.load_file);
			 
			/* Get some database objects */
			DatabaseHelper helper = new DatabaseHelper(this);
			db = helper.getReadableDatabase();
			
			/* Query the database, and populate the ListView */
			final ArrayList<String> loadedData = new ArrayList<String>();
			String[] fields = new String[] {"_id","title","c1","c2","c3","c4"};
			final Cursor data = db.query("names",fields, null, null, null, null, null);
			data.moveToFirst();
			
			for(int i = 0; i < data.getCount(); i++) 
			{
				loadedData.add(data.getString(1) + ":          " + data.getString(2) + ","+ data.getString(3) +","+ data.getString(4) + "," +data.getString(5));
				data.moveToNext();
			}

			final ListView chordsListView = getListView();
			arrayAdapter = new ArrayAdapter<String>(this, R.layout.row, loadedData);
			chordsListView.setAdapter(arrayAdapter);
			registerForContextMenu(chordsListView);
			
			
			/* Called when an item is pressed */
			chordsListView.setOnItemClickListener(new OnItemClickListener() 
			{
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) 
				{
					/* Split the data in the item, and start a new activity */
					String chordsName = (String) ((TextView) paramView).getText();
					String c1,c2,c3,c4;
				    String[] second_split = chordsName.split(":");
				    String title = second_split[0];
				    String second_str = second_split[1];
				    String []split = second_str.split(",");
				      
				    c1 = split[0].trim();
				    c2 = split[1].trim();
				    c3 = split[2].trim();
				    c4 = split[3].trim();
				      
				      
				    /* Package the data up into an intent, and send 'er off! */
				    Intent myIntent = new Intent(LoadChords.this, MainActivity.class);
				    Bundle bundle = new Bundle();
				    bundle.putString("data", title + "," + c1 + "," + c2 + "," + c3 + "," + c4);
				    myIntent.putExtras(bundle);
		            startActivityForResult(myIntent, 0);
				}
			});
			
			
			/* Called when an item is long pressed (We want to delete it */
			chordsListView.setOnItemLongClickListener(new OnItemLongClickListener() 
			{
				public boolean onItemLongClick(AdapterView<?> paramAdapterView,final View paramView, int paramInt, long paramLong) 
				{
					/* Get a dialog/builder and set it all up */
					AlertDialog.Builder builder = new AlertDialog.Builder(LoadChords.this);
	        		final AlertDialog alertDialog;
	        		
	        		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	        		{
	      			  	public void onClick(DialogInterface arg0, int arg1) 
	      			  	{
		      				/* Delete the item out of the database; refresh the ListView */
		      				String key = (String) ((TextView) paramView).getText();
		      				String[] split = key.split(":");
	      		    		db.delete("names","title" + "=?", new String[] {split[0] });
	      		    		ArrayList<String> loaded_data2 = new ArrayList<String>();
		      		  		String [] fields = new String[] {"_id","title","c1","c2","c3","c4"};
		      		  		Cursor data = db.query("names",fields, null, null, null, null, null);
		      		  		data.moveToFirst();
		      		  		
		      		  		for(int i = 0; i < data.getCount(); i++) 
		      		  		{
			      		  		loaded_data2.add(data.getString(1) + ":          " + data.getString(2) + ","+ data.getString(3) +","+ data.getString(4) + "," +data.getString(5));
			      		  		data.moveToNext();
		      		  		}
	      		  	
			      			chordsListView.setAdapter(refreshArrayAdapter());
			      			data.close();
	      			  	}});
	        		
	        		/* Add a "Cancel" options */
		      		builder.setNegativeButton("Cancel", null);
		      		
		      		/* Add a tile, message, and show it! */
		      		builder.setTitle("Delete Chords");
		      		builder.setMessage("Are you sure you want to delete these chords?");
		      		alertDialog = builder.create();
		      		alertDialog.show();
	      		
					return false;
				}
			});
		} catch (Exception e) {
			Log.e("ERROR","LoadChords onCreate error: " + String.valueOf(e));
		}
	}
	
	/* When the activity is destroyed, close the database */
	public void onDestroy() 
	{
		super.onDestroy();
		db.close();
		Log.e("VARS","Closing database...");
	}
	
	
	/* Return an updated array adapter */
	public ArrayAdapter<String> refreshArrayAdapter() 
	{
		ArrayList<String> loadedData = new ArrayList<String>();

		String [] fields = new String[] {"_id","title","c1","c2","c3","c4"};
		Cursor data = db.query("names",fields, null, null, null, null, null);
		data.moveToFirst();
		
		for(int i = 0; i < data.getCount(); i++) 
		{
		    loadedData.add(data.getString(1) + ":          " + data.getString(2) + ","+ data.getString(3) +","+ data.getString(4) + "," +data.getString(5));
			data.moveToNext();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row,loadedData);
		return adapter;
	}
}


