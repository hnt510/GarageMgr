package util;

import android.telephony.SmsManager;

public class AppUtil {
	/**
	 * 发送短信
	 * @param sendTo 
	 */
	public static void sendReminder(String sendTo,String carnum) {
		SmsManager smsManager = SmsManager.getDefault();
		String myMessage = "车牌号为"+carnum+"的车主"+"您的停车时间已经到2个小时了！";
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
