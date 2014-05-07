package util;

import java.util.TimerTask;

import android.widget.Toast;
/**
 * SMS sender timer
 * @author ninteo
 *
 */
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
		if(AppUtil.isValidMobile(sendTo)){
		AppUtil.sendReminder(sendTo,carnum);}
	}
}
