package com.boredream.boreweibo.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.BrowserPic;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageBrowserActivity extends BaseActivity implements OnClickListener {
	private ViewPager vp_image_brower;
	private TextView tv_image_index;
	private Button btn_save;
	private Button btn_original_image;

	private Status status;
	private ImageBrowserAdapter adapter;
	private ArrayList<PicUrls> imgUrls;
	private int initPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_brower);
		
		initData();
		initView();
		setData();
	}

	private void initData() {
		status = (Status) getIntent().getSerializableExtra("status");
		initPosition = getIntent().getIntExtra("position", 0);
		// 获取图片数据集合(单图也有对应的集合,集合的size为1)
		imgUrls = status.getPic_urls();
	}

	private void initView() {
		vp_image_brower = (ViewPager) findViewById(R.id.vp_image_brower);
		tv_image_index = (TextView) findViewById(R.id.tv_image_index);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_original_image = (Button) findViewById(R.id.btn_original_image);

		btn_save.setOnClickListener(this);
		btn_original_image.setOnClickListener(this);
	}
	
	private void setData() {
		// 单图则不显示位置索引了
		tv_image_index.setVisibility(imgUrls.size() > 1 ? View.VISIBLE : View.GONE);
		
		adapter = new ImageBrowserAdapter(this, imgUrls);
		vp_image_brower.setAdapter(adapter);
		
		// 设置初始化位置的索引文字和vp的当前item
		final int size = status.getPic_urls().size();
		tv_image_index.setText((initPosition + 1) + "/" + status.getPic_urls().size());
		vp_image_brower.setCurrentItem(Integer.MAX_VALUE / size / 2 * size + initPosition);
		
		// 设置vp切换页时的监听
		vp_image_brower.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 显示当前图片索引文字
				tv_image_index.setText((position % size + 1) + "/" + status.getPic_urls().size());
				// 是否需要显示原图按钮
				BrowserPic pic = adapter.getBrowserPic(position);
				btn_original_image.setVisibility(pic.isShowOriginalPic() ? View.GONE : View.VISIBLE);
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
		BrowserPic getBrowserPic = adapter.getBrowserPic(vp_image_brower.getCurrentItem());
		switch (v.getId()) {
		case R.id.btn_save:
			Bitmap bitmap = adapter.getBitmap(vp_image_brower.getCurrentItem());
			
			// 图片名称为 "img-图片质量-图片"
			String fileName = "img-" + (getBrowserPic.isShowOriginalPic() ? 
					"ori-" + getBrowserPic.getPic().getImageId() :
					"mid-" + getBrowserPic.getPic().getImageId());
			
			// 系统封装的图片保存方法
			String insertImage = MediaStore.Images.Media.insertImage(getContentResolver(), 
					bitmap, fileName.substring(0, fileName.lastIndexOf(".")), "BoreWeiboImg");
			if(insertImage == null) {
				showToast("图片保存失败");
			} else {
				showToast("图片保存成功");
			}
			
			// 自定义的图片保存方法
//			try {
//				ImageUtils.saveFile(this, bitmap, fileName);
//				showToast("图片保存成功");
//			} catch (IOException e) {
//				e.printStackTrace();
//				showToast("图片保存失败");
//			}
			break;
		case R.id.btn_original_image:
			// 设置需要显示原图标志位,并更新视图
			getBrowserPic.setShowOriginalPic(true);
			adapter.notifyDataSetChanged();
			
			btn_original_image.setVisibility(View.GONE);
			break;
		}
	}
	
	private class ImageBrowserAdapter extends PagerAdapter {

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

}
