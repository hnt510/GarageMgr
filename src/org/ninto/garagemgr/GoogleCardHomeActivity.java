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

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import dao.SqlHelper;

public class GoogleCardHomeActivity extends Activity implements OnDismissCallback {

	private static final String CAR_NUMBER = "CAR_NUMBER";
	private static final String NAME = "NAME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String TIME = "TIME";
	private static final String SERVER_IP="SERVER_IP"; 
	
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
			// TODO: handle exception
		}
		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);

		mGoogleCardsAdapter = new GoogleCardsAdapter(this,userList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(listView);

		listView.setAdapter(swingBottomInAnimationAdapter);

		mGoogleCardsAdapter.addAll(getItems());
		
		//Intent intent = new Intent(this, SocketServer.class);
		//startService(intent);
	}

	private ArrayList<Integer> getItems() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		for (int i = 0; i < userList.size(); i++) {
			items.add(i);
		}
		return items;
	}

	@Override
	public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			mGoogleCardsAdapter.remove(position);
		}
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
	}

