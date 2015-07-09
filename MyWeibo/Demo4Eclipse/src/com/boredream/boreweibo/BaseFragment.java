package com.boredream.boreweibo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.boredream.boreweibo.activity.MainActivity;
import com.boredream.boreweibo.api.BoreWeiboAPI;
import com.boredream.boreweibo.utils.DialogUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseFragment extends Fragment {
	
	protected MainActivity activity;
	protected Dialog progressDialog;;
	
	protected ImageLoader imageLoader;
	protected BoreWeiboAPI weiboApi;
	protected Gson gson;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = (MainActivity) getActivity();
		progressDialog = DialogUtils.createLoadingDialog(activity);
		
		imageLoader = ImageLoader.getInstance();
		weiboApi = new BoreWeiboAPI(activity);
		gson = new Gson();
	}
	
	protected void intent2Activity(Class<? extends Activity> tarActivity) {
		Intent intent = new Intent(activity, tarActivity);
		startActivity(intent);
	}

}
