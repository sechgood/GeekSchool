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
	// ������ϵ��
	public static void addContact(Context context, ContactBean person) {
		ContentResolver resolver = context.getContentResolver();

		// �����µ�raw contact����,����ȡ���ص�raw contact id
		ContentValues values = new ContentValues();
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// ��data���в��������½���ϵ�˶�Ӧ��������Ϣ
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.DISPLAY_NAME, person.name);
		resolver.insert(Data.CONTENT_URI, values);

		// ��data���в��������½���ϵ�˶�Ӧ�ĺ�����Ϣ
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, person.phone);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		resolver.insert(Data.CONTENT_URI, values);
	}

	// ��ȡ��ϵ����Ϣ
	public static ArrayList<ContactBean> getContacts(Context context) {
		ContentResolver resolver = context.getContentResolver();

		// ��ѯraw contact��ȫ������
		Cursor rawCursor = resolver.query(
				RawContacts.CONTENT_URI, null, null, null, null);

		ArrayList<ContactBean> list = new ArrayList<ContactBean>();
		ContactBean p;
		
		while (rawCursor.moveToNext()) {
			p = new ContactBean();
			
			// ����raw contact id��ȡdata���ж�Ӧ������
			String raw_contact_id = rawCursor.getString(
					rawCursor.getColumnIndex(RawContacts._ID));
			p.raw_contact_id = raw_contact_id;
			
			Cursor dataCursor = resolver.query(Data.CONTENT_URI, null,
					Data.RAW_CONTACT_ID + "=" + raw_contact_id, null, null);

			// data���ж�Ӧ�����ݿ����Ƕ���
			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex(Data.DATA1));
				String minetype = dataCursor.getString(dataCursor
						.getColumnIndex(Data.MIMETYPE));

				if (minetype.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// �������� "vnd.android.cursor.item/name"
					p.name = data1;
				} else if (minetype.equals(Phone.CONTENT_ITEM_TYPE)) {
					// �绰���� "vnd.android.cursor.item/phone_v2"
					p.phone = data1;
				} else if (minetype.equals(Email.CONTENT_ITEM_TYPE)) {
					// �������� "vnd.android.cursor.item/email_v2"
					// ������Ϣ�Ĵ���ͬ��
				}
			}

			dataCursor.close();
			list.add(p);
		}
		rawCursor.close();
		return list;
	}

	// �޸���ϵ��
	public static void update(Context context, ContactBean person) {
		ContentResolver resolver = context.getContentResolver();

		ArrayList<ContentProviderOperation> ops = 
				new ArrayList<ContentProviderOperation>();

		// ��data���ж�Ӧ��ϵ������Ϊ������data1���������
		ops.add(ContentProviderOperation
				.newUpdate(Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "=?",
						new String[] { person.raw_contact_id, StructuredName.CONTENT_ITEM_TYPE })
				.withValue(StructuredName.DISPLAY_NAME, person.name).build());
		// ��data���ж�Ӧ��ϵ������Ϊ�ֻ���data1���������
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

	// ɾ����ϵ��
	public static void deleteContact(Context context, ContactBean p) {
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(RawContacts.CONTENT_URI, RawContacts._ID + "=? ",
				new String[] { p.raw_contact_id });
	}

}
