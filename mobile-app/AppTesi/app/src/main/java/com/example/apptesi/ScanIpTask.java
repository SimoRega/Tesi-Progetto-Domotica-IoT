package com.example.apptesi;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ScanIpTask extends AsyncTask<Void, String, Void> {

    /*
    Scan IP 192.168.1.100~192.168.1.110
    you should try different timeout for your network/devices
    */
    private ArrayList<String> ipList;
    private ArrayAdapter<String> adapter;
    static final String subnet = "192.168.1.";
    static final int lower = 100;
    static final int upper = 110;
    static final int timeout = 5000;
    private Context ctx=null;

    public ScanIpTask(Context ctx, ArrayList<String> ipList,ArrayAdapter<String> adapter){
        this.ctx=ctx;
        this.ipList=ipList;
        this.adapter=adapter;
    }

    @Override
    protected void onPreExecute() {
        ipList.clear();
        adapter.notifyDataSetInvalidated();
        Toast.makeText(ctx, "Scan IP...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = lower; i <= upper; i++) {
            String host = subnet + i;

            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(timeout)){
                    publishProgress(InetAddress.getByName(host).getHostName()+
                            " "+inetAddress.toString());
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
        //Toast.makeText(ctx, values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Toast.makeText(ctx, "Done", Toast.LENGTH_LONG).show();
    }
}