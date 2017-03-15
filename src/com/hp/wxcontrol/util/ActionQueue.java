package com.hp.wxcontrol.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActionQueue {
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

}
