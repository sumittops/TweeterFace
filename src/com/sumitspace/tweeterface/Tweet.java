package com.sumitspace.tweeterface;

public class Tweet {
		public String name, screenName, text;
	public Tweet(){
		
	}
	public Tweet(String n, String sn, String txt){
		name = n;
		screenName = sn;
		text = txt;
	}
}
class Post{
	public String source, title, text;
	public Post(){
		title = "";
		text = "";
		
	}
	public Post(String t, String txt, String src){
		title = t;
		text = txt;
		source =src;
	}
	
}
