package org.ninto.garagemgr;

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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.AppUtil;
import dao.SqlHelper;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private TypedArray mNavMenuIconsTypeArray;
	private NavDrawerListAdapter mAdapter;
    private String[] mActivityTitles= new String[]{"我的车库","我要进库"};
	private List<NavDrawerItem> mNavDrawerItems;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	
	private ConfirmationReceiver receiver;
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
		
		ApplicationContainer.getInstance().addActivity(this);
		
		setContentView(R.layout.activity_logout);
		SharedPreferences settings = getSharedPreferences("ip_setting", 0);
		host = settings.getString(SERVER_IP, "NOT EXIST");
		
		mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        
      	mNavMenuIconsTypeArray = getResources()
 				.obtainTypedArray(R.array.outcomming_icons);
 		
        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        
		// adding nav drawer items to array
		// Home
		mNavDrawerItems.add(new NavDrawerItem(mActivityTitles[0], mNavMenuIconsTypeArray
				.getResourceId(0, -1)));
		// Find People
		mNavDrawerItems.add(new NavDrawerItem(mActivityTitles[1], mNavMenuIconsTypeArray
				.getResourceId(1, -1)));
        
		// Recycle the typed array
		mNavMenuIconsTypeArray.recycle();
        
		// setting the nav drawer list adapter
		mAdapter = new NavDrawerListAdapter(getApplicationContext(),
						mNavDrawerItems);
		
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }
		
        registerBroadcastReceiver();
		helper = new SqlHelper(this, 0); 
	}

	  
	private void registerBroadcastReceiver() {
		receiver = new ConfirmationReceiver();
		IntentFilter filter = new IntentFilter("android.intent.action.DELETE_CONFIRM");
		registerReceiver(receiver, filter);
	}


	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
	   Intent home = new Intent(Intent.ACTION_MAIN);
	   home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	   home.addCategory(Intent.CATEGORY_HOME);
	   startActivity(home);
	   ApplicationContainer.getInstance().exit(); 
	  }
	  return super.onKeyDown(keyCode, event);
	 }
	
	//button onclick
	@SuppressLint("SimpleDateFormat")
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
					new Thread(new SendRunnable("OUTREQ")).start();
					Toast toast=Toast.makeText(getApplicationContext(), "等待服务端确认出库中...", Toast.LENGTH_SHORT);  
					//显示toast信息  
					toast.show(); 
				}catch(Exception e){
					Toast toast=Toast.makeText(getApplicationContext(), "未找到该车主信息", Toast.LENGTH_SHORT);  
					//显示toast信息  
					toast.show(); 
				}

		}catch(SQLException e){
			Toast toast=Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);  
			//显示toast信息  
			toast.show(); 
	}
	}
	
	public class SendRunnable implements Runnable{
		
		String sendType; 
		Socket socket = null;
		public SendRunnable(String type){
			sendType=type;
		}
		@Override
		public void run() {
			try {
				// 实例化Socket

				if(host=="NOT EXIST"){
					socket = new Socket(DEFAULT_HOST, PORT);
				}else{
					socket = new Socket(host, PORT);
				}
				if(sendType.equals("OUTREQ")){
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())), true);
					out.println(usr.name+"EOF"+usr.carNum+"EOF"+usr.phoneNum+
							"EOF"+usr.time+"EOF"+"OUTREQ"+"EOF"+"ENDTRANSMISION");
					out.close();
				}else{
					PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				// 填充信息
					out.println(usr.name+"EOF"+usr.carNum+"EOF"+usr.phoneNum+
							"EOF"+usr.time+"EOF"+"OUT"+"EOF"+"ENDTRANSMISION");
					out.close();
				}
				//System.out.println("msg=" + edittext.getText());
				// 关闭
			} catch (Exception e) {
				e.printStackTrace();
				try {
					socket.shutdownOutput();
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			try {
				socket.shutdownOutput();
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
    private void showFeeDialog(Context context,int duration,final String carNum) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   
        builder.setTitle("总停车费用");  
        //calculate fee
		if(duration<=60){
			double fee= Number2(duration*0.1);
			builder.setMessage("您目前已停"+String.valueOf(duration)
					+"分钟，需要交"+String.valueOf(fee)+"元"); 
		}else if(duration<=120){
			double fee = Number2(60*0.1+(duration-60)*0.2);
			builder.setMessage("您目前已停"+String.valueOf(duration)
					+"分钟，需要交"+String.valueOf(fee)+"元"); 
		}else {
			double fee = Number2(60*0.1+60*0.2+(duration-120)*0.3);
			builder.setMessage("您目前已停"+String.valueOf(duration)
					+"分钟，需要交"+String.valueOf(fee)+"元");
		} 
    	//delete user info in database
        builder.setPositiveButton("我要出库",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) { 
                        	if(helper.delete(carNum)==0){
            					Toast toast=Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT);  
            					//显示toast信息  
            					toast.show();
                        	}else{
                        		Intent intent = new Intent(LogoutActivity.this, GoogleCardHomeActivity.class);
                        		startActivity(intent);
                        		Toast toast=Toast.makeText(getApplicationContext(), "汽车已出库", Toast.LENGTH_SHORT);  
            					//显示toast信息  
            					toast.show();
                        	}
                    		new Thread(new SendRunnable("OUT")).start();
                    }  
                });  
        //do nothing
        builder.setNeutralButton("暂时不出库",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {   
                    }  
                });    
        builder.show();  
    }  

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
	
    private void selectItem(int position) {

    	if(position==0){
    		Intent intent = new Intent(this, GoogleCardHomeActivity.class);
    		startActivity(intent);
    	}else if(position==1){
    		Intent intent = new Intent(this, LoginActivity.class);
    		startActivity(intent);
    	}else{
    		Log.w("Error", "Drawer Wrong!");
    	}
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logout, menu);
		return true;
	}
	
    public class ConfirmationReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("android.intent.action.DELETE_CONFIRM")){
	        	String stringUser = intent.getStringExtra("User");
	        	String [] split=stringUser.split("EOF");
	        	String time = split[3];
	        	int duration=AppUtil.convertTime(new SimpleDateFormat("ddHHmm").format(new Date())) 
						- AppUtil.convertTime(time);
	        	showFeeDialog(LogoutActivity.this, duration, split[1]);
			}
		}
    	
    }
	
	public double Number2(double pDouble) 
	{ 
	   BigDecimal bd=new BigDecimal(pDouble); 
	   @SuppressWarnings("static-access")
	BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP); 
	   pDouble=bd1.doubleValue(); 
	   @SuppressWarnings("unused")
	long ll = Double.doubleToLongBits(pDouble); 	  
	   return pDouble; 
	} 

}
