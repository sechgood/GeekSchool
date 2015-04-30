package com.boredream.boreweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;

import com.boredream.boreweibo.utils.LogUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class SimpleRequestListener implements RequestListener {

	private Context context;
	private Dialog progressDialog;

	public SimpleRequestListener(Context context, Dialog progressDialog) {
		this.context = context;
		this.progressDialog = progressDialog;
	}
	
	public void onComplete(String response) {
		onDone();
		LogUtils.show("REQUEST onComplete", response);
	}

	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		onDone();
		LogUtils.show("REQUEST onComplete4binary", responseOS.size() + "");
		
	}

	public void onIOException(IOException e) {
		onDone();
		LogUtils.show("REQUEST onIOException", e.toString());
	}

	public void onError(WeiboException e) {
		onDone();
		LogUtils.show("REQUEST onError", e.toString());
	}
	
	public void onDone() {
		if(progressDialog != null && progressDialog.getContext() == context) {
			progressDialog.dismiss();
		}
	}

}