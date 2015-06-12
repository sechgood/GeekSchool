package com.boredream.boreweibo.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.boredream.boreweibo.entity.BrowserPic;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.utils.DialogUtils;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

public class ImageBrowserAdapter extends PagerAdapter {

	private Activity context;
	private ArrayList<BrowserPic> pics;
	private ImageLoader mImageLoader;

	public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
		this.context = context;
		this.mImageLoader = ImageLoader.getInstance();
		initImgs(picUrls);
	}

	private void initImgs(ArrayList<PicUrls> picUrls) {
		pics = new ArrayList<BrowserPic>();
		BrowserPic browserPic;
		for(PicUrls picUrl : picUrls) {
			browserPic = new BrowserPic();
			browserPic.setPic(picUrl);
			Bitmap oBm = mImageLoader.getMemoryCache().get(picUrl.getOriginal_pic());
			File discCache = mImageLoader.getDiscCache().get(picUrl.getOriginal_pic());
			if(oBm != null || discCache != null) {
				browserPic.setOriginalPic(true);
				oBm.recycle();
			}
			pics.add(browserPic);
		}
	}
	
	public BrowserPic getPic(int position) {
		return pics.get(position);
	}

	@Override
	public int getCount() {
		if (pics.size() > 1) {
			return Integer.MAX_VALUE;
		}
		return pics.size();
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
		
		final BrowserPic browserPic = pics.get(position % pics.size());
		PicUrls picUrls = browserPic.getPic();
		
		String url = browserPic.isOriginalPic() ? picUrls.getOriginal_pic() : picUrls.getBmiddle_pic();
		
		mImageLoader.loadImage(url, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				browserPic.setBitmap(loadedImage);
				
				float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
				int height = Math.max((int) (screenWidth * scale), screenHeight);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
				iv.setLayoutParams(params);
				iv.setImageBitmap(loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
		});
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.finish();
			}
		});
		container.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		return sv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
}
