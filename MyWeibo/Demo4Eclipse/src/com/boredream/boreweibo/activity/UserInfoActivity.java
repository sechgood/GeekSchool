package com.boredream.boreweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.StatusAdapter;
import com.boredream.boreweibo.adapter.StatusGridImgsAdapter;
import com.boredream.boreweibo.adapter.TabCommentAdapter;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.Comment;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.entity.response.CommentsResponse;
import com.boredream.boreweibo.entity.response.StatusTimeLineResponse;
import com.boredream.boreweibo.utils.DateUtils;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.boredream.boreweibo.utils.StringUtils;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.widget.WrapHeightGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class UserInfoActivity extends BaseActivity implements 
	OnClickListener, OnItemClickListener, OnCheckedChangeListener {
	
	private View user_info_head;
	private ImageView iv_avatar;
	private TextView tv_name;
	private TextView tv_follows;
	private TextView tv_fans;
	private LinearLayout ll_edit_sign;
	private TextView tv_sign;
	
	private View shadow_user_info_tab;
	private RadioGroup shadow_rg_user_info;
	private RadioButton shadow_rb_info;
	private RadioButton shadow_rb_status;
	private RadioButton shadow_rb_photos;
	private RadioButton shadow_rb_manager;
	private View user_info_tab;
	private RadioGroup rg_user_info;
	private RadioButton rb_info;
	private RadioButton rb_status;
	private RadioButton rb_photos;
	private RadioButton rb_manager;
	
	private PullToRefreshListView lv_user_info;
	private View footView;
	
	private boolean isCurrentUser;
	private User user;
	private String userName;
	
	private List<Status> statuses = new ArrayList<Status>();
	private StatusAdapter statusAdapter;
	private long curPage = 1;
	private boolean isLoadingMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_info);

		userName = intent.getStringExtra("userName");
		if(TextUtils.isEmpty(userName)) {
			isCurrentUser = true;
			user = application.currentUser;
		}

		initView();
		
		loadData();
		
//		loadComments(1);
	}

	private void initView() {
		new TitleBuilder(this)
				.setLeftImage(R.drawable.ic_launcher)
				.setLeftOnClickListener(this)
				.build();
		
		initInfoHead();
		initTab();
		initListView();
	}

	private void initInfoHead() {
		user_info_head = View.inflate(this, R.layout.user_info_head, null);
		iv_avatar = (ImageView) user_info_head.findViewById(R.id.iv_avatar);
		tv_name = (TextView) user_info_head.findViewById(R.id.tv_name);
		tv_follows = (TextView) user_info_head.findViewById(R.id.tv_follows);
		tv_fans = (TextView) user_info_head.findViewById(R.id.tv_fans);
		ll_edit_sign = (LinearLayout) user_info_head.findViewById(R.id.ll_edit_sign);
		tv_sign = (TextView) user_info_head.findViewById(R.id.tv_sign);
	}

	private void initTab() {
		shadow_user_info_tab = findViewById(R.id.user_info_tab);
		shadow_rg_user_info = (RadioGroup) findViewById(R.id.rg_user_info);
		shadow_rb_info = (RadioButton) findViewById(R.id.rb_info);
		shadow_rb_status = (RadioButton) findViewById(R.id.rb_status);
		shadow_rb_photos = (RadioButton) findViewById(R.id.rb_photos);
		shadow_rb_manager = (RadioButton) findViewById(R.id.rb_manager);
		
		shadow_rg_user_info.setOnCheckedChangeListener(this);
		
		user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
		rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
		rb_info = (RadioButton) user_info_tab.findViewById(R.id.rb_info);
		rb_status = (RadioButton) user_info_tab.findViewById(R.id.rb_status);
		rb_photos = (RadioButton) user_info_tab.findViewById(R.id.rb_photos);
		rb_manager = (RadioButton) user_info_tab.findViewById(R.id.rb_manager);
		
		rg_user_info.setOnCheckedChangeListener(this);
	}
	
	private void initListView() {
		lv_user_info = (PullToRefreshListView) findViewById(R.id.lv_user_info);
		footView = View.inflate(this, R.layout.footview_loading, null);
		final ListView lv = lv_user_info.getRefreshableView();
		statusAdapter = new StatusAdapter(this, statuses);
		lv_user_info.setAdapter(statusAdapter);
		lv.addHeaderView(user_info_head);
		lv.addHeaderView(user_info_tab);
		lv_user_info.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				loadComments(1);
			}
		});
		lv_user_info.setOnLastItemVisibleListener(
				new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
//						loadComments(curPage + 1);
					}
				});
		lv_user_info.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 0-pullHead 1-detailHead 2-tab
				shadow_user_info_tab.setVisibility(firstVisibleItem >= 2 ?
						View.VISIBLE : View.GONE);
			}
		});
	}

	private void loadData() {
		if(isCurrentUser) {
			setUserInfo();
		} else {
			loadUserInfo();
		}
		loadStatuses(1);
	}

	private void setUserInfo() {
		if(user == null) {
			return;
		}
		// set data
		tv_name.setText(user.getName());
		imageLoader.displayImage(user.getProfile_image_url(), iv_avatar,
				ImageOptHelper.getAvatarOptions());
		tv_follows.setText("关注 " + user.getFollowers_count());
	}
	
	private void loadUserInfo() {
		weiboApi.usersShow("", userName,
				new SimpleRequestListener(this, progressDialog){

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						
					}
			
		});
	}
	
	private void loadStatuses(final long requestPage) {
		if(isLoadingMore) {
			return;
		}
		
		isLoadingMore = true;
		weiboApi.statusesUser_timeline(
				user == null ? -1 : user.getId(), 
				userName, 
				requestPage,
				new SimpleRequestListener(this, progressDialog) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						showLog("status comments = " + response);
						
						if(requestPage == 1) {
							statuses.clear();
						}

						addStatus(gson.fromJson(response, StatusTimeLineResponse.class));
					}
					
					@Override
					public void onDone() {
						super.onDone();
						
						isLoadingMore = false;
						lv_user_info.onRefreshComplete();
					}

				});
	}
	
	private void addStatus(StatusTimeLineResponse response) {
		for(Status status : response.getStatuses()) {
			if(!statuses.contains(status)) {
				statuses.add(status);
			}
		}
		statusAdapter.notifyDataSetChanged();
		
		if(curPage < response.getTotal_number()) {
			addFootView(lv_user_info, footView);
		} else {
			removeFootView(lv_user_info, footView);
		}
	}
	
	private void addFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if(lv.getFooterViewsCount() == 1) {
			lv.addFooterView(footView);
		}
	}
	
	private void removeFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if(lv.getFooterViewsCount() > 1) {
			lv.removeFooterView(footView);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			break;
		case R.id.ll_share_bottom:
			break;
		case R.id.ll_comment_bottom:
			break;
		case R.id.ll_like_bottom:
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, ImageBrowserActivity.class);
//		intent.putExtra("status", status);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		switch (checkedId) {
		case R.id.rb_info:
			rb_info.setChecked(true);
			shadow_rb_info.setChecked(true);
			
			break;
		case R.id.rb_status:
			rb_status.setChecked(true);
			shadow_rb_status.setChecked(true);
			
			break;
		case R.id.rb_photos:
			rb_photos.setChecked(true);
			shadow_rb_photos.setChecked(true);
			
			break;
		case R.id.rb_manager:
			rb_manager.setChecked(true);
			shadow_rb_manager.setChecked(true);
			
			break;

		default:
			break;
		}
	}

}
