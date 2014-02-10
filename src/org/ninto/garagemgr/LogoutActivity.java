package org.ninto.garagemgr;

import java.text.SimpleDateFormat;
import java.util.Date;

import util.AppUtil;
import dao.SqlHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogoutActivity extends Activity {

	private static final String CAR_NUMBER = "CAR_NUMBER";
	private static final String NAME = "NAME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String TIME = "TIME";
	
	private EditText carNumberView;
	private SqlHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		
		helper = new SqlHelper(this, 0); 
	}

	//main logic
	public void checkUserFee(View view){
		carNumberView=(EditText)findViewById(R.id.carLogout);
		String carNumber=carNumberView.getText().toString();
		String time;
		try{
			Cursor cur=helper.query(carNumber);
			//get parking time out
				int TIME_COLUMN_INDEX = cur.getColumnIndexOrThrow(TIME);
				try{
					time = cur.getString(TIME_COLUMN_INDEX);					
					//calculate parking fees
					int duration=AppUtil.convertTime(new SimpleDateFormat("ddHHmm").format(new Date())) 
							- AppUtil.convertTime(time);
					showFeeDialog(LogoutActivity.this, duration, time);
				}catch(Exception e){
					Toast toast=Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);  
					//��ʾtoast��Ϣ  
					toast.show(); 
				}

		}catch(SQLException e){
			Toast toast=Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);  
			//��ʾtoast��Ϣ  
			toast.show(); 
	}
	}
	
    private void showFeeDialog(Context context,int duration,final String time) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   
        builder.setTitle("��Ǯ");  
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
        builder.setPositiveButton("��",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        try{
                        	if(helper.delete(time)==0){
            					Toast toast=Toast.makeText(getApplicationContext(), "ɾ��ʧ��", Toast.LENGTH_SHORT);  
            					//��ʾtoast��Ϣ  
            					toast.show();
                        	}else{
                        		Toast toast=Toast.makeText(getApplicationContext(), "���������", Toast.LENGTH_SHORT);  
            					//��ʾtoast��Ϣ  
            					toast.show();
                        	}
                        }catch(SQLException e){
        					Toast toast=Toast.makeText(getApplicationContext(), "���ݿ����", Toast.LENGTH_SHORT);  
        					//��ʾtoast��Ϣ  
        					toast.show();
                        }  
                    }  
                });  
        builder.setNeutralButton("����",  
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
