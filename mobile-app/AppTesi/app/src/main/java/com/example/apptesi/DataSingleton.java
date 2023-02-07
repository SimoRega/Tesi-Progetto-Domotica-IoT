package com.example.apptesi;

import com.example.apptesi.device.SmartDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSingleton {
    private static DataSingleton dataSingleton;
    private static String currentState="";
    private static String lastState="";
    private static List<String> data=new ArrayList<>();
    private static Map<String,String> devices = new HashMap<>();
    private static List<SmartDevice> smartDevices = new ArrayList<>();
    private static JSONObject energyJSON ;

    private static List<JSONObject> measuresCollection = new ArrayList<>();


    private DataSingleton() {

    }
    public void setMeasures(String message) {
        measuresCollection.clear();
        try {
            energyJSON = new JSONObject(message);
            JSONArray measures = energyJSON.getJSONArray("measures");
            for(int i=0; i<measures.length();i++){
                measuresCollection.add(measures.getJSONObject(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

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

    synchronized public static void addSmartDevice(SmartDevice sd){
        smartDevices.add(sd);
    }

    synchronized public static List<SmartDevice> getSmartDevices(){ return smartDevices; }

    synchronized public static SmartDevice getSmartDevice(String name){
        for(SmartDevice s : smartDevices){
            if(s.getName().equals(name))
                return s;
        }
        return null;
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

    public List<JSONObject> getMeasures() {
        return measuresCollection;
    }


}
