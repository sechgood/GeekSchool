package com.boredream.boreweibo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.boredream.boreweibo.entity.response.ErrorResponse;
import com.boredream.boreweibo.utils.Logger;
import com.boredream.boreweibo.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class SimpleRequestListener2<T> implements RequestListener {

	private Context context;
	private Class<T> clazz;
	private Dialog progressDialog;
	private Gson gson;

	public SimpleRequestListener2(Context context, Class<T> clazz, Dialog progressDialog) {
		this.context = context;
		this.clazz = clazz;
		this.progressDialog = progressDialog;
		this.gson = new Gson();
	}
	
	protected void onCompleteSuccess(T response) {
		onAllDone();
		Logger.show("REQUEST onCompleteSuccess", response.toString());
	}
	
	protected void onCompleteFail(ErrorResponse error) {
		onAllDone();
		ToastUtils.showToast(context, error.toString(), Toast.LENGTH_SHORT);
		Logger.show("REQUEST onCompleteFail", error.toString());
	}
	
	public void onComplete(String response) {
		try {
			ErrorResponse error = gson.fromJson(response, ErrorResponse.class);
			if(!error.isNull()) {
				onCompleteFail(error);
			} else {
				T t = gson.fromJson(response, clazz);
				onCompleteSuccess(t);
			}
		} catch (JsonSyntaxException e) {
			ErrorResponse error = new ErrorResponse();
			error.setError_code("233333333");
			error.setError("JsonSyntaxException ~");
			onCompleteFail(error);
		}
		onAllDone();
		Logger.show("REQUEST onComplete", response);
	}

	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		onAllDone();
		Logger.show("REQUEST onComplete4binary", responseOS.size() + "");
		
	}

	public void onIOException(IOException e) {
		onAllDone();
		ToastUtils.showToast(context, e.getMessage(), Toast.LENGTH_SHORT);
		Logger.show("REQUEST onIOException", e.toString());
	}

	public void onError(WeiboException e) {
		onAllDone();
		ToastUtils.showToast(context, e.getMessage(), Toast.LENGTH_SHORT);
		Logger.show("REQUEST onError", e.toString());
	}
	
	public void onAllDone() {
		if(progressDialog != null) {
			progressDialog.dismiss();
		}
	}

}
