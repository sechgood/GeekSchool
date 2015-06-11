package com.boredream.boreweibo.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ImageBrowserAdapter extends PagerAdapter {

	private Activity context;
	private ArrayList<PicUrls> picUrls;
	private ImageLoader mImageLoader;

	public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
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
		ScrollView sv = new ScrollView(context);
		FrameLayout.LayoutParams svParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, 
				FrameLayout.LayoutParams.MATCH_PARENT);
		sv.setLayoutParams(svParams);
		
		LinearLayout ll = new LinearLayout(context);
		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(llParams);
		sv.addView(ll);
		
		final int screenHeight = DisplayUtils.getScreenHeightPixels(context);
		final int screenWidth = DisplayUtils.getScreenWidthPixels(context);
		
		final ImageView iv = new ImageView(context);
		iv.setScaleType(ScaleType.FIT_CENTER);
		ll.addView(iv);
		
		PicUrls url = picUrls.get(position % picUrls.size());
		
		mImageLoader.loadImage(url.getOriginal_pic(), 
				new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
				int height = Math.max((int) (screenWidth * scale), screenHeight);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
				iv.setLayoutParams(params);
				iv.setImageBitmap(loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
		container.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		return sv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
