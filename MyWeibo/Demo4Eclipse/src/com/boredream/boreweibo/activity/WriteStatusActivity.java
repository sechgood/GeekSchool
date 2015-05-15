package com.boredream.boreweibo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.WriteStatusGridImgsAdapter;
import com.boredream.boreweibo.api.BoreWeiboAPI;
import com.boredream.boreweibo.constants.AccessTokenKeeper;
import com.boredream.boreweibo.constants.URLs;
import com.boredream.boreweibo.utils.DialogUtils;
import com.boredream.boreweibo.utils.ImageUtils;
import com.boredream.boreweibo.widget.WrapHeightGridView;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;

public class WriteStatusActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private EditText et_write_status;
	private WrapHeightGridView gv_write_status;
	private ImageView iv_image;
	private ImageView iv_at;
	private ImageView iv_topic;
	private ImageView iv_emoji;
	private ImageView iv_add;

	private WriteStatusGridImgsAdapter adapter;
	private ArrayList<Uri> imgUris = new ArrayList<Uri>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write_status);

		initView();

	}

	private void initView() {
		et_write_status = (EditText) findViewById(R.id.et_write_status);
		gv_write_status = (WrapHeightGridView) findViewById(R.id.gv_write_status);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		iv_at = (ImageView) findViewById(R.id.iv_at);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
		iv_add = (ImageView) findViewById(R.id.iv_add);

		adapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
		gv_write_status.setAdapter(adapter);
		gv_write_status.setOnItemClickListener(this);

		iv_image.setOnClickListener(this);
		iv_at.setOnClickListener(this);
		iv_topic.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_add.setOnClickListener(this);
	}

	private void updateImgs() {

		gv_write_status.setVisibility(imgUris.size() == 0 ? View.GONE : View.VISIBLE);
		adapter.notifyDataSetChanged();
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
			break;
		case R.id.iv_add:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position < adapter.getCount() - 1) {
			
		} else {
			DialogUtils.showImagePickDialog(this);
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
