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
		int[] indexs = {2, 5, 7};
		int[] imgs = {R.drawable.push_icon_app_small_2, R.drawable.push_icon_app_small_2, 
				R.drawable.push_icon_app_small_2, R.drawable.push_icon_app_small_2, 
				R.drawable.push_icon_app_small_2, R.drawable.push_icon_app_small_2, 
				R.drawable.push_icon_app_small_2, R.drawable.push_icon_app_small_2,
				R.drawable.push_icon_app_small_2};
		String[] infos = {"新的朋友", "微博等级", "我的相册", "我的收藏", "赞", "微博支付", "个性化", "草稿箱", "更多"};
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
