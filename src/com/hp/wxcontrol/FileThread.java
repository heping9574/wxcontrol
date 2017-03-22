package com.hp.wxcontrol;

import com.hp.wxcontrol.util.FileUtil;

import android.content.Context;

/**
 * @Title: FileThread.java  
 * @Package com.hp.wxcontrol  
 * @Description: TODO
 * @author heping
 * @date 2017-3-22 下午1:27:56
 * @version 1.0
 */
public class FileThread implements Runnable {

	private Context context;
	private String url;
	
	public FileThread(Context context, String url) {
		
		this.context = context;
		this.url = url;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileUtil pu = new FileUtil(context, url);
		pu.saveFile();
	}

}
