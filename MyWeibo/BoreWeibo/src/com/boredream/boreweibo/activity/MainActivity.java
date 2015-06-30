package com.boredream.boreweibo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.fragment.FragmentController;
import com.boredream.boreweibo.fragment.HomeFragment;

public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener, OnClickListener {
	
	private static final int REQUEST_CODE_WRITE_STATUS = 110;
	
	private RadioGroup rg_tab;
	private ImageView iv_add;
	private FragmentController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		controller = FragmentController.getInstance(this, R.id.fl_content);
		controller.showFragment(0);
		
		initView();
	}
	
	private void initView() {
		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		
		rg_tab.setOnCheckedChangeListener(this);
		iv_add.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_home:
			controller.showFragment(0);
			break;
		case R.id.rb_meassage:
			controller.showFragment(1);
			break;
		case R.id.rb_search:
			controller.showFragment(2);
			break;
		case R.id.rb_user:
			controller.showFragment(3);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add:
			Intent intent = new Intent(MainActivity.this, WriteStatusActivity.class);
			startActivityForResult(intent, REQUEST_CODE_WRITE_STATUS);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		if(arg0 == RESULT_CANCELED) {
			return;
		}
		
		switch (arg1) {
		case REQUEST_CODE_WRITE_STATUS:
			Fragment fragment = controller.getFragment(0);
			if(fragment instanceof HomeFragment) {
				HomeFragment homeFragment = (HomeFragment) fragment;
				homeFragment.loadData(1);
			}
			break;

		default:
			break;
		}
	}
}
