package util;

import android.telephony.SmsManager;

public class AppUtil {
	/**
	 * 发送短信
	 * @param sendTo 填入要发送的人
	 */
	public static void sendReminder(String sendTo) {
		SmsManager smsManager = SmsManager.getDefault();
		String myMessage = "您的停车时间已经快到2个小时了！";
		smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
	}
}
