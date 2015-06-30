package com.boredream.boreweibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.EmotionGvAdapter;
import com.boredream.boreweibo.adapter.EmotionPagerAdapter;
import com.boredream.boreweibo.adapter.WriteStatusGridImgsAdapter;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.Emotion;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.utils.DialogUtils;
import com.boredream.boreweibo.utils.DisplayUtils;
import com.boredream.boreweibo.utils.ImageUtils;
import com.boredream.boreweibo.utils.StringUtils;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.widget.WrapHeightGridView;

public class WriteStatusActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private EditText et_write_status;
	private WrapHeightGridView gv_write_status;
	private View include_retweeted_status_card;
	private ImageView iv_rstatus_img;;
	private TextView tv_rstatus_username;;
	private TextView tv_rstatus_content;;
	
	private ImageView iv_image;
	private ImageView iv_at;
	private ImageView iv_topic;
	private ImageView iv_emoji;
	private ImageView iv_add;

	private LinearLayout ll_emotion_dashboard;
	private ViewPager vp_emotion_dashboard;
	private RadioGroup rg_emotion_dashboard;
	private RadioButton rb_emotion_dashboard_recently;
	private RadioButton rb_emotion_dashboard_default;
	private RadioButton rb_emotion_dashboard_emoji;
	private RadioButton rb_emotion_dashboard_langxiaohua;

	private WriteStatusGridImgsAdapter statusImgsAdapter;
	private ArrayList<Uri> imgUris = new ArrayList<Uri>();
	private EmotionPagerAdapter emotionPagerGvAdapter;
	
	// 被转发的微博
	private Status retweeted_status;
	// 需要转发的实际微博内容(是原微博信息还是转发信息)
	private Status card_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write_status);

		retweeted_status = (Status) getIntent().getSerializableExtra("status");
		
		initView();
	}

	private void initView() {
		new TitleBuilder(this)
				.setTitleText("发微博")
				.setLeftText("取消")
				.setLeftOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						WriteStatusActivity.this.finish();
					}
				})
				.setRightText("发送")
				.setRightOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						uploadStatus();
					}
				})
				.build();

		et_write_status = (EditText) findViewById(R.id.et_write_status);
		gv_write_status = (WrapHeightGridView) findViewById(R.id.gv_write_status);
		include_retweeted_status_card = findViewById(R.id.include_retweeted_status_card);
		iv_rstatus_img = (ImageView) findViewById(R.id.iv_rstatus_img);
		tv_rstatus_username = (TextView) findViewById(R.id.tv_rstatus_username);
		tv_rstatus_content = (TextView) findViewById(R.id.tv_rstatus_content);
		
		iv_image = (ImageView) findViewById(R.id.iv_image);
		iv_at = (ImageView) findViewById(R.id.iv_at);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
		iv_add = (ImageView) findViewById(R.id.iv_add);

		ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
		vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
		rg_emotion_dashboard = (RadioGroup) findViewById(R.id.rg_emotion_dashboard);
		rb_emotion_dashboard_recently = (RadioButton) findViewById(R.id.rb_emotion_dashboard_recently);
		rb_emotion_dashboard_default = (RadioButton) findViewById(R.id.rb_emotion_dashboard_default);
		rb_emotion_dashboard_emoji = (RadioButton) findViewById(R.id.rb_emotion_dashboard_emoji);
		rb_emotion_dashboard_langxiaohua = (RadioButton) findViewById(R.id.rb_emotion_dashboard_langxiaohua);

		statusImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
		gv_write_status.setAdapter(statusImgsAdapter);
		gv_write_status.setOnItemClickListener(this);

		iv_image.setOnClickListener(this);
		iv_at.setOnClickListener(this);
		iv_topic.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_add.setOnClickListener(this);

		initEmotion();
		initRetweetedStatus();
	}

	private void initEmotion() {
		List<GridView> gvs = new ArrayList<GridView>();
		List<String> emotionNames = null;

		int gvWidth = DisplayUtils.getScreenWidthPixels(this);
		int padding = DisplayUtils.dp2px(this, 8);

		int itemWidth = (gvWidth - padding * 8) / 7;
		int gvHeight = itemWidth * 3 + 4 * padding;

		for (Map.Entry<String, Integer> entry : Emotion.emojiMap.entrySet()) {
			if (emotionNames == null) {
				emotionNames = new ArrayList<String>();
			}

			emotionNames.add(entry.getKey());

			if (emotionNames.size() == 20) {
				createEmotionGridView(gvs, emotionNames, gvWidth, padding, itemWidth, gvHeight);
				emotionNames = null;
			}
		}

		if (emotionNames != null) {
			createEmotionGridView(gvs, emotionNames, gvWidth, padding, itemWidth, gvHeight);
			emotionNames = null;
		}

		emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
		vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
		vp_emotion_dashboard.setLayoutParams(params);
	}
	
	private void initRetweetedStatus() {
		// 转发微博特殊处理
		if(retweeted_status != null) {
			// 转发的微博是否包含转发内容
			Status rrStatus = retweeted_status.getRetweeted_status();
			if(rrStatus != null) {
				String content = "//@" + retweeted_status.getUser().getName() 
						+ ":" + retweeted_status.getText();
				et_write_status.setText(StringUtils.getWeiboContent(this, et_write_status, content, true));
				
				card_status = rrStatus;
			} else {
				card_status = retweeted_status;
			}
			
			imageLoader.displayImage(card_status.getThumbnail_pic(), iv_rstatus_img);
			tv_rstatus_username.setText("@" + card_status.getUser().getName());
			tv_rstatus_content.setText(card_status.getText());
			
			iv_image.setVisibility(View.GONE);
			include_retweeted_status_card.setVisibility(View.VISIBLE);
		}
	}

	private void uploadStatus() {
		String imgFilePath = null;
		if (imgUris.size() > 0) {
			Uri uri = imgUris.get(0);
			imgFilePath = ImageUtils.getImageAbsolutePath(this, uri);
		}

		weiboApi.statusesUpload(et_write_status.getText().toString(), imgFilePath,
				new SimpleRequestListener(this, progressDialog) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						showToast("微博发送成功");
						WriteStatusActivity.this.finish();
					}
				});
	}

	private void createEmotionGridView(List<GridView> gvs, List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setPadding(padding, padding, padding, padding);
		gv.setHorizontalSpacing(padding);
		gv.setVerticalSpacing(padding);
		LayoutParams params = new LayoutParams(gvWidth, gvHeight);
		gv.setLayoutParams(params);

		EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(this);
		gvs.add(gv);
	}

	private void updateImgs() {
		gv_write_status.setVisibility(imgUris.size() == 0 ? View.GONE : View.VISIBLE);
		statusImgsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			DialogUtils.showImagePickDialog(this);
			break;
		case R.id.iv_at:
			break;
		case R.id.iv_topic:
			break;
		case R.id.iv_emoji:
			ll_emotion_dashboard.setVisibility(
					ll_emotion_dashboard.getVisibility() == View.VISIBLE ?
							View.GONE : View.VISIBLE);
			break;
		case R.id.iv_add:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object itemAdapter = parent.getAdapter();

		if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
			if (position < statusImgsAdapter.getCount() - 1) {

			} else {
				DialogUtils.showImagePickDialog(this);
			}
		} else if (itemAdapter instanceof EmotionGvAdapter) {
			EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

			if (position == emotionGvAdapter.getCount() - 1) {
				et_write_status.dispatchKeyEvent(new KeyEvent(
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			} else {
				String emotionName = emotionGvAdapter.getItem(position);

				int curPosition = et_write_status.getSelectionStart();
				StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
				sb.insert(curPosition, emotionName);

				et_write_status.setText(StringUtils.getWeiboContent(
						this, et_write_status, sb.toString()));
				et_write_status.setSelection(curPosition + emotionName.length());
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		switch (requestCode) {
		case ImageUtils.GET_IMAGE_BY_CAMERA:
			imgUris.add(ImageUtils.imageUriFromCamera);
			updateImgs();
			break;
		case ImageUtils.GET_IMAGE_FROM_PHONE:
			imgUris.add(data.getData());
			updateImgs();
			break;

		default:
			break;
		}
	}

}
