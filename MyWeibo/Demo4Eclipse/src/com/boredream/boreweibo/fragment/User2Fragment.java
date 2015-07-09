package com.boredream.boreweibo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boredream.boreweibo.BaseApplication;
import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.activity.UserInfoActivity;
import com.boredream.boreweibo.adapter.UserItemAdapter;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.constants.AccessTokenKeeper;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.entity.UserItem;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.widget.WrapHeightListView;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class User2Fragment extends BaseFragment {

	private LinearLayout ll_userinfo;
	
	private ImageView iv_avatar;
	private TextView tv_subhead;
	private TextView tv_caption;

	private TextView tv_status_count;
	private TextView tv_follow_count;
	private TextView tv_fans_count;
	
	private WrapHeightListView lv_user_items;

	private User user;
	private View view;

	private UserItemAdapter adapter;
	private List<UserItem> userItems;
	
	private Oauth2AccessToken mAccessToken;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag_user2, null);
		mAccessToken = AccessTokenKeeper.readAccessToken(activity);
		
		initView();
		
		setItem();

		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// show/hide方法不会走生命周期
		System.out.println(getClass().getSimpleName() + " ... onStart()");
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		// 用onHiddenChanged方法中的hidden参数fragment的显示/隐藏情况
		System.out.println(getClass().getSimpleName() + " ... onHiddenChanged() " + hidden);
		if(!hidden) {
			weiboApi.usersShow(mAccessToken.getUid(), "",
					new SimpleRequestListener(activity, null) {

						@Override
						public void onComplete(String response) {
							super.onComplete(response);
							
							BaseApplication application = (BaseApplication) activity.getApplication();
							application.currentUser = new Gson().fromJson(response, User.class);
							
							setUserInfo();
						}
					});
		}
	}

	private void initView() {
		new TitleBuilder(view)
			.setTitleText("我")
			.build();
		
		ll_userinfo = (LinearLayout) view.findViewById(R.id.ll_userinfo);
		ll_userinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, UserInfoActivity.class);
				startActivity(intent);
			}
		});
		
		tv_status_count = (TextView) view.findViewById(R.id.tv_status_count);
		tv_follow_count = (TextView) view.findViewById(R.id.tv_follow_count);
		tv_fans_count = (TextView) view.findViewById(R.id.tv_fans_count);

		iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
		tv_subhead = (TextView) view.findViewById(R.id.tv_subhead);
		tv_caption = (TextView) view.findViewById(R.id.tv_caption);
		
		lv_user_items = (WrapHeightListView) view.findViewById(R.id.lv_user_items);
		userItems = new ArrayList<UserItem>();
		adapter = new UserItemAdapter(activity, userItems);
		lv_user_items.setAdapter(adapter);
	}

	private void setUserInfo() {
		user = ((BaseApplication)activity.getApplication()).currentUser;
		
		if(user == null) {
			return;
		}
		
		// set data
		tv_subhead.setText(user.getName());
		tv_caption.setText("简介:" + user.getDescription());
		imageLoader.displayImage(user.getAvatar_large(), iv_avatar);
		tv_status_count.setText("" + user.getStatuses_count());
		tv_follow_count.setText("" + user.getFriends_count());
		tv_fans_count.setText("" + user.getFollowers_count());
	}
	
	private void setItem() {
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_1, "新的朋友", ""));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博等级", "Lv13"));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_3, "我的相册", "(17)"));
		userItems.add(new UserItem(true, R.drawable.push_icon_app_small_4, "微博支付", ""));
		userItems.add(new UserItem(true, R.drawable.push_icon_app_small_5, "更多", "收藏、名片"));
		adapter.notifyDataSetChanged();
	}

}
