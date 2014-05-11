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
