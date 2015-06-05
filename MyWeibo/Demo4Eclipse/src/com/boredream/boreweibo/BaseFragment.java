package com.boredream.boreweibo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		System.out.println(getClass().getSimpleName() + " ... onCreate");
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

	@Override
	public void onAttach(Activity activity) {
		System.out.println(getClass().getSimpleName() + " ... onAttach");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println(getClass().getSimpleName() + " ... onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		System.out.println(getClass().getSimpleName() + " ... onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		System.out.println(getClass().getSimpleName() + " ... onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		System.out.println(getClass().getSimpleName() + " ... onDetach");
		super.onDetach();
	}

	@Override
	public void onPause() {
		System.out.println(getClass().getSimpleName() + " ... onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		System.out.println(getClass().getSimpleName() + " ... onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		System.out.println(getClass().getSimpleName() + " ... onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		System.out.println(getClass().getSimpleName() + " ... onStop");
		super.onStop();
	}
	
	
}
