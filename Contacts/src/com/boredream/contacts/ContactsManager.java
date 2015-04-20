package com.boredream.contacts;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

public class ContactsManager {
	// 新增联系人
	public static void addContact(Context context, ContactBean person) {
		ContentResolver resolver = context.getContentResolver();

		// 插入新的raw contact数据,并获取返回的raw contact id
		ContentValues values = new ContentValues();
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// 往data表中插入我们新建联系人对应的名字信息
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.DISPLAY_NAME, person.name);
		resolver.insert(Data.CONTENT_URI, values);

		// 往data表中插入我们新建联系人对应的号码信息
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, person.phone);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		resolver.insert(Data.CONTENT_URI, values);
	}

	// 获取联系人信息
	public static ArrayList<ContactBean> getContacts(Context context) {
		ContentResolver resolver = context.getContentResolver();

		// 查询raw contact表全部数据
		Cursor rawCursor = resolver.query(
				RawContacts.CONTENT_URI, null, null, null, null);

		ArrayList<ContactBean> list = new ArrayList<ContactBean>();
		ContactBean p;
		
		while (rawCursor.moveToNext()) {
			p = new ContactBean();
			
			// 利用raw contact id获取data表中对应的数据
			String raw_contact_id = rawCursor.getString(
					rawCursor.getColumnIndex(RawContacts._ID));
			p.raw_contact_id = raw_contact_id;
			
			Cursor dataCursor = resolver.query(Data.CONTENT_URI, null,
					Data.RAW_CONTACT_ID + "=" + raw_contact_id, null, null);

			// data表中对应的数据可能是多条
			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex(Data.DATA1));
				String minetype = dataCursor.getString(dataCursor
						.getColumnIndex(Data.MIMETYPE));

				if (minetype.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// 姓名类型 "vnd.android.cursor.item/name"
					p.name = data1;
				} else if (minetype.equals(Phone.CONTENT_ITEM_TYPE)) {
					// 电话类型 "vnd.android.cursor.item/phone_v2"
					p.phone = data1;
				} else if (minetype.equals(Email.CONTENT_ITEM_TYPE)) {
					// 邮箱类型 "vnd.android.cursor.item/email_v2"
					// 其他信息的处理同理
				}
			}

			dataCursor.close();
			list.add(p);
		}
		rawCursor.close();
		return list;
	}

	// 修改联系人
	public static void update(Context context, ContactBean person) {
		ContentResolver resolver = context.getContentResolver();

		ArrayList<ContentProviderOperation> ops = 
				new ArrayList<ContentProviderOperation>();

		// 向data表中对应联系人类型为姓名的data1中添加数据
		ops.add(ContentProviderOperation
				.newUpdate(Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "=?",
						new String[] { person.raw_contact_id, StructuredName.CONTENT_ITEM_TYPE })
				.withValue(StructuredName.DISPLAY_NAME, person.name).build());
		// 向data表中对应联系人类型为手机的data1中添加数据
		ops.add(ContentProviderOperation
				.newUpdate(Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "=?",
						new String[] { person.raw_contact_id, Phone.CONTENT_ITEM_TYPE })
				.withValue(Phone.NUMBER, person.phone).build());
		try {
			resolver.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}

	// 删除联系人
	public static void deleteContact(Context context, ContactBean p) {
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(RawContacts.CONTENT_URI, RawContacts._ID + "=? ",
				new String[] { p.raw_contact_id });
	}

}
