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

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * Application Container used as activity killer
 * @author ninteo
 *
 */
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

	             public void exit(){
	                  for(Activity activity:activityList){
	                        activity.finish();
	                  }
                      System.exit(0);
	            }
	}