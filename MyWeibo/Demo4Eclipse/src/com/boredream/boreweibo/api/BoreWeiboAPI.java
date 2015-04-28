package com.boredream.boreweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.boredream.boreweibo.constants.AccessTokenKeeper;
import com.boredream.boreweibo.utils.Logger;
import com.boredream.boreweibo.utils.URLs;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;

public class BoreWeiboAPI extends WeiboAPI{
	
	private Handler mainLooperHandler = new Handler(Looper.getMainLooper());

	private BoreWeiboAPI(Oauth2AccessToken oauth2AccessToken) {
		super(oauth2AccessToken);
	}
	
	public BoreWeiboAPI(Context context) {
		this(AccessTokenKeeper.readAccessToken(context));
	}
	
	@Override
	protected void request(String url, WeiboParameters params, String httpMethod, RequestListener listener) {
		super.request(url, params, httpMethod, listener);
	}

	public void requestInMainLooper(String url, WeiboParameters params, String httpMethod, 
			final RequestListener listener) {
		
		// 通用必要参数
		params.add("access_token", mAccessToken.getToken());
		
		Logger.show("API", "url = " + parseGetUrlWithParams(url, params));
		// 主线程处理
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
	
	/**
	 * 转换get方式的url,将get参数与url拼接
	 * 
	 * @param url
	 *            原url
	 * @param getParams
	 *            需要拼接的参数map集合
	 * @return 拼装完成的url
	 */
	public static String parseGetUrlWithParams(String url, WeiboParameters getParams) {
		StringBuilder newUrl = new StringBuilder(url);
		if (getParams != null && getParams.size() > 0) {
			newUrl.append("?");
			for (int i=0; i<getParams.size(); i++) {
				newUrl.append(getParams.getKey(i) + "=" + getParams.getValue(i) + "&");
			}
			newUrl.substring(0, newUrl.length() - 2);
		}
		return newUrl.toString();
	}

	// Weibo API
	public void usersShow(String uid, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("uid", uid);
		requestInMainLooper(URLs.usersShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
	public void statusesHome_timeline(long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("page", page);
		requestInMainLooper(URLs.statusesHome_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
}
