package com.example.apptesi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageListenerService extends Service {
    private static final String CHANNEL_ID = "MessageListenerServiceChannel";
    private static int NOTIFICATION_ID = 1;
    private static final int PORT = 8080;

    private boolean running = false;
    private ServerSocket serverSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    running = true;
                    while (running) {
                        Socket socket = serverSocket.accept();

                        InputStream inputStream = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        String message = reader.readLine();
                        showNotification(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Message Listener Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String message) {

        try{
            String tmpString[]=message.split("@");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(tmpString[0])
                    .setContentText(tmpString[1])
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[] {2000,1000})
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID++, builder.build());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}