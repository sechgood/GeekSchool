package com.boredream.boreweibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.entity.PicUrls;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StatusGridImgsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<PicUrls> datas;
	private ImageLoader imageLoader;

	public StatusGridImgsAdapter(Context context, ArrayList<PicUrls> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public PicUrls getItem(int position) {
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
			convertView = View.inflate(context, R.layout.item_grid_image, null);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		System.out.println(parent.getWidth());
		
		// set data
		PicUrls item = getItem(position);
		imageLoader.displayImage(item.getThumbnail_pic(), holder.iv_image);

		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_image;
	}

}
