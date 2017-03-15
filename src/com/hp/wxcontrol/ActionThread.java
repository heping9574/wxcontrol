package com.hp.wxcontrol;

import static com.hp.wxcontrol.util.Constants.TAG;

import com.hp.wxcontrol.util.ActionQueue;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

public class ActionThread implements Runnable {
	
	Context context;
	
	public ActionThread(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		
		while(true) {
			
			if (ActionQueue.queue.size() > 0) {

				ClipboardManager cm = (ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);

				String action = cm.getText().toString();

				if (action == null || action.equals("")) {

					action = ActionQueue.queue.poll();
				}

				cm.setText(action);
			}
			
//			Log.d(TAG, "ActionThread run queue.size :" + ActionQueue.queue.size());
		}
	}

}
