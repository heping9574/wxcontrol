package com.hp.wxcontrol;

import static com.hp.wxcontrol.util.Constants.TAG;
import android.content.Context;
import android.util.Log;

import com.hp.wxcontrol.util.FileUtil;

public class PictureThread implements Runnable {
	
	private String imgurl = "";
	private Context context;
	
	public PictureThread(Context context, String imgurl) {
		this.context = context;
		this.imgurl = imgurl;
	}

	public void run() {
		FileUtil pu = new FileUtil(context, imgurl);
		pu.savePicture();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			Log.d(TAG, "[PictureThread] err: " + e.toString());
//		}
	}
}
