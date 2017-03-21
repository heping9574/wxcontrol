package com.hp.wxcontrol.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

import static com.hp.wxcontrol.util.Constants.TAG;

public class MD5Util {

	/**
	 * 32位MD5加密方法 16位小写加密只需getMd5Value("xxx").substring(8, 24);即可
	 * 
	 * @param sSecret
	 * @return
	 */
	private static String getMd5Value(String sSecret) {
		try {
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(sSecret.getBytes());
			int i;
			StringBuffer buf = new StringBuffer();
			byte[] b = bmd5.digest();// 加密
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取设备注册Sign值
	 * @param regId
	 * @param name
	 * @param imei
	 * @return
	 */
	public static String getDeviceRegSignValue(String regId, String name, String imei) {
		Log.d(TAG, "getDeviceRegSignValue|" + "regId=" + regId + "|name=" + name + "|imei=" + imei);
		String sign = getMd5Value(
				(getMd5Value(regId + Constants.KEY).substring(0, 10)) + 
				name + 
				imei
			);
		Log.d(TAG, "getDeviceRegSignValue|" + "sign=" + sign);
		return sign;
	}
}
