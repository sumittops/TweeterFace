package com.sumitspace.tweeterface;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	SharedPreferences prefs;
	Button login, update;
	Twitter twitter;
	RequestToken rToken;
	ListView timeline;
	TextView user, loading;
	ConnectivityManager connMgr;
	boolean preLogin;
	public static boolean firstLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preLogin = true;
		firstLoad = true;
		prefs = getSharedPreferences("TweetfacePrefs", 0);
		login = (Button) findViewById(R.id.button1);
		update = (Button) findViewById(R.id.newstatus);
		loading = (TextView) findViewById(R.id.loading);
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(AppData.TWITTER_CONSUMER_KEY,
				AppData.TWITTER_CONSUMER_SECRET);
		user = (TextView) findViewById(R.id.username);
		user.setText("Your home timeline:");
		timeline = (ListView) findViewById(R.id.timeline);
		connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new LoginToTwitter().execute();
			}
		});
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,NewTweet.class));
				
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			if (isTwitterLoggedInAlready()) {
				if (firstLoad) {
					
					new DirectGetTimeline().execute();
				}
			} else {
				if (preLogin)
					loginToTwitter();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"No internet connection found", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Uri uri = intent.getData();

		if (uri != null) {
			String verifier = uri
					.getQueryParameter(AppData.URL_TWITTER_OAUTH_VERIFIER);
			new GetTwitterTimeline().execute(verifier);
		}
	}

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return prefs.getBoolean(AppData.PREF_KEY_TWITTER_LOGIN, false);
	}

	private void loginToTwitter() {
		login.setVisibility(View.VISIBLE);
	}

	class DirectGetTimeline extends AsyncTask<String, String, String> {
		List<twitter4j.Status> statuses;
		Tweet tweets[];
		boolean successful = false;
		String userName;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			login.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String accessToken = prefs.getString(AppData.PREF_KEY_OAUTH_TOKEN,
					"");
			String accessTokenSecret = prefs.getString(
					AppData.PREF_KEY_OAUTH_SECRET, "");
			if (accessToken != "") {
				AccessToken access_token = new AccessToken(accessToken,
						accessTokenSecret);
				Twitter dTwitter = new TwitterFactory().getInstance();
				dTwitter.setOAuthConsumer(AppData.TWITTER_CONSUMER_KEY,
						AppData.TWITTER_CONSUMER_SECRET);
				dTwitter.setOAuthAccessToken(access_token);
				try {
					statuses = dTwitter.getHomeTimeline();
					successful = true;
				} catch (TwitterException te) {
					Log.d("DirectGetTimeline",
							"Something went wrong(Timeline Exception) ->"
									+ te.getMessage());
					te.printStackTrace();
				} catch (IllegalStateException te) {
					Log.d("DirectGetTimeline",
							"Can't connect  ->" + te.getMessage());
					te.printStackTrace();
					
				}catch(Exception e){
					Log.d("DirectGetTimeline",
							"Can't connect  ->" + e.getMessage());
					e.printStackTrace();
				}
				
			}
			firstLoad = false;
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(successful){
			tweets= new Tweet[statuses.size()];
			int i = 0;
			for (twitter4j.Status s : statuses) {
				tweets[i] = new Tweet(s.getUser().getName(),"@"+s.getUser().getScreenName(),s.getText());
				i++;
			}
			
			login.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			user.setVisibility(View.VISIBLE);
			timeline.setVisibility(View.VISIBLE);
			update.setVisibility(View.VISIBLE);
			TweetAdapter ta = new TweetAdapter(MainActivity.this,R.layout.tweetlist,tweets);
			timeline.setAdapter(ta);
		}else{
			Toast.makeText(MainActivity.this, "Can't connect to Twitter right now",Toast.LENGTH_LONG).show();
			finish();
		}

		}

	}

	class LoginToTwitter extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d("LoginToTwitter", "Started LoginToTwitter");
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Getting ready to log you in");
			pDialog.setCancelable(false);
			pDialog.show();
		
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				rToken = twitter
						.getOAuthRequestToken(AppData.TWITTER_CALLBACK_URL);
				Log.d("RequestTokenObtained", "" + rToken);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rToken
						.getAuthenticationURL())));
				preLogin = false;
			} catch (TwitterException e) {
				Log.d("LoginToTwitter", "Failed to obtain request token");
				e.printStackTrace();
			} catch (IllegalStateException e) {
				if (!twitter.getAuthorization().isEnabled()) {
					Log.d("IllegalStateException",
							"OAuth consumer key/secret is not set.");
					finish();
				}
			} catch (Exception e) {
				Log.e("LoginToTwitter", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
		}

	}

	class GetTwitterTimeline extends AsyncTask<String, String, String> {
		boolean successful = false;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			login.setVisibility(View.GONE);
			timeline.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			Twitter tt = new TwitterFactory().getInstance();
			tt.setOAuthConsumer(AppData.TWITTER_CONSUMER_KEY,
					AppData.TWITTER_CONSUMER_SECRET);
			try {
				AccessToken aToken = tt.getOAuthAccessToken(rToken, params[0]);
				Editor e = prefs.edit();
				e.putString(AppData.PREF_KEY_OAUTH_TOKEN, aToken.getToken());
				e.putString(AppData.PREF_KEY_OAUTH_SECRET,
						aToken.getTokenSecret());
				e.putBoolean(AppData.PREF_KEY_TWITTER_LOGIN, true);
				e.putString("username", tt.getScreenName());
				e.commit();
				successful = true;
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(successful){
			new DirectGetTimeline().execute();
			}else{
				Toast.makeText(getApplicationContext(), "Can't connect to twitter now. Try again later.", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.facebooklink){
			Intent i = new Intent(MainActivity.this,FacebookActivity.class);
			startActivity(i);
			finish();
		}
		if(item.getItemId() == R.id.logouttwitter){
			Editor edit = prefs.edit();
			edit.remove(AppData.PREF_KEY_OAUTH_SECRET);
			edit.remove(AppData.PREF_KEY_OAUTH_TOKEN);
			edit.remove(AppData.PREF_KEY_TWITTER_LOGIN);
			edit.commit();
			startActivity(new Intent(this, StartActivity.class));
			finish();
			finish();
		}
		if(item.getItemId() == R.id.action_settings){
			Intent i = new Intent(MainActivity.this,Settings.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}
