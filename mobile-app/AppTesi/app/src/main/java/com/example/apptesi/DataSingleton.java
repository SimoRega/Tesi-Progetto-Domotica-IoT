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


    private DataSingleton() throws JSONException {
        String tmp = "{\"measures\":[{\"ApparentPower\":0,\"Current\":0,\"Factor\":0,\"Power\":0,\"ReactivePower\":0,\"Today\":0,\"Total\":0.004,\"TotalStartTime\":\"2023-02-01T10:51:48\",\"Voltage\":238,\"Yesterday\":0.003},{\"ApparentPower\":0,\"Current\":0,\"Factor\":0,\"Power\":0,\"ReactivePower\":0,\"Today\":0,\"Total\":0.004,\"TotalStartTime\":\"2023-02-01T11:51:48\",\"Voltage\":237,\"Yesterday\":0.003},{\"ApparentPower\":1385,\"Current\":5.958,\"Factor\":0.99,\"Power\":1372,\"ReactivePower\":188,\"Today\":0.002,\"Total\":0.005,\"TotalStartTime\":\"2023-02-01T12:51:48\",\"Voltage\":232,\"Yesterday\":0.003},{\"ApparentPower\":1408,\"Current\":5.967,\"Factor\":0.26,\"Power\":362,\"ReactivePower\":1361,\"Today\":0.004,\"Total\":0.007,\"TotalStartTime\":\"2023-02-01T13:51:48\",\"Voltage\":236,\"Yesterday\":0.003},{\"ApparentPower\":402,\"Current\":1.7,\"Factor\":0.9,\"Power\":363,\"ReactivePower\":174,\"Today\":0.004,\"Total\":0.007,\"TotalStartTime\":\"2023-02-01T14:51:48\",\"Voltage\":237,\"Yesterday\":0.003},{\"ApparentPower\":718,\"Current\":3.07,\"Factor\":0.99,\"Power\":710,\"ReactivePower\":106,\"Today\":0.005,\"Total\":0.008,\"TotalStartTime\":\"2023-02-01T15:51:48\",\"Voltage\":234,\"Yesterday\":0.003},{\"ApparentPower\":1387,\"Current\":5.967,\"Factor\":0.99,\"Power\":1370,\"ReactivePower\":218,\"Today\":0.006,\"Total\":0.009,\"TotalStartTime\":\"2023-02-01T16:51:48\",\"Voltage\":232,\"Yesterday\":0.003},{\"ApparentPower\":1387,\"Current\":5.967,\"Factor\":0.99,\"Power\":170,\"ReactivePower\":218,\"Today\":0.006,\"Total\":0.009,\"TotalStartTime\":\"2023-02-01T17:51:48\",\"Voltage\":232,\"Yesterday\":0.003},{\"ApparentPower\":1087,\"Current\":5.967,\"Factor\":0.99,\"Power\":356,\"ReactivePower\":218,\"Today\":0.006,\"Total\":0.009,\"TotalStartTime\":\"2023-02-01T18:51:48\",\"Voltage\":232,\"Yesterday\":0.003},{\"ApparentPower\":138,\"Current\":5.967,\"Factor\":0.99,\"Power\":1370,\"ReactivePower\":218,\"Today\":0.006,\"Total\":0.009,\"TotalStartTime\":\"2023-02-01T22:30:48\",\"Voltage\":232,\"Yesterday\":0.003}]}";
        energyJSON = new JSONObject(tmp);
        JSONArray measures = energyJSON.getJSONArray("measures");
        for(int i=0; i<measures.length();i++){
            measuresCollection.add(measures.getJSONObject(i));
        }
    }

    public static DataSingleton getInstance(){
        if(dataSingleton==null){
            try {
                dataSingleton = new DataSingleton();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
