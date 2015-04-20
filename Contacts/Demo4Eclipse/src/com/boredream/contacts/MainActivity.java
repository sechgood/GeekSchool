package com.boredream.contacts;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.boredream.contacts.BoreLetterBar.OnLetterChangedListener;

public class MainActivity extends Activity implements OnClickListener {
	private ListView lv;
	private BoreLetterBar lb;
	private TextView tv_overlay;
	
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
		lb = (BoreLetterBar) findViewById(R.id.lb_contacts);
		tv_overlay = (TextView) findViewById(R.id.tv_overlay);
		addBtn = (Button) findViewById(R.id.btn_add);
		lb.setOnLetterChangedListener(new OnLetterChangedListener() {
			@Override
			public void onLetterSelected(String letter) {
				if(TextUtils.isEmpty(letter)) {
					tv_overlay.setVisibility(View.GONE);
				} else {
					
				}
			}
		});
		adapter = new MyAdapter(dataList, MainActivity.this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				showUpdateDialog(position);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				showDeleteDialog(position);
				return true;
			}
			
		});
		addBtn.setOnClickListener(this);
	}

	private void getAllContact() {
		pd.show();
		dataList = ContactsManager.getContacts(this);
		adapter.updateList(dataList);
		pd.dismiss();
	}
	
	private void showUpdateDialog(final int position) {
		final ContactBean oldP = adapter.getItem(position);
		
		final LinearLayout ll = new LinearLayout(MainActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		final EditText etName = new EditText(MainActivity.this);
		etName.setHint("��������ϵ������");
		etName.setText(oldP.name);
		final EditText etPhone = new EditText(MainActivity.this);
		etPhone.setHint("������绰����");
		etPhone.setText(oldP.phone);
		ll.addView(etName);
		ll.addView(etPhone);
		new AlertDialog.Builder(MainActivity.this)
			.setTitle("�޸���ϵ��")
			.setView(ll)
			.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String name = etName.getText().toString();
							String phone = etPhone.getText().toString();
							
							ContactBean newP = new ContactBean();
							newP.raw_contact_id = oldP.raw_contact_id;
							newP.name = name;
							newP.phone = phone;
							
							ContactsManager.update(MainActivity.this, newP);
							
							getAllContact();
						}
					})
			.setNegativeButton("ȡ��", null)
			.show();
	}
	
	private void showDeleteDialog(final int position) {
		new AlertDialog.Builder(MainActivity.this)
			.setMessage("�Ƿ�ȷ��ɾ��?")
			.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ContactBean p = adapter.getItem(position);
							ContactsManager.deleteContact(MainActivity.this, p);
							
							getAllContact();
						}
					})
			.setNegativeButton("ȡ��", null)
			.show();
	}
	
	private void showAddDialog() {
		final LinearLayout ll = new LinearLayout(MainActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		final EditText etName = new EditText(MainActivity.this);
		etName.setHint("��������ϵ������");
		final EditText etPhone = new EditText(MainActivity.this);
		etPhone.setHint("������绰����");
		ll.addView(etName);
		ll.addView(etPhone);
		new AlertDialog.Builder(MainActivity.this)
			.setTitle("�����ϵ��")
			.setView(ll)
			.setPositiveButton("ȷ��",
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
			.setNegativeButton("ȡ��", null)
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
