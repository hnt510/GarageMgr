package util;

import java.util.TimerTask;


public class SendSMSTask extends TimerTask {
	String sendTo;
	String carnum;
	public SendSMSTask(String PHONE_NUMBER,String CAR_NUMBER){
		sendTo=PHONE_NUMBER;
		carnum=CAR_NUMBER;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		AppUtil.sendReminder(sendTo,carnum);
	}

}
