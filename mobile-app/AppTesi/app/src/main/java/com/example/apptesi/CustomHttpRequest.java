package com.example.apptesi;

import android.app.Activity;
import android.widget.Toast;

import com.example.apptesi.device.SmartDevice;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomHttpRequest{
    private OkHttpClient client;
    private String url="";
    private List<String> params=null;
    private boolean isStatusInfo = false;
    private DataSingleton ds = DataSingleton.getInstance();
    private Activity act;
    private String body = "{}";
    private boolean isPlug = false;

    private SmartDevice smartDevice;

    public CustomHttpRequest(String url,List<String> params){
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.url = url;
        this.body = body;
        this.params=params;
    }

    public CustomHttpRequest(SmartDevice smartDevice){
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.smartDevice=smartDevice;
    }

    synchronized public void makeHttpRequest(HttpRequestType rType){
        switch(rType){
            case STATE:
                makeStateRequest();
                break;
            case TOGGLE:
                makeToggleRequest();
                break;
            case COMMAND:
                break;
            case SERVER:
                makeServerRequest();
                break;
        }
    }

    private void makeServerRequest() {
        Thread tmpThread = new Thread(() -> {
            SmartDevice tmpSD = smartDevice;
            StringBuilder par = new StringBuilder();
            for(String tmpS : params){
                par.append(tmpS);
            }
            Request request = new Request.Builder().url(url+par).build();;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if(params.contains("energyDevice=")){
                            String devName= params.get(1);
                            String res = response.body().string();
                            ds.setMeasures(devName,res);
                            try {
                                List<JSONObject> measures = ds.getMeasures(ds.getSmartDevice(devName));
                                for (int i = 0; i < measures.size(); i++) {
                                    JSONObject resJSON = measures.get(i);

                                    float tmpAvgPower = ds.getAvgPower();
                                    float tmpAvgVoltage = ds.getAvgVoltage();
                                    float tmpAvgAmpere = ds.getAvgAmpere();
                                    float tmpTotPower = ds.getTotPower();
                                    int n = ds.getSmartDevices().size();
                                    DecimalFormat df = new DecimalFormat("0.00");

                                    ds.setAvgPower((tmpAvgPower + Float.parseFloat(resJSON.getJSONObject("ENERGY").getString("ApparentPower"))) / n);
                                    ds.setAvgVoltage((tmpAvgVoltage + Float.parseFloat(resJSON.getJSONObject("ENERGY").getString("Voltage"))) / n);
                                    ds.setAvgAmpere((tmpAvgAmpere + Float.parseFloat(resJSON.getJSONObject("ENERGY").getString("Current"))) / n);
                                    ds.setTotPower((tmpTotPower + Float.parseFloat(resJSON.getJSONObject("ENERGY").getString("ApparentPower"))));

                                    System.out.println("DATI "+ds.getAvgPower()+" "+ds.getAvgVoltage()+" "+ds.getAvgAmpere()+" "+ds.getTotPower());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        });
        tmpThread.start();
    }

    private void makeToggleRequest() {
        Thread tmpThread = new Thread(() -> {
            Request request = null;
            if(smartDevice.hasPM()){
                request = new Request.Builder().url("http://"+smartDevice.getIp()+"/?m=1&o=1").build();
            }
            else{
                //TODO toggle request sonoff
                /*
                String body="";
                JSONObject tmpJSON = new JSONObject();
                if(smartDevice.isOn()){
                    body="{'deviceid':'','data':{'switch':'off'}}";
                    try {
                        tmpJSON.put("deviceid","");
                        tmpJSON.put("data",new JSONObject().put("switch","off"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    body="{\"deviceid\":\"\",\"data\":{\"switch\":\"on\"}}";
                    try {
                        tmpJSON.put("deviceid","");
                        tmpJSON.put("data",new JSONObject().put("switch","on"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, tmpJSON.toString()) ;
                System.out.println("JSON : 8===D "+tmpJSON);
                request = new Request.Builder()
                        .url("http://"+smartDevice.getIp()+":8081/zeroconf/switch")
                        .method("POST",requestBody)
                        .addHeader("Content-Type"," text/plain")
                        .addHeader("User-Agent", "PostmanRuntime/7.30.1")
                        .addHeader("Postman-Token","35c9f02c-0c76-404a-aed3-e64d7c67e9a8" )
                        .addHeader("Host", "192.168.1.150:8081")
                        .addHeader("Accept-Encoding"," gzip, deflate, br")
                        .addHeader("Connection","keep-alive" )
                        .addHeader("Content-Length","44" )
                        .build();
                */
            }
            System.out.println("REQUEST: "+request);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                    }
                }
            });

        });
        tmpThread.start();
        smartDevice.toggle();
        System.out.println("TOGGLE "+smartDevice.getName()+" "+smartDevice.getState());
    }

    private void makeStateRequest() {
        Thread tmpThread = new Thread(() -> {
            Request request = null;
            if(smartDevice.hasPM()){
                request = new Request.Builder().url("http://"+smartDevice.getIp()+"/cm?cmnd=Status").build();
            }else{
                //TODO inforequest sonoff
                /*
                String body="{\"deviceid\": \"\",\"data\": \"\"} }";
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody requestBody = RequestBody.create(mediaType, body) ;
                request = new Request.Builder()
                        .url("http://"+smartDevice.getIp()+":8081/zeroconf/info")
                        .post(requestBody)
                        .build();

                 */
            }
            System.out.println("REQUEST: "+request);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseBody);
                            String tmp="";
                            if(smartDevice.hasPM()){
                                tmp = responseJSON.getJSONObject("Status").get("Power").toString();
                                smartDevice.setState(tmp.equals("1")?true:false);
                            }else{
                                tmp = responseJSON.getJSONObject("data").get("switch").toString();
                                smartDevice.setState(tmp.equals("on")?true:false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        tmpThread.start();
        System.out.println("STATE "+smartDevice.getName()+" "+smartDevice.getState());
    }

}