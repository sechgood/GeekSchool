package com.boredream.boreweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.StatusGridImgsAdapter;
import com.boredream.boreweibo.adapter.TabCommentAdapter;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.Comment;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.entity.response.CommentsResponse;
import com.boredream.boreweibo.utils.DateUtils;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.boredream.boreweibo.utils.StringUtils;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.widget.PinnedSectionListView;
import com.boredream.boreweibo.widget.PullToRefreshPSListView;
import com.boredream.boreweibo.widget.WrapHeightGridView;

public class StatusDetailActivity extends BaseActivity implements 
	OnClickListener, OnItemClickListener {

	private View status_detail_head;
	private ImageView iv_avatar;
	private RelativeLayout rl_content;
	private TextView tv_subhead;
	private TextView tv_body;
	private FrameLayout fl_imageview;
	private WrapHeightGridView gv_images;
	private ImageView iv_image;
	private TextView tv_content;
	private View include_retweeted_status;
	private TextView tv_retweeted_content;
	private FrameLayout fl_retweeted_imageview;
	private GridView gv_retweeted_images; 
	private ImageView iv_retweeted_image;
	private ImageView iv_location;
	private TextView tv_location;

	private PullToRefreshPSListView lv_comment;

	private LinearLayout ll_share_bottom;
	private ImageView iv_share_bottom;
	private TextView tv_share_bottom;
	private LinearLayout ll_comment_bottom;
	private ImageView iv_comment_bottom;
	private TextView tv_comment_bottom;
	private LinearLayout ll_like_bottom;
	private CheckBox cb_like_bottom;
	private TextView tv_like_bottom;

	private Status status;
	private List<Comment> comments = new ArrayList<Comment>();
	private TabCommentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_status_detail);

		status = (Status) intent.getSerializableExtra("status");

		initView();
		
		setData();
		
		loadComments();
	}

	private void initView() {
		new TitleBuilder(this)
				.setTitleText("微博正文")
				.setLeftImage(R.drawable.ic_launcher)
				.setLeftOnClickListener(this)
				.build();
		
		initDetailHead();
		initListView();
		initControlBar();
	}
	
	private void initDetailHead() {
		status_detail_head = View.inflate(this, R.layout.status_detail_head, null);
		iv_avatar = (ImageView) status_detail_head.findViewById(R.id.iv_avatar);
		rl_content = (RelativeLayout) status_detail_head.findViewById(R.id.rl_content);
		tv_subhead = (TextView) status_detail_head.findViewById(R.id.tv_subhead);
		tv_body = (TextView) status_detail_head.findViewById(R.id.tv_body);
		fl_imageview = (FrameLayout) status_detail_head.findViewById(R.id.fl_imageview);
		gv_images = (WrapHeightGridView) status_detail_head.findViewById(R.id.gv_images);
		iv_image = (ImageView) status_detail_head.findViewById(R.id.iv_image);
		tv_content = (TextView) status_detail_head.findViewById(R.id.tv_content);
		include_retweeted_status = status_detail_head.findViewById(R.id.include_retweeted_status);
		tv_retweeted_content = (TextView) status_detail_head.findViewById(R.id.tv_retweeted_content);
		fl_retweeted_imageview = (FrameLayout) status_detail_head.findViewById(R.id.fl_retweeted_imageview);
		gv_retweeted_images = (GridView) status_detail_head.findViewById(R.id.gv_retweeted_images);
		iv_retweeted_image = (ImageView) status_detail_head.findViewById(R.id.iv_retweeted_image);
		iv_location = (ImageView) status_detail_head.findViewById(R.id.iv_location);
		tv_location = (TextView) status_detail_head.findViewById(R.id.tv_location);
		gv_images.setOnItemClickListener(this);
		iv_image.setOnClickListener(this);
	}
	
	private void initListView() {
		lv_comment = (PullToRefreshPSListView) findViewById(R.id.lv_comment);
		PinnedSectionListView plv = lv_comment.getRefreshableView();
		adapter = new TabCommentAdapter(this, status, comments);
		lv_comment.setAdapter(adapter);
		plv.addHeaderView(status_detail_head);
		plv.setShadowVisible(false);
	}

	private void initControlBar() {
		ll_share_bottom = (LinearLayout) findViewById(R.id.ll_share_bottom);
		iv_share_bottom = (ImageView) findViewById(R.id.iv_share_bottom);
		tv_share_bottom = (TextView) findViewById(R.id.tv_share_bottom);
		ll_comment_bottom = (LinearLayout) findViewById(R.id.ll_comment_bottom);
		iv_comment_bottom = (ImageView) findViewById(R.id.iv_comment_bottom);
		tv_comment_bottom = (TextView) findViewById(R.id.tv_comment_bottom);
		ll_like_bottom = (LinearLayout) findViewById(R.id.ll_like_bottom);
		cb_like_bottom = (CheckBox) findViewById(R.id.cb_like_bottom);
		tv_like_bottom = (TextView) findViewById(R.id.tv_like_bottom);
		ll_share_bottom.setOnClickListener(this);
		ll_comment_bottom.setOnClickListener(this);
		ll_like_bottom.setOnClickListener(this);
	}

	private void setData() {
		// set data
		User user = status.getUser();
		imageLoader.displayImage(user.getProfile_image_url(), iv_avatar,
				ImageOptHelper.getAvatarOptions());
		tv_subhead.setText(user.getName());
		tv_body.setText(DateUtils.getShortTime(status.getCreated_at()) + 
				"  来自" + Html.fromHtml(status.getSource()));

		setImages(status, fl_imageview, gv_images, iv_image);
		
		if(TextUtils.isEmpty(status.getText())) {
			tv_content.setVisibility(View.GONE);
		} else {
			tv_content.setVisibility(View.VISIBLE);
			tv_content.setText(StringUtils.getWeiboContent(this, status.getText()));
			tv_content.setMovementMethod(new StringUtils.LinkTouchMovementMethod());
		}
		
		// retweeted
		Status retweetedStatus = status.getRetweeted_status();
		if(retweetedStatus != null) {
			include_retweeted_status.setVisibility(View.VISIBLE);
			String retweetContent = "@" + retweetedStatus.getUser().getName()
					+ ":" + retweetedStatus.getText();
			tv_retweeted_content.setText(StringUtils.getWeiboContent(
					this, retweetContent));
			setImages(retweetedStatus, fl_retweeted_imageview, 
					gv_retweeted_images, iv_retweeted_image);
		} else {
			include_retweeted_status.setVisibility(View.GONE);
		}
		
		// bottom bar
		tv_share_bottom.setText(status.getReposts_count() == 0 ?
				"转发" : status.getReposts_count()+"");
		cb_like_bottom.setChecked(status.isLiked());
		
		tv_comment_bottom.setText(status.getComments_count() == 0 ?
				"评论" : status.getComments_count()+"");
		
		tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
				"赞" : status.getAttitudes_count()+"");
	}

	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		ArrayList<PicUrls> picUrls = status.getPic_urls();
		String picUrl = status.getBmiddle_pic();
		
		if(picUrls.size() == 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);
			
			imageLoader.displayImage(picUrl, ivImg);
			
			ivImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(StatusDetailActivity.this, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", -1);
					startActivity(intent);
				}
			});
		} else if(picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);
			
			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(
					this, picUrls, gvImgs);
			gvImgs.setAdapter(imagesAdapter);
			
			gvImgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(StatusDetailActivity.this, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", position);
					startActivity(intent);
				}
			});
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}
	
	private void loadComments() {
		weiboApi.commentsShow(status.getId(), 1,
				new SimpleRequestListener(this, progressDialog) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);

						showLog("status comments = " + response);
						CommentsResponse loadComments = gson.fromJson(response, CommentsResponse.class);
						comments.addAll(loadComments.getComments());
						adapter.notifyDataSetChanged();
					}

				});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			break;
		case R.id.ll_share_bottom:
			break;
		case R.id.ll_comment_bottom:
			break;
		case R.id.ll_like_bottom:
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, ImageBrowserActivity.class);
		intent.putExtra("status", status);
		intent.putExtra("position", position);
		startActivity(intent);
	}

}
