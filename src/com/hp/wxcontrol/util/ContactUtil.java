package com.hp.wxcontrol.util;

import static com.hp.wxcontrol.util.Constants.TAG;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hp.wxcontrol.model.Contact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class ContactUtil extends AbstUtil {

	private Context context;
	private String url;
	private List<Contact> list = new ArrayList<Contact>();

	public ContactUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	public void savePhoneToContact() {

		try {

			// 下载通讯录
			URL webUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) webUrl
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);

			if (conn.getResponseCode() == 200) {

				InputStream is = conn.getInputStream();

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));

				String phone = "";

				while ((phone = br.readLine()) != null) {
					
					Contact contact = new Contact();
					contact.setPhone(phone);
					
					list.add(contact);

					Log.d(TAG, "ContactUtil|savePhoneToContact|" + phone);
				}
				
				int addNum = batchAddContact(list);
				
				Log.d(TAG, "ContactUtil|savePhoneToContact|Num:" + addNum);
			}

		} catch (Exception ex) {
			Log.d(TAG, "[ContactUtil] err: " + ex.toString());
		}
	}

	/**
	 * 批量添加联系人到通讯录
	 * @param list
	 * @return
	 * @throws RemoteException
	 * @throws OperationApplicationException
	 */
	public int batchAddContact(List<Contact> list) throws RemoteException,
			OperationApplicationException {

		if (list == null || list.size() <= 0) {
			return 0;
		}

		int rawContactInsertIndex = 0;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		for (Contact contact : list) {

			Log.e("", "" + contact.toString());
			if (contact.getPhone() == null || "".equals(contact.getPhone())) {
				continue;
			}

			rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_TYPE, null)
					.withValue(RawContacts.ACCOUNT_NAME, null).build());

			// 添加姓名
			// ops.add(ContentProviderOperation
			// .newInsert(Data.CONTENT_URI)
			// .withValueBackReference(Data.RAW_CONTACT_ID,
			// rawContactInsertIndex)
			// .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
			// .withValue(StructuredName.DISPLAY_NAME,
			// contact.getChinese_Name()).build());

			// 添加号码
			if (contact.getPhone() != null && !"".equals(contact.getPhone())) {

				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(ContactsContract.Data.MIMETYPE,
								Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, contact.getPhone())
						.withValue(Phone.TYPE, Phone.TYPE_MOBILE).build());
			}

		}
		if (ops != null) {
			// 真正添加
			ContentProviderResult[] results = context.getContentResolver()
					.applyBatch(ContactsContract.AUTHORITY, ops);
			if (results != null) {
				for (ContentProviderResult result : results) {
					Log.e("URI:" + result.uri, "count:" + result.count);
				}
				return results.length;
			}
		}

		return 0;
	}

}
