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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Filter; 
 




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
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mActivityTitles= new String[]{"我要进库","我要出库"};
	
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
        mDrawerList.setAdapter(new ArrayAdapter_Google<String>(this,
                R.layout.drawer_list_item, mActivityTitles));
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
			// TODO: handle exception
		}
		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);

		mGoogleCardsAdapter = new GoogleCardsAdapter(this,userList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(listView);

		listView.setAdapter(swingBottomInAnimationAdapter);

		mGoogleCardsAdapter.addAll(getItems());
		
		Intent intent = new Intent(this, SocketServer.class);
		startService(intent);
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
	        // update the main content by replacing fragments
	        //Fragment fragment = new PlanetFragment();
	        //Bundle args = new Bundle();
	        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
	        //fragment.setArguments(args);

	        //FragmentManager fragmentManager = getFragmentManager();
	        //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

	        // update selected item and title, then close the drawer
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
	        //setTitle(mPlanetTitles[position]);
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
	    
	    
	    /*
	     * Google's ArrayAdapter. I have no idea what is this about... I delete part of its code,but it still works fine..
	     */
	    /**
	     * A ListAdapter that manages a ListView backed by an array of arbitrary
	     * objects.  By default this class expects that the provided resource id references
	     * a single TextView.  If you want to use a more complex layout, use the constructors that
	     * also takes a field id.  That field id should reference a TextView in the larger layout
	     * resource.
	     *
	     * However the TextView is referenced, it will be filled with the toString() of each object in
	     * the array. You can add lists or arrays of custom objects. Override the toString() method
	     * of your objects to determine what text will be displayed for the item in the list.
	     *
	     * To use something other than TextViews for the array display, for instance, ImageViews,
	     * or to have some of data besides toString() results fill the views,
	     * override {@link #getView(int, View, ViewGroup)} to return the type of view you want.
	     */
	    public class ArrayAdapter_Google<T> extends BaseAdapter implements Filterable {
	        /**
	         * Contains the list of objects that represent the data of this ArrayAdapter.
	         * The content of this list is referred to as "the array" in the documentation.
	         */
	        private List<T> mObjects;

	        /**
	         * Lock used to modify the content of {@link #mObjects}. Any write operation
	         * performed on the array should be synchronized on this lock. This lock is also
	         * used by the filter (see {@link #getFilter()} to make a synchronized copy of
	         * the original array of data.
	         */
	        private final Object mLock = new Object();

	        /**
	         * The resource indicating what views to inflate to display the content of this
	         * array adapter.
	         */
	        private int mResource;

	        /**
	         * The resource indicating what views to inflate to display the content of this
	         * array adapter in a drop down widget.
	         */
	        private int mDropDownResource;

	        /**
	         * If the inflated resource is not a TextView, {@link #mFieldId} is used to find
	         * a TextView inside the inflated views hierarchy. This field must contain the
	         * identifier that matches the one defined in the resource file.
	         */
	        private int mFieldId = 0;

	        /**
	         * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
	         * {@link #mObjects} is modified.
	         */
	        private boolean mNotifyOnChange = true;

	        private Context mContext;

	        private ArrayList<T> mOriginalValues;
	        //private ArrayFilter mFilter;

	        private LayoutInflater mInflater;

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
	         *                 instantiating views.
	         */
	        public ArrayAdapter_Google(Context context, int textViewResourceId) {
	            init(context, textViewResourceId, 0, new ArrayList<T>());
	        }

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param resource The resource ID for a layout file containing a layout to use when
	         *                 instantiating views.
	         * @param textViewResourceId The id of the TextView within the layout resource to be populated
	         */
	        public ArrayAdapter_Google(Context context, int resource, int textViewResourceId) {
	            init(context, resource, textViewResourceId, new ArrayList<T>());
	        }

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
	         *                 instantiating views.
	         * @param objects The objects to represent in the ListView.
	         */
	        public ArrayAdapter_Google(Context context, int textViewResourceId, T[] objects) {
	            init(context, textViewResourceId, 0, Arrays.asList(objects));
	        }

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param resource The resource ID for a layout file containing a layout to use when
	         *                 instantiating views.
	         * @param textViewResourceId The id of the TextView within the layout resource to be populated
	         * @param objects The objects to represent in the ListView.
	         */
	        public ArrayAdapter_Google(Context context, int resource, int textViewResourceId, T[] objects) {
	            init(context, resource, textViewResourceId, Arrays.asList(objects));
	        }

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
	         *                 instantiating views.
	         * @param objects The objects to represent in the ListView.
	         */
	        public ArrayAdapter_Google(Context context, int textViewResourceId, List<T> objects) {
	            init(context, textViewResourceId, 0, objects);
	        }

	        /**
	         * Constructor
	         *
	         * @param context The current context.
	         * @param resource The resource ID for a layout file containing a layout to use when
	         *                 instantiating views.
	         * @param textViewResourceId The id of the TextView within the layout resource to be populated
	         * @param objects The objects to represent in the ListView.
	         */
	        public ArrayAdapter_Google(Context context, int resource, int textViewResourceId, List<T> objects) {
	            init(context, resource, textViewResourceId, objects);
	        }

	        /**
	         * Adds the specified object at the end of the array.
	         *
	         * @param object The object to add at the end of the array.
	         */
	        public void add(T object) {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    mOriginalValues.add(object);
	                    if (mNotifyOnChange) notifyDataSetChanged();
	                }
	            } else {
	                mObjects.add(object);
	                if (mNotifyOnChange) notifyDataSetChanged();
	            }
	        }

	        /**
	         * Adds the specified Collection at the end of the array.
	         *
	         * @param collection The Collection to add at the end of the array.
	         */
	        public void addAll(Collection<? extends T> collection) {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    mOriginalValues.addAll(collection);
	                    if (mNotifyOnChange) notifyDataSetChanged();
	                }
	            } else {
	                mObjects.addAll(collection);
	                if (mNotifyOnChange) notifyDataSetChanged();
	            }
	        }

	        /**
	         * Adds the specified items at the end of the array.
	         *
	         * @param items The items to add at the end of the array.
	         */
	        public void addAll(T ... items) {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    for (T item : items) {
	                        mOriginalValues.add(item);
	                    }
	                    if (mNotifyOnChange) notifyDataSetChanged();
	                }
	            } else {
	                for (T item : items) {
	                    mObjects.add(item);
	                }
	                if (mNotifyOnChange) notifyDataSetChanged();
	            }
	        }

	        /**
	         * Inserts the specified object at the specified index in the array.
	         *
	         * @param object The object to insert into the array.
	         * @param index The index at which the object must be inserted.
	         */
	        public void insert(T object, int index) {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    mOriginalValues.add(index, object);
	                    if (mNotifyOnChange) notifyDataSetChanged();
	                }
	            } else {
	                mObjects.add(index, object);
	                if (mNotifyOnChange) notifyDataSetChanged();
	            }
	        }

	        /**
	         * Removes the specified object from the array.
	         *
	         * @param object The object to remove.
	         */
	        public void remove(T object) {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    mOriginalValues.remove(object);
	                }
	            } else {
	                mObjects.remove(object);
	            }
	            if (mNotifyOnChange) notifyDataSetChanged();
	        }

	        /**
	         * Remove all elements from the list.
	         */
	        public void clear() {
	            if (mOriginalValues != null) {
	                synchronized (mLock) {
	                    mOriginalValues.clear();
	                }
	            } else {
	                mObjects.clear();
	            }
	            if (mNotifyOnChange) notifyDataSetChanged();
	        }

	        /**
	         * Sorts the content of this adapter using the specified comparator.
	         *
	         * @param comparator The comparator used to sort the objects contained
	         *        in this adapter.
	         */
	        public void sort(Comparator<? super T> comparator) {
	            Collections.sort(mObjects, comparator);
	            if (mNotifyOnChange) notifyDataSetChanged();
	        }

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public void notifyDataSetChanged() {
	            super.notifyDataSetChanged();
	            mNotifyOnChange = true;
	        }

	        /**
	         * Control whether methods that change the list ({@link #add},
	         * {@link #insert}, {@link #remove}, {@link #clear}) automatically call
	         * {@link #notifyDataSetChanged}.  If set to false, caller must
	         * manually call notifyDataSetChanged() to have the changes
	         * reflected in the attached view.
	         *
	         * The default is true, and calling notifyDataSetChanged()
	         * resets the flag to true.
	         *
	         * @param notifyOnChange if true, modifications to the list will
	         *                       automatically call {@link
	         *                       #notifyDataSetChanged}
	         */
	        public void setNotifyOnChange(boolean notifyOnChange) {
	            mNotifyOnChange = notifyOnChange;
	        }

	        private void init(Context context, int resource, int textViewResourceId, List<T> objects) {
	            mContext = context;
	            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            mResource = mDropDownResource = resource;
	            mObjects = objects;
	            mFieldId = textViewResourceId;
	        }

	        /**
	         * Returns the context associated with this array adapter. The context is used
	         * to create views from the resource passed to the constructor.
	         *
	         * @return The Context associated with this adapter.
	         */
	        public Context getContext() {
	            return mContext;
	        }

	        /**
	         * {@inheritDoc}
	         */
	        public int getCount() {
	            return mObjects.size();
	        }

	        /**
	         * {@inheritDoc}
	         */
	        public T getItem(int position) {
	            return mObjects.get(position);
	        }

	        /**
	         * Returns the position of the specified item in the array.
	         *
	         * @param item The item to retrieve the position of.
	         *
	         * @return The position of the specified item.
	         */
	        public int getPosition(T item) {
	            return mObjects.indexOf(item);
	        }

	        /**
	         * {@inheritDoc}
	         */
	        public long getItemId(int position) {
	            return position;
	        }

	        /**
	         * {@inheritDoc}
	         */
	        public View getView(int position, View convertView, ViewGroup parent) {
	            return createViewFromResource(position, convertView, parent, mResource);
	        }

	        private View createViewFromResource(int position, View convertView, ViewGroup parent,
	                int resource) {
	            View view;
	            TextView text;

	            if (convertView == null) {
	                view = mInflater.inflate(resource, parent, false);
	            } else {
	                view = convertView;
	            }

	            try {
	                if (mFieldId == 0) {
	                    //  If no custom field is assigned, assume the whole resource is a TextView
	                    text = (TextView) view;
	                } else {
	                    //  Otherwise, find the TextView field within the layout
	                    text = (TextView) view.findViewById(mFieldId);
	                }
	            } catch (ClassCastException e) {
	                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
	                throw new IllegalStateException(
	                        "ArrayAdapter requires the resource ID to be a TextView", e);
	            }

	            T item = getItem(position);
	            if (item instanceof CharSequence) {
	                text.setText((CharSequence)item);
	            } else {
	                text.setText(item.toString());
	            }

	            return view;
	        }

	        /**
	         * <p>Sets the layout resource to create the drop down views.</p>
	         *
	         * @param resource the layout resource defining the drop down views
	         * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
	         */
	        public void setDropDownViewResource(int resource) {
	            this.mDropDownResource = resource;
	        }

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public View getDropDownView(int position, View convertView, ViewGroup parent) {
	            return createViewFromResource(position, convertView, parent, mDropDownResource);
	        }

	        /**
	         * Creates a new ArrayAdapter from external resources. The content of the array is
	         * obtained through {@link android.content.res.Resources#getTextArray(int)}.
	         *
	         * @param context The application's environment.
	         * @param textArrayResId The identifier of the array to use as the data source.
	         * @param textViewResId The identifier of the layout used to create views.
	         *
	         * @return An ArrayAdapter<CharSequence>.
	         */
	        public ArrayAdapter_Google<CharSequence> createFromResource(Context context,
	                int textArrayResId, int textViewResId) {
	            CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
	            return new ArrayAdapter_Google<CharSequence>(context, textViewResId, strings);
	        }

			@Override
			public android.widget.Filter getFilter() {
				// TODO Auto-generated method stub
				return null;
			}


	       
	        }
		
	}

