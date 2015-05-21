package com.boredream.boreweibo.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.boredream.boreweibo.BaseFragment;

public class FragmentController {

	private int containerId;
	private android.support.v4.app.FragmentManager fm;

	private ArrayList<BaseFragment> fragments;

	private static FragmentController controller;

	public static FragmentController getInstance(FragmentActivity activity, int containerId) {
		if (controller == null) {
			controller = new FragmentController(activity, containerId);
		}
		return controller;
	}

	private FragmentController(FragmentActivity activity, int containerId) {
		this.containerId = containerId;
		fm = activity.getSupportFragmentManager();
		initFragments();
	}

	private void initFragments() {
		fragments = new ArrayList<BaseFragment>();
		fragments.add(new HomeFragment());
		fragments.add(new MessageFragment());
		fragments.add(new SearchFragment());
		fragments.add(new UserFragment());
		
		
		FragmentTransaction ft = fm.beginTransaction();
		for (Fragment fragment : fragments) {
			ft.add(containerId, fragment);
		}
		ft.commit();
	}

	public void showFragment(int position) {
		FragmentTransaction ft = fm.beginTransaction();
		
		// hide all
		for (Fragment fragment : fragments) {
			if (fragment != null) {
				ft.hide(fragment);
			}
		}
		
		// show one
		BaseFragment fragment = fragments.get(position);
		ft.show(fragment);
		
		ft.commit();
	}
	
	public Fragment getFragment(int position) {
		return fragments.get(position);
	}

}