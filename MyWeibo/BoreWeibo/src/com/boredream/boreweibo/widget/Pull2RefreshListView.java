package com.boredream.boreweibo.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.boredream.boreweibo.utils.DisplayUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Pull2RefreshListView extends PullToRefreshListView {

	public Pull2RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Pull2RefreshListView(Context context, com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode, com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
		// TODO Auto-generated constructor stub
	}

	public Pull2RefreshListView(Context context, com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
		// TODO Auto-generated constructor stub
	}

	public Pull2RefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ListView createListView(Context context, AttributeSet attrs) {
		System.out.println("createListView createListView createListView");
		ListView listView = super.createListView(context, attrs);
//		listView.setMinimumHeight(DisplayUtils.getScreenHeightPixels((Activity)context));
		return listView;
	}

	
	
}
