package com.boredream.boreweibo.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.ImageBrowserAdapter;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;

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
		position = intent.getIntExtra("position", position);
		if(position == -1) {
			imgUrls = new ArrayList<PicUrls>();
			PicUrls url = new PicUrls();
			url.setThumbnail_pic(status.getThumbnail_pic());
			url.setOriginal_pic(status.getOriginal_pic());
			imgUrls.add(url);
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
		switch (v.getId()) {
		case R.id.btn_save:

			break;
		case R.id.btn_original_image:

			break;
		}
	}

}
