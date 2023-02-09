package com.example.apptesi.device;

import androidx.annotation.NonNull;

import org.json.JSONObject;

public class SmartDevice {

    private String name;
    private String ip;
    private boolean hasPM;
    private String label;
    private boolean state = false;
    private String response = "";
    private JSONObject energyData = new JSONObject();
    //TODO isON for sonoff
    //private boolean isOn = true;

    public SmartDevice(String Name, String Ip, String label){
        this.name=Name;
        this.ip=Ip;
        this.label=label;
        if(name.contains("tasmota"))
            hasPM=true;
        /*else
            isOn = true;

         */
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean hasPM() {
        return hasPM;
    }

    public String getResponse() {
        return response;
    }
    /*
    public boolean isOn() {
        return isOn;
    }

     */

    public void toggle(){
        //this.isOn=!isOn;
        this.state=!state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState(){
        return state;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setEnergyData(JSONObject energyData) {
        this.energyData = energyData;
    }

    public JSONObject getEnergyData() {
        return energyData;
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder().append("Name: ").append(name).append(" | IP: ").append(ip).append(" | Label: ").append(label).toString();
    }
}
