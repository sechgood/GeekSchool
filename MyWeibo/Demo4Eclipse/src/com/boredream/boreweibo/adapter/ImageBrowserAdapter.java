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
import android.widget.ScrollView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;

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
		final ScrollView sv = (ScrollView) View.inflate(context, R.layout.item_image_brower, null);
		final ImageView iv_image_brower = (ImageView) sv.findViewById(R.id.iv_image_brower); 
		PicUrls url = picUrls.get(position % picUrls.size());
		
//		ImageSize minImageSize = new ImageSize(
//				DisplayUtils.getScreenWidthPixels(context), 
//				DisplayUtils.getScreenWidthPixels(context));
		mImageLoader.loadImage(url.getOriginal_pic(), 
//				minImageSize,
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
//				float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
//				int width = DisplayUtils.getScreenWidthPixels(context);
//				ImageView iv = new ImageView(context);
//				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//						width, (int) (width * scale));
//				iv.setLayoutParams(params);
//				iv.setImageBitmap(loadedImage);
//				sv.addView(iv);
				
				iv_image_brower.setImageBitmap(loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
		return sv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
