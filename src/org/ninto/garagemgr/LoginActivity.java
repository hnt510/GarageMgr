package org.ninto.garagemgr;

import java.text.SimpleDateFormat;
import java.util.Date;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		helper = new SqlHelper(this, 0);
	}

	/**
	 * button onclick
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
		Runnable doDbOperation = new Runnable() {
			public void run() {				
				if(helper.insert(name, carNumber, phoneNumber, time)){
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "成功保存", Toast.LENGTH_SHORT);  
    					//显示toast信息  
    					toast.show(); 
                    }
                    });
                    //jump
					Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(intent);
				}else{
                    mHandler.post(new Runnable(){
                    public void run(){
    					Toast toast=Toast.makeText(getApplicationContext(), "失败了", Toast.LENGTH_SHORT);  
    					//显示toast信息  
    					toast.show();
                    }
                    });
				}
			}
		};
		Thread thread = new Thread(doDbOperation,"dbOperate");
		thread.start();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
