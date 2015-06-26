package com.boredream.boreweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.boredream.boreweibo.constants.AccessTokenKeeper;
import com.boredream.boreweibo.constants.URLs;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;

public class BoreWeiboApi extends WeiboAPI {

	private Handler mainLooperHandler = new Handler(Looper.getMainLooper());
	
	public BoreWeiboApi(Oauth2AccessToken oauth2AccessToken) {
		super(oauth2AccessToken);
	}
	
	public BoreWeiboApi(Context context) {
		this(AccessTokenKeeper.readAccessToken(context));
	}
	
	public void requestInMainLooper(String url, WeiboParameters params,
			String httpMethod, final RequestListener listener) {
		request(url, params, httpMethod, new RequestListener() {
			
			@Override
			public void onIOException(final IOException e) {
				mainLooperHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onIOException(e);
					}
				});
			}
			
			@Override
			public void onError(final WeiboException e) {
				mainLooperHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onError(e);
					}
				});
			}
			
			@Override
			public void onComplete4binary(final ByteArrayOutputStream responseOS) {
				mainLooperHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onComplete4binary(responseOS);
					}
				});
			}
			
			@Override
			public void onComplete(final String response) {
				mainLooperHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onComplete(response);
					}
				});
			}
		});
	}

	@Override
	protected void request(String url, WeiboParameters params,
			String httpMethod, RequestListener listener) {
		// TODO Auto-generated method stub
		super.request(url, params, httpMethod, listener);
	}

	/**
	 * 获取当前登录用户及其所关注用户的最新微博
	 * 
	 * @param page
	 *            返回结果的页码。(单页返回的记录条数，默认为20。)
	 * @param listener
	 */
	public void statusesHome_timeline(long page, RequestListener listener) {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("page", page);
		requestInMainLooper(URLs.statusesHome_timeline, parameters, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * 
	 * @param id
	 *            需要查询的微博ID。
	 * @param page
	 *            返回结果的页码。(单页返回的记录条数，默认为50。)
	 * @param listener
	 */
	public void commentsShow(long id, long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("page", page);
		requestInMainLooper(URLs.commentsShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 对一条微博进行评论
	 * 
	 * @param id
	 *            需要评论的微博ID。
	 * @param comment
	 *            评论内容，必须做URLencode，内容不超过140个汉字。
	 * @param listener
	 */
	public void commentsCreate(long id, String comment, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("comment", comment);
		requestInMainLooper(URLs.commentsCreate, params , WeiboAPI.HTTPMETHOD_POST, listener);
	}
	
	
}