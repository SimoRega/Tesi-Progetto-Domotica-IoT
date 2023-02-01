package com.example.apptesi;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSingleton {
    private static DataSingleton dataSingleton;
    private static String currentState="";
    private static String lastState="";
    private static List<String> data=new ArrayList<>();
    private static Map<String,String> devices = new HashMap<>();

    private DataSingleton(){}

    public static DataSingleton getInstance(){
        if(dataSingleton==null){
            dataSingleton = new DataSingleton();
        }
        return dataSingleton;
    }

    synchronized public void setData(String s){
        data.add(s);
        lastState=currentState;
        try {
            currentState = String.valueOf(new JSONObject(s).getJSONObject("Status").get("Power"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    synchronized public static List<String> getData() {
        return data;
    }

    synchronized public static String getLastData(){
        return data.get(data.size() - 1);
    }

    synchronized public static String getCurrentState(){
        return currentState;
    }

    synchronized public static String getLastState() {
        return lastState;
    }

    synchronized public static void addDevice(String name, String ip){
        devices.put(name,ip);
    }

    synchronized public static Map<String,String> getDevices(){
        return devices;
    }
    synchronized public static List<String> getDevicesAsList(){
        List<String> dev = new ArrayList<>();
        for(Map.Entry<String,String> e : devices.entrySet()){
            //dev.add(e.getKey()+" : "+e.getValue());
            dev.add((e.getKey().split(".home"))[0]);
        }
        return dev;
    }

}
