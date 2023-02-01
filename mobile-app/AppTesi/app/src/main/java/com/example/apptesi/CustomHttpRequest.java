package com.example.apptesi;

import android.app.Activity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomHttpRequest implements Runnable{
    final OkHttpClient client = new OkHttpClient();
    private String url="";
    private List<String> params=null;
    private boolean isStatusInfo = false;
    private DataSingleton ds = null;
    private boolean isServer=false;
    private Activity act;

    public CustomHttpRequest(String url, List<String> params,Activity act){
        this.url=url;
        this.act=act;
        this.params=params;
        ds=DataSingleton.getInstance();
        if(params.contains("cm?cmnd=Status") && params.size()==1){
            isStatusInfo = true;
        }else if(params.contains("appMeasure")){
            isServer=true;
        }else{
            this.url+='?';
        }
    }
    public CustomHttpRequest(String url){
        this.url=url;
        ds=DataSingleton.getInstance();

        isServer=true;

    }

    @Override
    public void run() {
        if(!isServer){
            for(Map.Entry e : ds.getDevices().entrySet()){
                request("http://"+(String)e.getValue()+"/?");
            }
        }else{
            try {
                client.newCall(new Request.Builder().url(url).build()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void request(String dictUrl){
        StringBuilder paramsSB = new StringBuilder();
        for (String s : params) {
            paramsSB.append(s);
        }
        System.out.println("REST REQUEST : "+dictUrl+paramsSB);
        Request request = new Request.Builder()
                .url(dictUrl+paramsSB)
                .build();
        Response response=null;
        try {
            if(!isStatusInfo){
                client.newCall(request).execute();
            }
            response = client.newCall(new Request.Builder().url("http://192.168.1.105/cm?cmnd=Status").build()).execute();
            String tmpR=response.body().string();
            ds.setData(tmpR);
            System.out.println(ds.getCurrentState());

            /*
            act.runOnUiThread(()->{
                Button btn= act.findViewById(R.id.buttonREST);
                if(ds.getCurrentState().equals("1")){
                    btn.setBackgroundColor(Color.GREEN);
                }else{
                    btn.setBackgroundColor(Color.RED);
                }
            });

             */
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
}