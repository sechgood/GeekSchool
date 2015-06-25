package com.boredream.boreweibo.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.boredream.boreweibo.utils.DisplayUtils;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.boredream.boreweibo.utils.StringUtils;
import com.boredream.boreweibo.utils.TitleBuilder;
import com.boredream.boreweibo.widget.Pull2RefreshScrollView;
import com.boredream.boreweibo.widget.Pull2RefreshScrollView.OnScrollChangeListener;
import com.boredream.boreweibo.widget.WrapHeightGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class ScrollStatusDetailActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private ImageView iv_avatar;
	private RelativeLayout rl_content;
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

	private Pull2RefreshScrollView psv_status_detail;
	private ListView lv_comment;
	private View footView;

	private LinearLayout status_detail_tab;
	private RadioGroup shadow_rg_status_detail;
	private RadioButton shadow_rb_retweets;
	private RadioButton shadow_rb_comments;
	private RadioButton shadow_rb_likes;

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
	private boolean isComment;
	private List<Comment> comments = new ArrayList<Comment>();
	private StatusCommentAdapter adapter;
	private long curPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_status_scroll_detail);

		status = (Status) getIntent().getSerializableExtra("status");
		isComment = getIntent().getBooleanExtra("isComment", false);

		initView();

		setData();

		loadComments(1);
	}

	private void initView() {
		new TitleBuilder(this)
				.setTitleText("微博正文")
				.setLeftImage(R.drawable.navigationbar_back_sel)
				.setLeftOnClickListener(this);

		initDetailHead();
		initTab();
		initListView();
		initControlBar();
	}

	private void initDetailHead() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		tv_subhead = (TextView) findViewById(R.id.tv_subhead);
		tv_caption = (TextView) findViewById(R.id.tv_caption);
		include_status_image = (FrameLayout) findViewById(R.id.include_status_image);
		gv_images = (WrapHeightGridView) findViewById(R.id.gv_images);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		tv_content = (TextView) findViewById(R.id.tv_content);
		include_retweeted_status = findViewById(R.id.include_retweeted_status);
		tv_retweeted_content = (TextView) findViewById(R.id.tv_retweeted_content);
		fl_retweeted_imageview = (FrameLayout) include_retweeted_status.findViewById(R.id.include_status_image);
		gv_retweeted_images = (GridView) fl_retweeted_imageview.findViewById(R.id.gv_images);
		iv_retweeted_image = (ImageView) fl_retweeted_imageview.findViewById(R.id.iv_image);
		iv_image.setOnClickListener(this);
	}

	private void initTab() {
		status_detail_tab = (LinearLayout) findViewById(R.id.status_detail_tab);
		shadow_rg_status_detail = (RadioGroup) findViewById(R.id.rg_status_detail);
		shadow_rb_retweets = (RadioButton) findViewById(R.id.rb_retweets);
		shadow_rb_comments = (RadioButton) findViewById(R.id.rb_comments);
		shadow_rb_likes = (RadioButton) findViewById(R.id.rb_likes);
		shadow_rg_status_detail.setOnCheckedChangeListener(this);
		shadow_rb_retweets.setText("转发 " + status.getReposts_count());
		shadow_rb_comments.setText("评论 " + status.getComments_count());
		shadow_rb_likes.setText("赞 " + status.getAttitudes_count());
	}

	private void initListView() {
		// 下拉刷新控件
		psv_status_detail = (Pull2RefreshScrollView) findViewById(R.id.psv_status_detail);
		psv_status_detail.setOnScrollChangeListener(new OnScrollChangeListener() {
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				
			}
		});
		// listview
		lv_comment = (ListView) findViewById(R.id.lv_inner);
		LayoutParams params = (LayoutParams) lv_comment.getLayoutParams();
		params.height = DisplayUtils.getScreenHeightPixels(this)
				- DisplayUtils.dp2px(this, 40 + 56 + 48);
		adapter = new StatusCommentAdapter(this, comments);
		lv_comment.setAdapter(adapter);
		// footView加载更多
		footView = View.inflate(this, R.layout.footview_loading, null);
		addFootView(lv_comment, footView);
		// 设置各类监听
		psv_status_detail.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				loadComments(1);
			}
		});
		lv_comment.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 0-pullHead 1-detailHead 2-tab
//				shadow_status_detail_tab.setVisibility(firstVisibleItem >= 2 ?
//						View.VISIBLE : View.GONE);
			}
		});
	}

	private void initControlBar() {
		ll_share_bottom = (LinearLayout) findViewById(R.id.ll_share_bottom);
		iv_share_bottom = (ImageView) findViewById(R.id.iv_share_bottom);
		tv_share_bottom = (TextView) findViewById(R.id.tv_share_bottom);
		ll_comment_bottom = (LinearLayout) findViewById(R.id.ll_comment_bottom);
		iv_comment_bottom = (ImageView) findViewById(R.id.iv_comment_bottom);
		tv_comment_bottom = (TextView) findViewById(R.id.tv_comment_bottom);
		ll_like_bottom = (LinearLayout) findViewById(R.id.ll_like_bottom);
		iv_like_bottom = (ImageView) findViewById(R.id.iv_like_bottom);
		tv_like_bottom = (TextView) findViewById(R.id.tv_like_bottom);
		ll_share_bottom.setOnClickListener(this);
		ll_comment_bottom.setOnClickListener(this);
		ll_like_bottom.setOnClickListener(this);
	}

	private void setData() {
		if(isComment) {
			psv_status_detail.getRefreshableView().fullScroll(ScrollView.FOCUS_DOWN);
		}
		
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

						addData(gson.fromJson(response, CommentsResponse.class));
					}

					@Override
					public void onAllDone() {
						super.onAllDone();

						psv_status_detail.onRefreshComplete();
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

		if (curPage < response.getTotal_number()) {
			addFootView(lv_comment, footView);
		} else {
			removeFootView(lv_comment, footView);
		}
	}

	private void addFootView(ListView lv, View footView) {
		if (lv.getFooterViewsCount() == 1) {
			lv.addFooterView(footView);
		}
	}

	private void removeFootView(ListView lv, View footView) {
		if (lv.getFooterViewsCount() > 1) {
			lv.removeFooterView(footView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_iv_left:
			ScrollStatusDetailActivity.this.finish();
			break;
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
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.rb_retweets:
			break;
		case R.id.rb_comments:
			break;
		case R.id.rb_likes:
			break;

		default:
			break;
		}
	}

}
