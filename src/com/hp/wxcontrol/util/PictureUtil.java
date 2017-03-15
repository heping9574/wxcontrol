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
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class PictureUtil {
	
	private Context context;
	private String url;
	
	public PictureUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}
	public PictureUtil(Context context) {
		this.context = context;
	}
	
	public void savePicture() {
		
		//Bitmap bitmap = getHttpBitmap(url);//从网络获取图片 

		//saveImageToGallery(context, bitmap, url);
		saveImageToGallery(context, url);
		
		//savePicture(bitmap, url);//保存图片到SD卡 	
	}
	
//	public static void savePicture(String path) throws IOException {
//		//String pictureName = imgPath + getImgName(path);;
//        File file = new File(cache, getImgName(path));  
//        // 如果图片存在本地缓存目录，则不去服务器下载   
//        if (file.exists()) {  
//            //do nothing  
//        } else {  
//            // 从网络上获取图片  
//            URL url = new URL(path);  
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
//            conn.setConnectTimeout(5000);  
//            conn.setRequestMethod("GET");  
//            conn.setDoInput(true);  
//            if (conn.getResponseCode() == 200) {  
//  
//                InputStream is = conn.getInputStream();  
//                FileOutputStream fos = new FileOutputStream(file);  
//                byte[] buffer = new byte[1024];  
//                int len = 0;  
//                while ((len = is.read(buffer)) != -1) {  
//                    fos.write(buffer, 0, len);  
//                }  
//                is.close();  
//                fos.close();  
//            }
//            Log.d(TAG, "[PicutureUtil] save picture: " + getImgName(path));
//        }  
//	}
	
	private Bitmap getHttpBitmap(String url) {
		Bitmap bitmap = null;
		try {
			URL pictureUrl = new URL(url);
			InputStream in = pictureUrl.openStream();
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (MalformedURLException e) {
			Log.d(TAG, "[PictureUtil] getHttpBitmap error: " + e.toString());
		} catch (IOException e) {
			Log.d(TAG, "[PictureUtil] getHttpBitmap error: " + e.toString());
		}
		return bitmap;
	}

//	private static void savePicture(Bitmap bitmap, String url) {
//		String pictureName = imgPath + getImgName(url);;
//		File file = new File(pictureName);
//		FileOutputStream out;
//		try {
//			out = new FileOutputStream(file);
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//			out.flush();
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	//public void saveImageToGallery(Context context, Bitmap bmp, String url) {
	public void saveImageToGallery(Context context, String url) {
		
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
	    	Log.d(TAG, "[PicutureUtil] err: " + ex.toString());
	    }
/*		
	    // 首先保存图片
	    File appDir = new File(Environment.getExternalStorageDirectory(), "wxControl");
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    String fileName = getImgName(url);
	    File file = new File(appDir, fileName);
	    try {
	        FileOutputStream fos = new FileOutputStream(file);
	        bmp.compress(CompressFormat.JPEG, 100, fos);
	        fos.flush();
	        fos.close();
	    } catch (FileNotFoundException e) {
	    	Log.d(TAG, "[PictureUtil] saveImageToGallery error: " + e.toString());
	    } catch (IOException e) {
	    	Log.d(TAG, "[PictureUtil] saveImageToGallery error: " + e.toString());
		}
	    */
	    // 其次把文件插入到系统图库
//	    try {
//	        MediaStore.Images.Media.insertImage(context.getContentResolver(),
//					file.getAbsolutePath(), fileName, null);
//	    } catch (FileNotFoundException e) {
//	        e.printStackTrace();
//	    }
	    // 最后通知图库更新
	    //String path = Environment.getExternalStorageDirectory().getPath();
	    //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
	    //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(file.getPath()))));
	    
	    Log.d(TAG, "[PicutureUtil] save picture filename: " + fileName + ";path:" + Uri.fromFile(new File(file.getPath())));
	}
	
	public void refresh() {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wxControl"))));
	}
	
	private static String getImgName(String url) {
		if (url != null && url.length() > 0) {
			return url.substring(url.lastIndexOf("/")+1);
		} else {
			return "noname.jpg";
		}
	}
}