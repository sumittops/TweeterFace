package com.sumitspace.tweeterface;


public class AppData {
	
	//Constants
	static String TWITTER_CONSUMER_KEY = "p0pQtj4HmomEfX6VGaDT55oYr";
	static String TWITTER_CONSUMER_SECRET = "7JrFYwk6qlImExDynlgGCqIoaWSrCV7ubAtgeMNwCsQckO4S5Y";
	
	//Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String TWITTER_CALLBACK_URL = "oauth://sumitspace.com";
    
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
}
