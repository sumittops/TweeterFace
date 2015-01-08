package com.sumitspace.tweeterface;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTweet extends Activity{
	Button update;
	EditText tweetEdit;
	int count;
	Thread counter;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtweet);
		update = (Button) findViewById(R.id.updatebutton);
		tweetEdit = (EditText) findViewById(R.id.editText1);
		prefs = getSharedPreferences("TweetfacePrefs", 0);
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				count = tweetEdit.getText().toString().length();
				if(count<=140){
					new UpdateToTwitter().execute();
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(NewTweet.this);
					builder.setTitle("Tweet limit!");
					builder.setMessage("Please write a tweet within 140 characters. It is "+tweetEdit.getText().length()+" characters now.");
					builder.setPositiveButton("OK", null);
					builder.show();
				}
				
			}
		});
	}
	class UpdateToTwitter extends AsyncTask<String, String, String>{
		ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(NewTweet.this);
			pDialog.setMessage("Updating to twitter");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(AppData.TWITTER_CONSUMER_KEY, AppData.TWITTER_CONSUMER_SECRET);
			AccessToken accessToken = new AccessToken(prefs.getString(AppData.PREF_KEY_OAUTH_TOKEN,""), prefs.getString(AppData.PREF_KEY_OAUTH_SECRET,""));
			twitter.setOAuthAccessToken(accessToken);
			twitter4j.Status response = null;
			try {
				response = twitter.updateStatus(tweetEdit.getText().toString().trim());
				Log.d("Tweet updated:>",""+response);
			
			} catch (TwitterException e) {
				Toast.makeText(NewTweet.this, "Unable to send tweet now", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return response.toString();
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d("Update", "Successful: "+result);
			pDialog.dismiss();
			MainActivity.firstLoad = true;
			Intent i = new Intent(NewTweet.this,MainActivity.class);
			startActivity(i);
			finish();
		}
		
	}
		
	
}
