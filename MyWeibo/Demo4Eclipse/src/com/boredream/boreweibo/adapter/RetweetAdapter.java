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
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RetweetAdapter extends BaseAdapter {

	private Context context;
	private List<Status> datas;
	private ImageLoader imageLoader;

	public RetweetAdapter(Context context, List<Status> datas) {
		this.context = context;
		this.datas = datas;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Status getItem(int position) {
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
			holder.tv_caption = (TextView) convertView
					.findViewById(R.id.tv_caption);
			holder.tv_like = (TextView) convertView
					.findViewById(R.id.tv_like);
			holder.tv_comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		Status rStatus = getItem(position);
		User user = rStatus.getUser();
		
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
		holder.tv_subhead.setText(user.getName());
		holder.tv_caption.setText(rStatus.getCreated_at());
		holder.tv_like.setVisibility(View.INVISIBLE);
		holder.tv_comment.setText(rStatus.getText());
		
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
		public TextView tv_caption;
		public TextView tv_like;
		public TextView tv_comment;
	}

}
