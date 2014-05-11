package util;
/*
 * Copyright 2014 Huang Ning Tao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.telephony.SmsManager;

/**
 * Application utility
 * @author ninteo
 *
 */
public class AppUtil {
	/**
	 * 发送短信
	 * @param sendTo 
	 */
	public static void sendReminder(String sendTo,String carnum) {
		SmsManager smsManager = SmsManager.getDefault();
		String myMessage = "车牌号为"+carnum+"的车主"+"您的停车时间已经到2个小时了！";
		//if()
		smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
	}
	/**
	 * convert parking time
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
	/**
	 * check if phone number valid
	 * @param mobile
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean isValidMobile(String mobile){
		String MOBILE = "^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
		String CM = "^1(34[0-8]|(3[5-9]|5[017-9]|8[278])\\d)\\d{7}$";
		String CU = "^1(3[0-2]|5[256]|8[56])\\d{8}$";
		String CT = "^1((33|53|8[09])[0-9]|349)\\d{7}$";
		String PHS = "^0(10|2[0-5789]|\\d{3})\\d{7,8}$"; 
		Pattern pattern = Pattern.compile(MOBILE); 
		Matcher matcher = pattern.matcher(mobile); 
		return matcher.matches();
	}
}
