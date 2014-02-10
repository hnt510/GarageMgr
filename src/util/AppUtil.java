package util;

import android.telephony.SmsManager;

public class AppUtil {
	/**
	 * ���Ͷ���
	 * @param sendTo 
	 */
	public static void sendReminder(String sendTo) {
		SmsManager smsManager = SmsManager.getDefault();
		String myMessage = "����ͣ��ʱ���Ѿ��쵽2��Сʱ�ˣ�";
		smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
	}
	/**
	 * convert to total minutes
	 * @param time
	 * @return total minutes that the String time represent
	 */
	public static int convertTime(String time){
		char[] c=new char[time.length()];
		c=time.toCharArray();
		int dd=(c[0]-'0')*10+(c[1]-'0');
		int hh=(c[2]-'0')*10+(c[3]-'0');
		int mm=(c[4]-'0')*10+(c[5]-'0');
		return mm+hh*60+dd*60*24;
	}
}
