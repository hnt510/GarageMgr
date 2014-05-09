package org.ninto.garagemgr;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ApplicationContainer extends Application {


	 private List<Activity> activityList = new LinkedList<Activity>(); 
	 private static ApplicationContainer instance;
	 
	            private ApplicationContainer()
	            {
	            }
	             //����ģʽ�л�ȡΨһ��MyApplicationʵ�� 
	             public static ApplicationContainer getInstance()
	             {
	                            if(null == instance)
	                          {
	                             instance = new ApplicationContainer();
	                          }
	                 return instance;             

	             }
	             //���Activity��������
	             public void addActivity(Activity activity)
	             {
	                            activityList.add(activity);
	             }
	             //��������Activity��finish

	             public void exit()
	             {

	                          for(Activity activity:activityList)
	                         {
	                           activity.finish();
	                         }

	                           System.exit(0);

	            }
	}