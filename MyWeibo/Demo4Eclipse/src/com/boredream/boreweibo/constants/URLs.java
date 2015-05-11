package com.boredream.boreweibo.constants;

public interface URLs {

	String BASE_URL = "https://api.weibo.com/2/";
	
	String usersShow = BASE_URL + "users/show.json";
	String statusesHome_timeline = BASE_URL + "statuses/home_timeline.json";
	String statusesUser_timeline = BASE_URL + "statuses/user_timeline.json";
	String statusesRepost = BASE_URL + "statuses/repost.json";
	String commentsShow = BASE_URL + "comments/show.json";
	String statusesRepost_timeline = BASE_URL + "statuses/repost_timeline.json";
	
}
