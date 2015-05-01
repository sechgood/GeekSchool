package com.boredream.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.boredream.contacts.PinnedSectionListView.PinnedSectionListAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter implements PinnedSectionListAdapter{
	
	private static final int VIEW_TYPE_LETTER = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	
	private Context context;
	private List<Object> list = new ArrayList<Object>();
	private List<ContactBean> contacts;
	private Map<String, Integer> letterPosition;

	public MyAdapter(ArrayList<ContactBean> contacts, Context context) {
		this.contacts = contacts;
		this.context = context;
		this.letterPosition = new HashMap<String, Integer>();
		initList();
	}

	public void updateList(ArrayList<ContactBean> contacts) {
		this.contacts = contacts;
		initList();
	}

	private void initList() {
		list.clear();
		letterPosition.clear();
		Collections.sort(this.contacts, new Comparator<ContactBean>() {

			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				String lhsLetter = PinYinUtils.trans2PinYin(lhs.name).toUpperCase(Locale.CHINESE);
				String rhsLetter = PinYinUtils.trans2PinYin(rhs.name).toUpperCase(Locale.CHINESE);
				return lhsLetter.compareTo(rhsLetter);
			}
		});

		for (int i = 0; i < contacts.size(); i++) {
			ContactBean contact = contacts.get(i);
			String firstLetter = getFirstLetter(contact.name);
			if(!letterPosition.containsKey(firstLetter)) {
				letterPosition.put(firstLetter, list.size());
				list.add(firstLetter);
			}
			list.add(contact);
		}
	}
	
	private String getFirstLetter(String name) {
		String firsterLetter = "#";
		char c = PinYinUtils.trans2PinYin(name).toUpperCase(Locale.CHINESE).charAt(0);
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			firsterLetter = String.valueOf(c);
		}
		return firsterLetter;
	}

	public int getLetterPosition(String firstLetter) {
		Integer position = letterPosition.get(firstLetter);
		return position == null ? -1 : position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof ContactBean ? VIEW_TYPE_CONTENT : VIEW_TYPE_LETTER;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		int itemViewType = getItemViewType(position);
		return itemViewType == VIEW_TYPE_LETTER ? 
				getLetterView(position, convertView) : getContentView(position, convertView);
	}
	
	private View getLetterView(int position, View convertView) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.layout_lv_item_letter, null);
			holder = new ViewHolder();
			holder.letter = (TextView) convertView.findViewById(R.id.tv_letter);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String firstLetter = (String) getItem(position);
		holder.letter.setText(firstLetter);
		
		return convertView;
	}

	private View getContentView(int position, View convertView) {
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
		TextView letter;
		
		TextView name;
		TextView number;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == VIEW_TYPE_LETTER;
	}
}
