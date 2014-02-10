package org.ninto.garagemgr;

import dao.SqlHelper;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
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
		
		int parkingTime;
		try{
			Cursor cur=helper.query(carNumber);
			//get parking time out
			// Find the index to the column(s) being used.
			int TIME_COLUMN_INDEX = cur.getColumnIndexOrThrow(TIME);
			String time = cur.getString(TIME_COLUMN_INDEX);
			parkingTime = Integer.valueOf(time).intValue();
		}catch(SQLException e){
			Toast toast=Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);  
			//œ‘ ætoast–≈œ¢  
			toast.show(); 
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logout, menu);
		return true;
	}

}
