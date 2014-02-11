package util;

import java.util.TimerTask;


public class SendSMSTask extends TimerTask {
	String sendTo;
	public SendSMSTask(String PHONE_NUMBER){
		sendTo=PHONE_NUMBER;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		AppUtil.sendReminder(sendTo);
	}

}
