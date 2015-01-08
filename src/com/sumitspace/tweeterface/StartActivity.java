package com.sumitspace.tweeterface;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class StartActivity extends Activity {
	SharedPreferences prefs;
	ImageView facebook, twitter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		prefs = getSharedPreferences("TweetfacePrefs", 0);
		String openPref = prefs.getString("startup", "");
		if(openPref != ""){
			if(openPref.equals("facebook")){
				startActivity(new Intent(this,FacebookActivity.class));
				finish();
			}
			if(openPref.equals("twitter")){
				startActivity(new Intent(this,MainActivity.class));
				finish();
			}
		}
		facebook = (ImageView) findViewById(R.id.facebooklogo);
		twitter = (ImageView) findViewById(R.id.twitterlogo);
		
		facebook.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(StartActivity.this,FacebookActivity.class));
				finish();	
			}
		});
		
		twitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(StartActivity.this,MainActivity.class));
				finish();
			}
		});
	}

}
