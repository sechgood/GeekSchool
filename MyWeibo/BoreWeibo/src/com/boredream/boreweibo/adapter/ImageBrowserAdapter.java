package com.boredream.boreweibo.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.BrowserPic;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageBrowserAdapter extends PagerAdapter {

	private Activity context;
	private ArrayList<BrowserPic> pics;
	private ArrayList<View> picViews;
	private ImageLoader mImageLoader;

	public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
		this.context = context;
		this.mImageLoader = ImageLoader.getInstance();
		initImgs(picUrls);
	}

	private void initImgs(ArrayList<PicUrls> picUrls) {
		pics = new ArrayList<BrowserPic>();
		picViews = new ArrayList<View>();
		
		// 将图片信息进行封装,主要是添加是否显示原图属性showOriginalPic
		BrowserPic browserPic;
		for(PicUrls picUrl : picUrls) {
			browserPic = new BrowserPic();
			browserPic.setPic(picUrl);
			browserPic.setShowOriginalPic(false);
			pics.add(browserPic);
			// 填充显示图片的页面布局
			View view = View.inflate(context, R.layout.item_image_browser, null);
			picViews.add(view);
		}
	}
	
	public BrowserPic getBrowserPic(int position) {
		return pics.get(position % pics.size());
	}
	
	public Bitmap getBitmap(int position) {
		Bitmap bitmap = null;
		
		ImageView iv_image_browser = (ImageView) picViews.get(position % picViews.size())
				.findViewById(R.id.iv_image_browser);
		// 获取drawable数据
		Drawable drawable = iv_image_browser.getDrawable();
		
		// 判断图片里的drawable是否为空且是否可以转成bitmap对象
		if(drawable != null && drawable instanceof BitmapDrawable) {
			bitmap = ((BitmapDrawable)drawable).getBitmap();
		}
		return bitmap;
	}

	@Override
	public int getCount() {
		// 图片大于1张时,无限大小即无限轮播
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
		// position为处理后的值,需要用%获取
		View view = picViews.get(position % pics.size());
		
		final ImageView iv_image_browser = (ImageView) view.findViewById(R.id.iv_image_browser);
		// 获取图片数据对象
		final BrowserPic browserPic = pics.get(position % pics.size());
		PicUrls picUrls = browserPic.getPic();
		// 根据变量判断是需要显示原图还是中等图
		String url = browserPic.isShowOriginalPic() ?
				picUrls.getOriginal_pic() : picUrls.getBmiddle_pic();

		// loadImage方法加载图片,图片设置需要在监听中自己处理
		mImageLoader.loadImage(url, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				int screenHeight = DisplayUtils.getScreenHeightPixels(context);
				int screenWidth = DisplayUtils.getScreenWidthPixels(context);
				
				// 服务器返回的图片高度宽度比
				float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
				// 利用比例算出宽度为全屏的ImageView需要的高度
				int height = (int) (screenWidth * scale);
				// 高度不满全屏时,让其全屏显示,由于是fitCenter所以图片会纵向上居中显示
				if(height < screenHeight) {
					height = screenHeight;
				}
				
				// 将计算出的高度设置给ImageView
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
						iv_image_browser.getLayoutParams();
				params.height = height;
				params.width = screenWidth;
				// 设置图片
				iv_image_browser.setImageBitmap(loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
		});
		
		iv_image_browser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.finish();
			}
		});
		
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		// 用于解决notifyDataSetChanged()更新vp的问题
		return POSITION_NONE;
	}
}