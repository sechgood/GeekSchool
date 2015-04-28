package com.boredream.boreweibo.utils;

public interface URLs {

	String BASE_URL = "https://api.weibo.com/2/";
	
	String usersShow = BASE_URL + "users/show.json";
	String statusesHome_timeline = BASE_URL + "statuses/home_timeline.json";
	
}
