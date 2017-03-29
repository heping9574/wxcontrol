package com.hp.wxcontrol;

import static com.hp.wxcontrol.util.Constants.TAG;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.hp.wxcontrol.util.ActionQueue;
import com.hp.wxcontrol.util.Constants;
import com.hp.wxcontrol.util.DeciveUtil;
import com.hp.wxcontrol.util.HttpUtil;
import com.hp.wxcontrol.util.MD5Util;

public class MainActivity extends Activity {

	//private RadioGroup rg;

	public static boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//rg = (RadioGroup) findViewById(R.id.rg);

		//rg.setOnCheckedChangeListener(this);

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
		
		String rid = JPushInterface.getRegistrationID(getApplicationContext());

		String deciveId = DeciveUtil.getDeviceId(getApplicationContext());
		
		//调用JPush API设置Tag
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, deciveId), 20000);
		
		Log.d(TAG, "RegistrationID:" + rid + "; deciveid:" + deciveId);
	}
/**
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
			ActionQueue.queue.clear();
//			String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
//			getApplicationContext().sendBroadcast(new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR,	Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wxControl"))));
		} else if (R.id.radioButton7 == checkedId) {
			
		}

		ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		Toast.makeText(getApplicationContext(), String.valueOf(cm.getText()) + "--" + ActionQueue.queue.size(),
				Toast.LENGTH_SHORT).show();
	}
*/
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
					String name = DeciveUtil.getDeviceId(getApplicationContext());
					String imei = DeciveUtil.getDeviceId(getApplicationContext());
					String sign = MD5Util.getDeviceRegSignValue(reg_id, name, imei);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("sign", sign);
					params.put("name", name);
					params.put("reg_id", reg_id);
					params.put("imei", imei);
					
					Log.i(TAG, "httpUtil.doPostAsyn -- sign=" + sign + ";reg_id=" + reg_id + "; imei:" + imei + "; name:" + name);
					
					HttpUtil httpUtil = new HttpUtil();
					httpUtil.doPostAsyn(Constants.REG_URL, params, new HttpUtil.HttpCallBackListener() {
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
