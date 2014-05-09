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
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import util.SendSMSTask;
import dao.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
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
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */

public class LoginActivity extends Activity {
	
	// UI references.
	private EditText nameView;
	private EditText carNumberView;
	private EditText phoneNumberView;
	
	private SqlHelper helper;
	
	private static final String DEFAULT_HOST = "192.168.43.102";  
	private static final int PORT = 7631;  
	private static final String SERVER_IP="SERVER_IP"; 
	private String host=null;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private TypedArray mNavMenuIconsTypeArray;
	private NavDrawerListAdapter mAdapter;
    private String[] mActivityTitles= new String[]{"我的车库","我要出库"};
	private List<NavDrawerItem> mNavDrawerItems;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ApplicationContainer.getInstance().addActivity(this);
		setContentView(R.layout.activity_login);
		
		SharedPreferences settings = getSharedPreferences("ip_setting", 0);
		host = settings.getString(SERVER_IP, "NOT EXIST");
		
		mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        
      	mNavMenuIconsTypeArray = getResources()
 				.obtainTypedArray(R.array.incomming_icons);
 		
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
        
		helper = new SqlHelper(this, 0);
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
            		Intent intent = new Intent(LoginActivity.this, GoogleCardHomeActivity.class);
            		startActivity(intent);
					try {
						// 实例化Socket
						Socket socket=null;
						if(host=="NOT EXIST"){
								socket = new Socket(DEFAULT_HOST, PORT);
							}else{
								socket = new Socket(host, PORT);
							}
						// 创建socket对象，指定服务器端地址和端口号
						//socket = new Socket(IpAddress, Port);
						// 获取 Client 端的输出流
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream())), true);
						// 填充信息
						out.println(name+"EOF"+carNumber+"EOF"+phoneNumber+"EOF"+time+"EOF"+"INT"+"EOF"+"ENDTRANSMISION");
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
    		Intent intent = new Intent(this, LogoutActivity.class);
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
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
