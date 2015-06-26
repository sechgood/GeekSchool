package com.boredream.boreweibo.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.BrowserPic;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.boredream.boreweibo.utils.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageBrowserActivity extends BaseActivity implements OnClickListener {
	private ViewPager vp_image_brower;
	private TextView tv_image_index;
	private Button btn_save;
	private Button btn_original_image;
	private TextView tv_like;

	private Status status;
	private ImageBrowserAdapter adapter;
	private ArrayList<PicUrls> imgUrls;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_brower);
		initData();
		initView();
		setData();
	}

	private void initData() {
		status = (Status) intent.getSerializableExtra("status");
		position = intent.getIntExtra("position", -1);
		if(position == -1) {
			imgUrls = new ArrayList<PicUrls>();
			PicUrls url = new PicUrls();
			url.setThumbnail_pic(status.getThumbnail_pic());
			url.setOriginal_pic(status.getOriginal_pic());
			imgUrls.add(url);
			position = 0;
		} else {
			imgUrls = status.getPic_urls();
		}
	}

	private void initView() {
		vp_image_brower = (ViewPager) findViewById(R.id.vp_image_brower);
		tv_image_index = (TextView) findViewById(R.id.tv_image_index);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_original_image = (Button) findViewById(R.id.btn_original_image);
		tv_like = (TextView) findViewById(R.id.tv_like);

		btn_save.setOnClickListener(this);
		btn_original_image.setOnClickListener(this);
	}
	
	private void setData() {
		tv_image_index.setVisibility(imgUrls.size() > 1 ? View.VISIBLE : View.GONE);
		
		adapter = new ImageBrowserAdapter(this, imgUrls);
		vp_image_brower.setAdapter(adapter);
		
		final int size = status.getPic_urls().size();
		tv_image_index.setText((position % size + 1) + "/" + status.getPic_urls().size());
		vp_image_brower.setCurrentItem(Integer.MAX_VALUE / size / 2 * size + position);
		vp_image_brower.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tv_image_index.setText((arg0 % size + 1) + "/" + status.getPic_urls().size());
				BrowserPic pic = adapter.getPic(arg0 % size);
				btn_original_image.setVisibility(pic.isOriginalPic() ? View.GONE : View.VISIBLE);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		BrowserPic pic = adapter.getPic(vp_image_brower.getCurrentItem() % status.getPic_urls().size());
		switch (v.getId()) {
		case R.id.btn_save:
			Bitmap bitmap = pic.getBitmap();
			PicUrls picUrl = pic.getPic();
			String oriUrl = picUrl.getOriginal_pic();
			String midUrl = picUrl.getBmiddle_pic();
			String fileName = "img-" + (pic.isOriginalPic() ? 
					"ori-" + oriUrl.substring(oriUrl.lastIndexOf("/") + 1)
					: "mid-" + midUrl.substring(midUrl.lastIndexOf("/") + 1));
			if(bitmap != null) {
				try {
					ImageUtils.saveFile(this, bitmap, fileName);
					showToast("图片保存成功");
				} catch (IOException e) {
					e.printStackTrace();
					showToast("图片保存失败");
				}
			}
			break;
		case R.id.btn_original_image:
			pic.setOriginalPic(true);
			adapter.notifyDataSetChanged();
			btn_original_image.setVisibility(View.GONE);
			break;
		}
	}
	
	private class ImageBrowserAdapter extends PagerAdapter {

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
				browserPic.setOriginalPic(oBm != null || 
						(discCache != null && discCache.exists() && discCache.length() > 0));
				if(oBm != null) {
					oBm.recycle();
				}
				pics.add(browserPic);
			}
			
			btn_original_image.setVisibility(pics.get(position).isOriginalPic() ? View.GONE : View.VISIBLE);
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

}
