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
	             //单例模式中获取唯一的MyApplication实例 
	             public static ApplicationContainer getInstance()
	             {
	                            if(null == instance)
	                          {
	                             instance = new ApplicationContainer();
	                          }
	                 return instance;             

	             }
	             //添加Activity到容器中
	             public void addActivity(Activity activity)
	             {
	                            activityList.add(activity);
	             }
	             //遍历所有Activity并finish

	             public void exit()
	             {

	                          for(Activity activity:activityList)
	                         {
	                           activity.finish();
	                         }

	                           System.exit(0);

	            }
	}