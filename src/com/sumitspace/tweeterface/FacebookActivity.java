package com.sumitspace.tweeterface;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookActivity extends Activity{
	TextView tv;
	ConnectivityManager connMgr;
	ListView newsFeed;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook);
		tv = (TextView) findViewById(R.id.welcome);
		connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		newsFeed = (ListView)findViewById(R.id.newsfeed);
		ArrayList<String> permissionList = new ArrayList<String>();
		permissionList.add("read_stream");
	//	permissionList.add("publish_actions"); Required to update to Facebook. But the app needs to be first verified by the Facebook team before allowing this permission to the, thus requires time 
		permissionList.add("user_birthday");
		permissionList.add("user_friends");
		
		if(netInfo!=null && netInfo.isAvailable())
		Session.openActiveSession(this, true,  permissionList, new Session.StatusCallback(){
			
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if(session.isOpened())	{
					
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if(user!=null){
								tv.setText("Welcome, "+user.getName()+"!");
							}
							
						}
					}).executeAsync();
					Bundle params = new Bundle();
					params.putString("type", "status");
					params.putString("fields","id,name,from,message");
					new Request(session, "/me/home", params, HttpMethod.GET, new Request.Callback() {
						
						@Override
						public void onCompleted(Response response) {
								Log.d("LOADCOMPLETE",response.toString());
								Post[] posts = null;
								JSONArray arr =null;
								try {
									JSONObject result = response.getGraphObject().getInnerJSONObject();
									arr = result.getJSONArray("data");
									posts = new Post[arr.length()];
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								JSONObject oneStatus;
								String message,name,source;
								message = null;
								source=null;
								if(posts!=null){
									
										for(int i =0;i<arr.length();i++){
										try{
											oneStatus = arr.getJSONObject(i);
											
											message = oneStatus.getString("message");
											source= oneStatus.getJSONObject("from").getString("name");
											name = oneStatus.getString("name");
											posts[i] = new Post(name,message,source);
											
										}catch(JSONException e){
											posts[i] = new Post("",message,source);
										}
									}
									PostAdapter adapter = new PostAdapter(FacebookActivity.this, R.layout.facebookstatus, posts);
									newsFeed.setAdapter(adapter);
								}
							
						}
					}).executeAsync();
				}
			}
			
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main2, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.twitterlink){
			Intent i = new Intent(FacebookActivity.this,MainActivity.class);
			startActivity(i);
			finish();
		}
		if(item.getItemId() == R.id.logoutfacebook){
			Session s = Session.getActiveSession();
			s.closeAndClearTokenInformation();
			startActivity(new Intent(FacebookActivity.this, StartActivity.class));
			finish();
		}
		if(item.getItemId() == R.id.action_settings){
			Intent i = new Intent(FacebookActivity.this,Settings.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}
