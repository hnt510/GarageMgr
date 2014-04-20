package org.ninto.garagemgr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import util.SendSMSTask;
import dao.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */

public class LoginActivity extends Activity {
	
	// UI references.
	private EditText nameView;
	private EditText carNumberView;
	private EditText phoneNumberView;
	
	private SqlHelper helper;
	
	private static final String HOST = "192.168.2.102";  
	private static final int PORT = 7631;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		helper = new SqlHelper(this, 0);
	}

	/**
	 * button onclick saving things
	 * @param view
	 */
	public void saveInfo(View view){
		//extract 4 values
		nameView=(EditText)findViewById(R.id.name);
		final String name=nameView.getText().toString();
		
		carNumberView=(EditText)findViewById(R.id.carNumber);
		final String carNumber=carNumberView.getText().toString();
		
		phoneNumberView=(EditText)findViewById(R.id.phoneNumber);
		final String phoneNumber=phoneNumberView.getText().toString();
		
		final String time=new SimpleDateFormat("ddHHmm").format(new Date());

		final Handler mHandler = new Handler();
		
		//db operation,we create a thread to handle it
		Runnable doBackGroundOperation = new Runnable() {
			public void run() {
				if(helper.insert(name, carNumber, phoneNumber, time)){
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "�ɹ�����", Toast.LENGTH_SHORT);  
    					//��ʾtoast��Ϣ  
    					toast.show(); 
    					Timer timer = new Timer();
    					timer.schedule(new SendSMSTask(phoneNumber,carNumber), 10000);
                    }
                    });
                    //jump

            		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            		startActivity(intent);
					try {
						// ʵ����Socket
						Socket socket = new Socket(HOST, PORT);
						// ����socket����ָ���������˵�ַ�Ͷ˿ں�
						//socket = new Socket(IpAddress, Port);
						// ��ȡ Client �˵������
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream())), true);
						// �����Ϣ
						out.println(name+" "+carNumber+" "+phoneNumber+" "+time);
						//System.out.println("msg=" + edittext.getText());
						// �ر�
						out.close();
						socket.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "ʧ����", Toast.LENGTH_SHORT);  
    					//��ʾtoast��Ϣ  
    					toast.show();
                    }
                    });
				}
			}
		};
		Thread thread = new Thread(doBackGroundOperation,"BackGroundOperation");
		thread.start();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
