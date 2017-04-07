package com.hp.wxcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Title: BootReceiver.java  
 * @Package com.hp.wxcontrol  
 * @Description: TODO
 * @author heping
 * @date 2017-4-7 下午3:29:49
 * @version 1.0
 */
public class BootReceiver extends BroadcastReceiver {  
    
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot  
            Intent intent2 = new Intent(context, MainActivity.class);  
//          intent2.setAction("android.intent.action.MAIN");  
//          intent2.addCategory("android.intent.category.LAUNCHER");  
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            context.startActivity(intent2);  
        }  	
	}  
}  
