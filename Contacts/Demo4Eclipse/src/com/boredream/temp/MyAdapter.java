package com.boredream.temp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boredream.contacts.PinYinUtils;
import com.boredream.contacts.R;

public class MyAdapter extends BaseAdapter {
	
	private Context context;
	private List<ContactBean> contacts;

	public MyAdapter(ArrayList<ContactBean> contacts, Context context) {
		this.contacts = contacts;
		this.context = context;
	}

	public void updateList(ArrayList<ContactBean> contacts) {
		this.contacts = contacts;
		initList();
	}

	private void initList() {
		Collections.sort(this.contacts, new Comparator<ContactBean>() {

			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				String lhsLetter = PinYinUtils.trans2PinYin(lhs.name).toUpperCase(Locale.CHINESE);
				String rhsLetter = PinYinUtils.trans2PinYin(rhs.name).toUpperCase(Locale.CHINESE);
				return lhsLetter.compareTo(rhsLetter);
			}
		});
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public ContactBean getItem(int position) {
		return contacts.get(position);
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
			holder.number = (TextView) convertView.findViewById(R.id.item_number);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ContactBean person = (ContactBean) getItem(position);

		holder.name.setText(person.name);
		holder.number.setText(person.phone);

		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
	}

}
