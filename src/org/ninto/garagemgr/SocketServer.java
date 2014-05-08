package org.ninto.garagemgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import dao.SqlHelper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * network listener service
 * @author ninteo
 *
 */
public class SocketServer extends Service{
	
	private static final int PORT = 24358; 
	private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private Handler workHandler = new Handler();
    private SqlHelper helper;
    private byte[] msg = new byte[12000];
    
    //User data structure
    class User{
        public String NAME;
        public String CAR_NUMBER;
        public String PHONE_NUMBER;
        public String TIME;
    }
    
    @Override
    public void onCreate() {    
        startSocketServer();
        startUdpServer();
        helper = new SqlHelper(this, 0); 
        super.onCreate();
    }


    public void startUdpServer() {
		// TODO Auto-generated method stub
    	new Thread(new UdpRunnable()).start();
	}

    public class UdpRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			DatagramSocket dSocket = null;
			DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
			String inputString = null;
			User usr = new User();
			try {
				dSocket = new DatagramSocket(23654);
				while (true) {
					try {
						dSocket.receive(dPacket);
						inputString = new String(dPacket.getData());
						usr = extractUser(inputString);
						final String carNum = usr.CAR_NUMBER;
						notifyUser(usr);
						workHandler.post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								deleteUser(carNum);
							}
							
						});

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
    	
    }

	@Override
    public void onDestroy() {
        //startSocketServer();
        //helperListener = null;
        super.onDestroy();
    }
    
    /**
     * listening
     */
    public void startSocketServer() {
        
            try {
            	//start listen on PORT
                serverSocket = new ServerSocket(PORT);
                    
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            //start a new thread to listen
            new Thread(new AcceptRunnable()).start();
        
    }
    
    /*
     * Accept
     */
    public class AcceptRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    /**
                     * 监听连接， 阻塞
                     */
                    final Socket client  = serverSocket.accept();
                    
                    /**
                     * 回调主线程
                     */
                    workHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        	clientSocket = client;
                        	new Thread(new RecieverRunnable()).start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }    
            }
        }
    }

    
    
    /**
     * Socket read 线程（非阻塞）
     */
    public class RecieverRunnable implements Runnable {
        private boolean runnable = true;
        private BufferedReader read = null;
        String inputString = null;
        @Override
        public void run() {
            while (runnable) {
                try {
                	//read user info from clientSocket's inputeStream
                	read = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    inputString = read.readLine();
                    runnable=false;
                } catch (IOException e) {
                    runnable = false;
                    e.printStackTrace();
                }
                
                    workHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        	try {
                        		clientSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	
                            User usr=new User();
                        	usr = extractUser(inputString);
                        	//show notification
                        	notifyUser(usr);
                        	//delete user info in database
                        	deleteUser(usr.CAR_NUMBER);
                        }
                    });
                }
            }
        }
    
    private User extractUser(String inputString){
        User usr=new User();
    	//extract user info
    	String [] split=inputString.split("EOF");
    	usr.NAME=split[0];
    	usr.CAR_NUMBER=split[1];
    	usr.PHONE_NUMBER=split[2];
    	usr.TIME=split[3].substring(0, 6);
    	return usr;
    	
    }
    
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	private void notifyUser(User usr) {
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("有车出库")
		        .setVibrate(new long[]{100,250,100,500})
		        .setContentText("车主 "+usr.NAME+" 车牌号 "+usr.CAR_NUMBER+" 的用户已经出库！");
		
		NotificationCompat.InboxStyle inboxStyle =
		        new NotificationCompat.InboxStyle();
		
		inboxStyle.addLine("车主 "+usr.NAME+" 车牌号 "+usr.CAR_NUMBER+" 的用户已经出库！");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, HomeActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(HomeActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mBuilder.setStyle(inboxStyle);
		mNotificationManager.notify(510, mBuilder.build());
	}
	
	private void deleteUser(String carNum) {
		// TODO Auto-generated method stub
    	if(helper.delete(carNum)==0){
			Toast toast=Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT);  
			//显示toast信息  
			toast.show();
    	}else{
    		Toast toast=Toast.makeText(getApplicationContext(), "汽车已出库", Toast.LENGTH_SHORT);  
			//显示toast信息  
			toast.show();
    	}
	}
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
