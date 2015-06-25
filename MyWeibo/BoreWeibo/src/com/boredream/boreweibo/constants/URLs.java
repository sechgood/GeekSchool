package com.boredream.boreweibo.constants;

public interface URLs {

	// host
	String BASE_URL = "https://api.weibo.com/2/";
	
	// 首页微博列表
	String statusesHome_timeline = BASE_URL + "statuses/home_timeline.json";
	// 微博评论列表
	String commentsShow = BASE_URL + "comments/show.json";
	// 对一条微博进行评论
	String commentsCreate = BASE_URL + "comments/create.json";
	
}
