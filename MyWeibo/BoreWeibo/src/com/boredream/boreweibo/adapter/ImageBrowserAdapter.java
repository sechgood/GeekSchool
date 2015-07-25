package com.boredream.boreweibo.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.PicUrls;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageBrowserAdapter extends PagerAdapter {

	private Activity context;
	private ArrayList<PicUrls> picUrls;
	private ArrayList<View> picViews;
	
	private ImageLoader mImageLoader;

	public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
		this.context = context;
		this.picUrls = picUrls;
		this.mImageLoader = ImageLoader.getInstance();
		
		initImgs();
	}

	private void initImgs() {
		picViews = new ArrayList<View>();
		
		for(int i=0; i<picUrls.size(); i++) {
			// 填充显示图片的页面布局
			View view = View.inflate(context, R.layout.item_image_browser, null);
			picViews.add(view);
		}
	}

	@Override
	public int getCount() {
		// 图片大于1张时,无限大小即无限轮播
		if (picUrls.size() > 1) {
			return Integer.MAX_VALUE;
		}
		return picUrls.size();
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		int index = position % picUrls.size();
		View view = picViews.get(index);
		
		ImageView iv = (ImageView) view.findViewById(R.id.iv_image_browser);
		
		PicUrls picUrl = picUrls.get(index);
		String url = picUrl.getOriginal_pic();
		mImageLoader.displayImage(url, iv);
		
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}