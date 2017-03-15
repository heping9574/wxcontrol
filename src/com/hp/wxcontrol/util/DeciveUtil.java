package com.hp.wxcontrol.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class DeciveUtil {

	/**
	 * 通过wifi连接获取IP地址
	 * 
	 * @return
	 */
	private static String getlocalip(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		// Log.d(Tag, "int ip "+ipAddress);
		if (ipAddress == 0)
			return null;
		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
	}
}
