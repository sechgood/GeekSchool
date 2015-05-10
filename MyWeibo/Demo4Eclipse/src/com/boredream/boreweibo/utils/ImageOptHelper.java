package com.boredream.boreweibo.utils;

import android.graphics.Bitmap;

import com.boredream.boreweibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageOptHelper {
	
	public static DisplayImageOptions getImgOptions() {
		DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
			.cacheOnDisc()
			.cacheInMemory()
			.showStubImage(R.drawable.ic_launcher)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher)
			.displayer(new RoundedBitmapDisplayer(4)).build();
		return imgOptions;
	}
	
	
	public static DisplayImageOptions getAvatarOptions() {
		DisplayImageOptions	avatarOptions = new DisplayImageOptions.Builder()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisc()
			.cacheInMemory()
			.showStubImage(R.drawable.ic_launcher)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher)
			.displayer(new RoundedBitmapDisplayer(999))
			.build();
		return avatarOptions;
	}
	
	
	public static DisplayImageOptions getCornerOptions(int cornerRadiusPixels) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisc()
			.cacheInMemory()
			.showStubImage(R.drawable.ic_launcher)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher)
			.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels)).build();
		return options;
	}
	
}
