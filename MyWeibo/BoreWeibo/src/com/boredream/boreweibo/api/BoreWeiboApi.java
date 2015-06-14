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

	public void statusesHome_timeline(long page, RequestListener listener) {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("page", page);
		requestInMainLooper(URLs.statusesHome_timeline, parameters, HTTPMETHOD_GET, listener);
	}
}
