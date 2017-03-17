package com.hp.wxcontrol;

import com.hp.wxcontrol.util.ContactUtil;

import android.content.Context;

public class ContactThread implements Runnable {
	
	private Context context;
	private String url;
	
	public ContactThread(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	@Override
	public void run() {
		ContactUtil util = new ContactUtil(context, url);
		util.savePhoneToContact();
	}
	
}
