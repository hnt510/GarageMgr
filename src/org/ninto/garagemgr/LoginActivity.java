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
import android.content.Intent;
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
	
	private static final String HOST = "192.168.43.102";  
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
		
		//db and network operation,we create a thread to handle it
		Runnable doBackGroundOperation = new Runnable() {
			public void run() {
				if(helper.insert(name, carNumber, phoneNumber, time)){
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "汽车已入库", Toast.LENGTH_SHORT);  
    					//显示toast信息  
    					toast.show(); 
    					Timer timer = new Timer();
    					timer.schedule(new SendSMSTask(phoneNumber,carNumber), 10000);
                    }
                    });
                    //jump to HomeActivity
            		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            		startActivity(intent);
					try {
						// 实例化Socket
						Socket socket = new Socket(HOST, PORT);
						// 创建socket对象，指定服务器端地址和端口号
						//socket = new Socket(IpAddress, Port);
						// 获取 Client 端的输出流
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream())), true);
						// 填充信息
						out.println(name+"EOF"+carNumber+"EOF"+phoneNumber+"EOF"+time+"EOF"+"IN");
						//System.out.println("msg=" + edittext.getText());
						// 关闭
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
				}else{
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "数据处理失败", Toast.LENGTH_SHORT);  
    					//显示toast信息  
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
