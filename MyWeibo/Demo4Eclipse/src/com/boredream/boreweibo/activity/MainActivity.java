package com.boredream.boreweibo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.fragment.FragmentController;
import com.boredream.boreweibo.fragment.HomeFragment;

public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener {
	
	private RadioGroup rg_tab;
	private ImageView iv_add;
	private FragmentController fc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_tab);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		initView();
		
		initData();
	}
	
	private void initView() {
		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		
		rg_tab.setOnCheckedChangeListener(this);
		iv_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, WriteStatusActivity.class);
				startActivityForResult(intent, 110);
			}
		});
	}

	private void initData() {
		fc = FragmentController.getInstance(this, R.id.fl_content);
		((RadioButton)rg_tab.getChildAt(0)).setChecked(true);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_home:
			fc.showFragment(0);
			break;
		case R.id.rb_shop:
			fc.showFragment(1);
			break;
		case R.id.rb_search:
			fc.showFragment(2);
			break;
		case R.id.rb_user:
			fc.showFragment(3);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		Fragment fragment = fc.getFragment(0);
		if(fragment instanceof HomeFragment) {
			HomeFragment homeFragment = (HomeFragment) fragment;
			homeFragment.initData();
		}
	}
}
