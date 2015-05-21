package com.boredream.boreweibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.Message;

public class MessageAdapter extends BaseAdapter {

	private Context context;
	private List<Message> messages;

	public MessageAdapter(Context context, List<Message> datas) {
		this.context = context;
		this.messages = datas;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Message getItem(int position) {
		return messages.get(position);
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
			convertView = View.inflate(context, R.layout.item_message, null);
			holder.iv_message_item = (ImageView) convertView.findViewById(R.id.iv_message_item);
			holder.tv_message_item = (TextView) convertView.findViewById(R.id.tv_message_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		Message message = getItem(position);
		holder.iv_message_item.setImageResource(message.getImg());
		holder.tv_message_item.setText(message.getMessage());

		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_message_item;
		public TextView tv_message_item;
	}

}
