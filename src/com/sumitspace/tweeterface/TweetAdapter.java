package com.sumitspace.tweeterface;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweet>{
	Activity context;
	int layoutResourceId;
	Tweet[] data = null;
	public TweetAdapter(Activity context, int resource, Tweet[] objects) {
		super(context, resource, objects);
		this.context = context;
		layoutResourceId = resource;
		data = objects;
	}
	static class TweetHolder{
		TextView nameLabel, screenLabel, textLabel;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TweetHolder holder = null;
		if(row== null){
			LayoutInflater inflater =context.getLayoutInflater();
			row = inflater.inflate(layoutResourceId,parent,false);
			holder = new TweetHolder();
			holder.nameLabel = (TextView) row.findViewById(R.id.name);
			holder.screenLabel = (TextView) row.findViewById(R.id.screenname);
			holder.textLabel = (TextView) row.findViewById(R.id.tweettext);
			
			row.setTag(holder);
		}else{
			holder = (TweetHolder) row.getTag();
		}
		Tweet tweet = data[position];
		holder.nameLabel.setText(tweet.name);
		holder.screenLabel.setText(tweet.screenName);
		holder.textLabel.setText(tweet.text);
		return row;
	}
	

}
class PostAdapter extends ArrayAdapter<Post>{
	Activity context;
	int layoutResourceId;
	Post[] data = null;
	static class PostHolder{
		TextView nameLabel,  titleLabel, textLabel;
	}
	public PostAdapter(Activity context, int resource,
			Post[] objects) {
		super(context, resource, objects);
		this.context = context;
		layoutResourceId = resource;
		data = objects;	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			PostHolder holder = null;
			if(row== null){
				LayoutInflater inflater =context.getLayoutInflater();
				row = inflater.inflate(layoutResourceId,parent,false);
				holder = new PostHolder();
				holder.nameLabel = (TextView) row.findViewById(R.id.src);
				holder.titleLabel = (TextView) row.findViewById(R.id.title);
				holder.textLabel = (TextView) row.findViewById(R.id.statustext);
				
				row.setTag(holder);
			}else{
				holder = (PostHolder) row.getTag();
			}
			Post post= data[position];
			holder.nameLabel.setText(post.source);
			holder.titleLabel.setText(post.title);
			holder.textLabel.setText(post.text);
			return row;
	}
}