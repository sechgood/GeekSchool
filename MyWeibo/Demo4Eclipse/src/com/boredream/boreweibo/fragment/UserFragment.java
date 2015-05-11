package com.boredream.boreweibo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.activity.UserInfoActivity;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.utils.ToastUtils;
import com.boredream.boreweibo.widget.GroupSettingListView;
import com.boredream.boreweibo.widget.GroupSettingListView.OnSettingItemClickLister;

public class UserFragment extends BaseFragment {
	
	private View view;
	private GroupSettingListView gslv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag_user, null);

		initView();
		
		return view;
	}
	
	private void initView() {
		new TitleBuilder(view).setTitleText("我").build();

		gslv = (GroupSettingListView) view.findViewById(R.id.gslv);
		int[] indexs = {1, 3};
		int[] imgs = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, 
				R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
		String[] infos = {"Setting1", "Setting2", "Setting3", "Setting4", "Setting5", "Setting6"};
		gslv.setAdapterData(indexs, imgs, infos);
		
		gslv.setOnSettingItemClickLister(new OnSettingItemClickLister() {
			@Override
			public void onItemClick(ViewGroup parent, View item, int position) {
				if(position == 0) {
					Intent intent = new Intent(activity, UserInfoActivity.class);
					startActivity(intent);
				}
			}
		});
	}
	
}
