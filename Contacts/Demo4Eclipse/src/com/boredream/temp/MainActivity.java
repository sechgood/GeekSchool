package com.boredream.temp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boredream.contacts.BoreLetterBar;
import com.boredream.contacts.BoreLetterBar.OnLetterChangedListener;
import com.boredream.contacts.R;

public class MainActivity extends Activity implements OnClickListener {
	private ListView lv;
	
	private Button addBtn;
	private ArrayList<ContactBean> dataList = new ArrayList<ContactBean>();
	private MyAdapter adapter;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		getAllContact();
	}

	private void initView() {
		pd = new ProgressDialog(this);
		lv = (ListView) findViewById(R.id.lv_contacts);
		addBtn = (Button) findViewById(R.id.btn_add);
		adapter = new MyAdapter(dataList, MainActivity.this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Object item = adapter.getItem(position);
				if(item instanceof ContactBean) {
					showUpdateDialog((ContactBean)item);
				}
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				ContactBean contact = adapter.getItem(position);
				showDeleteDialog(contact);
				return true;
			}
			
		});
		addBtn.setOnClickListener(this);
	}

	private void getAllContact() {
		pd.show();
		dataList = ContactsManager.getContacts(this);
		adapter.updateList(dataList);
		adapter.notifyDataSetChanged();
		pd.dismiss();
	}
	
	private void showUpdateDialog(final ContactBean oldContact) {
		final LinearLayout ll = new LinearLayout(MainActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		final EditText etName = new EditText(MainActivity.this);
		etName.setHint("请输入联系人名称");
		etName.setText(oldContact.name);
		final EditText etPhone = new EditText(MainActivity.this);
		etPhone.setHint("请输入电话号码");
		etPhone.setText(oldContact.phone);
		ll.addView(etName);
		ll.addView(etPhone);
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("修改联系人")
		.setView(ll)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = etName.getText().toString();
				String phone = etPhone.getText().toString();
				
				ContactBean newP = new ContactBean();
				newP.raw_contact_id = oldContact.raw_contact_id;
				newP.name = name;
				newP.phone = phone;
				
				ContactsManager.update(MainActivity.this, newP);
				
				getAllContact();
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}
	
	private void showDeleteDialog(final ContactBean contact) {
		new AlertDialog.Builder(MainActivity.this)
			.setMessage("是否确定删除?")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ContactsManager.deleteContact(MainActivity.this, contact);
							getAllContact();
						}
					})
			.setNegativeButton("取消", null)
			.show();
	}
	
	private void showAddDialog() {
		final LinearLayout ll = new LinearLayout(MainActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		final EditText etName = new EditText(MainActivity.this);
		etName.setHint("请输入联系人名称");
		final EditText etPhone = new EditText(MainActivity.this);
		etPhone.setHint("请输入电话号码");
		ll.addView(etName);
		ll.addView(etPhone);
		new AlertDialog.Builder(MainActivity.this)
			.setTitle("添加联系人")
			.setView(ll)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = etName.getText().toString();
					String phone = etPhone.getText().toString();
					
					ContactBean p = new ContactBean();
					p.name = name;
					p.phone = phone;
					ContactsManager.addContact(MainActivity.this, p);
					
					getAllContact();
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			showAddDialog();
			break;

		default:
			break;
		}
	}


}
