package com.boredream.boreweibo;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.boredream.boreweibo.api.BoreWeiboAPI;
import com.boredream.boreweibo.constants.CommonConstants;
import com.boredream.boreweibo.utils.DialogUtils;
import com.boredream.boreweibo.utils.Logger;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity {

	protected String TAG;

	protected BaseApplication application;
	protected SharedPreferences sp;
	protected Intent intent;
	protected Dialog progressDialog;
	
	protected ImageLoader imageLoader;
	protected BoreWeiboAPI weiboApi;
	protected Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TAG = this.getClass().getSimpleName();
		showLog("onCreate()");
		
		application = (BaseApplication) getApplication();
		sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
		intent = getIntent();
		progressDialog = DialogUtils.createLoadingDialog(this);
		application.addActivity(this);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		imageLoader = ImageLoader.getInstance();
		weiboApi = new BoreWeiboAPI(this);
		gson = new Gson();
	}
	
	protected void intent2Activity(Class<?> tarActivity) {
		Intent intent = new Intent(this, tarActivity);
		startActivity(intent);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		showLog("onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		showLog("onResume()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		showLog("onDestroy()");
		
		application.removeActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		showLog("onStop()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		showLog("onPause()");
	}

	protected void finishActivity() {
		this.finish();
	}

	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected void showLog(String msg) {
		Logger.show(TAG, msg);
	}

}
