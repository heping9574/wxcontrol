package com.hp.wxcontrol.model;

import com.hp.wxcontrol.util.HookUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedModule implements IXposedHookLoadPackage {
	
	private double latitude = 0.00; //纬度
	private double longitude = 0.00; //经度
	
	public XposedModule(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		if (!lpparam.packageName.equals("com.tencent.mm"))
			return;

		//XposedBridge.log("hook com.tencent.mm");
		XposedBridge.log("hook com.tencent.mm");
		
		//HookUtils.HookAndChange(lpparam.classLoader, 39.9, 116.36, 0, 0);
		HookUtils.HookAndChange(lpparam.classLoader, latitude, longitude, 0, 0);
	}

}
