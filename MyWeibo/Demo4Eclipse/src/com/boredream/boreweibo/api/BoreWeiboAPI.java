package com.boredream.boreweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.boredream.boreweibo.constants.AccessTokenKeeper;
import com.boredream.boreweibo.constants.URLs;
import com.boredream.boreweibo.utils.Logger;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
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

	/**
	 * 根据用户ID获取用户信息(uid和screen_name二选一)
	 * 
	 * @param uid
	 *            根据用户ID获取用户信息
	 * @param screen_name
	 *            需要查询的用户昵称。
	 * @param listener
	 */
	public void usersShow(String uid, String screen_name, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if(!TextUtils.isEmpty(uid)) {
			params.add("uid", uid);
		} else if(!TextUtils.isEmpty(screen_name)) {
			params.add("screen_name", screen_name);
		}
		requestInMainLooper(URLs.usersShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 上传图片并发布一条新微博
	 * 
	 * @param context
	 * @param status
	 *            要发布的微博文本内容。
	 * @param imgFilePath
	 *            要上传的图片绝对路径。
	 * @param listener
	 */
	public void statusesUpload(String status, String imgFilePath, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("access_token", mAccessToken.getToken());
		params.add("status", status);
		params.add("pic", imgFilePath);
		requestInMainLooper(URLs.statusesUpload, params, WeiboAPI.HTTPMETHOD_POST, listener);
	}
	
	public void statusesHome_timeline(long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("page", page);
		requestInMainLooper(URLs.statusesHome_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 获取某个用户最新发表的微博列表(uid和screen_name二选一)
	 * 
	 * @param uid
	 *            需要查询的用户ID。
	 * @param screen_name
	 *            需要查询的用户昵称。
	 * @param page
	 *            返回结果的页码。(单页返回的记录条数，默认为20。)
	 * @param listener
	 */
	public void statusesUser_timeline(long uid, String screen_name, long page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if(uid > 0) {
			params.add("uid", uid);
		} else if(!TextUtils.isEmpty(screen_name)) {
			params.add("screen_name", screen_name);
		}
		params.add("page", page);
		requestInMainLooper(URLs.statusesUser_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 转发微博
	 * 
	 * @param id
	 *            要转发的微博ID。
	 * @param status
	 *            添加的转发文本，必须做URLencode，内容不超过140个汉字，不填则默认为“转发微博”。
	 * @param is_comment
	 *            是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0 。
	 * @param listener
	 */
	public void statusesRepost(long id, String status, int is_comment, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("status", status);
		params.add("is_comment", is_comment);
		requestInMainLooper(URLs.statusesRepost, params , WeiboAPI.HTTPMETHOD_POST, listener);
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
	 * 获取指定微博的转发微博列表
	 * 
	 * @param id
	 *            要转发的微博ID。
	 * @param page
	 *            返回结果的页码(单页返回的记录条数，默认为20。)
	 * @param listener
	 */
	public void statusesRepostTimeline(long id, int page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("id", id);
		params.add("page", page);
		requestInMainLooper(URLs.statusesRepost_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
	}
	
}
