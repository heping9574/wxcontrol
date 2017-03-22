package com.hp.wxcontrol.util;

import static com.hp.wxcontrol.util.Constants.TAG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil extends AbstUtil {
	
	private Context context;
	private String url;
	
	public FileUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}
	public FileUtil(Context context) {
		this.context = context;
	}
	
	public void saveFile() {
		
		// 保存文件目录
	    File appDir = new File("/mnt/sdcard/TouchSprite/lua");
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    String fileName = getImgName(url);
	    File file = new File(appDir, fileName);

	    try {
	    
			// 下载图片
			URL webUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) webUrl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			if (conn.getResponseCode() == 200) {
	
				InputStream is = conn.getInputStream();
				
				FileOutputStream fos = new FileOutputStream(file);
				
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		
	    } catch (Exception ex) {
	    	Log.d(TAG, "FileUtil|saveFile|err: " + ex.toString());
	    }
	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(file.getPath()))));

	    Log.d(TAG, "FileUtil|saveFile|filename: " + fileName + ";path:" + Uri.fromFile(new File(file.getPath())));

	}
	
	public void savePicture() {
		
		//Bitmap bitmap = getHttpBitmap(url);//从网络获取图片 

		//saveImageToGallery(context, bitmap, url);
		saveImageToGallery(context, url);
		
		//savePicture(bitmap, url);//保存图片到SD卡 	
	}
	
	//public void saveImageToGallery(Context context, Bitmap bmp, String url) {
	private void saveImageToGallery(final Context context, String url) {
		
		// 首先保存图片
	    File appDir = new File(Environment.getExternalStorageDirectory(), "wxControl");
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    String fileName = getImgName(url);
	    File file = new File(appDir, fileName);

	    try {
	    
			// 下载图片
			URL webUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) webUrl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			if (conn.getResponseCode() == 200) {
	
				InputStream is = conn.getInputStream();
				
				FileOutputStream fos = new FileOutputStream(file);
				
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		
	    } catch (Exception ex) {
	    	Log.d(TAG, "[FileUtil] err: " + ex.toString());
	    }
	    
	    scanFileAsync(context, file.getPath());
	    scanDirAsync(context, appDir.getPath());
	    
	    Log.d(TAG, "[FileUtil] save picture filename: " + fileName + ";path:" + Uri.fromFile(new File(file.getPath())));
	}
	
	public static void refresh(Context context) {
		//context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wxControl"))));
		//context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File("file://sdcard/wxControl"))));
		context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR",Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wxControl"))));
		Log.d(TAG, "refresh file complete !");
	}
	
	//扫描指定文件
    public void scanFileAsync(Context ctx, String filePath) {
    	Log.d(TAG, "scanFileAsync|filePath=" + filePath);
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }
     
    //扫描指定目录
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
    public void scanDirAsync(Context ctx, String dir) {
    	Log.d(TAG, "scanDirAsync|dir=" + dir);
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }

	
	/**
	 * 删除wxControl文件夹下的文件
	 */
	public static void deleteFiles(Context context) {
		File appDir = new File(
				Environment.getExternalStorageDirectory(),
				"wxControl");
		
		if (appDir.isDirectory() && appDir.list().length > 0) {

			File[] files = appDir.listFiles();

			for (File file : files) {		
				file.delete();
				
				MediaScannerConnection.scanFile(context.getApplicationContext(), 
						new String[]{file.getPath()}, null, null);  

//
//		        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		        scanIntent.setData(Uri.fromFile(new File(file.getPath())));
//		        context.sendBroadcast(scanIntent);
			}
		}
	

//        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
//        scanIntent.setData(Uri.fromFile(new File(appDir.getPath())));
//        context.sendBroadcast(scanIntent);
	}
}
