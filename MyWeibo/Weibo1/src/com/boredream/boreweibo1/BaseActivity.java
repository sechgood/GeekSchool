package com.boredream.boreweibo1;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.boredream.boreweibo1.constants.CommonConstants;
import com.boredream.boreweibo1.utils.Logger;
import com.boredream.boreweibo1.utils.ToastUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity {

	protected String TAG;

	protected BaseApplication application;
	protected SharedPreferences sp;
	protected Intent intent;
	
	protected ImageLoader imageLoader;
	protected Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application.addActivity(this);
		
		TAG = this.getClass().getSimpleName();
		
		application = (BaseApplication) getApplication();
		sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
		intent = getIntent();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		imageLoader = ImageLoader.getInstance();
		gson = new Gson();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		application.removeActivity(this);
	}
	
	protected void intent2Activity(Class<? extends Activity> tarActivity) {
		Intent intent = new Intent(this, tarActivity);
		startActivity(intent);
	}
	
	protected void showToast(String msg) {
		ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
	}

	protected void showLog(String msg) {
		Logger.show(TAG, msg);
	}

}
