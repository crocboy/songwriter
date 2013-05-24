package com.ozone.songwriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends Activity 
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		Button back_to_home = (Button)findViewById(R.id.back_to_home);
		Button visit_site = (Button)findViewById(R.id.visit_site);
		Button send_email = (Button)findViewById(R.id.send_email);
		
		/* User pressed the Back to Home button */
		back_to_home.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});
		
		/* The user wants to visit my website! */
		visit_site.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("https://sites.google.com/site/ozoneforandroid/") );
				startActivity( browse );
			}
		});
		
		/* The user wants to send me an email! */
		send_email.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "ozoneforandroid@gmail.com"});
				HelpActivity.this.startActivity(emailIntent);
			}
		});
	}
	
	
	public void onPause() 
	{
		super.onPause();
		this.finish();
	}

}
