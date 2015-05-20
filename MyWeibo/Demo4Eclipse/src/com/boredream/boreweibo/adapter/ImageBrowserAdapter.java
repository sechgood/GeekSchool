package com.boredream.boreweibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.widget.WrapHeightImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageBrowserAdapter extends PagerAdapter {

	private Context context;
	private ArrayList<PicUrls> picUrls;
	private ImageLoader mImageLoader;

	public ImageBrowserAdapter(Context context, ArrayList<PicUrls> picUrls) {
		this.context = context;
		this.picUrls = picUrls;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
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
		WrapHeightImageView iv = new WrapHeightImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		iv.setLayoutParams(params);
		
		PicUrls url = picUrls.get(position % picUrls.size());
		mImageLoader.displayImage(url.getBmiddle_pic(), iv);
		container.addView(iv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		return iv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
