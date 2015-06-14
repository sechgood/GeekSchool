package com.boredream.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.StatusAdapter;
import com.boredream.boreweibo.api.BoreWeiboApi;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.response.StatusTimeLineResponse;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.utils.ToastUtils;
import com.google.gson.Gson;

public class HomeFragment extends BaseFragment {

	private View view;
	private ListView lv_home;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView();
		loadData(1);
		return view;
	}

	private void loadData(int page) {
		BoreWeiboApi api = new BoreWeiboApi(activity);
		
		api.statusesHome_timeline(page, 
				new SimpleRequestListener(activity, null){

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						StatusTimeLineResponse timeLineResponse = new Gson().fromJson(response, StatusTimeLineResponse.class);
						lv_home.setAdapter(new StatusAdapter(activity, timeLineResponse.getStatuses()));
					}
			
		});
	}

	private void initView() {
		view = View.inflate(activity, R.layout.frag_home, null);
		
		new TitleBuilder(view)
			.setTitleText("首页")
			.setLeftText("LEFT")
			.setLeftOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ToastUtils.showToast(activity, "left onclick", Toast.LENGTH_SHORT);
				}
			});
		lv_home = (ListView) view.findViewById(R.id.lv_home);
	}
	
}
