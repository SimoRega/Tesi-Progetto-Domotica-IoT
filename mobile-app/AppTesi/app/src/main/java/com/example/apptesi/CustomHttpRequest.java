package com.example.apptesi;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomHttpRequest implements Runnable{
    private OkHttpClient client;
    private String url="";
    private List<String> params=null;
    private boolean isStatusInfo = false;
    private DataSingleton ds = DataSingleton.getInstance();
    private Activity act;

    private boolean toServer = false;
    private String body = "{}";
    private boolean isPlug = false;

    public CustomHttpRequest(String url, boolean toServer){
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.url = url;
        this.isPlug = isPlug;
        this.toServer=toServer;
    }
    public CustomHttpRequest(String url, String body){
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.url = url;
        this.body = body;
    }

    @Override
    public void run() {
        try {
            if(isPlug){
                client.newCall(new Request.Builder().url(url).build()).execute();
            }else{
                System.out.println(body);

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody requestBody = RequestBody.create(mediaType, body) ;
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    System.out.println(response);
                } catch (IOException e) {
                    // Handle the exception
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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