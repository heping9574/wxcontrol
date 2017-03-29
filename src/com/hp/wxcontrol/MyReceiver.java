package com.hp.wxcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.content.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.wxcontrol.util.ActionQueue;
import com.hp.wxcontrol.util.DBUtil;
import com.hp.wxcontrol.util.ParamUtil;
import com.hp.wxcontrol.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import static com.hp.wxcontrol.util.Constants.TAG;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
//				+ ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			// 打开自定义的Activity
			Intent i = new Intent(context, MainActivity.class);
			i.putExtras(bundle);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle
						.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(
							bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it = json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" + myKey + " - "
								+ json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// 处理自定义消息
	private void processCustomMessage(Context context, Bundle bundle) {
		//if (MainActivity.isForeground) {

			String title = bundle.getString(JPushInterface.EXTRA_TITLE);
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE); // 命令代码
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA); // JSON数据

			StringBuffer sb = new StringBuffer();

			try {
				JSONObject jsonObject = new JSONObject(extras);

				// Intent msgIntent = new
				// Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
				// msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);

				String rid = JPushInterface.getRegistrationID(context);

				sb.append("title=" + title).append(";message=" + message)
						.append(";extras=" + extras).append(";rid=" + rid);

				int action = Integer.valueOf(message);
				/*
				 * ******************
				 * 100:连接 1000:登录 1001:账号切换 3001:朋友圈发文 3002:朋友圈小视频发布
				 * 3003:朋友圈分享链接 3004:朋友圈点赞 3005:朋友圈评论 4000:搜索加好友 4001:通讯录加好友
				 * 4002:搜索加公众号 4003:附近的人打招呼 4004:漂流瓶 5000:好友群发消息 5001:好友群发图片
				 * 5002:好友逐个发消息（清僵尸粉） 5003:好友逐个发图片（清僵尸粉） 5004:好友逐个发小视频
				 * 5005:好友逐个发收藏 6000:微信群里发消息 6001:微信群里发小视频 6002:微信群里发名片
				 * 6003:微信群里发图片 6004:微信群里发收藏 6005:微信群里发红包
				 */
				String scheduleId = jsonObject.getString("scheduleId");
				String imgurl = "";
				JSONArray imgUrlArray;
				
				List<Object> params = new ArrayList<Object>();				
				params.add(scheduleId);
				params.add(action);

				switch (action) {
				case 100:
					break;
				case 200: // 文件更新
					new Thread(new FileThread(context, jsonObject.getString("fileUrl"))).start();
				case 1000:
					break;
				case 1001:
					break;
				case 3001: // 朋友圈发图文
					imgUrlArray = jsonObject.getJSONArray("imgurl"); // 朋友圈发图文的图片URL数组

					FileUtil.deleteFiles(context);
					
					for (int i = 0; i < imgUrlArray.length(); i++) {

						JSONObject imgUrlJson = (JSONObject) imgUrlArray.get(i);

						new Thread(new PictureThread(context,
								imgUrlJson.getString("url"))).start(); // 下载图片到本地
					}
					
//					PictureUtil pu = new PictureUtil(context);
//					pu.refresh();
//					FileUtil.refresh(context);
					
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 3002: // 朋友圈发文字
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 3003: // 朋友圈分享链接
					params.add(jsonObject.getString("shareurl"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 3004: // 朋友圈点赞
					params.add(jsonObject.getString("num"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 3005: // 朋友圈评论
					params.add(jsonObject.getString("num"));
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 4000: // 搜索加好友
					break;
				case 4001: // 通讯录加好友
					break;
				case 4002: // 搜索加公众号
					break;
				case 4003: // 附近的人打招呼
					params.add(jsonObject.getString("num"));
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 4004: // 漂流瓶
					break;
				case 4005: // 通讯录自动添加新朋友
					params.add(jsonObject.getString("num"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 5000: // 好友-群发消息
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 5001: // 好友-群发图片
					imgurl = jsonObject.getString("imgurl");
					new Thread(new PictureThread(context, imgurl)).start(); // 下载图片到本地
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 5002:
					break;
				case 5003:
					break;
				case 5004:
					break;
				case 5005:
					break;
				case 6000: // 微信群-发消息
					params.add(jsonObject.getString("num"));
					params.add(jsonObject.getString("content"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 6001: // 微信群-发视频
					break;
				case 6002: // 微信群-发名片
					break;
				case 6003: // 微信群-发图片
	
					imgUrlArray = jsonObject.getJSONArray("imgurl"); // 朋友圈发图文的图片URL数组

					FileUtil.deleteFiles(context);
					
					for (int i = 0; i < imgUrlArray.length(); i++) {

						JSONObject imgUrlJson = (JSONObject) imgUrlArray.get(i);

						new Thread(new PictureThread(context,
								imgUrlJson.getString("url"))).start(); // 下载图片到本地
					}
					
					params.add(imgUrlArray.length());
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 6004: // 微信群-发收藏
					break;
				case 6005: // 微信群-发红包
					break;
				case 6006: // 微信群-扫描加微信群			
					imgUrlArray = jsonObject.getJSONArray("imgurl"); // 二维码图文的图片URL数组
					
					FileUtil.deleteFiles(context);

					for (int i = 0; i < imgUrlArray.length(); i++) {

						JSONObject imgUrlJson = (JSONObject) imgUrlArray.get(i);

						new Thread(new PictureThread(context,
								imgUrlJson.getString("url"))).start(); // 下载图片到本地
					}
					
					//FielUtil.refresh(context);
					params.add(imgUrlArray.length());
					ActionQueue.queue.add(ParamUtil.getParamString(params));					
					break;
				case 6007: // 扫码加微信好友
					
					break;
				case 7000: // 浏览新闻
					//params.add(jsonObject.getString("num"));
					ActionQueue.queue.add(ParamUtil.getParamString(params));
					break;
				case 8000: // 设置GPS坐标
					double latitude = jsonObject.getDouble("latitude"); // 经度
					double longitude = jsonObject.getDouble("longitude");// 维度
				case 8001: // 导入通讯录
					String url = jsonObject.getString("url");
					new Thread(new ContactThread(context, url)).start();
				case 9999: // 退出脚本
					ActionQueue.queue.add(ParamUtil.getParamString(params));
				default:
					break;
				}

			} catch (Exception e) {
				Log.d(TAG,
						"MyReceiver|processCustomMessage|err" + e.toString());

			}

			Log.d(TAG, "MyReceiver|processCustomMessage|message=" + sb);

			// if (!ExampleUtil.isEmpty(extras)) {
			// try {
			// JSONObject extraJson = new JSONObject(extras);
			// if (extraJson.length() > 0) {
			// msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
			// }
			// } catch (JSONException e) {
			//
			// }
			//
			// }
			// context.sendBroadcast(msgIntent);
		//}
	}
}
