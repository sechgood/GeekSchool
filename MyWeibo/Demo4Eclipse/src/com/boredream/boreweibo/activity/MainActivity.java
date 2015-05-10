package com.boredream.boreweibo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.fragment.FragmentController;

public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener {
	
	private RadioGroup rg_tab;
	private FragmentController fc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_tab);
		
		initView();
		
		initData();
	}
	
	private void initView() {
		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
		rg_tab.setOnCheckedChangeListener(this);
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
}
