package com.sumitspace.tweeterface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioButton;

public class Settings extends Activity{
	SharedPreferences prefs;
	RadioButton ch1, ch2, ch3;
	Editor edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		ch1 = (RadioButton) findViewById(R.id.radioButton1);
		ch2 = (RadioButton) findViewById(R.id.radioButton2);
		ch3 = (RadioButton) findViewById(R.id.radioButton3);
		prefs = getSharedPreferences("TweetfacePrefs", 0);
		String choice = prefs.getString("startup", "");
		System.out.println("Preferneces stored: "+choice);
		if(choice.equals("facebook")){
			ch1.setChecked(true);
			ch2.setChecked(false);
			ch3.setChecked(false);
		}
		if(choice.equals("twitter")){
			ch1.setChecked(false);
			ch2.setChecked(true);
			ch3.setChecked(false);
		}
		if(choice.equals("open")){
			ch1.setChecked(false);
			ch2.setChecked(false);
			ch3.setChecked(true);
		}
		if(choice.equals("")){
			ch1.setActivated(false);
			ch2.setActivated(false);
			ch3.setActivated(false);
		}
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		edit = prefs.edit();
		if(ch1.isChecked()){
			edit.putString("startup","facebook");
		}
		if(ch2.isChecked()){
			edit.putString("startup","twitter");
		}
		if(ch3.isChecked()){
			edit.putString("startup","open");
		}
		edit.commit();
	}
}
