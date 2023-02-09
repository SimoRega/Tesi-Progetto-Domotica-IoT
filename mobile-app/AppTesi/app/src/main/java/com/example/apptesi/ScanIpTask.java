package com.example.apptesi;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.apptesi.device.SmartDevice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ScanIpTask extends AsyncTask<Void, String, Void> {

    private ArrayList<String> ipList;
    private ArrayAdapter<String> adapter;
    static final String subnet = "192.168.1.";
    static final int lower = 2;
    static final int upper = 254;
    static final int timeout = 200;
    private Context ctx=null;
    private DataSingleton ds = DataSingleton.getInstance();

    public ScanIpTask(Context ctx, ArrayList<String> ipList,ArrayAdapter<String> adapter){
        this.ctx=ctx;
        this.ipList=ipList;
        this.adapter=adapter;
    }

    @Override
    protected void onPreExecute() {
        ipList.clear();
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = lower; i <= upper; i++) {
            String host = subnet + i;
            if(this.isCancelled())
                return null;
            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(timeout)){
                    String tmps=InetAddress.getByName(host).getHostName();
                    publishProgress(tmps.split(".homenet")[0]+
                            "\n "+ inetAddress);
                    if(tmps.split(".homenet")[0].contains("DesktopSimo")){
                        ds.setServer(new SmartDevice(tmps.split(".homenet")[0],inetAddress.getHostAddress(),"SERVER"));
                        Intent intentNotifications = new Intent(ctx,MessageListenerService.class);
                        ctx.startService(intentNotifications);
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        ipList.add(values[0]);
        adapter.notifyDataSetInvalidated();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }
}