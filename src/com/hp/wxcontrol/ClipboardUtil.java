package com.hp.wxcontrol;

import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

	private final static ClipboardUtil instance = new ClipboardUtil();
	
	private ClipboardUtil() {
		
	}
	
	public static ClipboardUtil getInstance() {
		return instance;
	}
	
	public void write(String action, String jsonStr) {
//		ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        // 将文本内容放到系统剪贴板里。
//        cm.setText(tvMsg.getText());
	}
}
