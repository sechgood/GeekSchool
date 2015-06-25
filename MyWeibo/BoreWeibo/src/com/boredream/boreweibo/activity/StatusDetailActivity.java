package com.boredream.boreweibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.adapter.StatusCommentAdapter;
import com.boredream.boreweibo.adapter.StatusGridImgsAdapter;
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
import com.boredream.boreweibo.widget.WrapHeightGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class StatusDetailActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private static final int REQUEST_CODE_WRITE_COMMENT = 2333;

	private RelativeLayout rl_body;
	
	private View status_detail_head;
	private ImageView iv_avatar;
	private TextView tv_subhead;
	private TextView tv_caption;
	private FrameLayout include_status_image;
	private WrapHeightGridView gv_images;
	private ImageView iv_image;
	private TextView tv_content;
	private View include_retweeted_status;
	private TextView tv_retweeted_content;
	private FrameLayout fl_retweeted_imageview;
	private GridView gv_retweeted_images;
	private ImageView iv_retweeted_image;

	private PullToRefreshListView lv_comment;
	private View footView;

	private View shadow_status_detail_tab;
	private RadioGroup shadow_rg_status_detail;
	private RadioButton shadow_rb_retweets;
	private RadioButton shadow_rb_comments;
	private RadioButton shadow_rb_likes;
	private View status_detail_tab;
	private RadioGroup rg_status_detail;
	private RadioButton rb_retweets;
	private RadioButton rb_comments;
	private RadioButton rb_likes;

	private LinearLayout ll_bottom_control;
	private LinearLayout ll_share_bottom;
	private ImageView iv_share_bottom;
	private TextView tv_share_bottom;
	private LinearLayout ll_comment_bottom;
	private ImageView iv_comment_bottom;
	private TextView tv_comment_bottom;
	private LinearLayout ll_like_bottom;
	private ImageView iv_like_bottom;
	private TextView tv_like_bottom;

	private Status status;
	private boolean scroll2Comment;
	private List<Comment> comments = new ArrayList<Comment>();
	private StatusCommentAdapter adapter;
	private long curPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_status_detail);

		status = (Status) getIntent().getSerializableExtra("status");
		scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);

		initView();

		setData();
		
		// start load data
		addFootView(lv_comment, footView);
		loadComments(1);
	}

	private void initView() {
		new TitleBuilder(this)
				.setTitleText("微博正文")
				.setLeftImage(R.drawable.navigationbar_back_sel)
				.setLeftOnClickListener(this);

		rl_body = (RelativeLayout) findViewById(R.id.rl_body);
		
		initDetailHead();
		initTab();
		initListView();
		initControlBar();
	}

	private void initDetailHead() {
		status_detail_head = View.inflate(this, R.layout.item_status, null);
		status_detail_head.setBackgroundResource(R.color.white);
		status_detail_head.findViewById(R.id.ll_bottom_control).setVisibility(View.GONE);
		iv_avatar = (ImageView) status_detail_head.findViewById(R.id.iv_avatar);
		tv_subhead = (TextView) status_detail_head.findViewById(R.id.tv_subhead);
		tv_caption = (TextView) status_detail_head.findViewById(R.id.tv_caption);
		include_status_image = (FrameLayout) status_detail_head.findViewById(R.id.include_status_image);
		gv_images = (WrapHeightGridView) status_detail_head.findViewById(R.id.gv_images);
		iv_image = (ImageView) status_detail_head.findViewById(R.id.iv_image);
		tv_content = (TextView) status_detail_head.findViewById(R.id.tv_content);
		include_retweeted_status = status_detail_head.findViewById(R.id.include_retweeted_status);
		tv_retweeted_content = (TextView) status_detail_head.findViewById(R.id.tv_retweeted_content);
		fl_retweeted_imageview = (FrameLayout) include_retweeted_status.findViewById(R.id.include_status_image);
		gv_retweeted_images = (GridView) fl_retweeted_imageview.findViewById(R.id.gv_images);
		iv_retweeted_image = (ImageView) fl_retweeted_imageview.findViewById(R.id.iv_image);
		iv_image.setOnClickListener(this);
	}

	private void initTab() {
		shadow_status_detail_tab = findViewById(R.id.status_detail_tab);
		shadow_rg_status_detail = (RadioGroup) shadow_status_detail_tab
				.findViewById(R.id.rg_status_detail);
		shadow_rb_retweets = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_retweets);
		shadow_rb_comments = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_comments);
		shadow_rb_likes = (RadioButton) shadow_status_detail_tab
				.findViewById(R.id.rb_likes);
		shadow_rg_status_detail.setOnCheckedChangeListener(this);
		shadow_rb_retweets.setText("转发 " + status.getReposts_count());
		shadow_rb_comments.setText("评论 " + status.getComments_count());
		shadow_rb_likes.setText("赞 " + status.getAttitudes_count());

		status_detail_tab = View.inflate(this, R.layout.status_detail_tab, null);
		rg_status_detail = (RadioGroup) status_detail_tab
				.findViewById(R.id.rg_status_detail);
		rb_retweets = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_retweets);
		rb_comments = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_comments);
		rb_likes = (RadioButton) status_detail_tab
				.findViewById(R.id.rb_likes);
		rg_status_detail.setOnCheckedChangeListener(this);
		rb_retweets.setText("转发 " + status.getReposts_count());
		rb_comments.setText("评论 " + status.getComments_count());
		rb_likes.setText("赞 " + status.getAttitudes_count());
	}

	private void initListView() {
		// 下拉刷新控件
		lv_comment = (PullToRefreshListView) findViewById(R.id.lv_comment);
		adapter = new StatusCommentAdapter(this, comments);
		lv_comment.setAdapter(adapter);
		// footView加载更多
		footView = View.inflate(this, R.layout.footview_loading, null);
		// 刷新的View - ListView
		final ListView lv = lv_comment.getRefreshableView();
		lv.addHeaderView(status_detail_head);
		lv.addHeaderView(status_detail_tab);
		// 设置各类监听
		lv_comment.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadComments(1);
			}
		});
		lv_comment.setOnLastItemVisibleListener(
				new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						loadComments(curPage + 1);
					}
				});
		lv_comment.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 0-pullHead 1-detailHead 2-tab
				shadow_status_detail_tab.setVisibility(firstVisibleItem >= 2 ?
						View.VISIBLE : View.GONE);
			}
		});
	}

	private void initControlBar() {
		ll_bottom_control = (LinearLayout) findViewById(R.id.status_detail_controlbar);
		ll_share_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_share_bottom);
		iv_share_bottom = (ImageView) ll_bottom_control.findViewById(R.id.iv_share_bottom);
		tv_share_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_share_bottom);
		ll_comment_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_comment_bottom);
		iv_comment_bottom = (ImageView) ll_bottom_control.findViewById(R.id.iv_comment_bottom);
		tv_comment_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_comment_bottom);
		ll_like_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_like_bottom);
		iv_like_bottom = (ImageView) ll_bottom_control.findViewById(R.id.iv_like_bottom);
		tv_like_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_like_bottom);
		ll_bottom_control.setBackgroundResource(R.color.white);
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
		tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) +
				"  来自" + Html.fromHtml(status.getSource()));

		setImages(status, include_status_image, gv_images, iv_image);

		if (TextUtils.isEmpty(status.getText())) {
			tv_content.setVisibility(View.GONE);
		} else {
			tv_content.setVisibility(View.VISIBLE);
			SpannableString weiboContent = StringUtils.getWeiboContent(
					this, tv_content, status.getText());
			tv_content.setText(weiboContent);
		}

		// retweeted
		Status retweetedStatus = status.getRetweeted_status();
		if (retweetedStatus != null) {
			include_retweeted_status.setVisibility(View.VISIBLE);
			String retweetContent = "@" + retweetedStatus.getUser().getName()
					+ ":" + retweetedStatus.getText();
			SpannableString weiboContent = StringUtils.getWeiboContent(
					this, tv_retweeted_content, retweetContent);
			tv_retweeted_content.setText(weiboContent);
			setImages(retweetedStatus, fl_retweeted_imageview,
					gv_retweeted_images, iv_retweeted_image);
		} else {
			include_retweeted_status.setVisibility(View.GONE);
		}

		// bottom bar
		tv_share_bottom.setText(status.getReposts_count() == 0 ?
				"转发" : status.getReposts_count() + "");

		tv_comment_bottom.setText(status.getComments_count() == 0 ?
				"评论" : status.getComments_count() + "");

		tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
				"赞" : status.getAttitudes_count() + "");
	}

	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		if (status == null) {
			return;
		}

		ArrayList<PicUrls> picUrls = status.getPic_urls();
		String picUrl = status.getBmiddle_pic();

		if (picUrls != null && picUrls.size() == 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);

			imageLoader.displayImage(picUrl, ivImg);
		} else if (picUrls != null && picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);

			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(this, picUrls);
			gvImgs.setAdapter(imagesAdapter);
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}

	private void loadComments(final long requestPage) {
		weiboApi.commentsShow(status.getId(), requestPage,
				new SimpleRequestListener(this, null) {

					@Override
					public void onComplete(String response) {
						super.onComplete(response);

						showLog("status comments = " + response);

						if (requestPage == 1) {
							comments.clear();
						}

						
						CommentsResponse commentsResponse = gson.fromJson(response, CommentsResponse.class);
						tv_comment_bottom.setText(commentsResponse.getTotal_number() == 0 ? 
								"评论" : commentsResponse.getTotal_number() + "");
						addData(commentsResponse);
						
						if(scroll2Comment) {
							lv_comment.getRefreshableView().setSelection(2);
							scroll2Comment = false;
						}
					}

					@Override
					public void onAllDone() {
						super.onAllDone();

						lv_comment.onRefreshComplete();
					}

				});
	}

	private void addData(CommentsResponse response) {
		for (Comment comment : response.getComments()) {
			if (!comments.contains(comment)) {
				comments.add(comment);
			}
		}
		adapter.notifyDataSetChanged();
		
		if (comments.size() < response.getTotal_number()) {
			addFootView(lv_comment, footView);
		} else {
			removeFootView(lv_comment, footView);
		}
	}

	private void addFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if (lv.getFooterViewsCount() == 1) {
			lv.addFooterView(footView);
		}
	}

	private void removeFootView(PullToRefreshListView plv, View footView) {
		ListView lv = plv.getRefreshableView();
		if (lv.getFooterViewsCount() > 1) {
			lv.removeFooterView(footView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_iv_left:
			StatusDetailActivity.this.finish();
			break;
		case R.id.iv_image:
			break;
		case R.id.ll_share_bottom:
			break;
		case R.id.ll_comment_bottom:
			Intent intent = new Intent(this, WriteCommentActivity.class);
			intent.putExtra("status", status);
			startActivityForResult(intent, REQUEST_CODE_WRITE_COMMENT);
			break;
		case R.id.ll_like_bottom:
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.rb_retweets:
			rb_retweets.setChecked(true);
			shadow_rb_retweets.setChecked(true);

			break;
		case R.id.rb_comments:
			rb_comments.setChecked(true);
			shadow_rb_comments.setChecked(true);

			break;
		case R.id.rb_likes:
			rb_likes.setChecked(true);
			shadow_rb_likes.setChecked(true);

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_CANCELED) {
			return;
		}
		
		switch (requestCode) {
		case REQUEST_CODE_WRITE_COMMENT:
			boolean sendCommentSuccess = data.getBooleanExtra("sendCommentSuccess", false);
			if(sendCommentSuccess) {
				scroll2Comment = true;
				loadComments(1);
			}
			break;

		default:
			break;
		}
	}
	
}
