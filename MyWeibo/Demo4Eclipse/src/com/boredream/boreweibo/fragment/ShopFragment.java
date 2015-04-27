package com.boredream.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boredream.boreweibo.BaseFragment;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.utils.TitleBuilder;

public class ShopFragment extends BaseFragment {
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag_home, null);

		initView();
		
		return view;
	}
	
	private void initView() {
		new TitleBuilder(view).setTitleText("咖店").build();
	}
	
}
