package org.ninto.garagemgr;

/*
 * Copyright 2013 Niek Haarman
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

/* 
 * Copyright (C) 2006 The Android Open Source Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */  

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Filter; 
 

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import dao.SqlHelper;

public class GoogleCardHomeActivity extends Activity {

	private static final String CAR_NUMBER = "CAR_NUMBER";
	private static final String NAME = "NAME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String TIME = "TIME";
	private static final String SERVER_IP="SERVER_IP"; 
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	private TypedArray mNavMenuIconsTypeArray;
	private NavDrawerListAdapter mAdapter;
    private String[] mActivityTitles= new String[]{"我要进库","我要出库"};
	private List<NavDrawerItem> mNavDrawerItems;
	
	private GoogleCardsAdapter mGoogleCardsAdapter;
	private SqlHelper helper;
	public static class User{
		String name;
		String phoneNum;
		String carNum;
		String time;
	}

	public ArrayList<User> userList = new ArrayList<User>(); 
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_googlecards_main);
		
		mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        
      	mNavMenuIconsTypeArray = getResources()
 				.obtainTypedArray(R.array.nav_drawer_icons);
 		
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
		try {
			Cursor cur=helper.query_all();
			int NAME_COLUMN_INDEX = cur.getColumnIndex(NAME);
			int CARNUM__COLUMN_INDEX=cur.getColumnIndex(CAR_NUMBER);
			int PHONENUM_COLUMN_INDEX=cur.getColumnIndex(PHONE_NUMBER);
			int TIME_COLUMN_INDEX = cur.getColumnIndexOrThrow(TIME);
			if(cur.getCount()!=0){
				while (cur.moveToNext()) {
					User usr = new User();
					usr.name=cur.getString(NAME_COLUMN_INDEX);
					usr.carNum=cur.getString(CARNUM__COLUMN_INDEX);
					usr.phoneNum=cur.getString(PHONENUM_COLUMN_INDEX);
					usr.time = cur.getString(TIME_COLUMN_INDEX);
					userList.add(usr);
					}
				}else {
					User usr = new User();
					usr.name="空的";
					usr.carNum="无";
					usr.phoneNum="无";
					usr.time="无";
					userList.add(usr);
			}
		} catch (Exception e) {
			Log.e("SQLERROR","Query Execute Error!");
		}
		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);

		mGoogleCardsAdapter = new GoogleCardsAdapter(this,userList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mGoogleCardsAdapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(listView);

		listView.setAdapter(swingBottomInAnimationAdapter);

		mGoogleCardsAdapter.addAll(getItems());
		
		Intent intent = new Intent(this, SocketServer.class);
		startService(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	private ArrayList<Integer> getItems() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		for (int i = 0; i < userList.size(); i++) {
			items.add(i);
		}
		return items;
	}


	private static class GoogleCardsAdapter extends ArrayAdapter<Integer> {

		public ArrayList<User> userList = new ArrayList<User>(); 
		private final Context mContext;
		org.ninto.garagemgr.GoogleCardHomeActivity.User usr = new org.ninto.garagemgr.GoogleCardHomeActivity.User();
		
		public GoogleCardsAdapter(final Context context,ArrayList List) {
			mContext = context;
			userList=List;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			ViewHolder viewHolder;
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.googlecards_card, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.nameView = (TextView) view.findViewById(R.id.activity_googlecards_card_name);
				viewHolder.numberView = (TextView) view.findViewById(R.id.activity_googlecards_card_number);
				viewHolder.timeView = (TextView) view.findViewById(R.id.activity_googlecards_card_time);
				view.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			usr=userList.get(getItem(position));
			viewHolder.nameView.setText(usr.name);
			viewHolder.numberView.setText(usr.carNum);
			viewHolder.timeView.setText(usr.time);
			return view;
		}

		}

		private static class ViewHolder {
			TextView numberView;
			TextView nameView;
			TextView timeView;
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.action_settings:
		            popIpDialog(GoogleCardHomeActivity.this);
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
		    LayoutInflater inflater = GoogleCardHomeActivity.this.getLayoutInflater();
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
		
	    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            selectItem(position);
	        }
	    }

	    private void selectItem(int position) {

	    	if(position==0){
	    		Intent intent = new Intent(this, LoginActivity.class);
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
	    
	    

	}

