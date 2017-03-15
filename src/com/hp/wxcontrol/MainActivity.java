package com.hp.wxcontrol;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import com.hp.wxcontrol.ExampleUtil;
import com.hp.wxcontrol.util.ActionQueue;
import com.hp.wxcontrol.util.HttpUtil;
import com.hp.wxcontrol.util.MD5Util;
import com.hp.wxcontrol.util.PictureUtil;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import static com.hp.wxcontrol.util.Constants.TAG;

public class MainActivity extends Activity implements 
		RadioGroup.OnCheckedChangeListener {

	private RadioGroup rg;

	public static boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rg = (RadioGroup) findViewById(R.id.rg);

		rg.setOnCheckedChangeListener(this);

		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
//	
		new Thread() {
			public void run() {
				
				Looper.prepare();
				new Handler().post(new ActionThread(getApplicationContext()));
				Looper.loop();
			}
		}.start();
		
		ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText("");
		
		//调用JPush API设置Tag
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, "13146451976"));
		
		String rid = JPushInterface.getRegistrationID(getApplicationContext());
		
		Log.d(TAG, "RegistrationID:" + rid);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		String action = "-1";

		if (R.id.radioButton == checkedId) {
			action = "3004";
		} else if (R.id.radioButton2 == checkedId) {
			action = "3005";
		} else if (R.id.radioButton3 == checkedId) {
			action = "6000";
		} else if (R.id.radioButton4 == checkedId) {
			action = "1001";
		} else if (R.id.radioButton5 == checkedId) {
			action = "3006";
		} else if (R.id.radioButton6 == checkedId) {
			action = "";
//			String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
//			getApplicationContext().sendBroadcast(new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR,	Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wxControl"))));
		}
//
		ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		Toast.makeText(getApplicationContext(), String.valueOf(cm.getText()) + "--" + ActionQueue.queue.size(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}
	
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					Log.d(TAG, "Set alias in handler.");
					JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
					break;

				case MSG_SET_TAGS:
					Log.d(TAG, "Set tags in handler.");
					JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
					break;

				default:
					Log.i(TAG, "Unhandled msg - " + msg.what);
			}
		}
	};
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
				case 0:
					logs = "Set tag and alias success";
					
					String reg_id = JPushInterface.getRegistrationID(getApplicationContext());
					String sign = MD5Util.getMd5Value(reg_id + "123456");
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("sign", sign);
					params.put("name", "13146451976");
					params.put("reg_id", reg_id);
					
					Log.i(TAG, "httpUtil.doPostAsyn -- sign=" + sign + ";reg_id=" + reg_id);
					
					HttpUtil httpUtil = new HttpUtil();
					httpUtil.doPostAsyn("http://106.75.10.209/api/device/register", params, new HttpUtil.HttpCallBackListener() {
						@Override
						public void onFinish(String result) {
							Log.i(TAG, "httpUtil.doPostAsyn:" + result);
						}
			
						@Override
						public void onError(Exception e) {
			
						}
					});
					
					Log.i(TAG, logs);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					Log.i(TAG, logs);
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}

			ExampleUtil.showToast(logs, getApplicationContext());
		}

	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
				case 0:
					logs = "Set tag and alias success";
					Log.i(TAG, logs);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					Log.i(TAG, logs);
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}

			ExampleUtil.showToast(logs, getApplicationContext());
		}

	};

}
