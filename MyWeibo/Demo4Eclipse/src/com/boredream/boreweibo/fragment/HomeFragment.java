package com.boredream.boreweibo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.activity.StatusDetailActivity;
import com.boredream.boreweibo.adapter.StatusAdapter;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.response.HomeTimeLineResponse;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class HomeFragment extends BaseFragment {

	private View view;
	
	private PullToRefreshListView plv_home;
	private View footView;
	private List<Status> statuses;
	private StatusAdapter adapter;
	
	private long curPage = 1;
	private boolean isLoadingMore;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag_home, null);
		
		initView();
		initData();
		
		return view;
	}

	private void initView() {
		new TitleBuilder(view).setTitleText("首页").build();
		
		plv_home = (PullToRefreshListView) view.findViewById(R.id.plv_home);
		plv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(1);
			}
		});
		plv_home.setOnLastItemVisibleListener(
				new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						loadData(curPage + 1);
					}
				});
		
		footView = View.inflate(activity, R.layout.footview_loading, null);
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
	
	private void initData() {
		statuses = new ArrayList<Status>();
		adapter = new StatusAdapter(activity, statuses);
		plv_home.setAdapter(adapter);
		
		loadData(1);
	}
	
	private void loadData(final long requestPage) {
		if(isLoadingMore) {
			return;
		}
		
		isLoadingMore = true;
		weiboApi.statusesHome_timeline(curPage, 
				new SimpleRequestListener(activity, progressDialog){

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						if(requestPage == 1) {
							statuses.clear();
						}
						curPage = requestPage;
						
						addData(gson.fromJson(response, HomeTimeLineResponse.class));
					}

					@Override
					public void onDone() {
						super.onDone();
						
						isLoadingMore = false;
						plv_home.onRefreshComplete();
					}
		});
	}

	private void addData(HomeTimeLineResponse resBean) {
		for(Status status : resBean.getStatuses()) {
			if(!statuses.contains(status)) {
				statuses.add(status);
			}
		}
		adapter.notifyDataSetChanged();
		
		if(curPage < resBean.getTotal_number()) {
			addFootView(plv_home, footView);
		} else {
			removeFootView(plv_home, footView);
		}
	}
}
