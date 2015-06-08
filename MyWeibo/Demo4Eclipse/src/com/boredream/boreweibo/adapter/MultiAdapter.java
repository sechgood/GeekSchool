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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.Comment;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MultiAdapter extends BaseAdapter {
	
	private OnCheckedChangeListener onCheckedChangeListener;
	
	public void setOnCheckedChangeListener(
			OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	private static final int VIEW_TYPE_TAB = 0;
	private static final int VIEW_TYPE_LIST = 1;
	
	private static final int LIST_TYPE_RETWEET = 11;
	private static final int LIST_TYPE_COMMENT = 12;
	

	private Context context;
	private List<Status> retweets = new ArrayList<Status>();
	private List<Comment> comments = new ArrayList<Comment>();
	/**
	 * 11-retweet 12-comment
	 */
	private int listType;
	private ImageLoader imageLoader;

	public MultiAdapter(Context context) {
		this.context = context;
		this.imageLoader = ImageLoader.getInstance();
	}
	
	public void setRetweets(List<Status> retweets) {
		this.retweets = retweets;
		this.listType = LIST_TYPE_RETWEET;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
		this.listType = LIST_TYPE_COMMENT;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == 0 ? VIEW_TYPE_TAB : VIEW_TYPE_LIST;
	}
	
	@Override
	public int getCount() {
		return listType == LIST_TYPE_RETWEET ? 
				retweets.size() + 1 : comments.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if(position > 0) {
			switch (listType) {
			case LIST_TYPE_RETWEET:
				return retweets.get(position - 1);
			case LIST_TYPE_COMMENT:
				return comments.get(position - 1);
			}
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
			holder.status_detail_rg = (RadioGroup) convertView
					.findViewById(R.id.rg_status_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderTab) convertView.getTag();
		}

		if(onCheckedChangeListener != null) {
			holder.status_detail_rg.setOnCheckedChangeListener(onCheckedChangeListener);
		}

		return convertView;
	}
	
	public static class ViewHolderTab {
		public RadioGroup status_detail_rg;
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
			holder.tv_caption = (TextView) convertView
					.findViewById(R.id.tv_caption);
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
			holder.tv_caption.setText(comment.getCreated_at());
			holder.tv_like.setVisibility(View.VISIBLE);
			holder.tv_like.setText(comment.getFloor_num()+"");
			holder.tv_comment.setText(comment.getText());
		} else if(obj instanceof Status) {
			Status rStatus = (Status) obj;
			
			User user = rStatus.getUser();
			
			imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar, 
					ImageOptHelper.getAvatarOptions());
			holder.tv_subhead.setText(user.getName());
			holder.tv_caption.setText(rStatus.getCreated_at());
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
		public TextView tv_caption;
		public TextView tv_like;
		public TextView tv_comment;
	}

}
