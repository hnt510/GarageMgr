package org.ninto.garagemgr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SocketServer extends Service{
	
	private static final int PORT = 24358; 
	private Socket clientSocket = null;
    private DataInputStream cInputStream = null;
    private DataOutputStream cOutputStream = null;
    private ServerSocket serverSocket = null;
    private Handler workHandler = new Handler();
    
    @Override
    public void onCreate() {    
        startSocketServer();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        startSocketServer();
        //helperListener = null;
        super.onDestroy();
    }
    
    /**
     * listening
     */
    public void startSocketServer() {
        
            try {
                /**
                 * ����ServerSocket
                 */
                serverSocket = new ServerSocket(PORT);
                    
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            /**
             * Accept Socket
             */
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
                     * �������ӣ� ����
                     */
                    final Socket client  = serverSocket.accept();
                    
                    /**
                     * �ص����߳�
                     */
                    workHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onGetClient(client);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }    
            }
        }
    }

    private void onGetClient(Socket client) {    
        if(initClientSocket(client)==0) {
          ;
        }        
    }
    
    private int initClientSocket(Socket cSocket) {
        try {
            
            /**
             * ����
             */
            if  (cInputStream != null) {
                cInputStream.close();
                cInputStream = null;
            }            
            if  (cOutputStream != null) {
                cOutputStream.close();
                cOutputStream = null;
            }
            if  (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
            
            /**
             * ����
             */
            clientSocket = cSocket;
            cInputStream = new DataInputStream(clientSocket.getInputStream());
            cOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            new Thread(new RecieverRunnable()).start();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    
    /**
     * Socket read �̣߳���������
     */
    public class RecieverRunnable implements Runnable {
        private boolean runnable = true;
        @Override
        public void run() {
            while (runnable) {
                String inputString = null;
                int len = 0;
                try {
                    /**
                     * �ȴ�read���ݰ���С������
                     */
                    len = cInputStream.readInt();
                    
                    /**
                     * read�����ݰ���С��read���ݣ�����
                     */
                    /*if (len > 0) {
                        byte[] input = new byte[len];
                        int l = -1;
                        int readlen = 0;
                        while(len-readlen > 0 && (l = cInputStream.read(input, readlen , len-readlen)) != -1){
                            readlen += l;
                        }
                        inputString = URLDecoder.decode(new String(input), "UTF-8");*/
                    
                    
                } catch (IOException e) {
                    runnable = false;
                    e.printStackTrace();
                }
                
                /**
                 * ��������
                 */
                /*final RequestInfo requestInfo = parseInputString(inputString);
                if (requestInfo != null) {
                    /**
                     * read���󣬻ص����߳�
                     */
                    workHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //onGetRequest(requestInfo);
                        }
                    });
                }
            }
        }
    
    
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
