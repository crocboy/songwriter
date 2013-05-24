package com.ozone.songwriter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* This class is a wrapper around an SQL database */
public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) 
	{
		super(context, "SongWriter", null, 1);
	}

	/* Create our table */
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL("CREATE TABLE IF NOT EXISTS names ("
				+ "_id"
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, c1 VARCHAR, c2 VARCHAR, c3 VARCHAR, c4 VARCHAR)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// Steps to upgrade the database for the new version ...
	}
}