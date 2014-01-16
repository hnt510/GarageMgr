package org.ninto.garagemgr;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	// UI references.
	private EditText nameView;
	private EditText carNumberView;
	private EditText phoneNumberView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		

	}

	/**
	 * button onclick
	 * @param view
	 */
	public void saveInfo(View view){
		
		nameView=(EditText)findViewById(R.id.name);
		String name=nameView.getText().toString();
		
		carNumberView=(EditText)findViewById(R.id.carNumber);
		String carNumber=carNumberView.getText().toString();
		
		phoneNumberView=(EditText)findViewById(R.id.phoneNumber);
		String phoneNumber=phoneNumberView.getText().toString();
		
		String time=new SimpleDateFormat("ddHHmm").format(new Date());

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
