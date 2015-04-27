package com.boredream.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class HomeFragment extends BaseFragment {

	private View view;
	
	private PullToRefreshListView plv_home;
	
	private int curPage = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag_home, null);
		
		initView();
		
		loadData();
		
		return view;
	}

	private void initView() {
		new TitleBuilder(view).setTitleText("首页").build();
		
		plv_home = (PullToRefreshListView) view.findViewById(R.id.plv_home);
		plv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData();
			}
		});
		plv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}
	
	private void loadData() {
		
	}
}
