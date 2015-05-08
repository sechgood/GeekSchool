package com.boredream.boreweibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.Comment;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentAdapter extends BaseAdapter {

	private Context context;
	private List<Comment> datas;
	private ImageLoader imageLoader;

	public CommentAdapter(Context context, List<Comment> datas) {
		this.context = context;
		this.datas = datas;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Comment getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
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
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		Comment comment = getItem(position);
		User user = comment.getUser();
		
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar, 
				ImageOptHelper.getAvatarOptions());
		holder.tv_subhead.setText(user.getName());
		holder.tv_body.setText(comment.getCreated_at());
		holder.tv_like.setText(comment.getFloor_num()+"");
		holder.tv_comment.setText(comment.getText());
		
		holder.ll_comments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		return convertView;
	}

	public static class ViewHolder {
		public LinearLayout ll_comments;
		public ImageView iv_avatar;
		public TextView tv_subhead;
		public TextView tv_body;
		public TextView tv_like;
		public TextView tv_comment;
	}

}
