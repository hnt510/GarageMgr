package util;

import android.telephony.SmsManager;

public class AppUtil {
	/**
	 * ���Ͷ���
	 * @param sendTo ����Ҫ���͵���
	 */
	public static void sendReminder(String sendTo) {
		SmsManager smsManager = SmsManager.getDefault();
		String myMessage = "����ͣ��ʱ���Ѿ��쵽2��Сʱ�ˣ�";
		smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
	}
}
