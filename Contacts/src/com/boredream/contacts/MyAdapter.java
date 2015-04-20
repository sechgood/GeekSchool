package com.boredream.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<ContactBean> list;

	public MyAdapter(ArrayList<ContactBean> list, Context context) {
		this.list = list;
		this.context = context;
		Collections.sort(this.list, new Comparator<ContactBean>() {

			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
	}
	
	public void updateList(ArrayList<ContactBean> list) {
		this.list = list;
		Collections.sort(this.list, new Comparator<ContactBean>() {

			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ContactBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.layout_lv_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			holder.number = (TextView) convertView .findViewById(R.id.item_number);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ContactBean person = list.get(position);
		
		holder.name.setText(person.name);
		holder.number.setText(person.phone);
		
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
	}
}
