package com.boredream.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.utils.ToastUtils;

public class HomeFragment extends BaseFragment {

	private View view;
	private TextView titlebar_tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		
		return view;
	}
	
}
