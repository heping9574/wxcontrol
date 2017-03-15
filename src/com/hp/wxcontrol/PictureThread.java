package com.hp.wxcontrol;

import static com.hp.wxcontrol.util.Constants.TAG;
import android.content.Context;
import android.util.Log;

import com.hp.wxcontrol.util.PictureUtil;

public class PictureThread implements Runnable {
	
	private String imgurl = "http://i0.51wan.com/gameImages/jxqy/rmyx165x120.jpg?v1.0";
	private Context context;
	
	public PictureThread(Context context, String imgurl) {
		this.context = context;
		this.imgurl = imgurl;
	}

	public void run() {
		PictureUtil pu = new PictureUtil(context, imgurl);
		pu.savePicture();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			Log.d(TAG, "[PictureThread] err: " + e.toString());
//		}
	}
}
