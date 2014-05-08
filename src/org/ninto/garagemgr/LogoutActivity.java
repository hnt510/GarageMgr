package org.ninto.garagemgr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ninto.garagemgr.SocketServer.UdpRunnable;

import util.AppUtil;
import dao.SqlHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Car comes out activity
 * @author ninteo
 *
 */
public class LogoutActivity extends Activity {

	private static final String CAR_NUMBER = "CAR_NUMBER";
	private static final String NAME = "NAME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String TIME = "TIME";
	private static final String DEFAULT_HOST = "192.168.43.102";  
	private static final int PORT = 7631;  
	private static final String SERVER_IP="SERVER_IP"; 
	
	private EditText carNumberView;
	private SqlHelper helper;
	private String host=null;
	class User{
		String name;
		String phoneNum;
		String carNum;
		String time;
	}
	
	public User usr= new User();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		SharedPreferences settings = getSharedPreferences("ip_setting", 0);
		host = settings.getString(SERVER_IP, "NOT EXIST");
		helper = new SqlHelper(this, 0); 
	}

	//button onclick
	public void checkUserFee(View view){
		carNumberView=(EditText)findViewById(R.id.carLogout);
		String carNumber=carNumberView.getText().toString();
		try{
			Cursor cur=helper.query(carNumber);
			//work parking time out
				int NAME_COLUMN_INDEX = cur.getColumnIndex(NAME);
				int CARNUM__COLUMN_INDEX=cur.getColumnIndex(CAR_NUMBER);
				int PHONENUM_COLUMN_INDEX=cur.getColumnIndex(PHONE_NUMBER);
				int TIME_COLUMN_INDEX = cur.getColumnIndexOrThrow(TIME);
				try{
					usr.name=cur.getString(NAME_COLUMN_INDEX);
					usr.carNum=cur.getString(CARNUM__COLUMN_INDEX);
					usr.phoneNum=cur.getString(PHONENUM_COLUMN_INDEX);
					usr.time = cur.getString(TIME_COLUMN_INDEX);					
					//calculate parking time
					int duration=AppUtil.convertTime(new SimpleDateFormat("ddHHmm").format(new Date())) 
							- AppUtil.convertTime(usr.time);
					//logout dialog
					showFeeDialog(LogoutActivity.this, duration, usr.carNum);
				}catch(Exception e){
					Toast toast=Toast.makeText(getApplicationContext(), "δ�ҵ��ó�����Ϣ", Toast.LENGTH_SHORT);  
					//��ʾtoast��Ϣ  
					toast.show(); 
				}

		}catch(SQLException e){
			Toast toast=Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);  
			//��ʾtoast��Ϣ  
			toast.show(); 
	}
	}
	
	public class SendRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				// ʵ����Socket
				Socket socket = null;
				if(host=="NOT EXIST"){
					socket = new Socket(DEFAULT_HOST, PORT);
				}else{
					socket = new Socket(host, PORT);
				}
				// ����socket����ָ���������˵�ַ�Ͷ˿ں�
				//socket = new Socket(IpAddress, Port);
				// ��ȡ Client �˵������
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				// �����Ϣ
				out.println(usr.name+"EOF"+usr.carNum+"EOF"+usr.phoneNum+"EOF"+usr.time+"EOF"+"OUT");
				//System.out.println("msg=" + edittext.getText());
				// �ر�
				out.close();
				socket.shutdownOutput();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
    private void showFeeDialog(Context context,int duration,final String carNum) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   
        builder.setTitle("��ͣ������");  
        //calculate fee
		if(duration<=60){
			double fee= duration*0.1;
			builder.setMessage("��Ŀǰ��ͣ"+String.valueOf(duration)
					+"���ӣ���Ҫ��"+String.valueOf(fee)+"Ԫ"); 
		}else if(duration<=120){
			double fee = 60*0.1+(duration-60)*0.2;
			builder.setMessage("��Ŀǰ��ͣ"+String.valueOf(duration)
					+"���ӣ���Ҫ��"+String.valueOf(fee)+"Ԫ"); 
		}else {
			double fee = 60*0.1+60*0.2+(duration-120)*0.3;
			builder.setMessage("��Ŀǰ��ͣ"+String.valueOf(duration)
					+"���ӣ���Ҫ��"+String.valueOf(fee)+"Ԫ");
		} 
    	//delete user info in database
        builder.setPositiveButton("��Ҫ����",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) { 
                        	if(helper.delete(carNum)==0){
            					Toast toast=Toast.makeText(getApplicationContext(), "ɾ��ʧ��", Toast.LENGTH_SHORT);  
            					//��ʾtoast��Ϣ  
            					toast.show();
                        	}else{
                        		Toast toast=Toast.makeText(getApplicationContext(), "�����ѳ���", Toast.LENGTH_SHORT);  
            					//��ʾtoast��Ϣ  
            					toast.show();
                        	}
                    		new Thread(new SendRunnable()).start();
                    }  
                });  
        //do nothing
        builder.setNeutralButton("��ʱ������",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {   
                    }  
                });    
        builder.show();  
    }  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logout, menu);
		return true;
	}

}
