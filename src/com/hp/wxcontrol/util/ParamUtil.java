package com.hp.wxcontrol.util;

import java.util.List;

import android.util.Log;
import static com.hp.wxcontrol.util.Constants.TAG;

/**
 * @Title: ParamUtil.java  
 * @Package com.hp.wxcontrol.util  
 * @Description: 参数工具类
 * @author heping
 * @date 2017-3-22 上午9:55:32
 * @version 1.0
 */
public class ParamUtil {

	/**
	 * 获取参数拼接后字符串
	 * @param params
	 * @return
	 */
	public static String getParamString(List<Object> params) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		if (params != null && params.size() > 0){
			for (Object param : params) {
				sb.append(param).append(",");
			}
			result = sb.toString();
			result = result.substring(0, result.length() - 1);
		}
		Log.d(TAG, "ParamUtil|getParamString|result=" + result);
		
		return result;
	}
}
