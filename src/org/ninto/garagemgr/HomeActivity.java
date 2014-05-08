package org.ninto.garagemgr;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * main user interface
 * @author ninteo
 *
 */
public class HomeActivity extends Activity {
	
	private static final String SERVER_IP="SERVER_IP"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(this, SocketServer.class);
		startService(intent);

	}
	
	public void jumpToLogin(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	public void jumpToLogout(View view) {
		Intent intent = new Intent(this, LogoutActivity.class);
		startActivity(intent);
	}
	
	public void showCard(View view) {
		Intent intent = new Intent(this, GoogleCardHomeActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            popIpDialog(HomeActivity.this);
	            return true;
	        case R.id.return_to_default:
	            clearPreference();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void clearPreference() {
		// TODO Auto-generated method stub
 	   SharedPreferences settings = getSharedPreferences("ip_setting", 0);
 	   SharedPreferences.Editor editor = settings.edit();
 	   editor.clear().commit();
	}

	private void popIpDialog(Context context) {
		// TODO Auto-generated method stub
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle("改变服务端IP地址");  
	    builder.setMessage("输入服务端IP地址");
	    // Get the layout inflater
	    LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
	    final View view = inflater.inflate(R.layout.dialog_ip_change, null);
	    builder.setView(view)
	    // Add action buttons
	           .setPositiveButton(R.string.Change, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   SharedPreferences settings = getSharedPreferences("ip_setting", 0);
	            	   SharedPreferences.Editor editor = settings.edit();
	            	   EditText ipEditText = (EditText)view.findViewById(R.id.ipAddress);
	            	   String ipString=ipEditText.getText().toString().trim();
	            	   if(checkIP(ipString)){
	            		   editor.putString(SERVER_IP,ipString);
	            		   editor.commit();
	            	   }else {
	            		   Toast toast=Toast.makeText(getApplicationContext(), "输入的IP不合法", Toast.LENGTH_SHORT);  
	    					//显示toast信息  
	    					toast.show(); 
					}
	               }
	           })
	           .setNegativeButton(R.string.cancel_change, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   //LoginDialogFragment.this.getDialog().cancel();
	               }
	           });      
	    builder.show();
	}
	public static boolean checkIP(String checkStr)   
	   {   
	       try   {   
	               String number = checkStr.substring(0,checkStr.indexOf('.'));   
	               if(Integer.parseInt(number) > 255) 
	                  return false;   
	               checkStr = checkStr.substring(checkStr.indexOf('.')+ 1);   
	               number = checkStr.substring(0,checkStr.indexOf('.'));   
	               if(Integer.parseInt(number) > 255)
	               return false;   
	               checkStr = checkStr.substring(checkStr.indexOf('.')+ 1);   
	               number = checkStr.substring(0,checkStr.indexOf('.'));   
	               if(Integer.parseInt(number) > 255)
	                return false;   
	               number = checkStr.substring(checkStr.indexOf('.')+ 1);   
	               if (Integer.parseInt(number) > 255)   
	                return false;   
	               return true;   
	              }   
	       catch (Exception e)   
	              {   
	               return false;   
	               }   
	    }

}
