package com.hp.wxcontrol.util;

public abstract class AbstUtil {
	
	protected String getImgName(String url) {
		if (url != null && url.length() > 0) {
			return url.substring(url.lastIndexOf("/")+1);
		} else {
			return "noname.jpg";
		}
	}
}
