package com.boredream.boreweibo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.Comment;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.boredream.boreweibo.widget.PinnedSectionListView.PinnedSectionListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TabCommentAdapter extends BaseAdapter 
	implements PinnedSectionListAdapter{

	private static final int VIEW_TYPE_TAB = 0;
	private static final int VIEW_TYPE_COMMENT = 1;

	private Context context;
	private Status status;
	private List<Comment> comments;
	private ImageLoader imageLoader;

	public TabCommentAdapter(Context context, Status status, List<Comment> comments) {
		this.context = context;
		this.status = status;
		this.comments = comments;
		this.imageLoader = ImageLoader.getInstance();
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == 0 ? VIEW_TYPE_TAB : VIEW_TYPE_COMMENT;
	}
	
	@Override
	public int getCount() {
		return comments.size() + 1;
	}

	@Override
	public Comment getItem(int position) {
		if(position > 0) {
			return comments.get(position - 1);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItemViewType(position) == VIEW_TYPE_TAB ?
				getTabView(position, convertView) : getListView(position, convertView);
	}
	
	private View getTabView(int position, View convertView) {
		ViewHolderTab holder;
		if (convertView == null) {
			holder = new ViewHolderTab();
			convertView = View.inflate(context, R.layout.status_detail_tab, null);
			holder.rg_status_detail = (RadioGroup) convertView
					.findViewById(R.id.rg_status_detail);
			holder.rb_retweets = (RadioButton) convertView
					.findViewById(R.id.rb_retweets);
			holder.rb_comments = (RadioButton) convertView
					.findViewById(R.id.rb_comments);
			holder.rb_likes = (RadioButton) convertView
					.findViewById(R.id.rb_likes);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderTab) convertView.getTag();
		}
		
		holder.rb_retweets.setText("转发 " + status.getReposts_count());
		holder.rb_comments.setText("评论 " + status.getComments_count());
		holder.rb_likes.setText("赞 " + status.getAttitudes_count());
		
		return convertView;
	}
	
	public static class ViewHolderTab {
		public RadioGroup rg_status_detail;
		public RadioButton rb_retweets;
		public RadioButton rb_comments;
		public RadioButton rb_likes;
	}

	private View getListView(int position, View convertView) {
		ViewHolderList holder;
		if (convertView == null) {
			holder = new ViewHolderList();
			convertView = View.inflate(context, R.layout.item_comment, null);
			holder.ll_comments = (LinearLayout) convertView
					.findViewById(R.id.ll_comments);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.tv_subhead = (TextView) convertView
					.findViewById(R.id.tv_subhead);
			holder.tv_body = (TextView) convertView
					.findViewById(R.id.tv_body);
			holder.tv_like = (TextView) convertView
					.findViewById(R.id.tv_like);
			holder.tv_comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderList) convertView.getTag();
		}

		// set data
		Object obj = getItem(position);
		if(obj instanceof Comment) {
			Comment comment = (Comment) obj;
			
			User user = comment.getUser();
			
			imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar, 
					ImageOptHelper.getAvatarOptions());
			holder.tv_subhead.setText(user.getName());
			holder.tv_body.setText(comment.getCreated_at());
			holder.tv_like.setVisibility(View.VISIBLE);
			holder.tv_like.setText(comment.getFloor_num()+"");
			holder.tv_comment.setText(comment.getText());
		} else if(obj instanceof Status) {
			Status rStatus = (Status) obj;
			
			User user = rStatus.getUser();
			
			imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar, 
					ImageOptHelper.getAvatarOptions());
			holder.tv_subhead.setText(user.getName());
			holder.tv_body.setText(rStatus.getCreated_at());
			holder.tv_like.setVisibility(View.GONE);
			holder.tv_comment.setText(rStatus.getText());
		}
		
		holder.ll_comments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		return convertView;
	}
	
	public static class ViewHolderList {
		public LinearLayout ll_comments;
		public ImageView iv_avatar;
		public TextView tv_subhead;
		public TextView tv_body;
		public TextView tv_like;
		public TextView tv_comment;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == VIEW_TYPE_TAB;
	}

}
