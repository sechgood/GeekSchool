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
	private Dialog loadingDialog;

	public SimpleRequestListener(Context context, Dialog loadingDialog) {
		this.context = context;
		this.loadingDialog = loadingDialog;
	}
	
	public void onComplete(String response) {
		if(loadingDialog != null && loadingDialog.getContext() == context) {
			loadingDialog.dismiss();
		}
		LogUtils.show("REQUEST onComplete", response);
	}

	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		if(loadingDialog != null && loadingDialog.getContext() == context) {
			loadingDialog.dismiss();
		}
		LogUtils.show("REQUEST onComplete4binary", responseOS.size() + "");
		
	}

	public void onIOException(IOException e) {
		LogUtils.show("REQUEST onIOException", e.toString());
	}

	public void onError(WeiboException e) {
		LogUtils.show("REQUEST onError", e.toString());
	}

}
