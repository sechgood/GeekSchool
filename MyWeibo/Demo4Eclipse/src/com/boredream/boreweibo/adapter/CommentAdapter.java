package com.boredream.boreweibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.StatusComment;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentAdapter extends BaseAdapter {

	private Context context;
	private List<StatusComment> datas;
	private ImageLoader imageLoader;

	public CommentAdapter(Context context, List<StatusComment> datas) {
		this.context = context;
		this.datas = datas;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public StatusComment getItem(int position) {
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
		StatusComment bean = getItem(position);
//		User user = 
//		
//		imageLoader.displayImage(user.getAvatarUrl(), holder.iv_avatar, 
//				ImageOptHelper.getAvatarOptions());
//		holder.tv_subhead.setText(user.getUsername());
//		holder.tv_body.setText(bean.getCreatedAt());
//		holder.tv_like.setText(bean.getLikeCount()+"");
//		holder.tv_comment.setText(bean.getContent());
		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_avatar;
		public TextView tv_subhead;
		public TextView tv_body;
		public TextView tv_like;
		public TextView tv_comment;
	}

}
